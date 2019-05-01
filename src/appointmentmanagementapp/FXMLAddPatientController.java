 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appointmentmanagementapp;

import DBAccess.ClinicDBAccess;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import model.Patient;
import model.Person;

/**
 * FXML Controller class for the Add patient form
 * 
 * 
 */

public class FXMLAddPatientController implements Initializable {
    @FXML
    private TextField lName;
    @FXML
    private TextField lSurname;
    @FXML
    private TextField lID;
    @FXML
    private TextField lTelephone;
    @FXML
    private Button bOk;

    ClinicDBAccess clinicDBAccess;
    ObservableList<Person> personObservableList;
    Image img = null;
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        binds();
    }   


    @FXML
    private void onClickSubmit(ActionEvent event) {
        if(!utils.isNumeric(lTelephone.getText())){
            utils.warningTelephoneDialog("Patient ");
        }else{
            
            Patient p = new Patient(lID.getText(), lName.getText(), lSurname.getText(), lTelephone.getText(), img);
            clinicDBAccess.getPatients().add(p);
            clinicDBAccess.saveDB();
            personObservableList.add((Person)p);

            onClickCancel(event);
            utils.confirmationDialogCreateDelete("Patient", "created", p);
        }
    }

    @FXML
    private void onClickCancel(ActionEvent event) {
        Stage stage = (Stage) bOk.getScene().getWindow();
        stage.close();
    }
    
    @FXML
    private void onClickBrowseImg(ActionEvent event) {
        img = utils.browseImage2(event);
    }
    
    public void initStage(ClinicDBAccess cla, ObservableList<Person> pOL){
        clinicDBAccess = cla;
        personObservableList = pOL;
    }
    
    
    private void binds(){
        BooleanBinding boolB = lName.textProperty().isEmpty().or(
                lSurname.textProperty().isEmpty().or(
                lID.textProperty().isEmpty().or(
                lTelephone.textProperty().isEmpty())));      
        
        bOk.disableProperty().bind(boolB);
    }
    
    
    
    
    
}
