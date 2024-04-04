package com.example.socialnetwork.Domain;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Message extends Entity<Long>{
    private Long from;
    private String color;

    public String getAlignment() {
        return alignment;
    }

    public void setAlignment(String alignment) {
        this.alignment = alignment;
    }

    private String alignment;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    private ArrayList<Long> to;
    private String message;
    private LocalDateTime date;
    private Long reply;

    public Message(Long from, ArrayList<Long> to, String message, LocalDateTime date)
    {
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = date;
        this.reply= null;
    }

    public Message(Long from, ArrayList<Long> to, String message)
    {
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = LocalDateTime.now();
        this.reply = null;
    }
    public Message(Long from, ArrayList<Long> to, String message, LocalDateTime date,Long reply)
    {
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = date;
        this.reply= reply;
    }

    public Message(Long from, ArrayList<Long> to, String message,Long reply)
    {
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = LocalDateTime.now();
        this.reply = reply;
    }

    public Long getFrom() { return this.from; }

    public void setFrom(Long from) { this.from = from; }

    public ArrayList<Long> getTo() { return this.to; }

    public void setTo(ArrayList<Long> to) { this.to = to; }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getDate() { return date; }

    public void setDate(LocalDateTime date) { this.date = date; }

    public Long getReply() {
        return reply;
    }

    public void setReply(Long reply) {
        this.reply = reply;
    }

    @Override
    public String toString() {
        return "Message{" +
                "text='" + message + '\'' +
                ", sender=" + from +
                ", receiver=" + to +
                ", date=" + date +
                ",reply=" +reply+
                '}';
    }
}
