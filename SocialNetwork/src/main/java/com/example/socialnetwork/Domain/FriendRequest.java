package com.example.socialnetwork.Domain;

import java.time.LocalDateTime;

public class FriendRequest extends Entity<Tuple<Long,Long>>
{
    private Tuple<Long, Long> friendsPair;
    Long to;
    Long from;
    private Status status;

    public Long getTo() {
        return to;
    }

    public Long getFrom() {
        return from;
    }

    private LocalDateTime date;

    public FriendRequest(Tuple<Long,Long>pair)
    {
        this.friendsPair=pair;
        this.from=pair.first();
        this.to=pair.second();
        this.status = Status.PENDING;
        this.date = LocalDateTime.now();
    }

    public Tuple<Long, Long> getFriendsPair() {
        return friendsPair;
    }

    public void setFriendsPair(Tuple<Long, Long> friendsPair) {
        this.friendsPair = friendsPair;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public FriendRequest(Tuple<Long,Long>pair, Status status, LocalDateTime date)
    {
        this.friendsPair=pair;
        this.status = status;
        this.from=pair.first();
        this.to=pair.second();
        this.date =date;
    }

    public String toString() {
        return "Friend Request{" +
                "From=" + friendsPair.first() +
                "To=" + friendsPair.second() +
                ", date=" + date +
                ",Status="+status+
                '}';
    }


}
