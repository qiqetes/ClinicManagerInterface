/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appointmentmanagementapp;

import DBAccess.ClinicDBAccess;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.LocalDateTimeStringConverter;
import model.Appointment;
import model.Doctor;
import model.ExaminationRoom;
import model.LocalTimeAdapter;
import model.Patient;
import model.Person;

/**
 * FXML Controller class
 *
 * @author Qiqete
 */
public class FXMLAddAppointmentController implements Initializable {
    @FXML
    private ChoiceBox<Person> chPatient;
    @FXML
    private ChoiceBox<Person> chDoctor;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ChoiceBox<String> chStartH;
    @FXML
    private ChoiceBox<String> chEndH;
    @FXML
    private Button bOk;

    ClinicDBAccess clinicDBAccess;
    ObservableList<Appointment> appObservableList;   
    
    ObservableList<Person> patObservableList;
    ObservableList<Person> docObservableList;
    ObservableList<ExaminationRoom> obsRooms;
    ArrayList<String> hours = new ArrayList();
    ObservableList<String> obsHours;
    
    Doctor selectedDoctor;
    @FXML
    private Label lRoom;
    
            
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        clinicDBAccess = ClinicDBAccess.getSingletonClinicDBAccess();
        
        //How the objects are going to be displayed in the choice boxes       
        chPatient.setConverter(new StringConverter<Person>() {

            @Override
            public String toString(Person p) {
                return p.getName() + " " + p.getSurname();
            }

            @Override
            public Person fromString(String string) {
                throw new UnsupportedOperationException("DON'T USE ME!"); 
            }
        });
        chDoctor.setConverter(new StringConverter<Person>() {

            @Override
            public String toString(Person p) {
                return p.getName() + " " + p.getSurname();
            }

            @Override
            public Person fromString(String string) {
                throw new UnsupportedOperationException("DON'T USE ME!"); 
            }
        });
//        chRoom.setConverter(new StringConverter<ExaminationRoom>() {
//
//            @Override
//            public String toString(ExaminationRoom room) {
//                return "Room " + Integer.toString(room.getIdentNumber());
//            }
//
//            @Override
//            public ExaminationRoom fromString(String string) {
//                throw new UnsupportedOperationException("DON'T USE ME!"); 
//            }
//        });
        
        //Choice boxes initialization
        patObservableList = FXCollections.observableArrayList(utils.toPersonArray(clinicDBAccess.getPatients()));
        chPatient.setItems(patObservableList);
        //chPatient.getSelectionModel().selectFirst(); //To show the first item by default
        
        docObservableList = FXCollections.observableArrayList(utils.toPersonArray(clinicDBAccess.getDoctors()));
        chDoctor.setItems(docObservableList);
        //chDoctor.getSelectionModel().selectFirst();       
                
//        utils.initChHours(hours);
//        obsHours = FXCollections.observableArrayList(hours);
//        chStartH.setItems(obsHours);
        //chStartH.getSelectionModel().selectFirst(); //To show the first item by default
        
//        chEndH.setItems(obsHours);
        //chEndH.getSelectionModel().select(1); //To show the second item by default
        
        obsRooms = FXCollections.observableArrayList(clinicDBAccess.getExaminationRooms());        
//        chRoom.setItems(obsRooms);
        //chRoom.getSelectionModel().selectFirst(); //To show the first item by default
        
        
        //Bindings
        bindings();
        
        //Listeners
        chDoctor.getSelectionModel().selectedItemProperty().addListener((observable, oldVal, newVal)->{
                selectedDoctor = utils.searchDoctor(newVal.getIdentifier(), clinicDBAccess);
                lRoom.setText("Room " + selectedDoctor.getExaminationRoom().getIdentNumber() + " with " + selectedDoctor.getExaminationRoom().getEquipmentDescription());
                
                hours.clear();
                utils.initChHours2(hours, selectedDoctor.getVisitStartTime(),selectedDoctor.getVisitEndTime());
                
                obsHours = FXCollections.observableArrayList(hours);
                chStartH.setItems(obsHours);
                chEndH.setItems(obsHours);
        });
    }    

    public void initStage(ObservableList<Appointment> app){
        appObservableList = app;
    }

    @FXML
    private void onClickSubmit(ActionEvent event) {   
        if(utils.isDayPast(datePicker.getValue())){
            warningDayIsPast();
        }
        else if(utils.hoursMismatch(chStartH, chEndH)){
            utils.warningDialogHoursMismatch("Appointment");
        }else if( !isAValidVisitDay()){
            warningDoctorHasNoConsult();
        }
        else{
            try{
                //Create the LocalDateTime: 
                LocalDate lD = datePicker.getValue();
                LocalTimeAdapter lTA = new LocalTimeAdapter();
                LocalTime lT = lTA.unmarshal(chStartH.getValue());
                LocalDateTime lDT = LocalDateTime.of(lD, lT);

                //Create Appointment
                Doctor doctor = (Doctor) chDoctor.getValue();
                Patient patient = (Patient) chPatient.getValue();
                Appointment app = new Appointment(lDT, doctor, patient);

                //Add to the appointments observable list
                appObservableList.add(app);
                clinicDBAccess.getAppointments().add(app);
                clinicDBAccess.saveDB();
                
                onClickCancel(event);

                //Confirmation dialog
                utils.confirmationDialogCreateDelete("Apointment", "created", app);
                
            }catch(Exception e){System.out.println("Exception occurred");}

        }
    }

    @FXML
    private void onClickCancel(ActionEvent event) {
        Stage stage = (Stage) bOk.getScene().getWindow();
        stage.close();
    }
    
    private void bindings(){
        BooleanBinding boolB = chPatient.valueProperty().isNull().or(
                chDoctor.valueProperty().isNull().or(
                datePicker.valueProperty().isNull().or(
                chStartH.valueProperty().isNull().or(
                chEndH.valueProperty().isNull()))));
        
        bOk.disableProperty().bind(boolB);
    }
    
    private boolean isAValidVisitDay(){
        String[] visitDays = selectedDoctor.getVisitDays().toString().substring(1,selectedDoctor.getVisitDays().toString().length() -1 ).toUpperCase().replace(" ","").split(",");
        boolean b = true;
        for(int i = 0; i< visitDays.length; i++){
            if(visitDays[i].equals(datePicker.getValue().getDayOfWeek().name())) return true;
        }
        
//        if(selectedDoctor.getVisitDays().contains(datePicker.getValue().getDayOfWeek().name())){
//            
//        }
//        String ds = selectedDoctor.getVisitDays();
        return false;
    }
    
    private void warningDayIsPast(){
        Alert alert= new Alert(AlertType.WARNING);
        alert.setTitle("Input not valid");
        alert.setHeaderText("Appointment"+ " couldn't be added ");
        String doc;
        alert.setContentText("The day you are trying to pass is not a valid day since it's a past day");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
             System.out.println("OK");
        }   
    }
    
    private void warningDoctorHasNoConsult(){
        Alert alert= new Alert(AlertType.WARNING);
        alert.setTitle("Day not valid");
        alert.setHeaderText("Appointment"+ " couldn't be added ");
        String doc;
        alert.setContentText("The doctor " + selectedDoctor.getSurname() + " has visits only " + selectedDoctor.getVisitDays().toString());
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
             System.out.println("OK");
        }   
    }
}
