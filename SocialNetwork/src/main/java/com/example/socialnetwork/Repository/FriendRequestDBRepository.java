package com.example.socialnetwork.Repository;

import com.example.socialnetwork.Domain.*;
import com.example.socialnetwork.Repository.Paging.Page;
import com.example.socialnetwork.Repository.Paging.Pageable;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FriendRequestDBRepository implements Repository<Tuple<Long,Long>, FriendRequest> {

    private String url;
    private String username;
    private String password;


    public FriendRequestDBRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Optional<FriendRequest> findOne(Tuple<Long,Long> id) {

        String query = "SELECT * FROM friendRequests WHERE  \"from\" = ? AND \"to\" = ?";
        FriendRequest friendRequest   = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setLong(1, id.first());
            statement.setLong(2, id.second());
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                Long from = resultSet.getLong("from");
                Long to = resultSet.getLong("to");
                Timestamp date = resultSet.getTimestamp("date");
                String statusString = resultSet.getString("status");
                LocalDateTime dateTime = new Timestamp(date.getTime()).toLocalDateTime();
                Tuple<Long, Long> pair=new Tuple<>(from,to);
                Status status= Status.valueOf(statusString);

                friendRequest = new FriendRequest(pair,status,dateTime);
                friendRequest.setId(pair);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return Optional.ofNullable(friendRequest);

    }

    @Override
    public Iterable<FriendRequest> findAll() {

        String query = "SELECT * FROM friendRequests ";
        List<FriendRequest> friendships = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery();
        ) {
            while(resultSet.next()){
                Long from = resultSet.getLong("from");
                Long to = resultSet.getLong("to");
                Timestamp date = resultSet.getTimestamp("date");
                String statusString = resultSet.getString("status");
                LocalDateTime dateTime = new Timestamp(date.getTime()).toLocalDateTime();
                Tuple<Long, Long> pair=new Tuple<>(from,to);
                Status status= Status.valueOf(statusString);

                FriendRequest friendRequest = new FriendRequest(pair,status,dateTime);
                friendRequest.setId(pair);
                friendships.add(friendRequest);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return friendships;

    }


    @Override
    public Optional<FriendRequest> save(FriendRequest entity) {

        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }

        String query = "INSERT INTO friendrequests(\"from\",\"to\",\"status\",\"date\") VALUES (?,?,?,?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setLong(1, entity.getFriendsPair().first());
            statement.setLong(2, entity.getFriendsPair().second());
            statement.setString(3, entity.getStatus().toString());
            Timestamp date = Timestamp.valueOf(entity.getDate());
            statement.setTimestamp(4, date);
            statement.executeUpdate();



        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return Optional.ofNullable(entity);
    }

    @Override
    public Optional<FriendRequest> delete(Tuple<Long,Long> id) {
        Optional<FriendRequest> optionalFriendRequest = findOne(id);

        if (optionalFriendRequest.isEmpty()) {
            throw new IllegalArgumentException("FriendRequest with id " + id + " does not exist!");
        }

        String query = "DELETE FROM friendRequests WHERE  \"from\" = ? AND \"to\" = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)
        ) {
            statement.setLong(1, id.first());
            statement.setLong(2, id.second());
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                return optionalFriendRequest;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }


    @Override
    public Optional<FriendRequest> update(FriendRequest entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        if(findOne(entity.getId()) == null)
            throw new IllegalArgumentException("The entity does not exist!");

        String query = "UPDATE friendrequests SET \"status\" = ?,\"date\" = ? WHERE  \"from\" = ? AND \"to\" = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setString(1, entity.getStatus().toString());
            Timestamp date = Timestamp.valueOf(entity.getDate());
            statement.setTimestamp(2, date);
            statement.setLong(3, entity.getId().first());
            statement.setLong(4, entity.getId().second());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(entity);
    }


//
//    ///PAGING
//
    public int getNumberOfElementsFROM(User user){
        int numberOfElements = 0;
        Long id = user.getId();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             // pas 2: design & execute SLQ
             PreparedStatement statement = connection.prepareStatement("select count(*) as count from friendrequests where \"from\"= ?");

        ) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            // pas 3: process result set
            while (resultSet.next()){
                numberOfElements = resultSet.getInt("count");
            }
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return numberOfElements;
    }

    public int getNumberOfElementsTo(User user){
        int numberOfElements = 0;
        Long id = user.getId();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             // pas 2: design & execute SLQ
             PreparedStatement statement = connection.prepareStatement("select count(*) as count from friendrequests where \"to\"= ?");

        ) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            // pas 3: process result set
            while (resultSet.next()){
                numberOfElements = resultSet.getInt("count");
            }
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return numberOfElements;
    }

    public Page<FriendRequest> findAllOnPageFROM(Pageable pageable, User user){


        int numberOfElements = getNumberOfElementsFROM(user);
        int limit = pageable.getPageSize();
        int offset = pageable.getPageSize()*pageable.getPageNumber();
        System.out.println(offset + " ?>= "+numberOfElements);
        if(offset >= numberOfElements)
            return new Page<>(new ArrayList<>(), numberOfElements);
        // prereq: create empty set
        List<FriendRequest> friendRequests = new ArrayList<>();
        // pas 1: connect to the database
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from friendrequests WHERE \"from\"=? limit ? offset ?");
        ) {
            statement.setLong(1, user.getId());
            statement.setInt(3, offset);
            statement.setInt(2,limit);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                Long from = resultSet.getLong("from");
                Long to = resultSet.getLong("to");
                Timestamp date = resultSet.getTimestamp("date");
                String statusString = resultSet.getString("status");
                LocalDateTime dateTime = new Timestamp(date.getTime()).toLocalDateTime();
                Tuple<Long, Long> pair=new Tuple<>(from,to);
                Status status= Status.valueOf(statusString);

                FriendRequest friendRequest = new FriendRequest(pair,status,dateTime);
                friendRequest.setId(pair);
                friendRequests.add(friendRequest);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return new Page<>(friendRequests,numberOfElements);
    }

    public Page<FriendRequest> findAllOnPageTO(Pageable pageable, User user){


        int numberOfElements = getNumberOfElementsTo(user);
        int limit = pageable.getPageSize();
        int offset = pageable.getPageSize()*pageable.getPageNumber();
        System.out.println(offset + " ?>= "+numberOfElements);
        if(offset >= numberOfElements)
            return new Page<>(new ArrayList<>(), numberOfElements);
        // prereq: create empty set
        List<FriendRequest> friendRequests = new ArrayList<>();
        // pas 1: connect to the database
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from friendrequests WHERE \"to\"=? limit ? offset ?");
        ) {
            statement.setLong(1, user.getId());
            statement.setInt(3, offset);
            statement.setInt(2,limit);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                Long from = resultSet.getLong("from");
                Long to = resultSet.getLong("to");
                Timestamp date = resultSet.getTimestamp("date");
                String statusString = resultSet.getString("status");
                LocalDateTime dateTime = new Timestamp(date.getTime()).toLocalDateTime();
                Tuple<Long, Long> pair=new Tuple<>(from,to);
                Status status= Status.valueOf(statusString);

                FriendRequest friendRequest = new FriendRequest(pair,status,dateTime);
                friendRequest.setId(pair);
                friendRequests.add(friendRequest);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return new Page<>(friendRequests,numberOfElements);
    }


    public Page<FriendRequest> findAll(Pageable pageable, User user, int from) {
        if(from==1)
            return findAllOnPageFROM(pageable,user);
        else
            return findAllOnPageTO(pageable,user);
    }
}
