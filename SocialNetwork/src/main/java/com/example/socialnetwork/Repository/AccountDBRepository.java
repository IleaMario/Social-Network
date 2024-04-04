package com.example.socialnetwork.Repository;

import com.example.socialnetwork.Domain.Account;
import com.example.socialnetwork.Validators.Validator;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class AccountDBRepository implements Repository<String, Account> {

    private String url;
    private String username;
    private String password;

    private Validator<Account> validator;


    public AccountDBRepository(String url, String username, String password, Validator<Account> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator=validator;
    }

    @Override
    public Optional<Account> findOne(String usernameAccpunt) {
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from accounts " +
                    "where username = ?");

        ) {
            statement.setString(1, usernameAccpunt);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                String password = resultSet.getString("password");
                Long userID = resultSet.getLong("userid");
                Account u = new Account(usernameAccpunt,password,userID);
                u.setId(usernameAccpunt);
                return Optional.ofNullable(u);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public Iterable<Account> findAll() {
        Set<Account> users = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from accounts");
             ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next())
            {
                String username=resultSet.getString("username");
                String password=resultSet.getString("password");
                Long userID=resultSet.getLong("userid");
                Account account=new Account(username,password,userID);
                account.setId(username);
                users.add(account);

            }
            return new ArrayList<>(users);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<Account> save(Account entity) {

        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        validator.validate(entity);

        String query = "INSERT INTO accounts(\"username\",\"password\",\"userid\") VALUES (?,?,?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setString(1, entity.getUsername());
            statement.setString(2, entity.getPassword());
            statement.setLong(3, entity.getUserId());
            statement.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return Optional.ofNullable(entity);
    }

    @Override
    public Optional<Account> delete(String u) {
        Optional<Account> optionalAccount = findOne(u);

        if (optionalAccount.isEmpty()) {
            throw new IllegalArgumentException("Account with id " + u + " does not exist!");
        }

        String query = "DELETE FROM accounts WHERE \"username\" = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)
        ) {
            statement.setString(1, u);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                return optionalAccount;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Optional.empty();
    }


    @Override
    public Optional<Account> update(Account entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        validator.validate(entity);
        if(findOne(entity.getId()) == null)
            throw new IllegalArgumentException("The entity does not exist!");

        String query = "UPDATE users SET \"userId\" = ?,\"password\" = ? WHERE \"username\" = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setLong(1, entity.getUserId());
            statement.setString(2, entity.getPassword());
            statement.setString(3, entity.getUsername());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Optional.ofNullable(entity);
    }
}
