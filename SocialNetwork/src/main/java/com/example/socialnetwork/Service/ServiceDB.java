package com.example.socialnetwork.Service;


import com.example.socialnetwork.Domain.*;
import com.example.socialnetwork.Repository.Paging.Page;
import com.example.socialnetwork.Repository.Paging.Pageable;
import com.example.socialnetwork.Repository.Paging.PagingRepository;
import com.example.socialnetwork.Repository.Repository;
import com.example.socialnetwork.utils.events.ChangeEventType;
import com.example.socialnetwork.utils.events.UserTaskChangeEvent;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.StreamSupport;


public class ServiceDB implements  Observable<UserTaskChangeEvent> {
    private final PagingRepository<Long,User> RepoUser;
    private final Repository<Long, Friendship> RepoFriendship;

    private final Repository<Tuple<Long,Long>, FriendRequest> RepoFriendRequest;
    private final Repository<String, Account> RepoAccount;

    private final Repository<Long, Message> RepoMessage;

    private List<Observer<UserTaskChangeEvent>> observersUser =new ArrayList<>();

    public ServiceDB(PagingRepository<Long, User> repoUser, Repository<Long, Friendship> repoFriendship, Repository<Tuple<Long, Long>, FriendRequest> repoFriendRequest, Repository<String, Account> repoAccount, Repository<Long, Message> repoMessage) {
        RepoUser = repoUser;
        RepoFriendship = repoFriendship;
        RepoFriendRequest = repoFriendRequest;
        RepoAccount=repoAccount;
        RepoMessage = repoMessage;
    }

    ///USER

    public  User saveUser(User e) {
        try{
            var o=RepoUser.save(e);
            if(o.isPresent()) {
                var u=o.get();
                return o.get();
            }
        } catch (Exception exception){
            System.out.println(exception.getMessage());
        }
        return null;

    }


    public User updateUser(User e) {

        var o=RepoUser.update(e);
        if(o.isPresent())
            return o.get();
        return null;
    }


    public User deleteUser(User t) {
        try {
            if (t == null)
                throw new IllegalArgumentException("The user does not exist!");

            // Create a list to store the friendships to delete
            List<Friendship> friendshipsToDelete = new ArrayList<>();

            var l = RepoFriendship.findAll();
            var f = l.iterator();

            while (f.hasNext()) {
                var fr = f.next();
                if (Objects.equals(fr.getFriendsPair().first(), t.getId()) || Objects.equals(fr.getFriendsPair().second(), t.getId())) {
                    friendshipsToDelete.add(fr);
                }
            }

            // Now, delete the friendships
            for (Friendship friendship : friendshipsToDelete) {
                RepoFriendship.delete(friendship.getId());
            }

            var o=RepoUser.delete(t.getId());
            if(o.isPresent())
                return o.get();
            else{
                return null;
            }

        } catch (IllegalArgumentException e) {
            System.out.println("Invalid user!");
        }

        return null;
    }

    public User findUserById(Long id) {

        var o= RepoUser.findOne(id);
        if(o.isPresent())
            return o.get();
        else
            return null;
    }

    public Iterable<User> getAllUsers() {
        return RepoUser.findAll();
    }


    //FRIENDSHIPS

    public Friendship createFriendship(User e1, User e2) {
        try {
            Tuple<Long, Long> friends = new Tuple<>(e1.getId(), e2.getId());
            var friendship = new Friendship(friends);
            RepoFriendship.save(friendship);
            return findFriendshipByUserPair(e1, e2);

        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        return null;
    }

    public Friendship findFriendshipByUserPair(User e1, User e2) {
        for (var it : RepoFriendship.findAll()) {
            var pair = it.getFriendsPair();
            if (pair.first().equals(e1.getId()) && pair.second().equals(e2.getId()))
                return it;
        }
        return null;
    }
    public Friendship findFriendshipById(Long id) {
        var o=RepoFriendship.findOne(id);
        if(o.isPresent())
            return o.get();
        else
            return null;
    }


    public Friendship deleteFriendship(User e1, User e2) {
        var find = findFriendshipByUserPair(e1, e2);
        if (find != null) {
            RepoFriendship.delete(find.getId());
            notifyObservers(new UserTaskChangeEvent(ChangeEventType.DELETE,find));
        }
        return find;
    }


    public List<Long> getFriends(Long u) {
        List<Long> friends = new ArrayList<>();
        var friendships=RepoFriendship.findAll();
        for (var f : friendships) {
            if (f.getFriendsPair().first().equals(u))
                friends.add(f.getFriendsPair().second());
            if (f.getFriendsPair().second().equals(u))
                friends.add(f.getFriendsPair().first());

        }
        return friends;
    }


    private List<User> DFS(User u, Map<User, Boolean> visited) {
        List<User> Community = new ArrayList<>();
//        visited.put(u, Boolean.TRUE);
//        Community.add(u);
//        getFriends(u).forEach(id->{
//            var it=RepoUser.findOne(id).get();
//            if (!visited.containsKey(it) || !visited.get(it)) {
//                List<User> list = DFS(it, visited);
//                Community.addAll(list);
//            }
//        });
        return Community;
    }

    public int NumberOfCommunities() {
        Map<User, Boolean> visited = new HashMap<>();
        RepoUser.findAll().forEach(x -> {
            visited.put(x, Boolean.FALSE);
        });
        int CommNumb = 0;

        for (var it : RepoUser.findAll()) {
            if (!visited.get(it)) {
                ++CommNumb;
                DFS(it, visited);
            }
        }
        return CommNumb;
    }

    public List<List<User>> getAllCommunities() {
        Map<User, Boolean> visited = new HashMap<>();
        RepoUser.findAll().forEach(x -> visited.put(x, Boolean.FALSE));
        List<List<User>> Community = new ArrayList<>();
        ((List<User>) RepoUser.findAll())
                .stream()
                .filter(it -> !visited.get(it))
                .map(it -> DFS(it, visited))
                .forEach(Community::add);
        return Community;
    }





    public Iterable<Friendship> getAllFriendship() {
        return (ArrayList)RepoFriendship.findAll();
    }

    public void showFriendshipsInMonth() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Introduceti ID-ul utilizatorului:");
        String userIdString = scanner.nextLine();

        try {
            Long userId = Long.parseLong(userIdString);
            System.out.println("Introduceti luna (1-12):");
            int month = Integer.parseInt(scanner.nextLine());

            // Verificăm dacă luna este validă
            if (month < 1 || month > 12) {
                System.out.println("Luna introdusa nu este valida!");
                return;
            }

            // Obținem utilizatorul
            var user = findUserById(userId);

            // Verificăm dacă utilizatorul există
            if (user == null) {
                System.out.println("Utilizatorul nu exista!");
                return;
            }

            // Filtrăm relațiile de prietenie pentru a include doar cele din luna specificată
            var friendshipsInMonth = ((List<Friendship>) getAllFriendship())
                    .stream()
                    .filter(friendship -> {
                        var creationDate = friendship.getDate();
                        return creationDate.getMonthValue() == month;
                    })
                    .filter(friendship -> {
                        var pair = friendship.getFriendsPair();
                        return pair.first().equals(userId) || pair.second().equals(userId);
                    })
                    .toList();




            if (friendshipsInMonth.isEmpty()) {
                System.out.println("Nu exista relatii de prietenie in luna specificata!");
            } else {
                System.out.println("Nume|Prenume|Data de la care sunt prieteni");
                friendshipsInMonth.forEach(frindship->{
                    if(frindship.getFriendsPair().first().equals(userId)){
                        User u= findUserById(frindship.getFriendsPair().second());
                        System.out.println(u.getFirstName()+" |"+u.getLastName()+" |"+frindship.getDate());
                    }
                    else{
                        User u= findUserById(frindship.getFriendsPair().first());
                        System.out.println(u.getFirstName()+" "+u.getLastName()+" "+frindship.getDate());
                    }
                });
            }
        } catch (NumberFormatException e) {
            System.out.println("ID-ul sau luna introduse nu sunt valide!");
        }
    }

    //ACCOUNTS

    public Account saveAccount(Account e) {
        try{
            var o=RepoAccount.save(e);
            if(o.isPresent()) {
                Account a=o.get();
                notifyObservers(new UserTaskChangeEvent(ChangeEventType.ADD,a));
                return a;
            }
        } catch (Exception exception){
            System.out.println(exception.getMessage());
        }
        return null;

    }


    public Account updateAccount(Account e) {

        var o=RepoAccount.update(e);
        if(o.isPresent()) {
            Account a=o.get();
            //notifyObservers(new UserTaskChangeEvent(ChangeEventType.UPDATE,u));
            return a;
        }
        return null;
    }


    public Account deleteAccount(Account t) {
        try {
            if (t == null)
                throw new IllegalArgumentException("The user does not exist!");
            var o=RepoAccount.delete(t.getUsername());
            if(o.isPresent()) {
                Account u=o.get();
                notifyObservers(new UserTaskChangeEvent(ChangeEventType.DELETE,u));
                return u;
            }
            else{
                return null;
            }

        } catch (IllegalArgumentException e) {
            System.out.println("Invalid user!");
        }

        return null;
    }


    public Account findAccountByUsername(String username) {

        var o= RepoAccount.findOne(username);
        if(o.isPresent())
            return o.get();
        else
            return null;
    }

    public Iterable<Account> getAllAccounts() {
        return (ArrayList)RepoAccount.findAll();
    }


    ///FRIENDREQUEST

    public FriendRequest saveFriendRequest(FriendRequest friendRequest) {
        try{

            Optional<User> from = RepoUser.findOne(friendRequest.getFriendsPair().first());
            Optional<User> to = RepoUser.findOne(friendRequest.getFriendsPair().second());

            if(from.isEmpty() || to.isEmpty())
                throw new Exception("One or both users do not exist!");

            Friendship friendship1=findFriendshipByUserPair(from.get(),to.get());
            Friendship friendship2=findFriendshipByUserPair(to.get(),from.get());

            if(friendship2!=null || friendship1!=null){
                throw new Exception("Already friends!");
            }

            Tuple<Long,Long> pair=new Tuple<>(to.get().getId(), from.get().getId());
            FriendRequest f = findFriendRequestById(pair);

            if(f != null && f.getStatus() == Status.PENDING)
                throw  new Exception("You have already received a friend request from this user with Pending status");


            Tuple<Long,Long> pair2=new Tuple<>(from.get().getId(), to.get().getId());
            FriendRequest f2 = findFriendRequestById(pair2);

            if(f2 != null && f2.getStatus() == Status.PENDING)
                throw  new Exception("You have already sent a friend request to this user with Pending status");

            var o=RepoFriendRequest.save(friendRequest);
            if(o.isPresent()) {
                FriendRequest a=o.get();
                notifyObservers(new UserTaskChangeEvent(ChangeEventType.ADD,a));
                return a;
            }
        } catch (Exception exception){
            System.out.println(exception.getMessage());
        }
        return null;

    }

    public FriendRequest updateFriendRequest(FriendRequest e) {

        var o=RepoFriendRequest.update(e);
        if(o.isPresent()) {
            FriendRequest a=o.get();
            notifyObservers(new UserTaskChangeEvent(ChangeEventType.UPDATE,a));
            return a;
        }
        return null;
    }


    public FriendRequest deleteFriendRequest(FriendRequest t) {
        try {
            if (t == null)
                throw new IllegalArgumentException("The user does not exist!");
            var o=RepoFriendRequest.delete(t.getId());
            if(o.isPresent()) {
                FriendRequest u=o.get();
                notifyObservers(new UserTaskChangeEvent(ChangeEventType.DELETE,u));
                return u;
            }
            else{
                return null;
            }

        } catch (IllegalArgumentException e) {
            System.out.println("Invalid user!");
        }

        return null;
    }

    public FriendRequest findFriendRequestById(Tuple<Long,Long> id) {

        var o= RepoFriendRequest.findOne(id);
        if(o.isPresent())
            return o.get();
        else
            return null;
    }

    public Iterable<FriendRequest> getAllFriendRequest() {
        return (ArrayList)RepoFriendRequest.findAll();
    }

    public void accept(long from, long to){

        Optional<FriendRequest> optionalFriendRequest = RepoFriendRequest.findOne(new Tuple<>(from, to));

        try {
            if (optionalFriendRequest.isEmpty())
                throw new Exception("There is no friend request from user with ID " + from + "!");

            var friendRequest=optionalFriendRequest.get();
            if(friendRequest.getStatus() != Status.PENDING)
                throw new Exception("Can't accept this friend request!");

            friendRequest.setStatus(Status.ACCEPTED);
            friendRequest.setDate(LocalDateTime.now());

            RepoFriendRequest.update(friendRequest);
            this.notifyObservers(new UserTaskChangeEvent(ChangeEventType.UPDATE, friendRequest));

            Tuple<Long,Long> pair=new Tuple<>(from, to);
            Friendship friendship = new Friendship(pair);

            var friendship1=RepoFriendship.save(friendship).get();
            friendship.setId(friendship1.getId());
            notifyObservers(new UserTaskChangeEvent(ChangeEventType.ADD, friendship1));
        }
        catch(Exception e){
            System.out.println(e);
        }

    }

    public void reject(long from, long to)
    {
        Optional<FriendRequest> optionalFriendRequest = RepoFriendRequest.findOne(new Tuple<>(from, to));

        try {
            if (optionalFriendRequest.isEmpty())
                throw new Exception("There is no friend request from user with ID " + from + "!");

            var friendRequest = optionalFriendRequest.get();
            if (friendRequest.getStatus() != Status.PENDING)
                throw new Exception("Already accepted! Please delete your friend if you want to!");

            friendRequest.setStatus(Status.REJECTED);
            friendRequest.setDate(LocalDateTime.now());

            RepoFriendRequest.update(friendRequest);
            this.notifyObservers(new UserTaskChangeEvent(ChangeEventType.DELETE, friendRequest));

        }
        catch (Exception e){
            System.out.println(e);
        }

    }


    //MESSAGE

    public Message saveMessage(Message message) {
//        try{
//            var o=RepoMessage.save(e);
//            if(o.isPresent()) {
//                Message a=o.get();
//                //notifyObservers(new UserTaskChangeEvent(ChangeEventType.ADD,a));
//                return a;
//            }
//        } catch (Exception exception){
//            System.out.println(exception.getMessage());
//        }
//        return null;

        try {

            ArrayList<Long> recipients = message.getTo();
            ArrayList<Long> finalRecipients = new ArrayList<>();

            final String[] errorMessage = {""};
            recipients.forEach(
                    x -> {
                        User u1=findUserById(x);
                        User u2=findUserById(message.getFrom());
                        if (findFriendshipByUserPair(u1,u2)!=null ||findFriendshipByUserPair(u2,u1) !=null) {
                            finalRecipients.add(x);
                        } else {
                            errorMessage[0] += "You are not friends with user " + x + "!\n";
                        }
                    });

            message.setTo(finalRecipients);

            if (finalRecipients.size() > 0)
                RepoMessage.save(message);

            notifyObservers(new UserTaskChangeEvent(ChangeEventType.ADD, message));

            if (errorMessage[0].compareTo("") != 0)
                throw new Exception(errorMessage[0]);
        }
        catch (Exception ex){
            System.out.println(ex);
        }
        return message;

    }


    public Message updateMessage(Message e) {

        var o=RepoMessage.update(e);
        if(o.isPresent()) {
            Message a=o.get();
            //notifyObservers(new UserTaskChangeEvent(ChangeEventType.UPDATE,u));
            return a;
        }
        return null;
    }


    public Message deleteMessage(Message t) {
        try {
            if (t == null)
                throw new IllegalArgumentException("The user does not exist!");
            var o=RepoMessage.delete(t.getId());
            if(o.isPresent()) {
                Message u=o.get();
                notifyObservers(new UserTaskChangeEvent(ChangeEventType.DELETE,u));
                return u;
            }
            else{
                return null;
            }

        } catch (IllegalArgumentException e) {
            System.out.println("Invalid user!");
        }

        return null;
    }


    public Message findMessageById(Long id) {

        var o= RepoMessage.findOne(id);
        if(o.isPresent())
            return o.get();
        else
            return null;
    }

    public Message replyToAll(Message message, ArrayList<Long> arguments, Long from) {

        try{
            ArrayList<Long> recipients = new ArrayList<>();
            arguments.forEach(x -> {
                if(!x.equals(from))
                {
                    recipients.add(x);
                } });
            message.setTo(recipients);
            var o= RepoMessage.save(message);
            if(o.isPresent())
                return o.get();
            else
                return null;
        }
        catch (Exception e){
            System.out.println(e);
        }
        return null;
    }

    public Message replyToOne(Message message, Long recipient) {
        try {
            ArrayList<Long> recipients = new ArrayList<>();
            recipients.add(recipient);
            message.setTo(recipients);
            var o = RepoMessage.save(message);
            this.notifyObservers(new UserTaskChangeEvent(ChangeEventType.ADD, o.get()));
            if(o.isPresent())
                return o.get();
            else
                return null;
        }
        catch (Exception e){
            System.out.println(e);
        }
        return null;
    }


    public List<Message> getConversation(Long id1, Long id2)
    {
        Iterable<Message> all = RepoMessage.findAll();
        List<Message> messages = new ArrayList<>();
        for(Message message : all)
            for(Long id : message.getTo())
                if(id.equals(id2) && message.getFrom().equals(id1) || id.equals(id1) && message.getFrom().equals(id2))
                {
                    messages.add(message);
                }

        messages.sort(new Comparator<Message>()
        {
            @Override
            public int compare(Message o1, Message o2)
            {
                if(o1.getDate().isEqual(o2.getDate()))
                    return 0;
                if(o1.getDate().isBefore(o2.getDate()))
                    return -1;
                return 1;
            }
        });
        return messages;
    }

    public List<Message> getGroupConversation(Long from, ArrayList<Long> friends)
    {
        Iterable<Message> all = RepoMessage.findAll();
        List<Message> messages = new ArrayList<>();
        for(Message message : all)
        {
            if(message.getFrom().equals(from) && message.getTo() == friends)
                messages.add(message);
            //else
        }
        return messages;
    }

    public Map<User, LocalDateTime> getFriends2(long id)
    {
        Map<User, LocalDateTime> dto = new HashMap<>();

        Iterable<Friendship> iterable = RepoFriendship.findAll();
        StreamSupport.stream(iterable.spliterator(), false)
                .filter(x -> (x.getFriendsPair().getLeft().equals(id) || x.getFriendsPair().getRight().equals(id)))
                .forEach( friendship -> {
                            User friend;
                            if(friendship.getFriendsPair().first().equals(id))
                                friend = findUserById(friendship.getFriendsPair().getRight());
                            else
                                friend = findUserById(friendship.getFriendsPair().getLeft());
                            dto.put(friend, friendship.getDate());
                        }
                );

        return dto;
    }

    public Iterable<Message> getAllMessages() {
        return (ArrayList)RepoMessage.findAll();
    }

    @Override
    public void addObserver(Observer<UserTaskChangeEvent> e) {
        observersUser.add(e);
    }

    @Override
    public void removeObserver(Observer<UserTaskChangeEvent> e) {
        observersUser.remove(e);
    }

    @Override
    public void notifyObservers(UserTaskChangeEvent t) {
        observersUser.stream().forEach(x->x.update(t));
    }


    public Page<User> usersOnPage(Pageable pageable){
        return RepoUser.findAll(pageable);
    }

    public Page<FriendRequest> friendRequestOnPage(Pageable pageable, User u, int from){
        return RepoFriendRequest.findAll(pageable,u,from);
    }


}