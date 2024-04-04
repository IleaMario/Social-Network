package com.example.socialnetwork.Validators;



import com.example.socialnetwork.Domain.Account;
import com.example.socialnetwork.Domain.Friendship;
import com.example.socialnetwork.Domain.Message;
import com.example.socialnetwork.Domain.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidatorHelper {
    public static void ValidUserName(User entity) {
        String regex = "[a-z]{2,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(entity.getFirstName());
        if (!matcher.find())
            throw new ValidException("First Name Invalid");
        matcher = pattern.matcher(entity.getLastName());
        if (!matcher.find())
            throw new ValidException("Last Name Invalid");
    }

    public static void ValidMessage(Message message){
        if(message.getMessage().isEmpty())
            throw new ValidException("Message can't be empty");
    }

    public static void ValidFriendship(Friendship entity) {
        var friends = entity.getFriendsPair();
        if (friends.first().equals(friends.second()))
            throw new ValidException("Can't be friend with yourself");
    }

    public static void ValidAccount(Account entity) {
        String regex = "[.]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(entity.getUsername());
        if (!matcher.find())
            throw new ValidException("First Name Invalid");
        matcher = pattern.matcher(entity.getPassword());
        if (!matcher.find())
            throw new ValidException("Last Name Invalid");
    }
}
