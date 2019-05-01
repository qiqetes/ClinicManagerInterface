/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appointmentmanagementapp;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.Doctor;
import model.Person;

/**
 * FXML Controller class
 *
 * @author Qiqete
 */
public class FXMLShowDoctorDetailsController implements Initializable {
    @FXML
    private ImageView imgView;
    @FXML
    private Label lFirstName;
    @FXML
    private Label lLastName;
    @FXML
    private Label lID;
    @FXML
    private Label lTelephone;
    @FXML
    private Label lWorkDays;
    @FXML
    private Label lStartTime;
    @FXML
    private Label lEndTime;
    @FXML
    private Label lExamRoom;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    void initStage(Doctor d) {
        //TODO
        imgView.setImage(d.getPhoto());
        lFirstName.setText(d.getName());
        lLastName.setText(d.getSurname());
        lID.setText(d.getIdentifier());
        lTelephone.setText(d.getTelephon());
        lWorkDays.setText(d.getVisitDays().toString());
        lStartTime.setText(d.getVisitStartTime().toString());
        lEndTime.setText(d.getVisitEndTime().toString());
        lExamRoom.setText("Room " + d.getExaminationRoom().getIdentNumber());
        
    }

    @FXML
    private void onClickCloseWindow(ActionEvent event) {
        Stage stage = (Stage) lID.getScene().getWindow();
        stage.close();
    }
    
}
