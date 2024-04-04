package com.example.socialnetwork.Domain;

import java.time.LocalDateTime;


public class Friendship extends Entity<Long> {
    private Tuple<Long, Long> friendsPair;
    private LocalDateTime date;
    public Friendship(Tuple<Long, Long> friendsPair, LocalDateTime date){
        this.friendsPair=friendsPair;
        this.date=date;
    }
    public Friendship(Tuple<Long, Long> friendsPair){
        this.friendsPair=friendsPair;
        this.date=LocalDateTime.now();
    }
    public Tuple<Long, Long> getFriendsPair() {
        return friendsPair;
    }

    public void setFriendsPair(Tuple<Long, Long> friendsPair) {
        this.friendsPair = friendsPair;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Friendship{" +
                "friendsPair=" + friendsPair +
                ", date=" + date +
                ",ID="+id+
                '}';
    }
}
