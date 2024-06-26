package com.example.socialnetwork.utils.events;

import com.example.socialnetwork.Domain.Friendship;

public class FriendshipEvent implements Event{

    private ChangeEventType type;
    private Friendship friendship, oldFriendship;

    public FriendshipEvent(ChangeEventType type, Friendship friendship) {
        this.type = type;
        this.friendship = friendship;
    }
    public FriendshipEvent(ChangeEventType type, Friendship friendship, Friendship oldFriendship) {
        this.type = type;
        this.friendship = friendship;
        this.oldFriendship = oldFriendship;
    }

    public ChangeEventType getType() {
        return type;
    }

    public Friendship getFriendship() {
        return friendship;
    }

    public Friendship getOldFriendship() {
        return oldFriendship;
    }

}