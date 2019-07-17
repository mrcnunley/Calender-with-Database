/*
 * CN C195 Performance Assessment
 * Reports controller used to control all functions and calls for the 
 * reports page
 */
package View_Controller;

import Model.Database;
import Model.Timezone;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.util.Callback;
import javax.swing.JOptionPane;

public class ReportsController implements Initializable {

    @FXML
    private Label labelReports;
    @FXML
    private RadioButton radioType;
    @FXML
    private RadioButton radioConsultant;
    @FXML
    private RadioButton radioCurrent;
    @FXML
    private Button buttonReturn; 
    @FXML
    private TableView tableReport;
    
    private ObservableList<ObservableList> data;
    Database db = new Database();
    ResultSet rs = null;
    String currentUser = null;
    
    //SELECT * FROM appointment WHERE start BETWEEN "2019-06-01" AND "2019-06-30" ORDER BY start; 
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        rb = ResourceBundle.getBundle("Resources/rb");
        
        labelReports.setText(rb.getString("RLReports"));
        
        radioType.setText(rb.getString("RRType"));
        radioConsultant.setText(rb.getString("RRConsultant"));
        radioCurrent.setText(rb.getString("RRCurrent"));
        
        buttonReturn.setText(rb.getString("RBReturn"));
        
        //CN Lambda Expressions used for radio buttons to help clean up code and reduce the 
        // amount of method calls from the fxml page.
        radioType.setOnAction((ActionEvent event) -> {
            tableReport.getColumns().clear();
            rs = db.appointmentsByType();
            addDataToTable(rs);
        });
        
        radioConsultant.setOnAction((ActionEvent event) -> {
            tableReport.getColumns().clear();
            rs = db.appointmentByConsultant();
            addDataToTable(rs);
        });
        
        radioCurrent.setOnAction((ActionEvent event) -> {
            tableReport.getColumns().clear();
            rs = db.appointmentsForToday();
            addDataToTable(rs);
            try{
            if(!rs.last()){
                JOptionPane.showMessageDialog(null, "No appointments to report for today.", "Alert", JOptionPane.ERROR_MESSAGE);
            }
            }catch(SQLException e){
                e.printStackTrace();
            }
        });
    }  
    
    public void initData(String name) {
        currentUser = name;
    }
    
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
    
    //CN Dynamically add columns and rows to tables
    private void addDataToTable(ResultSet rs){
        Timezone tz = new Timezone();
        tableReport.getColumns().clear();
        data = FXCollections.observableArrayList();
        try{
            // CN table columns added dynamically
            for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){
                final int j = i;                
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));
                col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){                    
                    public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {                                                                                              
                        return new SimpleStringProperty(param.getValue().get(j).toString());                        
                    }                    
                });
                tableReport.getColumns().addAll(col); 
            }
            while(rs.next()){
                //CN Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i=1 ; i<=rs.getMetaData().getColumnCount(); i++){
                    //CN Iterate Column
                    row.add(rs.getString(i));
                }
                //CN Replace the UTC time in the database with local time to display in tableview
                String secondRow = row.get(2);
                String correctEndTime = tz.UTCtoLocal(secondRow);
                row.remove(2);
                row.add(2, correctEndTime);
                data.add(row);

            }
            //CN FINALLY ADDED TO TableView
            tableReport.setItems(data);
          }catch(Exception e){
              e.printStackTrace();             
          }
    }
    
}
