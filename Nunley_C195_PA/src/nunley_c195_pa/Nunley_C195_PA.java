/*
* CN Loads the Login page
* Uncomment line 27 to test internationalization, the two languages that I chose are English and Spanish
* I left the import for java.util.Locale to test with.
 */
package nunley_c195_pa;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Nunley_C195_PA extends Application {
    
    String currentUser = "From Main";

    public static void main(String[] args){
        launch(args);                        
    }
    
    public void start(Stage stage) throws Exception {
        //CN Uncomment next line to test internationalization
        //Locale.setDefault(new Locale("es"));
        
        ResourceBundle rb = ResourceBundle.getBundle("Resources/rb");
        FXMLLoader loader = new FXMLLoader();
        loader.setResources(rb);
        Parent root = loader.load(getClass().getResource("/View_Controller/Login.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
    
    public void setCurrentUser(String user) {
        currentUser = user;
    }
    public String getCurrentUser(){
        return currentUser;
    }
    
}