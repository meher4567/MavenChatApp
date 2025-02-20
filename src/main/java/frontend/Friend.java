package frontend;
public class Friend {
    private String name;
    private boolean isFavorite;

    public Friend(String name) {
        this.name = name;
        this.isFavorite = false;
    }

    public String getName() {
        return name;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Friend friend = (Friend) obj;
        return name.equals(friend.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
