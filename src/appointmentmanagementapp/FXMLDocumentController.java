/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appointmentmanagementapp;

import DBAccess.ClinicDBAccess;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Appointment;
import model.DateTimeAdapter;
import model.Days;
import model.Doctor;
import model.Patient;
import model.Person;


/**
 *
 * @author Qiqete
 */
public class FXMLDocumentController implements Initializable {
    @FXML
    private Label lHeaderSection;
    @FXML
    private Button bPatientsMenu;
    @FXML
    private Button bDoctorMenu;
    @FXML
    private TextField searchBarInput;
    @FXML
    private ListView<Person> listView;
    @FXML
    private ImageView portraitImg;
    @FXML
    private Text lPreviewCard;
    @FXML
    private Button bDetails;
    @FXML
    private Button bRemove;
    @FXML
    private Rectangle rectSelectPat;
    @FXML
    private Rectangle rectSelectDoc;
    @FXML
    private Button bAdd;
    private Text lDetails;
    private Text lAdd;
    private Text lRemove;
    @FXML
    private Button bAppointmentMenu;
    @FXML
    private Rectangle rectSelectApp;
    @FXML
    private TableView<Appointment> tableView;
    @FXML
    private TableColumn<Appointment, LocalDateTime> tableColTime;
    @FXML
    private TableColumn<Appointment, Doctor> tableColDoctor;
    @FXML
    private TableColumn<Appointment, Patient> tableColPatient;
    @FXML
    private VBox layoutListView;
    @FXML
    private VBox layoutTableView;
    private Rectangle rectRemoveButton;
    @FXML
    private Label lLocalDate;
    @FXML
    private AnchorPane previewCard;
    @FXML
    private Button bPatientAppointments;
    

    // Additional parameters
    private Button[] sideButtons = new Button[3];
    private Rectangle[] sideRect = new Rectangle[3];
    
    
    String menuMode = "Patient"; // Track menu mode
    public static ObservableList<Person> personObservableList; 
    public static ObservableList<Appointment> appObservableList;
    ArrayList<Person> personData = new ArrayList<Person>();
    ClinicDBAccess clinicDBAccess; // Singleton
    @FXML
    private StackPane pPatientAppointments;
   
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Singleton instance
        clinicDBAccess = ClinicDBAccess.getSingletonClinicDBAccess();
        
        //Additional listeners
        //      Change dynamically css when buttons are disabled
        bRemove.disableProperty().addListener((observable, oldVal, newVal)->{
            if(newVal.booleanValue() == true)
                rectRemoveButton.getStyleClass().add("disabledButton");
            else{
                rectRemoveButton.getStyleClass().remove("disabledButton");                
            }
        });
        //      Show a preview of the person selected in the preview card and change it
        //      dinamically when other person is selected in the list View
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldVal, newVal)->{
                Person p = listView.getSelectionModel().getSelectedItem();
                lPreviewCard.setText(p.getName() + " " + p.getSurname());
                portraitImg.setImage(p.getPhoto());
        });
        
        // Group buttons to handle css styles
        sideButtons[0] = bPatientsMenu; sideButtons[1] = bDoctorMenu; sideButtons[2] = bAppointmentMenu;
        sideRect[0] =rectSelectPat; sideRect[1] = rectSelectDoc; sideRect[2] = rectSelectApp;
        

        // ListView declarations
        personObservableList = FXCollections.observableArrayList(utils.toPersonArray(clinicDBAccess.getPatients()));
        listView.setCellFactory(c -> new PersonListCell());
        personObservableList.addListener(new ListChangeListener<Person>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends Person> arg0) {
                listView.setItems(personObservableList);
            }
        });

        // TableView columns declaration
        tableView.setColumnResizePolicy(tableView.CONSTRAINED_RESIZE_POLICY);
        appObservableList = FXCollections.observableArrayList(clinicDBAccess.getAppointments());
        tableView.setItems(appObservableList);
                    /*Local Date Time column:*/
        tableColTime.setCellValueFactory(new PropertyValueFactory<Appointment, LocalDateTime>("appointmentDateTime"));
        tableColTime.setCellFactory(c -> new TableCell<Appointment,LocalDateTime>(){
            @Override
            protected void updateItem(LocalDateTime item, boolean empty)
            {   
                super.updateItem(item, empty);
                if( item == null || empty ) setText(null);
                else setText(item.toString());
            }
        });
                    /*Patient Column:*/
        tableColPatient.setCellValueFactory(new PropertyValueFactory<Appointment, Patient>("patient"));
        tableColPatient.setCellFactory(c -> new TableCell<Appointment,Patient>(){
            @Override
            protected void updateItem(Patient item, boolean empty)
            {   
                super.updateItem(item, empty);
                if( item == null || empty ) setText(null);
                else setText(item.getName() + " " + item.getSurname());
            }
        });
                    /*Doctor Column:*/
        tableColDoctor.setCellValueFactory(new PropertyValueFactory<Appointment, Doctor>("doctor"));
        tableColDoctor.setCellFactory(c -> new TableCell<Appointment,Doctor>(){
            @Override
            protected void updateItem(Doctor item, boolean empty)
            {   
                super.updateItem(item, empty);
                if( item == null || empty ) setText(null);
                else setText(item.getName() + " " + item.getSurname());
            }
        });
        
        
        //Bindings
        bind();
        
        // Default values
        lPreviewCard.setText("");
        lLocalDate.setText(LocalDateTime.now().toLocalDate().toString());
        
        // Default option patients menu
        onClickOpenPatientsMenu(null);
        
        // Listens to new input on the search bar
        searchBarInput.textProperty().addListener((observable, oldValue, newValue) -> {
            personsStartingWith(newValue);
        });
    }    

    /* Methods to change the menu mode e.g. from Appointments to Patients */
    @FXML
    private void onClickOpenPatientsMenu(ActionEvent event) {
        changeMenu("Patient");

        // Change the content of the listView
        personObservableList = FXCollections.observableArrayList(utils.toPersonArray(clinicDBAccess.getPatients()));
        listView.setItems(personObservableList);
        
        // Toggle the list that is visible and adjust some css components
        toggleVisibilityCss(0);
        layoutListView.setVisible(true);
        layoutListView.setManaged(true);
        layoutTableView.setVisible(false);
        layoutTableView.setManaged(false);
        previewCard.setVisible(true);
        pPatientAppointments.setVisible(true);
        pPatientAppointments.setManaged(true);
        
    }

    @FXML
    private void onClickOpenDoctorMenu(ActionEvent event) {
        changeMenu("Doctor");
        
        // Change the content of the listView
        personObservableList = FXCollections.observableArrayList(utils.toPersonArray(clinicDBAccess.getDoctors()));
        listView.setItems(personObservableList);
        
        // Toggle the list that is visible and adjust some css components
        toggleVisibilityCss(1);
        layoutListView.setVisible(true);
        layoutListView.setManaged(true);
        layoutTableView.setVisible(false);
        layoutTableView.setManaged(false);
        previewCard.setVisible(true);
        pPatientAppointments.setVisible(false);
        pPatientAppointments.setManaged(false);
    }

    @FXML
    private void onClickOpenAppointmentMenu(ActionEvent event) {
        changeMenu("Appointment");
        appObservableList.clear();
        appObservableList.addAll(clinicDBAccess.getAppointments());
        
        // Toggle the list that is visible and adjust some css components
        toggleVisibilityCss(2);
        layoutListView.setVisible(false);
        layoutListView.setManaged(false);
        layoutTableView.setVisible(true);
        layoutTableView.setManaged(true);
        previewCard.setVisible(false);
        pPatientAppointments.setVisible(false);
        pPatientAppointments.setManaged(false);
    }

    
    /* Handles the opening of a form scene in a new window for adding patients, doctors or appointments */
    @FXML
    private void onClickAddForm(ActionEvent event) {
        try{
            FXMLLoader floatingLoader = new FXMLLoader(getClass().getResource("FXMLAdd" + menuMode + ".fxml"));
            AnchorPane root = (AnchorPane) floatingLoader.load();
            Stage floStage = new Stage();

            //      Some adjusts depending the menuMode
            if(menuMode.equals("Patient")){
                FXMLAddPatientController addPatientController = floatingLoader.<FXMLAddPatientController>getController();            
                addPatientController.initStage(clinicDBAccess,personObservableList);
            }
            if(menuMode.equals("Doctor")){
                FXMLAddDoctorController addDoctorController = floatingLoader.<FXMLAddDoctorController>getController();            
                addDoctorController.initStage(clinicDBAccess,personObservableList);
            }
            if(menuMode.equals("Appointment")){
                // FIXME: WHEN CREATED
                FXMLAddAppointmentController addAppointmentController = floatingLoader.<FXMLAddAppointmentController>getController();            
                addAppointmentController.initStage(appObservableList);
            }
            Scene scene = new Scene(root);
            floStage.setScene(scene);
            floStage.setTitle("Add " + menuMode);
            floStage.initModality(Modality.APPLICATION_MODAL);
            floStage.show();
        }catch(IOException e){
            e.printStackTrace();
        } 
    }
        
    
    /* Handle the removal of selected item on the lists */
    @FXML
    private void onClickRemove(ActionEvent event) {
        if(menuMode == "Patient"){
            Patient pat = utils.searchPatient(listView.getSelectionModel().getSelectedItem().getIdentifier(), clinicDBAccess);

            if(clinicDBAccess.hasAppointments(pat) == false){
                clinicDBAccess.getPatients().remove(pat);
                personObservableList.remove(listView.getSelectionModel().getSelectedIndex());
                
                
                // Save on memory
                clinicDBAccess.saveDB();
                utils.confirmationDialogCreateDelete(menuMode, "removed", pat);
            }
            else{
                // Show a modal with warning appointemnts pending
                errorDialog("remove", (Person)pat);
            }
        }else if(menuMode == "Doctor"){
            // FIX ME!!!!!!!!!!!!!!!!!!!!!!!!!! now it seems to work
            Doctor doc = utils.searchDoctor(listView.getSelectionModel().getSelectedItem().getIdentifier(), clinicDBAccess);

            if(clinicDBAccess.hasAppointments(doc) == false){
                clinicDBAccess.getDoctors().remove(doc);
                personObservableList.remove(listView.getSelectionModel().getSelectedIndex());
                
                clinicDBAccess.saveDB();
                utils.confirmationDialogCreateDelete(menuMode, "removed", doc);
            }
            else{
                //  Show a modal with warning appointemnts pending
                errorDialog("remove", (Person)doc);
            }
        }
        else{ // Appointments handler
            Appointment app = tableView.getSelectionModel().getSelectedItem();
            if(app.getAppointmentDateTime().compareTo(LocalDateTime.now()) < 0){
                // TODO: add Error dialog
                Alert alert= new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Appointment couldn't be removed");
                String doc;

                alert.setContentText("You can't remove a past Appointment");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK){
                     System.out.println("OK");
                }
            }else{
            clinicDBAccess.getAppointments().remove(app);
            appObservableList.remove(app);
            clinicDBAccess.saveDB();
            utils.confirmationDialogCreateDelete(menuMode, "removed", app);
            }
        }
    }
    
    /* Helps with the assignment of some values when changing menu */
    private void changeMenu(String mode){
        menuMode = mode;
        lHeaderSection.setText(menuMode + "s");
        bAdd.setText("Add " + menuMode);
        bRemove.setText("Remove " + menuMode);
        bDetails.setText(menuMode + " Details");

        // Needed to clear the selection of the tableview and listview in order to make bindings work properly
        tableView.getSelectionModel().clearSelection();
        listView.getSelectionModel().clearSelection();
        
    }
    
    

    @FXML
    private void onClickShowDetails(ActionEvent event) {
        try{
            FXMLLoader floatingLoader = new FXMLLoader(getClass().getResource("FXMLShow" + menuMode + "Details.fxml"));
            AnchorPane root = (AnchorPane) floatingLoader.load();
            Stage floStage = new Stage();

            //      Some adjusts depending the menuMode
            if(menuMode.equals("Patient")){
                FXMLShowPatientDetailsController showPatientDetailsController = floatingLoader.<FXMLShowPatientDetailsController>getController();            
                showPatientDetailsController.initStage(utils.searchPatient(listView.getSelectionModel().getSelectedItem().getIdentifier(), clinicDBAccess));
            }
            if(menuMode.equals("Doctor")){
                FXMLShowDoctorDetailsController showDoctorDetailsController = floatingLoader.<FXMLShowDoctorDetailsController>getController();            
                showDoctorDetailsController.initStage(utils.searchDoctor(listView.getSelectionModel().getSelectedItem().getIdentifier(), clinicDBAccess));
            }
            if(menuMode.equals("Appointment")){
                // FIXME: WHEN CREATED
                FXMLShowAppointmentDetailsController showAppointmentDetailsController = floatingLoader.<FXMLShowAppointmentDetailsController>getController();            
                showAppointmentDetailsController.initStage(tableView.getSelectionModel().getSelectedItem());
            }
            Scene scene = new Scene(root);
            floStage.setScene(scene);
            floStage.setTitle("Show " + menuMode + " details");
            floStage.initModality(Modality.APPLICATION_MODAL);
            floStage.show();
        }catch(IOException e){
            e.printStackTrace();
        }         
    }
    
    
    private void errorDialog(String s, Person p){        
        Alert alert= new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Operation couldn't be done");
        String doc;
        if(menuMode.equals("Patient")) doc = "patient";
        else doc= "doctor";
        alert.setContentText("You can't " + s + " the " + doc + " " + p.getName() + " " + p.getSurname() + " because it has pending appointments");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
             System.out.println("OK");
        }    
    }
    
    
    /* Auxiliar method that updates the person Data shown in the list */
    /* Used to display people depending the search input              */
    private void personsStartingWith(String s){
        s = s.toLowerCase();
        ArrayList<Person> personArray;
        if(menuMode.equals("Patient"))
            personArray = utils.toPersonArray(clinicDBAccess.getPatients());
        else
            personArray = utils.toPersonArray(clinicDBAccess.getDoctors());
        
        while(!personObservableList.isEmpty()){personObservableList.remove(0);}
        
        while(!personArray.isEmpty()){
            System.out.println(personArray.get(0).getName());
            if(personArray.get(0).getName().toLowerCase().startsWith(s) ||
                    personArray.get(0).getSurname().toLowerCase().startsWith(s)){
                personObservableList.add(personArray.remove(0));
            }else{
            personArray.remove(0);
            }
        }
    }
    
    /* Auxiliar method to toggle visibility of some components in the buttons tab */
    private void toggleVisibilityCss(int mode){
        for(int i = 0; i< sideButtons.length; i++){
            if(i == mode){
                sideButtons[i].getStyleClass().add("sideButtonPressed");
               
                sideRect[i].getStyleClass().remove("hide"); 
            }else{
                sideButtons[i].getStyleClass().remove("sideButtonPressed");
                sideRect[i].getStyleClass().remove("hide"); 
                sideRect[i].getStyleClass().add("hide"); 
            }
        }
    }

    /* Binding handler for the remove and detail butoons */
    private void bind() {
        bRemove.disableProperty().bind((Bindings.equal(-1, listView.getSelectionModel().selectedIndexProperty()))
                .and(Bindings.equal(-1, tableView.getSelectionModel().selectedIndexProperty())));
        bDetails.disableProperty().bind((Bindings.equal(-1, listView.getSelectionModel().selectedIndexProperty()))
                .and(Bindings.equal(-1, tableView.getSelectionModel().selectedIndexProperty())));
        bPatientAppointments.disableProperty().bind((Bindings.equal(-1, listView.getSelectionModel().selectedIndexProperty())));
    }

    @FXML
    private void onClickShowPatientAppointments(ActionEvent event) {
        Person pat = listView.getSelectionModel().getSelectedItem();
        onClickOpenAppointmentMenu(event);
        appObservableList.clear();
        appObservableList.addAll(clinicDBAccess.getPatientAppointments(pat.getIdentifier()));
    }
    
    /* Format the text displayed on the listView cells */
    class PersonListCell extends ListCell<Person> {
        @Override
        protected void updateItem(Person item, boolean empty)
        {   super.updateItem(item, empty);
            if( item == null || empty ) setText(null);
            else setText(item.getName() + ", " + item.getSurname());
        }
    }
}