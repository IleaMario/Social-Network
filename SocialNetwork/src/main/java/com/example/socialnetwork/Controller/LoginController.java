package com.example.socialnetwork.Controller;

import com.example.socialnetwork.Domain.Account;
import com.example.socialnetwork.HomeApplication;
import com.example.socialnetwork.Service.ServiceDB;
import com.example.socialnetwork.utils.events.UserTaskChangeEvent;
import com.example.socialnetwork.utils.observer.Observer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class LoginController implements Observer<UserTaskChangeEvent> {

    private ServiceDB service;
    @FXML
    public TextField usernameField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public Label messageToUser;

    @FXML
    public void initialize() {
        usernameField.setText("");
        passwordField.setText("");

        usernameField.setText("");
        passwordField.setText("");
    }

    public void  setUserService(ServiceDB service){
        this.service = service;
    }
    public boolean verifyPassword(String enteredPassword, String storedPassword) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = md.digest(enteredPassword.getBytes());
        String hashedEnteredPassword = Base64.getEncoder().encodeToString(hashBytes);

        return hashedEnteredPassword.equals(storedPassword);
    }

    public void handleSubmitButtonAction(ActionEvent event){
        try{
            Account account = service.findAccountByUsername(usernameField.getText());
            if(account==null){
                messageToUser.setText("Invalid username!");
                throw new Exception("Invalid username!");
            }

            boolean passwordsMatch = verifyPassword(passwordField.getText(), account.getPassword());

            if(!passwordsMatch)
            {
                messageToUser.setTextFill(Color.DARKRED);
                messageToUser.setText("Invalid password!");
                throw new Exception("Invalid password!");
            }

            FXMLLoader loader = new FXMLLoader(HomeApplication.class.getResource("home-view.fxml"));
            AnchorPane root = loader.load();
            HomeController controller = loader.getController();
            controller.setActiveUser(service.findUserById(account.getUserId()));
            controller.setService(service);
            Scene scene = new Scene(root, 800,800);
            Stage stage = new Stage();
            stage.setTitle("Home");
            stage.setScene(scene);
            stage.show();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            messageToUser.setText(e.getMessage());
            messageToUser.setTextFill(Color.DARKRED);

            usernameField.setText("");
            passwordField.setText("");
        }
    }

    public void handleSignUpAction(ActionEvent actionEvent)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(HomeApplication.class.getResource("signup-view.fxml"));
            AnchorPane root = loader.load();
            SignUpController controller = loader.getController();
            Scene scene = new Scene(root, 400, 400);
            Stage stage = new Stage();
            stage.setTitle("Sign up");
            stage.setResizable(false);
            controller.setUserService(service);
            stage.setScene(scene);
            stage.show();


        }catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void update(UserTaskChangeEvent userTaskChangeEvent) {

    }
}

