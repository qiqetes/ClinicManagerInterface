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
import model.Appointment;
import model.Person;

/**
 * FXML Controller class
 *
 * @author Qiqete
 */
public class FXMLShowAppointmentDetailsController implements Initializable {
    @FXML
    private Label lDocName;
    @FXML
    private Label lDocSurname;
    @FXML
    private Label lPatName;
    @FXML
    private Label lPatSurname;
    @FXML
    private Label lRoom;
    @FXML
    private Label lAppDate;
    @FXML
    private Label lStartTime;
    @FXML
    private Label lEndTime;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    void initStage(Appointment a) {
        lDocName.setText(a.getDoctor().getName());
        lDocSurname.setText(a.getDoctor().getSurname());
        
        lPatName.setText(a.getPatient().getName());
        lPatSurname.setText(a.getPatient().getSurname());
        
        lRoom.setText("Room " + a.getDoctor().getExaminationRoom().getIdentNumber());
        lAppDate.setText(a.getAppointmentDay().toString());
        lStartTime.setText(a.getAppointmentDateTime().toString());
        
        
    }

    @FXML
    private void onClickCloseWindow(ActionEvent event) {
    }
    
}
