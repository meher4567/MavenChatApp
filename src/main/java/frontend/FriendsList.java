package frontend;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;

public class FriendsList {
    private Set<Friend> friends;

    public FriendsList() {
        friends = new HashSet<>();
    }

    public synchronized boolean addFriend(Friend friend) {
        return friends.add(friend);
    }

    public synchronized boolean removeFriend(Friend friend) {
        return friends.remove(friend);
    }

    public synchronized Set<Friend> getFriends() {
        return Collections.unmodifiableSet(friends);
    }

    public synchronized List<Friend> getList() {
        return new ArrayList<>(friends);
    }
}
