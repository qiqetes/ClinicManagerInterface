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
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import model.Appointment;
import model.Doctor;
import model.Patient;
import model.Person;

/**
 *
 * @author Qiqete
 */
public class utils {
    
    
    public static boolean isNumeric(String s){
        try {  
         if(s.length() != 9 ) return false;
         Integer.parseInt(s);  
         return true;  
      } catch (NumberFormatException e) {  
         return false;  
      } 
    }
    public static void warningTelephoneDialog(String s){        
        Alert alert= new Alert(AlertType.WARNING);
        alert.setTitle("Input not valid");
        alert.setHeaderText(s + "can't be added");        
        alert.setContentText("The telephone number must be numeric and have 9 digits");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
             System.out.println("OK");
        }    
    }
    
    public static void initChHours(ArrayList<String> hours){
        String toadd;
        for(int i=0; i<13; i++){
            for(int j=0; j<4; j++){
//                toadd= Integer.toString(8+i) +":"+ Integer.toString(j*15);
                toadd = String.format("%02d:%02d", 8+i, j*15);
                hours.add(toadd);
            }
        }
    }
    
    public static ArrayList toPersonArray(ArrayList list){
        ArrayList<Person> newList = new ArrayList<Person>();
        for(int i=0; i<list.size(); i++){
            Person p = (Person)list.get(i);
            newList.add(p);
        }
        return newList;
    }
    
    
    public static void warningDialogChBox(){
        Alert alert= new Alert(AlertType.WARNING);
        alert.setTitle("Input not valid");
        alert.setHeaderText("Doctor couldn't be added: ");
        String doc;
        alert.setContentText("Only one day of the week can be checked at a time");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
             System.out.println("OK");
        }   
    }
    
    public static void warningDialogHoursMismatch(String mode){
        Alert alert= new Alert(AlertType.WARNING);
        alert.setTitle("Input not valid");
        alert.setHeaderText(mode + " couldn't be added ");
        String doc;
        alert.setContentText("Starting time must be lower than End time");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
             System.out.println("OK");
        }   
    }
    
    public static void confirmationDialogCreateDelete(String s, String cd, Object obj){        
        Alert alert= new Alert(AlertType.INFORMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Operation succesful");
        if(s.equals("Patient")){
            Patient p = (Patient) obj;
            alert.setContentText("You have " + cd + " the patient: " + p.getName() + " " + p.getSurname());
        }
        else if(s.equals("Doctor")){
            Doctor d = (Doctor) obj;
            alert.setContentText("You have " + cd + " the doctor: " + d.getName() + " " + d.getSurname());
        }
        else{
            Appointment app = (Appointment) obj;
            alert.setContentText("You have " + cd + " the appointment: Doctor" + app.getDoctor().getSurname() + " with the patient " 
                            + app.getPatient().getName() + " " + app.getPatient().getSurname() +  ". At: " + app.getAppointmentDateTime().toString());
        }
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
             System.out.println("OK");
        }    
    }
    
    
    public static boolean hoursMismatch(ChoiceBox<String> ch1, ChoiceBox<String> ch2){
        String s1 = String.join("", ch1.getValue().split(":"));
        String s2 = String.join("", ch2.getValue().split(":"));
        
        
        return Integer.parseInt(s2) <= Integer.parseInt(s1);
    }
    
    public static void browseImage(Image img, Event event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Browse image");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.gif"));
        String path = System.getProperty("user.dir")+File.separator+"src"+File.separator+"resources";
        fileChooser.setInitialDirectory(new File(path));
        File selectedFile = fileChooser.showOpenDialog((((Node)event.getSource()).getScene().getWindow()));
        
        try{
            BufferedImage bufferedImage = ImageIO.read(selectedFile);
            img = SwingFXUtils.toFXImage(bufferedImage, null);
        }catch(IOException ioe){System.err.println("Fallo al asignar imagen");}
    }
    public static Image browseImage2(Event event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Browse image");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.gif"));
        String path = System.getProperty("user.dir")+File.separator+"src"+File.separator+"resources";
        fileChooser.setInitialDirectory(new File(path));
        File selectedFile = fileChooser.showOpenDialog((((Node)event.getSource()).getScene().getWindow()));
        
        try{
            BufferedImage bufferedImage = ImageIO.read(selectedFile);
            return SwingFXUtils.toFXImage(bufferedImage, null);
        }catch(IOException ioe){System.err.println("Fallo al asignar imagen");}
        return null;
    }
    
    public static Patient searchPatient(String id, ClinicDBAccess clinicDBAccess){
        ArrayList<Patient> patientlist = clinicDBAccess.getPatients();
        for(int i = 0; i<patientlist.size(); i++){
            if(patientlist.get(i).getIdentifier().equals(id)){
                return patientlist.get(i);
            }
        }
        return null;
    }
    public static Doctor searchDoctor(String id, ClinicDBAccess clinicDBAccess){
        ArrayList<Doctor> doctorlist = clinicDBAccess.getDoctors();
        for(int i = 0; i<doctorlist.size(); i++){
            if(doctorlist.get(i).getIdentifier().equals(id)){
                return doctorlist.get(i);
            }
        }
        return null;
    }
    public static boolean isDayPast(LocalDate day){
        return day.compareTo(LocalDate.now()) < 0;
    }

    static void initChHours2(ArrayList<String> hours, LocalTime visitStartTime, LocalTime visitEndTime) {
        String toadd;
        
        // NEEDS TO BE FIXED BUT KINDA WORKS
        // FIX ME: the start hour is always at min 00 and end min is 45
        
        for(int i=visitStartTime.getHour(); i<visitEndTime.getHour() + 1; i++){
            for(int j=0; j<4; j++){
                toadd = String.format("%02d:%02d", i, j*15);
                hours.add(toadd);
            }
        }
    }
}
