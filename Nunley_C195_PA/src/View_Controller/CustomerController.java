/*
 * CN C195 Performance Assessment
 * Customer controller has methods used to give functionality to the
 * customer.fxml page
 */
package View_Controller;

import Model.Database;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javax.swing.JOptionPane;

public class CustomerController implements Initializable {

    @FXML
    private Label labelCustomerpage;
    @FXML
    private Label labelSelect;
    @FXML
    private ComboBox comboCustomer;
    @FXML
    private Label labelNewCustomer;
    @FXML
    private Label labelName;
    @FXML
    private Label labelPhone;
    @FXML
    private Label labelAddress;
    @FXML
    private Label labelCity;
    @FXML
    private Label labelPostalCode;
    @FXML
    private Label labelAddress2;
    @FXML
    private Label labelCountry;
    @FXML
    private Button buttonReturn; 
    @FXML
    private Button buttonSave;
    @FXML
    private Button buttonModify;
    @FXML
    private Button buttonDelete;
    @FXML
    private TextField textCustomerName;
    @FXML
    private TextField textPhone;
    @FXML
    private TextField textAddress;
    @FXML
    private TextField textCity;
    @FXML
    private TextField textPostal;
    @FXML
    private TextField textAddress2;
    @FXML
    private TextField textCountry;

    boolean changesMade = false;
    boolean selectedCurrent = false;
    String currentUser = null;
    int custId = 0;
    int addressId = 0;
    int cityId = 0;
    int countryId = 0;
    
    Database db = new Database();   
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        rb = ResourceBundle.getBundle("Resources/rb");
        
        labelCustomerpage.setText(rb.getString("CuLCustomer"));
        labelSelect.setText(rb.getString("CuLSelect"));
        
        comboCustomer.setPromptText(rb.getString("CuCCustomers"));
              
        labelNewCustomer.setText(rb.getString("CuLNewCustomer"));
        labelName.setText(rb.getString("CuLName"));
        labelPhone.setText(rb.getString("CuLPhone"));
        labelAddress.setText(rb.getString("CuLAddress"));
        labelCity.setText(rb.getString("CuLCity"));
        labelPostalCode.setText(rb.getString("CuLPostalCode"));
        labelAddress2.setText(rb.getString("CuLAddress2"));
        labelCountry.setText(rb.getString("CuLCountry"));
        
        buttonSave.setText(rb.getString("CuBSave"));
        buttonModify.setText(rb.getString("CuBModify"));
        buttonDelete.setText(rb.getString("CuBDelete"));
        buttonReturn.setText(rb.getString("CuBReturn"));
        
        
        ArrayList customers = db.getAllCustomers();
        comboCustomer.getItems().addAll(customers);

        buttonSave.setDisable(true);
        buttonModify.setDisable(true);
        buttonDelete.setDisable(true);
        
        }

    public void initData(String name) {
        currentUser = name;
    }
     
    //CN Used to display existing customer information from database to fxml document
    public void getCustomerInformation(){
        String customerName= comboCustomer.getSelectionModel().getSelectedItem().toString();
        ResultSet rs = db.getCustomerInfoByName(customerName);

        try{            
            rs.first();
            custId = rs.getInt("customerId");
            addressId = rs.getInt("addressID");  
            cityId = rs.getInt("cityId");
            countryId = rs.getInt("countryId");
            textCustomerName.setText(rs.getString("customerName"));
            textAddress.setText(rs.getString("address"));
            textAddress2.setText(rs.getString("address2"));
            textPostal.setText(rs.getString("postalCode"));
            textPhone.setText(rs.getString("phone"));
            textCity.setText(rs.getString("city"));
            textCountry.setText(rs.getString("country"));
        }catch(SQLException e) {
            JOptionPane.showMessageDialog(null, "SQL error, can not retrieve customer information", "Alert", JOptionPane.ERROR_MESSAGE);
        }
        selectedCurrent = true;
        buttonDelete.setDisable(false);
        buttonSave.setDisable(true);
    }
    
    //CN Save a new customer based on user input, checks to see if each values exists in database first
    public void buttonSave(ActionEvent event) throws IOException  {
        ResultSet rs = null;
        
        String country = textCountry.getText(); 
        String city = textCity.getText();       
        String address = textAddress.getText();
        String address2 = textAddress2.getText();
        String postal = textPostal.getText();
        String phone = textPhone.getText();
        String customer = textCustomerName.getText();
        
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
	LocalDateTime now = LocalDateTime.now();
        String DateTime = dtf.format(now);

        rs = db.checkCountry(country); 
        
        //CN check to see if any values already exist in the database and use those rather than make a new entry
        try{
            if(!rs.first()){
                String addCountry = "\"" + country + "\", \"" + DateTime + "\", \"" + currentUser + "\", \"" + currentUser + "\"";
                db.saveCountry(addCountry);            
            }           
            rs = db.checkCountry(country);
            rs.last();
            countryId = rs.getInt("countryId");
        }catch(SQLException e){
            e.printStackTrace();
        }
        rs = db.checkCity(city);
        try{
            if(!rs.first()){
                String addCity = "\"" + city + "\", \"" + countryId + "\", \"" + DateTime + "\", \"" + currentUser + "\", \"" + currentUser + "\"";
                db.saveCity(addCity);            
            }           
            rs = db.checkCity(city);
            rs.last();
            cityId = rs.getInt("cityId");
        }catch(SQLException e){
            e.printStackTrace();
        }
        rs = db.checkAddress(address);
        try{
            if(!rs.first()){
                String addAddress = "\"" + address + "\", \"" + address2 + "\", \"" + cityId + "\", \"" + postal + "\", \"" + phone + "\", \"" + DateTime + "\", \"" + currentUser + "\", \"" + currentUser + "\"";
                db.saveAddress(addAddress);            
            }           
            rs = db.checkAddress(address);
            rs.last();
            addressId = rs.getInt("addressId");
        }catch(SQLException e){
            e.printStackTrace();
        }
        rs = db.checkCustomer(customer);
        try{
            if(!rs.first()){
                String addCustomer = "\"" + customer + "\", \"" + addressId + "\", \"1 \", \"" + DateTime + "\", \"" + currentUser + "\", \"" + currentUser + "\"";
                db.saveCustomer(addCustomer);            
            }           
        }catch(SQLException e){
            e.printStackTrace();
        }
        //CN Reload the window after to keep everything updated after siccessful save
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
    
    //CN Modify and existing customer based on user input.
    public void buttonModify(ActionEvent event) throws IOException  {      
        String country = textCountry.getText(); 
        String city = textCity.getText();       
        String address = textAddress.getText();
        String address2 = textAddress2.getText();
        String postal = textPostal.getText();
        String phone = textPhone.getText();
        String customer = textCustomerName.getText();
        
        //CN Get current time and date for UPDATE database
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
	LocalDateTime now = LocalDateTime.now();
        String DateTime = dtf.format(now);
        
        //CN Create the mofification string needed to UPDATE the database.
        String modCust = "customerName = \"" + customer + "\", lastUpdate = \"" + DateTime + "\", lastUpdateBy = \"" + currentUser + "\"";
        String modAddress = "address = \"" + address + "\", address2 = \"" + address2 + "\", postalCode = \"" + postal + "\", phone = \"" + phone + "\", lastUpdate = \"" + DateTime + "\", lastUpdateBy = \"" + currentUser + "\"";
        String modCity = "city = \"" + city + "\", lastUpdate = \"" + DateTime + "\", lastUpdateBy = \"" + currentUser + "\"";
        String modCountry = "country = \"" + country + "\", lastUpdate = \"" + DateTime + "\", lastUpdateBy = \"" + currentUser + "\"";
        
        db.modifyCustomerById(modCust, custId);
        db.modifyAdressById(modAddress, addressId);
        db.modifyCityById(modCity, cityId);
        db.modifyCountryById(modCountry, countryId);
        
        //CN Reload the page after the changes take effect.
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
    
    //CN Delete an existing customer from the database.
    public void buttonDelete(ActionEvent event) throws IOException {
        db.deleteCustomerFromDB(custId);
        
        //CN Reload the customer page after delete to update changes and retain the current user
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
    
    //CN Return to main menu and retain current user
    public void buttonMainMenu(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("Main_Menu.fxml"));
        Parent customerParent = loader.load();
        
        Scene customerScene = new Scene(customerParent);
        
        Main_MenuController controller = loader.getController();
        controller.initData(currentUser);
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();        
        window.setScene(customerScene);
        window.show();
    }
    
    //CN used to enable buttons based on user input.
    public void changeMade(){
        changesMade = true;
        if(selectedCurrent){
            buttonModify.setDisable(false);
        }else{
            buttonSave.setDisable(false);
        }
    }
}
