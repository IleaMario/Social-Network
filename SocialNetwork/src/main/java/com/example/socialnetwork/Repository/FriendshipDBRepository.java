package com.example.socialnetwork.Repository;

import com.example.socialnetwork.Domain.Friendship;
import com.example.socialnetwork.Domain.Tuple;
import com.example.socialnetwork.Validators.Validator;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;



public class FriendshipDBRepository implements Repository<Long, Friendship> {

    private String url;
    private String username;
    private String password;

    private Validator<Friendship> validator;


    public FriendshipDBRepository(String url, String username, String password,Validator<Friendship> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator=validator;
    }

    @Override
    public Optional<Friendship> findOne(Long longID) {

        String query = "SELECT * FROM friendships WHERE \"friendshipid\" = ?";
        Friendship friendship   = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);

        ) {
            statement.setLong(1, longID);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                Long idUser1 = resultSet.getLong("user1id");
                Long idUser2 = resultSet.getLong("user2id");
                Timestamp date = resultSet.getTimestamp("FriendsFrom");
                LocalDateTime friendsFrom = new Timestamp(date.getTime()).toLocalDateTime();
                Tuple<Long, Long> pair=new Tuple<>(idUser1,idUser2);
                friendship = new Friendship(pair,friendsFrom);
                friendship.setId(longID);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return Optional.ofNullable(friendship);

    }

    @Override
    public Iterable<Friendship> findAll() {

        String query = "SELECT * FROM friendships";
        List<Friendship> friendships = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery();
        ) {
            while(resultSet.next()){
                Long id = resultSet.getLong("friendshipid");
                Long idUser1 = resultSet.getLong("user1id");
                Long idUser2 = resultSet.getLong("user2id");
                Timestamp date = resultSet.getTimestamp("friendsfrom");
                LocalDateTime friendsFrom = LocalDateTime.ofInstant(date.toInstant(), ZoneOffset.ofHours(0));
                Tuple<Long, Long> pair=new Tuple<>(idUser1,idUser2);
                Friendship friendship = new Friendship(pair,friendsFrom);
                friendship.setId(id);
                friendships.add(friendship);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return friendships;

    }

    @Override
    public Optional<Friendship> save(Friendship entity) {

        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        validator.validate(entity);

        String query = "INSERT INTO friendships(\"user1id\",\"user2id\",\"friendsfrom\") VALUES (?,?,?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setLong(1, entity.getFriendsPair().first());
            statement.setLong(2, entity.getFriendsPair().second());
            Timestamp friendsFrom = Timestamp.valueOf(entity.getDate());
            statement.setTimestamp(3, friendsFrom);
            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return Optional.ofNullable(entity);
    }

    @Override
    public Optional<Friendship> delete(Long friendshipId) {
        Optional<Friendship> optionalFriendship = findOne(friendshipId);

        if (optionalFriendship.isEmpty()) {
            throw new IllegalArgumentException("Friendship with id " + friendshipId + " does not exist!");
        }

        String query = "DELETE FROM friendships WHERE \"friendshipid\" = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)
        ) {
            statement.setLong(1, friendshipId);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                return optionalFriendship;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }


    @Override
    public Optional<Friendship> update(Friendship entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        validator.validate(entity);
        if(findOne(entity.getId()) == null)
            throw new IllegalArgumentException("The entity does not exist!");

        String query = "UPDATE friendships SET \"user1id\" = ?,\"user2id\" = ?,\"friendsfrom\" = ? WHERE \"friendshipid\" = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setLong(1, entity.getFriendsPair().first());
            statement.setLong(2, entity.getFriendsPair().second());
            Timestamp friendsFrom = Timestamp.valueOf(entity.getDate());
            statement.setTimestamp(3, friendsFrom);
            statement.setLong(4, entity.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(entity);
    }
}
