/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appointmentmanagementapp;

import DBAccess.ClinicDBAccess;
import java.net.URL;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.Days;
import model.Doctor;
import model.ExaminationRoom;
import model.LocalTimeAdapter;
import model.Person;

/**
 * FXML Controller class
 *
 * @author Qiqete
 */
public class FXMLAddDoctorController implements Initializable {
    @FXML
    private TextField lName;
    @FXML
    private TextField lSurname;
    @FXML
    private TextField lID;
    @FXML
    private TextField lTelephone;
    @FXML
    private CheckBox chMon;
    @FXML
    private CheckBox chTue;
    @FXML
    private CheckBox chWed;
    @FXML
    private CheckBox chThu;
    @FXML
    private CheckBox chFri;
    @FXML
    private CheckBox chSat;
    @FXML
    private CheckBox chSun;
    @FXML
    private ChoiceBox<ExaminationRoom> chRoom;
    @FXML
    private Button bOk;
    @FXML
    private ChoiceBox<String> chStartHour;
    @FXML
    private ChoiceBox<String> chEndHour;
    
    ClinicDBAccess clinicDBAccess;
    ObservableList<Person> personObservableList;
    
    ArrayList<String> hours = new ArrayList();
    ObservableList<String> obsHours;
    ObservableList<ExaminationRoom> obsRooms;  
    CheckBox [] chDays = new CheckBox[7];
    Image img = null;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        binds();
        
        chDays = new CheckBox[]{chMon, chTue, chWed, chThu, chFri, chSat, chSun};
        
        
        utils.initChHours(hours);
        obsHours = FXCollections.observableArrayList(hours);
        chStartHour.setItems(obsHours);
        //chStartHour.getSelectionModel().selectFirst(); //To show the first item by default
        chEndHour.setItems(obsHours);
        //chEndHour.getSelectionModel().select(1); //To show the second item by default

        clinicDBAccess = ClinicDBAccess.getSingletonClinicDBAccess();
        
        obsRooms = FXCollections.observableArrayList(clinicDBAccess.getExaminationRooms());
        chRoom.setConverter(new StringConverter<ExaminationRoom>() {

            @Override
            public String toString(ExaminationRoom room) {
                return "Room " + Integer.toString(room.getIdentNumber());
            }

            @Override
            public ExaminationRoom fromString(String string) {
                throw new UnsupportedOperationException("DON'T USE ME!"); 
            }
        });
        chRoom.setItems(obsRooms);
        //chRoom.getSelectionModel().selectFirst(); //To show the first item by default
        
        
    }    
    
    public void initStage(ClinicDBAccess cla, ObservableList<Person> pOL){
        //clinicDBAccess = cla;
        personObservableList = pOL;
    }
    
    @FXML
    private void onClickBrowseImg(ActionEvent event) {
        //utils.browseImage(img, event); // Ha dejado de funcionar sin motivo alguno :)
        img = utils.browseImage2(event);
    }

    @FXML
    private void onClickSubmit(ActionEvent event) {
        if(!utils.isNumeric(lTelephone.getText())){
            utils.warningTelephoneDialog("Doctor ");
        }  
//        else if(moreThanOneCheckBoxSelected()){
//            utils.warningDialogChBox();
//        }
        else if(utils.hoursMismatch(chStartHour, chEndHour)){
            utils.warningDialogHoursMismatch("Doctor");
        }
        else{

                ExaminationRoom room = chRoom.getValue();
                LocalTimeAdapter lTA = new LocalTimeAdapter();
                LocalTime startHour = LocalTime.now();
                LocalTime endHour = LocalTime.now();
                
                try {
                    startHour = lTA.unmarshal(chStartHour.getValue());
                    endHour = lTA.unmarshal(chEndHour.getValue());
                } catch (Exception ex) {
                    Logger.getLogger(FXMLAddDoctorController.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                String identifier = lID.getText();
                String name = lName.getText();
                String surname = lSurname.getText();
                String telephone = lTelephone.getText();                
                Doctor doctor = new Doctor(room, null, startHour, endHour, identifier, name, surname, telephone, img);
                doctor.setVisitDays(daysSelected());
                
                //Add the doctor
                personObservableList.add((Person) doctor);
                clinicDBAccess.getDoctors().add(doctor);
                clinicDBAccess.saveDB();
                
                //Close window
                onClickCancel(event);
                
                //Confirmation dialog
                utils.confirmationDialogCreateDelete("Doctor", "created", doctor);
//            }catch(Exception e){System.out.println("Exception occurred");}
        }        
    }

    @FXML
    private void onClickCancel(ActionEvent event) {
        Stage stage = (Stage) bOk.getScene().getWindow();
        stage.close();
    }
    
    private void binds(){
        BooleanBinding boolA = lName.textProperty().isEmpty().or(
                lSurname.textProperty().isEmpty().or(
                lID.textProperty().isEmpty().or(
                lTelephone.textProperty().isEmpty().or(
                chStartHour.valueProperty().isNull().or(
                chEndHour.valueProperty().isNull().or(
                chRoom.valueProperty().isNull()))))));
        
        BooleanBinding boolB = chMon.selectedProperty().or(
                chTue.selectedProperty().or(
                chWed.selectedProperty().or(
                chThu.selectedProperty().or(
                chFri.selectedProperty().or(
                chSat.selectedProperty().or(
                chSun.selectedProperty()))))));
        BooleanBinding bool = boolA.or(boolB.not());
        bOk.disableProperty().bind(bool);
    }
    
//    private boolean moreThanOneCheckBoxSelected(){
//        //Only one check box (days of the week) can be selected at a time        
//        boolean sel = false, prev = false;
//        int i = 0; 
//        while(!sel && i < chDays.length){
//            if(chDays[i] != null && chDays[i].isSelected()){
//                if(prev){sel = true;}
//                else{prev = true;}
//            }     
//            i++;
//        }
//        return sel;
//    }
//     
    
    private ArrayList<Days> daysSelected(){
        ArrayList<Days> days = new ArrayList<>();
        Days[] dayArr = Days.values();
        for(int i = 0; i < chDays.length; i++){
            if(chDays[i].isSelected()){
                days.add(dayArr[i]);
            }
        }
        return days;
    }
}

