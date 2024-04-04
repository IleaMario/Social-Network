package com.example.socialnetwork.Service;


import com.example.socialnetwork.Domain.Friendship;
import com.example.socialnetwork.Domain.Message;
import com.example.socialnetwork.Domain.User;

public interface ServiceInterface {
    User save(User e);
    User update(User e);
    User delete(User e);
    User find(User e);

    Friendship createFriendship(User e1, User e2, Long ID);
    Friendship findFriendship(User e1, User e2);
    Friendship deleteFriendship(User e1,User e2);

    Message createMessage(String text, User receiver, User sender, Long ID);
    Message findMessage(String text,User receiver,User sender);
    Message deleteMessage(String text, User receiver, User sender);
    Iterable<User> getAllUsers();
    Iterable<Friendship> getAllFriendship();
    Iterable<Message> getAllMessage();
}
