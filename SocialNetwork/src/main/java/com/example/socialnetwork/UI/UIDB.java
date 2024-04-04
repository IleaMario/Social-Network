package com.example.socialnetwork.UI;

import com.example.socialnetwork.Domain.*;
import com.example.socialnetwork.Service.ServiceDB;
import com.example.socialnetwork.Validators.ValidException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class UIDB {
    private final ServiceDB service;

    public UIDB(ServiceDB service) {
        this.service = service;
    }
    public void printAllUsers(){
        System.out.println("Connected users:");
        service.getAllUsers().forEach(System.out::println);
        System.out.println("\n");
    }
    public void printAllFriendships(){
        System.out.println("Friendships:");
        service.getAllFriendship().forEach(System.out::println);
        System.out.println("\n");
    }
    public void printAllAccounts(){
        System.out.println("Acounts:");
        service.getAllAccounts().forEach(System.out::println);
        System.out.println("\n");
    }

    public void printAllFriendRequests(){
        System.out.println("Friend requests:");
        service.getAllFriendRequest().forEach(System.out::println);
        System.out.println("\n");
    }
    public void printAllMessages(){
        System.out.println("Messages:");
        service.getAllMessages().forEach(System.out::println);
        System.out.println("\n");
    }

    public void printAllCommunities(){
        System.out.println("Communities:");
        int nr=service.NumberOfCommunities();
        System.out.println("There are "+nr+"communities\n");
        service.getAllCommunities().forEach(System.out::println);
        System.out.println("\n");
    }



    ///MENIU

    void printMenu()
    {
        System.out.println("-------------------------------------------------------");
        System.out.println("0. Exit");
        System.out.println("1. Add User");
        System.out.println("2. Delete User");
        System.out.println("3. Add Friendship");
        System.out.println("4. Delete Friendship");
        System.out.println("5. Print Users");
        System.out.println("6. Print Friendships");
        System.out.println("7. Get Communities");
        System.out.println("9. Display Friendships from a Specific Month");
        System.out.println("10. Display Accounts");
        System.out.println("11. Add Account");
        System.out.println("12. Delete Account");
        System.out.println("13. Print all friend requests");
        System.out.println("14. Add Friend Request");
        System.out.println("15. Delete Friend Request");
        System.out.println("16. Print all Messages");
        System.out.println("17. Add a Message");
        System.out.println("18. Delete Message");
        System.out.println(">>>>> ");
    }

    public void run()
    {
        Scanner scan=new Scanner(System.in);
        label:
        while(true)
        {
            printMenu();
            String cmd = scan.nextLine();
            System.out.println("----------------------------------------------");
            switch (cmd) {
                case "1":
                    addUser();
                    break;
                case "2":
                    deleteUser();
                    break;
                case "3":
                    addFriendship();
                    break;
                case "4":
                    deleteFriendship();
                    break;
                case "5":
                    printAllUsers();
                    break;
                case "6":
                    printAllFriendships();
                    break;
                case "7":
                    printAllCommunities();
                    break;
                case "9":
                    service.showFriendshipsInMonth();
                    break;
                case "10":
                    printAllAccounts();
                    break;
                case "11":
                    addAccount();
                    break;
                case "12":
                    deleteAccount();
                    break;
                case "13":
                    printAllFriendRequests();
                    break;
                case "14":
                    addFriendRequest();
                    break;
                case "15":
                    deleteFriendRequest();
                    break;
                case "16":
                    printAllMessages();
                    break;
                case "17":
                    addMessage();
                    break;
                case "18":
                    deleteMessage();
                    break;
                case "19":
                    service.getConversation(3l,7l).forEach(System.out::println);
                    break;
                case "0":
                    break label;
                default:
                    System.out.println("COMANDA INVALIDA!");
                    break;
            }
        }
    }
    void addUser()
    {
        Scanner scan=new Scanner(System.in);
        System.out.println("First Name: ");
        String firstName = scan.nextLine();
        System.out.println("Last Name: ");
        String lastName = scan.nextLine();

        try {
            User u=new User(firstName, lastName);
            service.saveUser(u);
        }
        catch (ValidException e){
            System.out.println("Invalid user!");}
        catch (IllegalArgumentException e) {
            System.out.println("Invalid arguments!");}
    }

    void addFriendRequest()
    {
        Scanner scan=new Scanner(System.in);
        System.out.println("From: ");
        String from= scan.nextLine();
        System.out.println("To: ");
        String to= scan.nextLine();

        try {
            Tuple<Long,Long> pair= new Tuple<>(Long.parseLong(from),Long.parseLong(to));
            FriendRequest req=new FriendRequest(pair);

            service.saveFriendRequest(req);
        }
        catch (ValidException e){
            System.out.println("Invalid user!");}
        catch (IllegalArgumentException e) {
            System.out.println("Invalid arguments!");}
    }

    void addAccount()
    {
        Scanner scan=new Scanner(System.in);
        System.out.println("Username: ");
        String username = scan.nextLine();
        System.out.println("Password: ");
        String password = scan.nextLine();
        System.out.println("User id: ");
        String userID = scan.nextLine();

        try {
            Long uid=Long.parseLong(userID);
            Account a=new Account(username,password,uid);
            service.saveAccount(a);
        }
        catch (ValidException e){
            System.out.println("Invalid account!");}
        catch (IllegalArgumentException e) {
            System.out.println("Invalid arguments!");}
    }

    void addMessage()
    {
        Scanner scan=new Scanner(System.in);
        System.out.println("From: ");
        String from = scan.nextLine();
        System.out.println("Text: ");
        String text = scan.nextLine();
        System.out.println("To: ");
        String to = scan.nextLine();

        try {
            Long fromid=Long.parseLong(from);
            var arr= Arrays.stream(to.split(",")).toList();
            ArrayList<Long> toUsersID=new ArrayList<>();
            for (String ar : arr) {
                Long a = Long.parseLong(ar);
                toUsersID.add(a);
            }
            Message m=new Message(fromid,toUsersID,text);
            //System.out.println(m);
            service.saveMessage(m);
        }
        catch (ValidException e){
            System.out.println("Invalid account!");}
        catch (IllegalArgumentException e) {
            System.out.println("Invalid arguments!");}
    }

    void deleteUser()
    {
        printAllUsers();
        Scanner scan=new Scanner(System.in);
        System.out.println("Id: ");
        String val = scan.nextLine();
        try {
            Long id =  Long.parseLong(val);
            var user=service.findUserById(id);
            service.deleteUser(user);
        }
        catch (IllegalArgumentException e) {System.out.println("The id can not contain letters or symbols and can not be empty!!");}
    }

    void deleteAccount()
    {
        printAllAccounts();
        Scanner scan=new Scanner(System.in);
        System.out.println("Username: ");
        String u= scan.nextLine();
        try {
            var acc=service.findAccountByUsername(u);
            service.deleteAccount(acc);
        }
        catch (IllegalArgumentException e) {System.out.println("The id can not contain letters or symbols and can not be empty!!");}
    }

    void deleteFriendRequest()
    {
        Scanner scan=new Scanner(System.in);
        System.out.println("From: ");
        String from= scan.nextLine();
        System.out.println("To: ");
        String to= scan.nextLine();
        try {
            Tuple<Long,Long> pair= new Tuple<>(Long.parseLong(from),Long.parseLong(to));
            var req=service.findFriendRequestById(pair);
            service.deleteFriendRequest(req);
        }
        catch (IllegalArgumentException e) {System.out.println(e);}
    }

    void deleteMessage()
    {
        Scanner scan=new Scanner(System.in);
        System.out.println("Id: ");
        String id= scan.nextLine();

        try {
            Long idd=Long.parseLong(id);
            var req=service.findMessageById(idd);
            service.deleteMessage(req);
        }
        catch (IllegalArgumentException e) {System.out.println(e);}
    }

    void addFriendship()
    {
        printAllUsers();
        Scanner scan=new Scanner(System.in);
        System.out.println("Id-ul primului user: ");
        String val = scan.nextLine();
        System.out.println("Id-ul celui de-al doilea user: ");
        String val2 = scan.nextLine();
        try{
            long id1 = 0L;
            long id2 = 0L;
            try{
                id1 =  Long.parseLong(val);
                id2 =  Long.parseLong(val2);
            }
            catch(IllegalArgumentException e) {System.out.println("The id can not contain letters or symbols!");}
            User u1=service.findUserById(id1);
            User u2=service.findUserById(id2);
            service.createFriendship(u1,u2);
        }
        catch (ValidException e){System.out.println("The friendship is invalid!");}
        catch (IllegalArgumentException e) {System.out.println("Invalid arguments!");}
    }

    void deleteFriendship()
    {
        printAllFriendships();
        Scanner scan=new Scanner(System.in);
        System.out.println("Id friendship: ");
        String val = scan.nextLine();

        try
        {
            Long id1 =  Long.parseLong(val);
            var f=service.findFriendshipById(id1);
            if(f!=null){
                var u1=service.findUserById(f.getFriendsPair().first());
                var u2=service.findUserById(f.getFriendsPair().second());
                service.deleteFriendship(u1,u2);
            }
        }
        catch (IllegalArgumentException e) {System.out.println("Invalid arguments!");}
    }




}