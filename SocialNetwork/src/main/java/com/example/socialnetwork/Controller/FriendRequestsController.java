package com.example.socialnetwork.Controller;

import com.example.socialnetwork.Domain.*;
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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FriendRequestsController implements Observer<UserTaskChangeEvent>{

    private ServiceDB service;
    public TableView<FriendRequest> requestsSentTable;
    public TableColumn<FriendRequest, String> toColumnSent;
    public TableColumn<FriendRequest, String> statusColumnSent;
    public TableColumn<FriendRequest, LocalDateTime> dateColumnSent;

    public TableView<FriendRequest> requestsReceivedTable;
    public TableColumn<FriendRequest, String> fromColumnReceived;
    public TableColumn<FriendRequest, String> statusColumnReceived;
    public TableColumn<FriendRequest, LocalDateTime> dateColumnReceived;

    public Button buttonAccept;
    public Button buttonReject;
    public Label messageToUserForRequest;
    public Button buttonRemove;

    private User activeUser;


    //sent requests
    @FXML
    Button previousButton;

    @FXML
    Button nextButton;

    @FXML
    TextField pageField;

    public int currentPage = 0;
    public int elementsPerPage = 3;

    public int totalNumberOfElements = 0;

    //received requests

    @FXML
    Button previousButtonReceived;

    @FXML
    Button nextButtonReceived;

    @FXML
    TextField pageFieldReceived;

    public int currentPageReceived = 0;
    public int elementsPerPageReceived = 1;

    public int totalNumberOfElementsReceived = 0;


    ObservableList<FriendRequest> modelSent;
    ObservableList<FriendRequest> modelReceived;

    @FXML
    public void initialize() {


        this.modelSent = FXCollections.observableArrayList();
        this.modelReceived = FXCollections.observableArrayList();

        this.toColumnSent.setCellValueFactory(new PropertyValueFactory<FriendRequest, String>("to"));
        this.statusColumnSent.setCellValueFactory(new PropertyValueFactory<FriendRequest, String>("status"));
        this.dateColumnSent.setCellValueFactory(new PropertyValueFactory<FriendRequest, LocalDateTime>("date"));

        this.fromColumnReceived.setCellValueFactory(new PropertyValueFactory<FriendRequest, String>("from"));
        this.statusColumnReceived.setCellValueFactory(new PropertyValueFactory<FriendRequest, String>("status"));
        this.dateColumnReceived.setCellValueFactory(new PropertyValueFactory<FriendRequest, LocalDateTime>("date"));

        this.requestsSentTable.setItems(this.modelSent);


        this.requestsReceivedTable.setItems(this.modelReceived);
//        this.initReceived();
//        this.initSent();
//        service.addObserver(this);


        this.pageField.setOnKeyPressed(new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent event)
            {
                if(event.getCode().equals(KeyCode.ENTER))
                {
                    String text = pageField.getText();
                    elementsPerPage=Integer.valueOf(text);
                    initSentByPage();
                    pageField.clear();
                }
            }
        });
        this.pageFieldReceived.setOnKeyPressed(new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent event)
            {
                if(event.getCode().equals(KeyCode.ENTER))
                {
                    String text = pageFieldReceived.getText();
                    elementsPerPageReceived=Integer.valueOf(text);
                    initReceivedByPage();
                    pageFieldReceived.clear();
                }
            }
        });
    }

    public void goToPreviousPage(){
        currentPage--;
        initSentByPage();
    }

    public void goToNextPageReceived(){
        currentPageReceived++;
        initReceivedByPage();
    }

    public void goToPreviousPageReceived(){
        currentPageReceived--;
        initReceivedByPage();
    }

    public void goToNextPage(){
        currentPage++;
        initSentByPage();
    }

    private void handlePagingNavigationChecks(){
        previousButton.setDisable(currentPage == 0);
        nextButton.setDisable((currentPage + 1) * elementsPerPage >= totalNumberOfElements);
    }

    private void handlePagingNavigationChecksReceived(){
        previousButtonReceived.setDisable(currentPageReceived == 0);
        nextButtonReceived.setDisable((currentPageReceived + 1) * elementsPerPageReceived >= totalNumberOfElementsReceived);
    }

    public void initSentByPage()
    {
        Page<FriendRequest> moviesPage = service.friendRequestOnPage(new Pageable(currentPage, elementsPerPage), activeUser, 1);
        totalNumberOfElements = moviesPage.getTotalNumberOfElements();
        handlePagingNavigationChecks();
        List<FriendRequest> movieList = StreamSupport.stream(moviesPage.getElementsOnPage().spliterator(), false).toList();
        System.out.println(movieList);
        modelSent.setAll(movieList);
    }
    public void initSent()
    {
        Iterable<FriendRequest> friendRequests = service.getAllFriendRequest();
        List<FriendRequest> friendRequestList = StreamSupport.stream(friendRequests.spliterator() , false)
                .filter(x -> (x.getId().getLeft().equals(activeUser.getId())))
                .collect(Collectors.toList());
        this.modelSent.setAll(friendRequestList);
    }

    public void initReceived()
    {
        Iterable<FriendRequest> friendRequests = this.service.getAllFriendRequest();
        List<FriendRequest> friendRequestList = StreamSupport.stream(friendRequests.spliterator() , false)
                .filter(x -> (x.getId().getRight().equals(activeUser.getId())))
                .collect(Collectors.toList());
        this.modelReceived.setAll(friendRequestList);
    }
    public void initReceivedByPage()
    {
        Page<FriendRequest> moviesPage = service.friendRequestOnPage(new Pageable(currentPageReceived, elementsPerPageReceived), activeUser, 0);
        totalNumberOfElementsReceived = moviesPage.getTotalNumberOfElements();
        handlePagingNavigationChecksReceived();
        List<FriendRequest> movieList = StreamSupport.stream(moviesPage.getElementsOnPage().spliterator(), false).toList();
        System.out.println(movieList);
        modelReceived.setAll(movieList);
    }
    private void loadingContent(){
        this.initReceived();
        this.initSent();
        service.addObserver(this);
    }

    public void setService(ServiceDB ser) {
        this.service = ser;
        loadingContent();
    }

    @FXML
    public void handleAcceptFriendRequest(ActionEvent actionEvent)
    {
        FriendRequest selected = (FriendRequest) this.requestsReceivedTable.getSelectionModel().getSelectedItem();
        if (selected != null)
        {
            try
            {
                service.accept(selected.getFrom(), activeUser.getId());
                this.messageToUserForRequest.setText("Friend request accepted!");
                this.messageToUserForRequest.setTextFill(Color.DARKGREEN);
                initSent();
                initReceived();
            } catch (Exception e)
            {
                this.messageToUserForRequest.setText(e.getMessage());
                this.messageToUserForRequest.setTextFill(Color.DARKRED);
            }
        }
        else
        {
            this.messageToUserForRequest.setText("No request selected!");
            this.messageToUserForRequest.setTextFill(Color.DARKRED);
        }
    }

    public void handleRejectFriendRequest(ActionEvent actionEvent)
    {
        FriendRequest selected = (FriendRequest) this.requestsReceivedTable.getSelectionModel().getSelectedItem();
        if(selected != null)
        {
            try
            {
                service.reject(selected.getFrom(), activeUser.getId());
                this.messageToUserForRequest.setText("Friend request rejected!");
                this.messageToUserForRequest.setTextFill(Color.DARKGREEN);
                initSent();
                initReceived();
            } catch (Exception e)
            {
                this.messageToUserForRequest.setText(e.getMessage());
                this.messageToUserForRequest.setTextFill(Color.DARKRED);
            }
        }
        else
        {
            this.messageToUserForRequest.setText("No request selected!");
            this.messageToUserForRequest.setTextFill(Color.DARKRED);
        }
    }

    public User getActiveUser() {
        return activeUser;
    }

    public void setActiveUser(User activeUser) {
        this.activeUser = activeUser;
    }

    public void handleRemove(ActionEvent actionEvent)
    {
        FriendRequest selected = (FriendRequest) this.requestsSentTable.getSelectionModel().getSelectedItem();
        if(selected != null)
        {
            try
            {
                Tuple<Long,Long> pair=new Tuple(activeUser.getId(), selected.getTo());
                Tuple<Long,Long> pair2=new Tuple( selected.getTo(),activeUser.getId());
                FriendRequest fr1=service.findFriendRequestById(pair);
                if(fr1==null)
                    fr1=service.findFriendRequestById(pair2);
                service.deleteFriendRequest(fr1);
                this.messageToUserForRequest.setText("Friend request removed!");
                this.messageToUserForRequest.setTextFill(Color.DARKGREEN);
                initSent();
            } catch (Exception e)
            {
                this.messageToUserForRequest.setText(e.getMessage());
                this.messageToUserForRequest.setTextFill(Color.DARKRED);
            }
        }
        else
        {
            this.messageToUserForRequest.setText("No request selected!");
            this.messageToUserForRequest.setTextFill(Color.DARKRED);
        }
    }

    @Override
    public void update(UserTaskChangeEvent userTaskChangeEvent) {
        initReceived();
        initSent();
    }
}
