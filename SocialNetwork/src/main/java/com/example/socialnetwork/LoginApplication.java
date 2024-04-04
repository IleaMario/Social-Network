package com.example.socialnetwork;

import com.example.socialnetwork.Controller.LoginController;
import com.example.socialnetwork.Domain.*;
import com.example.socialnetwork.Repository.*;
import com.example.socialnetwork.Repository.Paging.PagingRepository;
import com.example.socialnetwork.Service.ServiceDB;
import com.example.socialnetwork.Validators.Validator;
import com.example.socialnetwork.Validators.ValidatorHelper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginApplication extends Application {

    private Validator<User> validatorUser;
    private PagingRepository<Long, User> repoUser;
    private Validator<Friendship> validatorFriendship;
    private Repository<Long, Friendship> repoFriendship;

    private Validator<Account> validatorAccount;
    private Repository<String, Account> repoAccount;

    Validator<Message> validatorMessage;
    Repository<Long, Message> repoMessage;
    Repository<Tuple<Long,Long>, FriendRequest> repoFriendRequest;

    private ServiceDB service;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        String url = "***";
        String username = "***";
        String password = "***";

        validatorUser= ValidatorHelper::ValidUserName;
        repoUser = new UserDBRepository(url, username, password,validatorUser);

        validatorFriendship=ValidatorHelper::ValidFriendship;
        repoFriendship = new FriendshipDBRepository(url, username, password,validatorFriendship);

        validatorAccount=ValidatorHelper::ValidAccount;
        repoAccount = new AccountDBRepository(url, username, password,validatorAccount);

        validatorMessage=ValidatorHelper::ValidMessage;
        repoMessage = new MessageDBRepository(url, username, password,validatorMessage);

        repoFriendRequest = new FriendRequestDBRepository(url, username, password);

        service=new ServiceDB(repoUser,repoFriendship, repoFriendRequest,repoAccount, repoMessage);

        initView(stage);
        stage.setTitle("Login");
        stage.show();

    }

    public void initView(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginApplication.class.getResource("login-view.fxml"));
        AnchorPane socialNetworkLayout = fxmlLoader.load(); // Încarcă conținutul fișierului FXML într-un AnchorPane
        stage.setScene(new Scene(socialNetworkLayout, 500, 400)); // Setează scena folosind conținutul încărcat
        LoginController controller = fxmlLoader.getController();
        controller.setUserService(service);
    }



}
