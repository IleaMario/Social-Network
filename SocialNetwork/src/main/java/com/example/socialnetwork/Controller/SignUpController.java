package com.example.socialnetwork.Controller;

import com.example.socialnetwork.Domain.Account;
import com.example.socialnetwork.Domain.User;
import com.example.socialnetwork.Service.ServiceDB;
import com.example.socialnetwork.utils.events.UserTaskChangeEvent;
import com.example.socialnetwork.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class SignUpController implements Observer<UserTaskChangeEvent> {

    public TextField usernameField;
    public PasswordField passwordField1;
    public PasswordField passwordField2;

    public ServiceDB service;
    public TextField firstNameField;
    public TextField lastNameField;
    public Label messageToUser;

    ObservableList<User> modelUsers = FXCollections.observableArrayList();
    ObservableList<User> modelFriends = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        this.usernameField.setText("");
        this.passwordField1.setText("");
        this.passwordField2.setText("");
        this.firstNameField.setText("");
        this.lastNameField.setText("");
    }

    // function for hashing the password using SHA-256
    private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = md.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(hashBytes);
    }


    public void handleSubmitButtonAction(ActionEvent actionEvent)
    {
        String username = this.usernameField.getText();
        String password1 = this.passwordField1.getText();
        String password2 = this.passwordField2.getText();
        String firstName = this.firstNameField.getText();
        String lastName = this.lastNameField.getText();

        if(password1.compareTo(password2) != 0)
        {
            messageToUser.setText("Passwords do not match!");
            messageToUser.setTextFill(Color.DARKRED);
            System.out.println(password2+" "+password1);
            this.passwordField1.setText("");
            this.passwordField2.setText("");
        }
        else  {

            try {
                User user = new User(firstName, lastName);
                User u = service.saveUser(user);

                String hashedPassword = hashPassword(password1);

                Account account = new Account(username, hashedPassword, u.getId());
                account.setId(username);
                service.saveAccount(account);

                messageToUser.setText("Account created successfully!");
                messageToUser.setTextFill(Color.DARKGREEN);
            } catch (Exception e) {
                messageToUser.setText(e.getMessage());
                messageToUser.setTextFill(Color.DARKRED);
                this.firstNameField.setText("");
                this.lastNameField.setText("");
                this.usernameField.setText("");
                this.passwordField1.setText("");
                this.passwordField2.setText("");

            }
        }
}


    public void setUserService(ServiceDB service) {
        this.service = service;

    }

    @Override
    public void update(UserTaskChangeEvent userTaskChangeEvent) {

    }
}
