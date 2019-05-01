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
import model.Patient;
import model.Person;

/**
 * FXML Controller class
 *
 * @author Qiqete
 */
public class FXMLShowPatientDetailsController implements Initializable {
    @FXML
    private Label lFirstName;
    @FXML
    private Label lLastName;
    @FXML
    private Label lID;
    @FXML
    private Label lTelephone;
    @FXML
    private ImageView imgView;
    
    private Patient patient = null;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    void initStage(Patient p) {
        lFirstName.setText(p.getName());
        lLastName.setText(p.getSurname());
        lID.setText(p.getIdentifier());
        lTelephone.setText(p.getTelephon());
        imgView.setImage(p.getPhoto());
        
    }

    @FXML
    private void onClickCloseWindow(ActionEvent event) {
        Stage stage = (Stage) lID.getScene().getWindow();
        stage.close();
    }
    
}
