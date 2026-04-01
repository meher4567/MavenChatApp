# Maven Chat App: Detailed Logic and Architecture Documentation

## 1. Scope of This Document

This document explains how the project works internally, with primary focus on logic and behavior.

- Main focus: backend and application logic
- Secondary focus: how the UI triggers those logic paths
- Not a UI styling guide

## 2. System Architecture (Logic-First View)

The application is organized into three practical layers:

1. Presentation layer (`frontend` package)
- Responsible for user actions and screen transitions
- Collects input, triggers DAO calls, and routes chat events

2. Application/transport layer (`backend` package)
- Handles socket communication between client and server
- Dispatches incoming messages to interested listeners
- Buffers outgoing messages on transient send failure

3. Persistence layer (`server` package)
- Encapsulates SQL operations through DAO classes
- Handles users, friend graph, friend requests, profiles, and messages
- Uses pooled DB connections from HikariCP

The project uses a direct desktop-client to socket-server pattern:
- Server listens on a TCP port and creates one thread per connection
- Each desktop client opens one socket to the server
- MySQL stores state (users, friends, requests, message history, offline messages)

## 3. Main Runtime Components and Responsibilities

### 3.1 `backend.ChatServer`

- Owns the server socket (`PORT = 12345`)
- Maintains active client writers in `ConcurrentHashMap<String, PrintWriter>`
- Maintains user online state in `ConcurrentHashMap<String, Boolean>`
- For each accepted socket, spawns a `ClientHandler` thread

`ClientHandler` responsibilities:
- Read first line as username and register session
- Broadcast join/leave notifications
- Deliver offline messages on connect
- Process message stream in line pairs:
  - line 1 = recipient
  - line 2 = message body
- Route online messages directly
- Persist offline messages when recipient is offline

### 3.2 `backend.ChatClient`

- Opens one socket to `serverAddress:serverPort`
- Sends username immediately after connecting
- Starts two background tasks:
  1. `ServerHandler`: reads server message stream continuously
  2. `MessageBufferHandler`: retries buffered outbound messages

It also manages listener-based event propagation:
- `addMessageListener` / `removeMessageListener`
- Incoming messages are dispatched to listeners
- `MessageComponent` listeners are filtered by matching friend name

### 3.3 `frontend.ChatPanel`

This class acts as the main logic coordinator on the client side after login.

Responsibilities:
- Creates and owns a single `ChatClient` instance per logged-in user
- Loads friend list from DB using `FriendDAO`
- Creates one `MessageComponent` per friend
- Registers message components as listeners on `ChatClient`
- Loads historical messages for each friend conversation
- Supports friend list refresh with add/remove detection

### 3.4 DAO layer (`server` package)

- `DatabaseConnection`: central pooled connection provider
- `UserDAO`: registration, login verification, user updates/deletes
- `AllUserDAO`: lightweight user index for search/add-friend flow
- `FriendDAO`: friend relationship CRUD
- `FriendRequestDAO`: request send/list/accept logic
- `MessageDAO`: insert, load, offline fetch/delete, update/delete
- `Profile`: basic profile insert/update

## 4. End-to-End Logic Flows

## 4.1 Application startup flow

1. Server process starts (`ChatServer.main`)
2. Server blocks on `accept()` in a loop
3. Client desktop app starts (`ChatApplicationWindow.main`)
4. Login/Register panel appears
5. Successful login creates `ChatPanel(username)`
6. `ChatPanel` initializes:
- loads friends
- creates `MessageComponent` per friend
- loads historical messages
- starts `ChatClient`

## 4.2 Registration flow

Initiator: `RegisterPanel`

1. User submits username/password/confirm/email
2. Input validation:
- all fields non-empty
- password equals confirm password
3. `UserDAO.registerUser(...)` called:
- password hashed before DB insert
- insert into `users`
4. `AllUserDAO.addUser(username)` called to maintain searchable user index

Persistence impact:
- `users` table gets auth record
- `allUsers` table gets discoverability record

## 4.3 Login flow

Initiator: `LoginPanel`

1. Validate non-empty username/password
2. Check user existence with `UserDAO.usernameExists`
3. Verify password via `UserDAO.loginUser`

`loginUser` logic:
- Fetch stored password by username
- Call `verifyPassword(input, storedHash)`

Password verification supports two formats:
1. New format: `PBKDF2$iterations$saltBase64$hashBase64`
2. Legacy format: hex SHA-256

Effect:
- Existing old DB users can still log in
- New/updated passwords are stored with PBKDF2 salted hashes

## 4.4 Friend discovery and request flow

Initiator: `AddFriendsComponent`

1. User searches by username
2. Guard checks:
- search not empty
- cannot add self
- not already friends (`FriendDAO.searchFriendByUsername`)
3. User existence checked in `allUsers`
4. If valid, `FriendRequestDAO.sendFriendRequest(sender, receiver)` inserts into `friend_requests`

## 4.5 Friend request acceptance flow

Initiator: `AcceptFriendRequestComponent`

1. Load pending sender list via `FriendRequestDAO.getPendingRequests(receiver)`
2. On accept:
- delete request row from `friend_requests`
- create two directional rows in `friends`:
  - sender -> receiver
  - receiver -> sender

Result:
- Friendship modeled as symmetric pair of directed rows
- Friend list queries stay simple (`WHERE username = ?`)

## 4.6 Friend removal flow

Initiator: `EditFriendsComponent`

1. Load friends via `FriendDAO.getFriends(username)`
2. On delete click:
- delete `username -> friend`
- delete `friend -> username`
3. Refresh friend list view

## 4.7 Real-time message send flow (online path)

Initiator: `MessageComponent.sendMessage`

1. User enters text and clicks send (or presses Enter)
2. `ChatClient.sendMessage(friend, text)` writes two lines to socket
3. Local UI immediately adds a `Message` object with type `sent`
4. Message persisted to DB via `MessageDAO.addMessage`

Server side routing:
1. `ClientHandler` reads recipient + message
2. Checks `userStatus[recipient]`
3. If online, writes sender + message to recipient writer

Receiver side:
1. `ChatClient.ServerHandler` reads sender + message
2. Dispatches to matching `MessageComponent` listener
3. Listener creates local `Message(type=received)` and appends to UI

## 4.8 Offline message flow

When recipient is offline:

1. Server cannot route directly
2. Server stores row in `messages` with `message_type = 'offline'`
3. On recipient reconnect, `checkAndSendOfflineMessages` executes:
- loads `MessageDAO.getOfflineMessages(userName)`
- sends each message to client socket
- deletes delivered offline rows via `deleteOfflineMessages`

This gives at-least-once style delivery after reconnection, with cleanup after push.

## 4.9 Conversation history load flow

Initiator: `MessageComponent.loadMessages`

1. Query sent messages (`LoadMessages(user, friend, 'sent')`)
2. Query received messages (`LoadMessages(friend, user, 'received')`)
3. Merge both lists
4. Remove duplicates via `HashSet`
5. Sort by exact timestamp ascending
6. Render sequentially in message panel

Result:
- Conversation reconstruction from persisted records
- Ordering based on DB timestamp parsing

## 5. Data and State Model

## 5.1 Message object semantics

`frontend.Message` carries:
- `message_id`
- `sender`
- `receiver`
- `text`
- `timeStamp`
- `message_type`

`message_type` values used in current logic:
- `sent` for sender-side saved messages
- `received` for recipient-side saved/loaded messages
- `offline` for temporarily stored server-side deferred delivery

## 5.2 Friendship model

- Stored in table `friends`
- Each friendship is represented in both directions
- This simplifies list queries and delete operations

## 5.3 Session state model in server memory

`ChatServer` in-memory state:
- `clients`: username -> socket writer
- `userStatus`: username -> boolean online/offline

Both use `ConcurrentHashMap` to remain safe under multi-threaded access from client handler threads.

## 6. Concurrency and Threading Model

## 6.1 Server concurrency

- One accept loop thread in `ChatServer.main`
- One `ClientHandler` thread per active socket connection
- Shared maps are concurrent collections

## 6.2 Client concurrency

For each `ChatClient`:
- main UI thread creates client
- one background read thread (`ServerHandler`)
- one background retry thread (`MessageBufferHandler`)

`MessageBufferHandler` behavior:
- blocks on `LinkedBlockingQueue.take()`
- retries buffered messages if send path previously failed

## 6.3 UI thread safety

Incoming message updates are wrapped with `SwingUtilities.invokeLater` inside `MessageComponent.onMessageReceived`, ensuring Swing component mutation happens on the EDT.

## 7. Security Logic

## 7.1 Database secret handling

`DatabaseConnection` now resolves config in this order:
1. JVM property
2. Environment variable
3. Default URL only (user/password remain required)

Required values:
- `chat.db.user` or `CHAT_DB_USER`
- `chat.db.password` or `CHAT_DB_PASSWORD`

## 7.2 Password hashing strategy

`UserDAO` now stores passwords with:
- PBKDF2WithHmacSHA256
- per-password random salt (16 bytes)
- 65,536 iterations
- 256-bit derived key

Storage format:
- `PBKDF2$<iterations>$<saltBase64>$<hashBase64>`

Backward compatibility:
- if hash does not start with `PBKDF2$`, verification falls back to legacy SHA-256 hex comparison

## 8. Error Handling and Recovery Behavior

- DAO methods generally return boolean or empty lists on failure
- SQL exceptions are caught and printed
- Socket read failure in `ChatClient.ServerHandler` stops buffer loop (`killBlockingThread`)
- Outbound send exceptions add payload to retry queue

## 9. Interface Summary (Secondary Focus)

Although UI is not the primary concern here, the interface wiring matters for logic triggering:

- `ChatApplicationWindow` uses card layout for login/register states
- `LoginPanel` and `RegisterPanel` invoke authentication and user creation logic
- `ChatPanel` orchestrates friend list, message view routing, and modal dialogs for profile/add/accept/edit friend workflows
- `MessageComponent` is conversation-scoped and acts as both renderer and message listener

The interface is effectively a command surface over DAO operations and client socket events.

## 10. Logic-Level Notes and Constraints

Current behavior includes a few implementation constraints worth knowing:

1. Message IDs are generated on client side with `AtomicInteger`, while DB schema also supports auto-increment IDs; this can create coupling assumptions.
2. Registration panel transitions to `"chat"` card, while the primary flow to active chat is implemented through login and `showChatPanel(...)`.
3. DAO layer is tightly coupled to UI components (direct DAO usage from Swing panels), which is simple but reduces test isolation.
4. Message persistence writes happen in both send path and historical load conventions via message types.
5. Logging currently relies on console prints rather than structured logging framework.

## 11. Practical Logic Trace (Single Message Example)

Example: user `alice` sends `hello` to `bob` while `bob` is online.

1. `MessageComponent(alice,bob).sendMessage()`
2. `ChatClient.sendMessage("bob", "hello")`
3. Socket sends two lines: `bob`, `hello`
4. `ChatServer.ClientHandler(alice)` receives lines
5. Finds `userStatus[bob] == true`
6. Calls `sendMessageToUser("bob", "alice", "hello")`
7. Bob socket receives two lines: `alice`, `hello`
8. Bob `ChatClient.ServerHandler` dispatches to matching `MessageComponent`
9. Bob `MessageComponent.onMessageReceived` adds a `received` message to UI
10. Alice already has local `sent` message persisted by sender-side path

This summarizes the full transport + dispatch + persistence behavior.

## 12. File Map for Core Logic

- `src/main/java/backend/ChatServer.java`: server lifecycle, routing, offline delivery
- `src/main/java/backend/ChatClient.java`: client transport, listener dispatch, retry buffer
- `src/main/java/frontend/ChatPanel.java`: post-login orchestration and friend/message wiring
- `src/main/java/frontend/MessageComponent.java`: conversation logic and message persistence hooks
- `src/main/java/server/DatabaseConnection.java`: pooled DB connectivity and config resolution
- `src/main/java/server/UserDAO.java`: authentication and password verification logic
- `src/main/java/server/MessageDAO.java`: conversation/offline message persistence
- `src/main/java/server/FriendDAO.java`: friendship relation operations
- `src/main/java/server/FriendRequestDAO.java`: request and acceptance pipeline
- `src/main/java/server/AllUserDAO.java`: searchable user index
