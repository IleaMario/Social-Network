package com.example.socialnetwork.Controller;

import com.example.socialnetwork.Domain.*;
import com.example.socialnetwork.FriendRequestsApplication;
import com.example.socialnetwork.MessageApplication;
import com.example.socialnetwork.Repository.Paging.Page;
import com.example.socialnetwork.Repository.Paging.Pageable;
import com.example.socialnetwork.Service.ServiceDB;
import com.example.socialnetwork.utils.events.UserTaskChangeEvent;
import com.example.socialnetwork.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

public class HomeController implements Observer<UserTaskChangeEvent> {

    public int currentPage = 0;
    public int elementsPerPage = 3;


    public int totalNumberOfElements = 0;
    private User activeUser;
    private ServiceDB service;
    @FXML
    public Label welcomeMessage;

    @FXML
    public Button buttonSendFriendRequest;
    @FXML
    public Button buttonRemoveFriend;
    @FXML
    public Button buttonRequests;

    @FXML
    public TableView<User> friendsTable;
    @FXML
    public TableColumn<User, String> friendColumnFirstName;
    @FXML
    public TableColumn <User, String> friendColumnLastName;
    @FXML
    public TableColumn<User, Long> friendColumnEmail;
    @FXML
    public TableView<User> usersTable;
    @FXML
    public TableColumn<User, String> userColumnFirstName;
    @FXML
    public TableColumn <User, String> userColumnLastName;
    @FXML
    public TableColumn<User, Long> userEmail;
    @FXML
    public Label messageToUser;

    @FXML
    public Button buttonRefresh;
    @FXML
    public Button buttonMessage;

    @FXML
    Button previousButton;

    @FXML
    Button nextButton;

    @FXML
    TextField pageField;


    ObservableList<User> modelUsers = FXCollections.observableArrayList();
    ObservableList<User> modelFriends = FXCollections.observableArrayList();


    @FXML
    public void initialize() {


        friendColumnFirstName.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        friendColumnLastName.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        friendColumnEmail.setCellValueFactory(new PropertyValueFactory<User, Long>("id"));
        userColumnFirstName.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        userColumnLastName.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        userEmail.setCellValueFactory(new PropertyValueFactory<User, Long>("id"));

        usersTable.setItems(modelUsers);
        friendsTable.setItems(modelFriends);

        this.pageField.setOnKeyPressed(new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent event)
            {
                if(event.getCode().equals(KeyCode.ENTER))
                {
                    String text = pageField.getText();
                    elementsPerPage=Integer.valueOf(text);
                    initUsers();
                    pageField.clear();
                }
            }
        });


    }

    private void  loadingContent(){
        welcomeMessage.setText("Welcome "+activeUser.getFirstName()+" "+activeUser.getLastName()+"!");
        initUsers();
        initFriends();
    }

    public void setActiveUser(User activeUser) {
        this.activeUser = activeUser;

    }


    public void setService(ServiceDB service) {
        this.service = service;
        service.addObserver(this);
        loadingContent();
        service.addObserver(this);

    }

    private void initUsers()
    {

        Page<User> moviesPage = service.usersOnPage(new Pageable(currentPage, elementsPerPage));
        totalNumberOfElements = moviesPage.getTotalNumberOfElements();
        handlePagingNavigationChecks();
        List<User> movieList = StreamSupport.stream(moviesPage.getElementsOnPage().spliterator(), false).toList();
        System.out.println(movieList);
        modelUsers.setAll(movieList);

    }

    public void goToPreviousPage(){
        currentPage--;
        initUsers();
    }

    public void goToNextPage(){
        currentPage++;
        initUsers();
    }

    private void handlePagingNavigationChecks(){
        previousButton.setDisable(currentPage == 0);
        nextButton.setDisable((currentPage + 1) * elementsPerPage >= totalNumberOfElements);

    }



    public void initFriends()
    {

        if(activeUser!=null ){
            List<Long> allUsers = service.getFriends(activeUser.getId());
            List<User> all=new ArrayList<>();;
            allUsers.forEach(id->{
                User u=service.findUserById(id);
                all.add(u);
            });
            allUsers.forEach(System.out::println);
            modelFriends.setAll(all);
        }
    }

    public void handleSendFriendRequest(ActionEvent actionEvent)
    {
        User selected = (User) usersTable.getSelectionModel().getSelectedItem();
        if (selected != null)
        {
            Tuple<Long,Long> pair= new Tuple<>(activeUser.getId(),selected.getId());
            FriendRequest friendRequest = new FriendRequest(pair);
            try
            {
                service.saveFriendRequest(friendRequest);
                initUsers();
                this.messageToUser.setText("Friend request sent successfully!");
                this.messageToUser.setTextFill(Color.DARKGREEN);
            }catch (Exception e) {
                this.messageToUser.setText(e.getMessage());
                this.messageToUser.setTextFill(Color.RED);
            }
        }
        else
        {
            this.messageToUser.setText("No item selected!");
            this.messageToUser.setTextFill(Color.DARKRED);
        }
    }

    public void handleRemoveFriend(ActionEvent actionEvent)
    {
        User selected = (User) friendsTable.getSelectionModel().getSelectedItem();
        if (selected != null)
        {
            try
            {
                Friendship deleted = service.deleteFriendship(activeUser,selected);
                Friendship deleted2 = service.deleteFriendship(selected,activeUser);
                initFriends();
                initUsers();
                this.messageToUser.setText("Friend removed successfully!");
                this.messageToUser.setTextFill(Color.DARKGREEN);
            }catch (Exception e)
            {
                this.messageToUser.setText(e.getMessage());
                this.messageToUser.setTextFill(Color.DARKRED);
            }
        }
        else
        {
            this.messageToUser.setText("No item selected!");
            this.messageToUser.setTextFill(Color.DARKRED);
        }
    }

    public void handleRequests(ActionEvent actionEvent)
    {
        try
        {

            FXMLLoader loader = new FXMLLoader(FriendRequestsApplication.class.getResource("friend-requests-view.fxml"));
            AnchorPane root = loader.load();
            FriendRequestsController controller = loader.getController();
            controller.setActiveUser(activeUser);
            controller.setService(service);
            Scene scene = new Scene(root, 800, 800);
            Stage stage = new Stage();
            stage.setTitle("Friend requests");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void handleRefresh(ActionEvent actionEvent)
    {
        welcomeMessage.setText("Welcome "+activeUser.getFirstName()+" "+activeUser.getLastName()+"!");
        initFriends();
        initUsers();
    }
    public void handleFresh(MouseEvent actionEvent)
    {
        initFriends();
    }

    public void handleMessage(ActionEvent actionEvent)
    {
        try
        {
            FXMLLoader loader= new FXMLLoader(MessageApplication.class.getResource("message-view.fxml"));
            AnchorPane root = loader.load();
            MessageController controller = loader.getController();
            controller.setActiveUser(activeUser);
            controller.setService(service);
            Scene scene = new Scene(root, 800, 800);
            Stage stage = new Stage();
            stage.setTitle("Messages");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void update(UserTaskChangeEvent userTaskChangeEvent) {
            initFriends();
            initUsers();
    }

}
