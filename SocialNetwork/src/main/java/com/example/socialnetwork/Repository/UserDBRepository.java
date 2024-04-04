package com.example.socialnetwork.Repository;

import com.example.socialnetwork.Domain.User;
import com.example.socialnetwork.Repository.Paging.Page;
import com.example.socialnetwork.Repository.Paging.Pageable;
import com.example.socialnetwork.Repository.Paging.PagingRepository;
import com.example.socialnetwork.Validators.Validator;

import java.sql.*;
import java.util.*;

public class UserDBRepository implements PagingRepository<Long, User> {

    private String url;
    private String username;
    private String password;

    private Validator<User> validator;


    public UserDBRepository(String url, String username, String password,Validator<User> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator=validator;
    }

    @Override
    public Optional<User> findOne(Long longID) {
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from users " +
                    "where UserId = ?");

        ) {
            statement.setInt    (1, Math.toIntExact(longID));
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                String firstName = resultSet.getString("FirstName");
                String lastName = resultSet.getString("LastName");
                User u = new User(firstName,lastName);
                u.setId(longID);
                return Optional.ofNullable(u);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }


    @Override
    public Iterable<User> findAll() {
        Set<User> users = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from users");
             ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next())
            {
                Long id= resultSet.getLong("UserId");
                String firstName=resultSet.getString("FirstName");
                String lastName=resultSet.getString("LastName");
                User user=new User(firstName,lastName);
                user.setId(id);
                users.add(user);

            }
            return new ArrayList<>(users);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<User> save(User entity) {

        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        validator.validate(entity);

        String query = "INSERT INTO users(\"firstname\",\"lastname\") VALUES (?,?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
            statement.executeUpdate();

            PreparedStatement statement3 = connection.prepareStatement("SELECT CURRVAL(pg_get_serial_sequence('users', 'userid'));");
            ResultSet resultSet = statement3.executeQuery();
            if(resultSet.next()) {

                Long ID = resultSet.getLong("currval");
                entity.setId(ID);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return Optional.ofNullable(entity);
    }

    @Override
    public Optional<User> delete(Long userId) {
        Optional<User> optionalUser = findOne(userId);

        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("User with id " + userId + " does not exist!");
        }

        String query = "DELETE FROM users WHERE \"userid\" = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)
        ) {
            statement.setLong(1, userId);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                return optionalUser;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Optional.empty();
    }


    @Override
    public Optional<User> update(User entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        validator.validate(entity);
        if(findOne(entity.getId()) == null)
            throw new IllegalArgumentException("The entity does not exist!");

        String query = "UPDATE users SET \"firstname\" = ?,\"lastname\" = ? WHERE \"userid\" = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
            statement.setLong(3, entity.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Optional.ofNullable(entity);
    }


    ///PAGING



    public int getNumberOfElements(){
        int numberOfElements = 0;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             // pas 2: design & execute SLQ
             PreparedStatement statement = connection.prepareStatement("select count(*) as count from users");
             ResultSet resultSet = statement.executeQuery();
        ) {
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
    public Page<User> findAllOnPage(Pageable pageable){
        int numberOfElements = getNumberOfElements();
        int limit = pageable.getPageSize();
        int offset = pageable.getPageSize()*pageable.getPageNumber();
        System.out.println(offset + " ?>= "+numberOfElements);
        if(offset >= numberOfElements)
            return new Page<>(new ArrayList<>(), numberOfElements);
        // prereq: create empty set
        List<User> users = new ArrayList<>();
        // pas 1: connect to db
        try (Connection connection = DriverManager.getConnection(url, username, password);
             // pas 2: design & execute SLQ
             PreparedStatement statement = connection.prepareStatement("select * from users limit ? offset ?");
        ) {
            statement.setInt(2, offset);
            statement.setInt(1,limit);
            ResultSet resultSet = statement.executeQuery();
            // pas 3: process result set
            while (resultSet.next())
            {
                Long id= resultSet.getLong("UserId");
                String firstName=resultSet.getString("FirstName");
                String lastName=resultSet.getString("LastName");
                User user=new User(firstName,lastName);
                user.setId(id);
                users.add(user);

            }
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
        // pas 3: return result list
        return new Page<>(users, numberOfElements);
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return findAllOnPage(pageable);
    }


}
