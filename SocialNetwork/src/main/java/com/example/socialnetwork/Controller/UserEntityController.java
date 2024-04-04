package com.example.socialnetwork.Controller;

import com.example.socialnetwork.Controller.alert.UserActionsAlert;
import com.example.socialnetwork.Domain.User;
import com.example.socialnetwork.Service.ServiceDB;
import com.example.socialnetwork.Validators.ValidException;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UserEntityController {
    Stage dialogStage;

    User user;

    ServiceDB serviceDB;

    @FXML
    public TextField firstName;

    @FXML
    public TextField lastName;

    ObservableList<User> sourceList;


    @FXML
    public void handleCancel(){
        dialogStage.close();
    }
    public void makeModificationsForSave(){
        String first = firstName.getText();
        String last = lastName.getText();

        // Save action
        User  newUser = new User(first,last);
        saveUser(newUser);
    }
    


    private void saveUser(User m)
    {
        try {
            User r= this.serviceDB.saveUser(m);
            if (r==null)
                dialogStage.close();
            UserActionsAlert.showMessage(null, Alert.AlertType.INFORMATION,"Save User","User saved");
        } catch (ValidException e) {
            UserActionsAlert.showErrorMessage(null,e.getMessage());
        }
        dialogStage.close();

    }

    public void handleSave(){
        String first = firstName.getText();
        String last = lastName.getText();
        if(this.user == null){
            // means we are doing a create operation(SAVE) 
            User newUser = new User(first,last);
            newUser = serviceDB.saveUser(newUser);
            if(newUser.getId() != null){
                sourceList.add(newUser);
                UserActionsAlert.showMessage(null, Alert.AlertType.INFORMATION, "Operation Status", "SUCCESS");
                handleCancel();
            }
        }
        else {
            //UPDATE
            user.setFirstName(first);
            user.setLastName(last);
            var updatedUser = serviceDB.updateUser(user);
            UserActionsAlert.showMessage(null, Alert.AlertType.INFORMATION, "Operation Status", "SUCCESS");
            handleCancel();
        }
        dialogStage.close();
    }

    public void setService(ServiceDB service, Stage stage, User m) {
        this.serviceDB = service;
        this.dialogStage=stage;
        this.user=m;
        if (null != m) { // if update
            initialiseTextFields(user.getFirstName(), user.getLastName());
        }
    }

    public void initialiseTextFields(String fName, String lName){
        firstName.setText(fName);
        lastName.setText(lName);
    }
}
