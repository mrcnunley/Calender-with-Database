/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.util.Callback;
import javax.swing.JOptionPane;

public class CalendarController implements Initializable {
    
    @FXML
    private Label labelCalendar;
    @FXML
    private RadioButton radioWeek;
    @FXML
    private RadioButton radioMonth;
    @FXML
    private Button buttonReturn;
    @FXML
    private TableView tableCalendar;
    
    Database db = new Database();
    ResultSet rs = null;
    private ObservableList<ObservableList> data;
    String currentUser = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        rb = ResourceBundle.getBundle("Resources/rb");
        
        labelCalendar.setText(rb.getString("CLCalendar"));
        
        radioWeek.setText(rb.getString("CRWeek"));
        radioMonth.setText(rb.getString("CRMonth"));        
        
        buttonReturn.setText(rb.getString("CBReturn"));
        
        //CN Lambda expressions used for radio buttons so there is no need
        // to write additional functions to be called from fxml document
        radioWeek.setOnAction((ActionEvent event) -> {
            rs = db.getWeekCalendar();
            addDataToTable(rs);
        });
        radioMonth.setOnAction((ActionEvent event) -> {
            rs = db.getMonthCalendar();
            addDataToTable(rs);
        });
    } 
    
    public void initData(String name) {
        currentUser = name;
    }

    //CN Used to dynamically add columns and rows to the tableview based on the ResultSet
    private void addDataToTable(ResultSet rs){
        Timezone tz = new Timezone();
        tableCalendar.getItems().clear();
        tableCalendar.getColumns().clear();
        data = FXCollections.observableArrayList();

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
                tableCalendar.getColumns().addAll(col); 
            }
            //CN Add data from ResultSet to Observable List
            while(rs.next()){
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i=1 ; i<=rs.getMetaData().getColumnCount(); i++){
                    row.add(rs.getString(i));
                }
                //CN Replace the UTC time in the database with local time to display in tableview
                String fourthRow = row.get(4);
                String correctEndTime = tz.UTCtoLocal(fourthRow);
                row.remove(4);
                row.add(4, correctEndTime);
                
                data.add(row);
            }
            //CN Add items to TableView
            tableCalendar.setItems(data);
        }catch(Exception e){
              JOptionPane.showMessageDialog(null, "There are no results to post.", "Alert", JOptionPane.ERROR_MESSAGE);
              System.out.println("Error on Building Data for Table");             
        }
    }
    
    //CN Return to the main menu and still keep current user information.
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
    
}
