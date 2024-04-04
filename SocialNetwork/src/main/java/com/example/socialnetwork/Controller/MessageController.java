package com.example.socialnetwork.Controller;

import com.example.socialnetwork.Domain.*;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MessageController  implements Observer<UserTaskChangeEvent> {

    private ServiceDB service;
    public TableView<User> friendsTable;
    public TableColumn<User, String> friendColumnFirstName;
    public TableColumn<User, String> friendColumnLastName;
    public ListView<Message> messageList;
    public TextField messageField;
    public Label errorMessage;

    private User activeUser;

    ObservableList<User> modelFriends = FXCollections.observableArrayList();
    ObservableList<Message> messages = FXCollections.observableArrayList();

    User friend;
    Message messageSelected;


    @FXML
    public void initialize() {

        this.friendColumnFirstName.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        this.friendColumnLastName.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        this.friendsTable.setItems(this.modelFriends);

        this.errorMessage.setText("");

        this.friendsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        this.friendsTable.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent event)
            {
                friend = (User)friendsTable.getSelectionModel().getSelectedItem();
                if(friend != null)
                {
                    initMessages(friend);
                    messageList.setItems(messages);
                }
            }
        });

        this.messageField.setOnKeyPressed(new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent event)
            {
                if(event.getCode().equals(KeyCode.ENTER))
                {
                    String text = messageField.getText();
                    ArrayList<Long> to = new ArrayList<>();
                    to.add(friend.getId());
                    Message message = new Message(activeUser.getId(), to, text, LocalDateTime.now());
                    try
                    {
                        service.saveMessage(message);
                        errorMessage.setText("");
                    }catch (Exception e)
                    {
                        errorMessage.setText(e.getMessage());
                        errorMessage.setTextFill(Color.DARKRED);}
                    messageField.clear();
                }
            }
        });

        messageList.setCellFactory(list -> new ListCell<Message>() {
            @Override
            protected void updateItem(Message item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle(""); //reset style
                } else {
//                    setText(item.getMessage());
                    if (item.getReply()==0) {

                        if ("blue".equals(item.getColor())) {
                            setText("Me:" + item.getMessage());
                            setStyle("-fx-text-fill: blue; -fx-alignment: center-right;"); // set color for sent messages
                        } else if ("green".equals(item.getColor())) {
                            setText("You:" + item.getMessage());
                            setStyle("-fx-text-fill: green; -fx-alignment: center-left;"); //set color for received messages
                        }
                    }
                    if (item.getReply()!=0) {
                        setText("Reply:"+" "+item.getMessage());
                        if ("blue".equals(item.getColor())) {
                            setStyle("-fx-text-fill: blue; -fx-alignment: center-right;"); //set color for sent messages
                        } else if ("green".equals(item.getColor())) {
                            setStyle("-fx-text-fill: green; -fx-alignment: center-left;"); // set color for received messages
                        }
                        Tooltip tooltip = new Tooltip(service.findMessageById(item.getReply()).getMessage());
                        setTooltip(tooltip);
                    }
                }
            }
        });


    }

    private void loadingContent(){
        this.initFriends();
        service.addObserver(this);
    }

    public void initFriends()
    {
        Map<User, LocalDateTime> allFriends = service.getFriends2(activeUser.getId());
        List<User> friendsList = new ArrayList<>();
        for (Map.Entry<User,LocalDateTime> entry : allFriends.entrySet())
            friendsList.add(entry.getKey());
        modelFriends.setAll(friendsList);
    }

    public void initMessages(User friend)
    {
        List<Message> conversation = service.getConversation(activeUser.getId(), friend.getId());
        //print the last 5 messages
        if(conversation.size()>5)
            conversation = conversation.subList(conversation.size()-5,conversation.size());
        List<Message> list = new ArrayList<>();
        for (Message x : conversation)
        {
            if(x.getFrom().equals(activeUser.getId())) {
                x.setColor("blue");
                x.setAlignment("left");
            }
            else {
                x.setColor("green");
                x.setAlignment("right");
            }
            list.add(x);
        }
        this.messages.setAll(list);
    }

    public void setService(ServiceDB service){
        this.service = service;
        loadingContent();
    }

    public void setActiveUser(User activeUser) {
        this.activeUser = activeUser;
    }

    public void handleSendToAll(ActionEvent actionEvent)
    {
        List<User> all = friendsTable.getSelectionModel().getSelectedItems();

        String text = messageField.getText();
        ArrayList<Long> to = new ArrayList<>();

        for(User it: all)
            to.add(it.getId());

        Message message = new Message(activeUser.getId(), to, text, LocalDateTime.now());
        try
        {
            service.saveMessage(message);
            this.errorMessage.setText("");
        }catch (Exception e) {
            this.errorMessage.setText(e.getMessage());
            this.errorMessage.setTextFill(Color.DARKRED);
        }
        this.messageField.clear();
        friend = (User)friendsTable.getSelectionModel().getSelectedItem();
        if(friend != null)
        {
            initMessages(friend);
            messageList.setItems(messages);
        }
    }

    public void handleReply(ActionEvent actionEvent)
    {
        String text = messageField.getText();
        ArrayList<Long> to = new ArrayList<>();
        to.add(friend.getId());


        Message message = new Message(activeUser.getId(),to, text, LocalDateTime.now(),messageSelected.getId());
        try
        {
            service.replyToOne(message,friend.getId());
            this.errorMessage.setText("");
        }catch (Exception e) {
            this.errorMessage.setText(e.getMessage());
            this.errorMessage.setTextFill(Color.DARKRED);
        }
        this.messageField.clear();
        friend = (User)friendsTable.getSelectionModel().getSelectedItem();
        if(friend != null)
        {
            initMessages(friend);
            messageList.setItems(messages);
        }
    }



    @Override
    public void update(UserTaskChangeEvent userTaskChangeEvent) {
            initFriends();
            initMessages(friendsTable.getSelectionModel().getSelectedItem());
    }
}