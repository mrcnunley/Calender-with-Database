/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import Model.Database;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
//import java.util.logging.Level;
//import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javax.swing.JOptionPane;
//import nunley_c195_pa.Nunley_C195_PA;
import javafx.stage.Stage;


/**
 * FXML Controller class
 *
 * @author mrc_m
 */
public class LoginController implements Initializable {

    @FXML
    private Button buttonCancel;
    @FXML
    private Button buttonAccept;
    @FXML
    private Label labelUsername;
    @FXML
    private Label labelPassword;
    @FXML
    private Label labelWelcome;
    @FXML
    private TextField textUser;
    @FXML
    private PasswordField textPassword;
    @FXML
    private Label labelIncorrect;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        rb = ResourceBundle.getBundle("Resources/rb");
               
        buttonCancel.setText(rb.getString("LSCancel"));
        buttonAccept.setText(rb.getString("LSOK"));
        labelUsername.setText(rb.getString("LSUsername"));
        labelPassword.setText(rb.getString("LSPassword"));
        labelWelcome.setText(rb.getString("LSWelcome"));
        
        //CN Lambda used to prevent making another method to assign in the fxml page.
        // Helps clean up code and makes more efficient use of code keeping on one page.
        buttonCancel.setOnAction((ActionEvent event) -> {
            int n = JOptionPane.showConfirmDialog(null, "Are you sure you want to log off?", "Log Off System", JOptionPane.YES_NO_OPTION);
            if(n == JOptionPane.YES_OPTION){
                System.exit(0);
            }
        });
        
    }
    Database db = new Database();
    String currentUser = null;
    Boolean loggedIn = false;
    
    public String getCurrentUser(){
        return currentUser;
    }
    
    //CN Get user data and check database for login information match
    public void loginUser(ActionEvent event) throws IOException{
        String user = textUser.getText();
        currentUser = user;
        String password = textPassword.getText();
        boolean isLoggedIn = false;
        
        try{
            isLoggedIn = db.userLogin(user, password);
            
            if(isLoggedIn){
                loggedIn = true;
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("Main_Menu.fxml"));
                Parent main_MenuParent = loader.load();
                
                Scene main_menuScene = new Scene(main_MenuParent);
                
                Main_MenuController controller = loader.getController();
                controller.initData(currentUser);
                
                Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();        
                window.setScene(main_menuScene);
                window.show();
            }else{
                ResourceBundle rb = ResourceBundle.getBundle("Resources/rb");
                labelIncorrect.setText(rb.getString("LSIncorrect"));
                labelIncorrect.setVisible(true);
            }
        }catch(SQLException e){
            JOptionPane.showMessageDialog(null, "Please verify your user name and password!", "Alert", JOptionPane.ERROR_MESSAGE);
        }
        userLog(loggedIn);
    } 
    
    //CN Create a user log and show login attempts successful and not
    public void userLog (Boolean log) {
        Logger logger = Logger.getLogger("MyLog");
         
        try {            
            //CN Need to create C:/temp/test folders to make sure it works.
            FileHandler fh = new FileHandler("C:/temp/test/LogFile.log", true);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
             if (log){
                // the following statement is used to log any messages
                logger.log(Level.INFO,  "{0}  Successful Login", "USER:" + currentUser);
            } else{
                logger.log(Level.INFO, "{0}  Failed Login", "USER:" + currentUser);
                }
             
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }         
    }

}
