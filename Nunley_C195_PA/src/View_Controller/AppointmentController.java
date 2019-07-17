/*
 * CN C195 Performance Assessment
 * Controller for the Appointment page
 * All functions need to control the appointment pages located here
 */
package View_Controller;

import Model.Database;
import Model.Timezone;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Callback;
import javax.swing.JOptionPane;

public class AppointmentController implements Initializable {
    
    @FXML
    private Button buttonReturn;
    @FXML
    private Button buttonSave;
    @FXML
    private Button buttonModify;
    @FXML
    private Button buttonDelete;
    @FXML
    private Label labelAppointment;
    @FXML
    private Label labelCurrent;
    @FXML
    private Label labelCustomer;
    @FXML
    private Label labelTitle;
    @FXML
    private Label labelDescription;
    @FXML
    private Label labelLocation;
    @FXML
    private Label labelContact;
    @FXML
    private Label labelURL;
    @FXML
    private Label labelType;
    @FXML
    private Label labelDate;
    @FXML
    private Label labelStartTime;
    @FXML
    private Label labelEndTime;
    @FXML
    private TextField textTitle;
    @FXML
    private TextField textDescription;
    @FXML
    private TextField textLocation;
    @FXML
    private TextField textContact;
    @FXML
    private TextField textURL;
    @FXML
    private ComboBox comboCustomer;
    @FXML
    private ComboBox comboType;
    @FXML
    private ComboBox comboStart;
    @FXML
    private ComboBox comboEnd;
    @FXML
    private DatePicker dpDate;
    @FXML
    private TableView tableAppointment;
    
    Database db = new Database();
    boolean changesMade = false, nameChanged = false, selectedCurrent = false;
    boolean typeChanged = false, startChanged = false, endChanged = false, dateChanged = false;
    String modcustName = null, modapptStart = null, modapptEnd = null, modType = null, moddate = null;
    String startingTime = "12:00:00", quittingTime = "23:00:00";//CN these are used to compare business hours, these are 7am to 5pm CST
    String[] lapptStart;
    String[] lapptEnd;
    private ObservableList<ObservableList> data;
    private ObservableList<ObservableList> correctedData;
    ResultSet rs = null;
    String currentUser = null;
    //CN Create a string array for the start and end combo boxes for the times
    String [] times = {"00:00","00:30","01:00","01:30","02:00","02:30","03:00","03:30","04:00","04:30","05:00","05:30","06:00","06:30","07:00","07:30","08:00","08:30",
                            "09:00","09:30","10:00","10:30","11:00","11:30","12:00","12:30","13:00","13:30","14:00","14:30","15:00","15:30","16:00","16:30",
                            "17:00","17:30","18:00","18:30","19:00","19:30","20:00","20:30","21:00","21:30","22:00","22:30","23:00","23:30"};
       
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        rb = ResourceBundle.getBundle("Resources/rb");
        
        buttonReturn.setText(rb.getString("ABReturn"));
        buttonSave.setText(rb.getString("ABSave"));
        buttonModify.setText(rb.getString("ABModify"));
        buttonDelete.setText(rb.getString("ABDelete"));
        
        labelAppointment.setText(rb.getString("ALAppointment"));
        labelCurrent.setText(rb.getString("ALCurrent"));
        labelCustomer.setText(rb.getString("ALCustomer"));
        labelTitle.setText(rb.getString("ALTitle"));
        labelDescription.setText(rb.getString("ALDescription"));
        labelLocation.setText(rb.getString("ALLocation"));
        labelContact.setText(rb.getString("ALContact"));
        labelURL.setText(rb.getString("ALURL"));
        labelType.setText(rb.getString("ALType"));
        labelDate.setText(rb.getString("ALDate"));
        labelStartTime.setText(rb.getString("ALStartTime"));
        labelEndTime.setText(rb.getString("ALEndTime"));
        
        //CN Add customers to comboBox
        ArrayList customers = db.getAllCustomers();
        comboCustomer.getItems().addAll(customers);
        
        comboType.getItems().add("business");
        comboType.getItems().add("personal");
        
        //CN Add values to comboBoxes for start and stop times
        comboStart.getItems().addAll(times);
        comboEnd.getItems().addAll(times);
        
        //CN Add all current and future appointments to the TableView
        rs = db.listAppointments();
        addDataToTable(rs);
        
        //CN Disable buttons until user action then enable when needed
        buttonSave.setDisable(true);
        buttonModify.setDisable(true);
        buttonDelete.setDisable(true);
    } 
    
    public void initData(String name) {
        currentUser = name;
    }
    
    //CN Get appointment information and display in text fields and combo boxes.
    public void getAppointmentInformation() {
        Timezone tz = new Timezone();
        selectedCurrent = true; 
        String locapptStart = null, locapptEnd = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        //CN Get appointment ID from selected.
        String picked = tableAppointment.getSelectionModel().getSelectedItem().toString().substring(1);
        String[] results = picked.split(",");
        String strId = results[0];
        int intId = Integer.parseInt(strId);
        
        rs = db.getAppointmentById(intId);
        
        try{            
            rs.first();
            textTitle.setText(rs.getString("title"));
            textDescription.setText(rs.getString("description"));
            textLocation.setText(rs.getString("location"));
            textURL.setText(rs.getString("url"));
            textContact.setText(rs.getString("contact"));
            comboType.setEditable(true);
            modType = rs.getString("type");
            comboType.getEditor().setText(rs.getString("type"));
            comboCustomer.setEditable(true);
            modcustName = rs.getString("customerName");
            comboCustomer.getEditor().setText(rs.getString("customerName"));
            modapptStart = rs.getString("start");
            modapptEnd = rs.getString("end");
        }catch(SQLException e) {
            JOptionPane.showMessageDialog(null, "SQL error, can not retrieve appointment information", "Alert", JOptionPane.ERROR_MESSAGE);
        }

        //CN Convert UTC times from database back to local to display date and times
        try{
        locapptStart = tz.UTCtoLocal(modapptStart);
        locapptEnd = tz.UTCtoLocal(modapptEnd);
        lapptStart = locapptStart.split(" ");
        lapptEnd = locapptEnd.split(" ");   
        LocalDate localDate = LocalDate.parse(lapptStart[0], formatter);
        dpDate.setValue(localDate);
        comboStart.setEditable(true);
        comboStart.getEditor().setText(lapptStart[1]);
        comboEnd.setEditable(true);
        comboEnd.getEditor().setText(lapptEnd[1]);
        }catch (ParseException e){
            JOptionPane.showMessageDialog(null, "Problem getting date and times.", "Alert", JOptionPane.ERROR_MESSAGE);
        }

        //CN Set buttons active that are needed
        selectedCurrent = true;
        buttonModify.setDisable(false);
        buttonDelete.setDisable(false);
        buttonSave.setDisable(true);
    }

    //CN Save a new appointment to the database based on user input
    public void buttonSave(ActionEvent event) throws IOException, SQLException  {
        //CN Gather all the information for the appointment
        String customer = null, title = null, description = null, location = null, contact = null, url = null, type = null, date = null, timeStart = null, timeEnd = null;

        try{
        customer = comboCustomer.getValue().toString();
        title = textTitle.getText();
        description = textDescription.getText();
        location = textLocation.getText();
        contact = textContact.getText();
        url = textURL.getText();
        type = comboType.getValue().toString();
        date = dpDate.getValue().toString();
        timeStart = comboStart.getValue().toString();
        timeEnd = comboEnd.getValue().toString();
        }catch (NullPointerException e) {
            JOptionPane.showMessageDialog(null, "Please make sure all fields are completed before saving.", "Alert", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //CN Get the users ID and the customers ID
        int userId = db.getUserIdFromName(currentUser);
        int custId = db.getCustomerIdFromName(customer);

        //CN Format start and end times for conversion and database uploading
        String startTime = date + " " + timeStart + ":00";
        String endTime = date + " " + timeEnd + ":00";
        String workdayStart = date + " " + startingTime;
        String workdayEnd = date + " " + quittingTime;
              
        //CN Get current date and time for database create and update values
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	LocalDateTime now = LocalDateTime.now();
        String DateTime = dtf.format(now);       
        
        //CN Convert start, end and current times to UTC
        Timezone tz = new Timezone();
        String UTCendTime = null, UTCstartTime = null, UTCdateTime = null, UTCstartingTime = null, UTCendingTime = null;
        try {
            UTCstartTime = tz.localToUTC(startTime);
            UTCendTime = tz.localToUTC(endTime);
            UTCdateTime = tz.localToUTC(DateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        //CN create the string needed to add the appointment
        String apptToAdd = "\"" + custId + "\", \"" + userId + "\", \"" + title + "\", \"" 
                + description + "\", \"" + location + "\", \"" + contact + "\", \"" + type + "\", \"" 
                + url + "\", \"" + UTCstartTime + "\", \"" + UTCendTime + "\", \"" + UTCdateTime + "\", \"" 
                + currentUser + "\", \"" + UTCdateTime + "\", \"" + currentUser + "\"";        
        
        //CN Convert to LocaDateTime to use the .isBefore() and .isAfter() comparicons to check that appt is within business hours
        LocalDateTime apToStart = LocalDateTime.parse(UTCstartTime, dtf);
        LocalDateTime apToEnd = LocalDateTime.parse(UTCendTime, dtf);
        LocalDateTime businessHourStart = LocalDateTime.parse(workdayStart, dtf);
        LocalDateTime businessHourEnd = LocalDateTime.parse(workdayEnd, dtf);
        
        //CN Compare to working hours and alert if not within the correct working hours
        if(apToStart.isBefore(businessHourStart)){
            JOptionPane.showMessageDialog(null, "Your appointment hours are earlier than normal working hours.", "Alert", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (apToEnd.isAfter(businessHourEnd)){
            JOptionPane.showMessageDialog(null, "Your appointment hours are later than normal working hours.", "Alert", JOptionPane.ERROR_MESSAGE);
            return;
        }
        //CN Check to make sure ending time is after starting time.
        if (apToEnd.isBefore(apToStart)){
            JOptionPane.showMessageDialog(null, "Your ending time must be later than your starting time.", "Alert", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        //CN call to check that existing appt is not overlapping another appt time
        boolean overlap = db.overlappingAppointment(UTCstartTime);
        if (overlap) {
            JOptionPane.showMessageDialog(null, "The times you have selected overlap another appointment, please correct.", "Alert", JOptionPane.ERROR_MESSAGE);
            return;
        }else{
            db.addNewAppointment(apptToAdd);
        }
        
        //CN Reload the page after the appointment is saved to add to current list        
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("Appointment.fxml"));
        Parent main_MenuParent = loader.load();
                
        Scene main_menuScene = new Scene(main_MenuParent);
                
        AppointmentController controller = loader.getController();
        controller.initData(currentUser);
                
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();        
        window.setScene(main_menuScene);
        window.show();
    }
    
    //CN Modify an existing appointment based on user inputs
    public void buttonModify(ActionEvent event) throws IOException, SQLException{
        //CN Get the appointmentId for the tableview selection
        String picked = tableAppointment.getSelectionModel().getSelectedItem().toString().substring(1);
        String[] results = picked.split(",");
        String strId = results[0];
        int apptId = Integer.parseInt(strId);
        
        //CN Gather all the information for the appoint
        String customer = null, type = null, date = null, startTime = null, endTime = null;
        if (nameChanged){
            customer = comboCustomer.getValue().toString();
        }else {
            customer = modcustName;  
        }
        
        if (typeChanged){
            type = comboType.getValue().toString();
        } else {
            type = modType;
        }
        
        if (dateChanged) {
            date = dpDate.getValue().toString();
        }else {
            date = lapptStart[0];
        }
        if (startChanged) {
            startTime = comboStart.getValue().toString();
        }else {
            startTime = lapptStart[1];
        }
        if (endChanged) {
            endTime = comboEnd.getValue().toString();
        }else {
            endTime = lapptEnd[1];
        }
        
        String title = textTitle.getText();
        String description = textDescription.getText();
        String location = textLocation.getText();
        String contact = textContact.getText();
        String url = textURL.getText();
        
        //CN Get the customers ID
        int userId = db.getUserIdFromName(currentUser);
        int custId = db.getCustomerIdFromName(customer);
        
        //CN Get current date and time for database update values
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	LocalDateTime now = LocalDateTime.now();
        String DateTime = dtf.format(now);

        //CN Format start and end times for conversion and database uploading
        String modstartTime = date + " " + startTime + ":00";
        String modendTime = date + " " + endTime + ":00";
        
        //CN Convert start, end and current times to UTC
        Timezone tz = new Timezone();
        String UTCendTime = null;
        String UTCstartTime = null;
        String UTCdateTime = null;
        try {
            UTCstartTime = tz.localToUTC(modstartTime);
            UTCendTime = tz.localToUTC(modendTime);
            UTCdateTime = tz.localToUTC(DateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        String modAppt = "customerId = \"" + custId + "\", userId = \"" + userId + "\", title = \"" + title + "\", "
                + "description = \"" + description + "\", location = \"" + location + "\", contact = \"" + contact + "\", "
                + "type = \"" + type + "\", url = \"" + url + "\", start = \"" + UTCstartTime + "\", end = \"" + UTCendTime + "\", "
                + "lastUpdate = \"" + UTCdateTime + "\", lastUpdateBy = \"" + currentUser + "\"";
        
        db.modifyAppointment(modAppt, apptId);
        
        //CN Reload the page after the appointment is saved to add to current list        
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("Appointment.fxml"));
        Parent main_MenuParent = loader.load();
                
        Scene main_menuScene = new Scene(main_MenuParent);
                
        AppointmentController controller = loader.getController();
        controller.initData(currentUser);
                
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();        
        window.setScene(main_menuScene);
        window.show();
    }
    
    //CN Delete a existing appointment from the database
    public void buttonDelete(ActionEvent event) throws IOException, SQLException {
        //CN Get the appointmentId of tableview selection
        String picked = tableAppointment.getSelectionModel().getSelectedItem().toString().substring(1);
        String[] results = picked.split(",");
        String strId = results[0];
        int intId = Integer.parseInt(strId);
        
        db.deleteAppointment(intId);
        
        //CN Reload the page to reflect the deleted row in the table view and update textfields.
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("Appointment.fxml"));
        Parent main_MenuParent = loader.load();
                
        Scene main_menuScene = new Scene(main_MenuParent);
                
        AppointmentController controller = loader.getController();
        controller.initData(currentUser);
                
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();        
        window.setScene(main_menuScene);
        window.show();
    }

    //CN Used to return to the main menu
    public void buttonMainMenu(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("Main_Menu.fxml"));
        Parent main_MenuParent = loader.load();
                
        Scene main_menuScene = new Scene(main_MenuParent);
                
        Main_MenuController controller = loader.getController();
        controller.initData(currentUser);
                
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();        
        window.setScene(main_menuScene);
        window.show();
    }
    
    //CN Used to dynamically add colums to a table view based on ResultSet
    private void addDataToTable(ResultSet rs){
        Timezone tz = new Timezone();
        tableAppointment.getItems().clear();
        tableAppointment.getColumns().clear();
        data = FXCollections.observableArrayList();
        correctedData = FXCollections.observableArrayList();
        try{
            //CN Dynamically add table columns from ResultSet
            for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){
                final int j = i;                
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){                    
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {                                                                                              
                        return new SimpleStringProperty(param.getValue().get(j).toString());                        
                    }                    
                });
                tableAppointment.getColumns().addAll(col); 
            }
            //CN Add data from ResultSet to Observable List
            while(rs.next()){
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i=1 ; i<=rs.getMetaData().getColumnCount(); i++){
                    row.add(rs.getString(i));
                }
                //CN Replace the UTC time in the database with local time to display in tableview
                String firstRow = row.get(1);
                String correctStartTime = tz.UTCtoLocal(firstRow);
                row.remove(1);
                row.add(1, correctStartTime);
                
                String fifthRow = row.get(6);
                String correctEndTime = tz.UTCtoLocal(fifthRow);
                row.remove(6);
                row.add(6, correctEndTime);
                
                data.add(row);
            }
            
            //CN Add items to TableView
            tableAppointment.setItems(data);            
        }catch(Exception e){
              e.printStackTrace();
              System.out.println("Error on Building Data for Table");             
        }
    }

    //CN All following methods used to verify changes are made to existing data for modification
    public void changeMade(){
        //changesMade = true;
        if(selectedCurrent){
            buttonModify.setDisable(false);
            buttonDelete.setDisable(false);
            buttonSave.setDisable(true);
        }else{
            buttonSave.setDisable(false);
        }
    }
    
    public void nameChanged() {
        nameChanged = true;
        //buttonModify.setDisable(false);
    }
    public void startChanged() {
        startChanged = true;
        //buttonModify.setDisable(false);
    }
    
    public void endChanged() {
        endChanged = true;
        //buttonModify.setDisable(false);
    }
    
    public void typeChanged() {
        typeChanged = true;
        //buttonModify.setDisable(false);
    }
    
    public void dateChanged() {
        dateChanged = true;
        //buttonModify.setDisable(false);
    }
}