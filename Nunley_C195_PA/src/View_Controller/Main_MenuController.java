/*
 * CN C195 Performance Assessment
 * Main Menu Controller used for navagation to all the different pages, buttons
 * for each page in the calendar program
 */
package View_Controller;

import Model.Database;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.stage.Stage;
import javax.swing.JOptionPane;

public class Main_MenuController implements Initializable {

    @FXML
    private Label labelWelcome;
    @FXML
    private Button buttonCustomer;
    @FXML
    private Button buttonAppointment;
    @FXML
    private Button buttonCalendar;
    @FXML
    private Button buttonReport;
    @FXML
    private Button buttonLogOff;
    @FXML
    private Label labelCustomer;
    @FXML
    private Label labelAppointment;
    @FXML
    private Label labelCalendar;
    @FXML
    private Label labelReport;
    @FXML
    private Label labelLogOff;
    @FXML
    private RadioButton radioWeek;
    @FXML
    private RadioButton radioMonth;
    
    String currentUser = null;
    Database db = new Database();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        rb = ResourceBundle.getBundle("Resources/rb");
        
        labelWelcome.setText(rb.getString("MMLWelcome"));
        
        buttonCustomer.setText(rb.getString("MMBCustomer"));
        labelCustomer.setText(rb.getString("MMLCustomer"));
        
        buttonAppointment.setText(rb.getString("MMBAppointment"));
        labelAppointment.setText(rb.getString("MMLAppointment"));
        
        buttonCalendar.setText(rb.getString("MMBCalendar"));
        labelCalendar.setText(rb.getString("MMLCalendar"));
        
        buttonReport.setText(rb.getString("MMBReport"));
        labelReport.setText(rb.getString("MMLReport"));
        
        buttonLogOff.setText(rb.getString("MMBLogOff"));
        labelLogOff.setText(rb.getString("MMLLogOff"));
        
        db.apptWarning();
        
        /*CN Lanbda expression for log off button, no need to create additional method to log off program.
        Additional dialog box to make sure user wants to log out of system.*/
        buttonLogOff.setOnAction((ActionEvent event) -> {
            int n = JOptionPane.showConfirmDialog(null, "Are you sure you want to log off?", "Log Off System", JOptionPane.YES_NO_OPTION);
            if(n == JOptionPane.YES_OPTION){
                System.exit(0);
            }
        });
    }
    
    public void initData(String name) {
        currentUser = name;

    }
   

    //CN Used to navigate to the appointment page, passes currentUser information
    public void buttonAppointment(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("Appointment.fxml"));
        Parent customerParent = loader.load();
        
        Scene customerScene = new Scene(customerParent);
        
        AppointmentController controller = loader.getController();
        controller.initData(currentUser);
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();        
        window.setScene(customerScene);
        window.show();
    }
    
    //CN Used for Customer page passes along currentUser information
    public void buttonCustomer(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("Customer.fxml"));
        Parent customerParent = loader.load();
        
        Scene customerScene = new Scene(customerParent);
        
        CustomerController controller = loader.getController();
        controller.initData(currentUser);
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();        
        window.setScene(customerScene);
        window.show(); 
    }
    
    //CN Used to get to Calendar page, passes currentUser information
    public void buttonCalendar(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("Calendar.fxml"));
        Parent customerParent = loader.load();
        
        Scene customerScene = new Scene(customerParent);
        
        CalendarController controller = loader.getController();
            //System.out.println(currentUser);

        controller.initData(currentUser);
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();        
        window.setScene(customerScene);
        window.show();
    }
    
    //CN Used to navagate to Reports page, passes currentUser information.
    public void buttonReports(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("Reports.fxml"));
        Parent customerParent = loader.load();
        
        Scene customerScene = new Scene(customerParent);
        
        ReportsController controller = loader.getController();
        controller.initData(currentUser);
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();        
        window.setScene(customerScene);
        window.show();
    }
}
