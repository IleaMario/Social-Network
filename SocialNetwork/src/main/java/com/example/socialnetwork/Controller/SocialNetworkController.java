package com.example.socialnetwork.Controller;

import com.example.socialnetwork.Controller.alert.UserActionsAlert;
import com.example.socialnetwork.Domain.User;
import com.example.socialnetwork.Service.ServiceDB;
import com.example.socialnetwork.SocialNetworkApplication;
import com.example.socialnetwork.utils.events.UserTaskChangeEvent;
import com.example.socialnetwork.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.StreamSupport;

public class SocialNetworkController implements Observer<UserTaskChangeEvent> {
    
    private ServiceDB service;
    @FXML
    public TableView<User> usersTable;

    @FXML
    public TableColumn<User, Long> idColumn;

    @FXML
    public TableColumn<User, String> firstNameColumn;

    @FXML
    private TableColumn<User, String> lastNameColumn;


    ObservableList<User> model = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<User, Long>("id"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        usersTable.setItems(model);
    }

    public void  setUserService(ServiceDB service){
        this.service = service;
        service.addObserver(this);
        initializeTableData();
    }

    public void initializeTableData(){
        Iterable<User> allUsers = service.getAllUsers();
        List<User> allUsersList = StreamSupport.stream(allUsers.spliterator(), false).toList();
        model.setAll(allUsersList);
    }


    @FXML
    public void handleAddUser(){
        initUserEntityModal(null);
    }

    @FXML
    public void handleUpdateUser(){
        User toBeUpdated = usersTable.getSelectionModel().getSelectedItem();
        if(toBeUpdated == null) {
            UserActionsAlert.showMessage(null, Alert.AlertType.ERROR, "Update Selection", "Please select movie before update");
            return;
        }
        initUserEditor(toBeUpdated);
    }


    public void initUserEditor(User user){
        try {

            FXMLLoader fxmlLoader = new FXMLLoader(SocialNetworkApplication.class.getResource("user-entity-view.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 600, 800);
            stage.setScene(scene);

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("User Edit");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setScene(scene);

            UserEntityController userEntityController = fxmlLoader.getController();
            userEntityController.setService(service, dialogStage, user);
            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void initUserEntityModal(User user){
        try {

            FXMLLoader fxmlLoader = new FXMLLoader(SocialNetworkApplication.class.getResource("user-entity-view.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 600, 800);
            stage.setScene(scene);

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("User Save");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setScene(scene);

            UserEntityController movieEntityController = fxmlLoader.getController();
            movieEntityController.setService(service, dialogStage, null);
            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void handleDeleteUser(ActionEvent actionEvent) {

        User toBeDeleted = (User) usersTable.getSelectionModel().getSelectedItem();
        if(toBeDeleted == null) {
            // means no movie selected for deletion
            UserActionsAlert.showErrorMessage(null, "Select a movie before hitting delete");
        }
        else{
            System.out.println(toBeDeleted);
            // movie selected for deletion; todo: handle delete logic
            Long oldId = toBeDeleted.getId();
            toBeDeleted = service.deleteUser(toBeDeleted);

            if(toBeDeleted == null || toBeDeleted.getId() == null){
                // delete failed
                UserActionsAlert.showMessage(null, Alert.AlertType.WARNING, "Delete Failed", "Please try again");
            }
            else {
                // delete success
                UserActionsAlert.showMessage(null, Alert.AlertType.INFORMATION, "Success", "The selected movie has been deleted");
                toBeDeleted.setId(oldId);
                model.remove(toBeDeleted);
            }
        }
    }

    @Override
    public void update(UserTaskChangeEvent userTaskChangeEvent) {
        initializeTableData();
    }
}


