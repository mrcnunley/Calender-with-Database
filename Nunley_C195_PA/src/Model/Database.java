/*
 * CN C195 Performance Assesement
 * All database communication is done here, all connections , Query, Update, Delete
 * and disconnections
 */
package Model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class Database {
    private static final String databaseName = "U04bFw";
    private static final String DBurl = "jdbc:mysql://52.206.157.109/" + databaseName;
    private static final String username = "U04bFw";
    private static final String password = "53688194359";
    private static final String driver = "com.mysql.jdbc.Driver";
    static Connection connect;
    
    //CN make a connection to the database
    public void makeConnection() throws ClassNotFoundException, SQLException {
        Class.forName(driver);
        connect = (Connection) DriverManager.getConnection(DBurl, username, password);
    }
    
    //CN close open connection to database.
    public void closeConnection() throws ClassNotFoundException, SQLException {
        connect.close();
    }
    
    //CN Verify the username and the password match.
    public boolean userLogin(String userName, String password) throws SQLException{
        boolean loggedIn = false;
        String User = userName;
        String Pass = password;
        
        String sql = "SELECT * FROM user WHERE active = 1 "
		      + "AND userName = ? AND password = ?";
        try {
            makeConnection();        
                try{
                    PreparedStatement stmt = connect.prepareStatement(sql);
                    stmt.setString(1, User);
                    stmt.setString(2, Pass);
                    ResultSet rs = stmt.executeQuery();
            
                    if(rs.next()){
                        loggedIn = true;
                    }

                    }catch(SQLException e){
                        JOptionPane.showMessageDialog(null, "Please verify your user name and password are correct.", "Alert", JOptionPane.ERROR_MESSAGE);
                        return loggedIn;
                    }       
            closeConnection();
        }catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(null, "Verify internet connection.", "Alert", JOptionPane.ERROR_MESSAGE);
            return loggedIn;
        }
        return loggedIn;
    }
    
    //CN Return an integer value for userId by matching up the name
    public int getUserIdFromName(String name){
        ResultSet rs = null;
        int userId = 0;
        try{
            makeConnection(); 
                try {
                    Statement stmt = connect.createStatement();
                    rs = stmt.executeQuery("SELECT userId FROM user WHERE userName = \"" + name + "\"");

                    //return rs;
                    rs.first();
                    userId = rs.getInt("userId");
                    return userId;
                }catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, "Unable to get user Id from name.", "Alert", JOptionPane.ERROR_MESSAGE);
                }
            closeConnection();
        }catch(ClassNotFoundException | SQLException e){
            JOptionPane.showMessageDialog(null, "Unable to get user Id from name.", "Alert", JOptionPane.ERROR_MESSAGE);
        }
        
        return userId;
    }
    
    //CN Return integer value for customerId by matching the name
    public int getCustomerIdFromName(String cust){       
        ResultSet rs = null;
        int custId = 0;
        try{
            makeConnection(); 
            try{
                Statement stmt = connect.createStatement();
                rs = stmt.executeQuery("SELECT customerId FROM customer WHERE customerName = \"" + cust + "\"");
                rs.first();
                custId = rs.getInt("customerId");
                return custId;
            }catch(SQLException e){
                JOptionPane.showMessageDialog(null, "Unable to get user Id from name.", "Alert", JOptionPane.ERROR_MESSAGE);
            }
            closeConnection();
        }catch(ClassNotFoundException | SQLException e){
            JOptionPane.showMessageDialog(null, "Unable to get customer Id from name.", "Alert", JOptionPane.ERROR_MESSAGE);
        }
        
        return custId;
    }
    
    //CN gather cutomer names and return arraylist for drop down menu.
    public ArrayList getAllCustomers(){
        ArrayList<String> customers = new ArrayList<String>();
        try{
            makeConnection();            
            Statement stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT customerName FROM customer");

            while(rs.next()){
                String sourcePattern;
                sourcePattern = rs.getString(1);
                customers.add(sourcePattern);
            }       
            closeConnection();    
        }catch(ClassNotFoundException | SQLException e){
            JOptionPane.showMessageDialog(null, "Unable to get all customer information.", "Alert", JOptionPane.ERROR_MESSAGE);
        }
        return customers;
                
    }
    
    //CN Populate the customer information when name is selected from drop down.
    public ResultSet getCustomerInfoByName(String name){         
        ResultSet rs = null;
        try{
            makeConnection();
                try{
                    Statement stmt = connect.createStatement();
                    rs = stmt.executeQuery("SELECT customer.customerName, customer.customerId, customer.addressId, "
                        + "address.address, address.address2, address.cityId, address.postalCode, address.phone, "
                        + "city.city, city.countryId, "
                        + "country.country "
                        + "FROM customer "
                        + "LEFT JOIN address ON customer.addressId = address.addressId "
                        + "LEFT JOIN city ON address.cityId = city.cityId "
                        + "LEFT JOIN country ON city.countryId = country.countryId "
                        + "WHERE customer.customerName = \"" + name +"\"");
                    return rs;
                }catch(SQLException e){
                    JOptionPane.showMessageDialog(null, "There has been a problem retreiving customer from the database.", "Alert", JOptionPane.ERROR_MESSAGE);
                }
            closeConnection();
        }catch(SQLException | ClassNotFoundException e){
            JOptionPane.showMessageDialog(null, "Verify internet connection.", "Alert", JOptionPane.ERROR_MESSAGE);
        }
        return rs;
    }
    
    //CN Delete a customer form the database
    public void deleteCustomerFromDB(int id) {
        int n = JOptionPane.showConfirmDialog(null, "Deleting customer will delete all appointments with that customer, Continue?", "Delete Customer", JOptionPane.YES_NO_OPTION);
        if (n == JOptionPane.NO_OPTION){
            return;
        }
        try{
            makeConnection();
            try{
                Statement stmt = connect.createStatement();
                stmt.execute("DELETE FROM customer WHERE customerId= "+id);
                stmt.execute("DELETE FROM appointment WHERE customerId= "+id); //CN Customer no longer exists, no appointments needed.
            }catch(SQLException e){
                JOptionPane.showMessageDialog(null, "Unable to delete customer.", "Alert", JOptionPane.ERROR_MESSAGE);
            }
            closeConnection();
        }catch(SQLException | ClassNotFoundException e){
            JOptionPane.showMessageDialog(null, "Verify internet connection.", "Alert", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    //CN Check to see if a country exists with in the country table
   public ResultSet checkCountry(String country) {
        ResultSet rs = null;
        try{
            makeConnection();
                try{
                    Statement stmt = connect.createStatement();
                    rs = stmt.executeQuery("SELECT countryId FROM country WHERE country = \"" + country + "\"");
                    return rs;
                }catch(SQLException e){
                    JOptionPane.showMessageDialog(null, "Unable to check country status.", "Alert", JOptionPane.ERROR_MESSAGE);
                }
            closeConnection();
        }catch(SQLException | ClassNotFoundException e){
            JOptionPane.showMessageDialog(null, "Verify internet connection.", "Alert", JOptionPane.ERROR_MESSAGE);
        }
        return rs;
    }
   
    //Cn Save a country to the database
    public void saveCountry(String country) {
        try{
            makeConnection();
            try{
                Statement stmt = connect.createStatement();
                stmt.execute("INSERT INTO country (country, createDate, createdBy, lastUpdateBy) "
                        + "VALUES (" + country + ")");
            }catch(SQLException e){
                JOptionPane.showMessageDialog(null, "Unable to save country to the database.", "Alert", JOptionPane.ERROR_MESSAGE);
            }
            closeConnection();
        }catch(SQLException | ClassNotFoundException e){
            JOptionPane.showMessageDialog(null, "Verify internet connection.", "Alert", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    //CN Modify a country within the country table
    public void modifyCountryById(String country, int countryId){
        try{
            makeConnection();
            try{
                Statement stmt = connect.createStatement();
                stmt.execute("UPDATE country SET " + country + " WHERE countryId = \"" + countryId + "\"");
            }catch(SQLException e){
                JOptionPane.showMessageDialog(null, "There is a problem modifying the country in the database.", "Alert", JOptionPane.ERROR_MESSAGE);
            }
            closeConnection();
        }catch(SQLException | ClassNotFoundException e){
            JOptionPane.showMessageDialog(null, "Verify internet connection.", "Alert", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    //CN Check to see if a city exists within the city table
    public ResultSet checkCity(String city) {
        ResultSet rs = null;
        try{
            makeConnection();
                try{
                    Statement stmt = connect.createStatement();
                    rs = stmt.executeQuery("SELECT cityId FROM city WHERE city = \"" + city + "\"");
                    return rs;
                }catch(SQLException e){
                    JOptionPane.showMessageDialog(null, "Unable to verify city name in database.", "Alert", JOptionPane.ERROR_MESSAGE);
                }
            closeConnection();
        }catch(SQLException | ClassNotFoundException e){
            JOptionPane.showMessageDialog(null, "Verify internet connection.", "Alert", JOptionPane.ERROR_MESSAGE);
        }
        return rs;
    }
    
    //CN Save a city to the city table
    public void saveCity(String city) {
        try{
            makeConnection();
            try{
                Statement stmt = connect.createStatement();
                stmt.execute("INSERT INTO city (city, countryId, createDate, createdBy, lastUpdateBy) "
                        + "VALUES (" + city + ")");
            }catch(SQLException e){
                JOptionPane.showMessageDialog(null, "Unable to save city name in database.", "Alert", JOptionPane.ERROR_MESSAGE);
            }
            closeConnection();
        }catch(SQLException | ClassNotFoundException e){
            JOptionPane.showMessageDialog(null, "Verify internet connection.", "Alert", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    //CN Modify a city in the city table by using Id
    public void modifyCityById(String city, int cityId){
        try{
            makeConnection();
            try{
                Statement stmt = connect.createStatement();
                stmt.execute("UPDATE city SET " + city + " WHERE cityId = \"" + cityId + "\"");
            }catch(SQLException e){
                JOptionPane.showMessageDialog(null, "There is a problem modifying the city in the database.", "Alert", JOptionPane.ERROR_MESSAGE);
            }
            closeConnection();
        }catch(SQLException | ClassNotFoundException e){
            JOptionPane.showMessageDialog(null, "Verify internet connection.", "Alert", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    //CN Check to see if an address already exsists with the address table
    public ResultSet checkAddress(String address) {
        ResultSet rs = null;
        try{
            makeConnection();
                try{
                    Statement stmt = connect.createStatement();
                    rs = stmt.executeQuery("SELECT addressId FROM address WHERE address = \"" + address + "\"");
                    return rs;
                }catch(SQLException e){
                    JOptionPane.showMessageDialog(null, "Unable to verify address in database.", "Alert", JOptionPane.ERROR_MESSAGE);
                }
            closeConnection();
        }catch(SQLException | ClassNotFoundException e){
            JOptionPane.showMessageDialog(null, "Verify internet connection.", "Alert", JOptionPane.ERROR_MESSAGE);
        }
        return rs;
    }
    
    //CN Save a new address to the address table
    public void saveAddress(String address) {
        try{
            makeConnection();
                try{
                    Statement stmt = connect.createStatement();
                    stmt.execute("INSERT INTO address (address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdateBy) "
                                + "VALUES (" + address + ")");
                }catch(SQLException e){
                JOptionPane.showMessageDialog(null, "Unable to save address in database.", "Alert", JOptionPane.ERROR_MESSAGE);
                }
            closeConnection();
        }catch(SQLException | ClassNotFoundException e){
            JOptionPane.showMessageDialog(null, "Verify internet connection.", "Alert", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    //CN Modify an existing address in the address table
    public void modifyAdressById(String address, int addId){
        try{
            makeConnection();
            try{
                Statement stmt = connect.createStatement();
                stmt.execute("UPDATE address SET " + address + " WHERE addressId = \"" + addId + "\"");
            }catch(SQLException e){
                JOptionPane.showMessageDialog(null, "There is a problem modifying the address in the database.", "Alert", JOptionPane.ERROR_MESSAGE);
            }
            closeConnection();
        }catch(SQLException | ClassNotFoundException e){
            JOptionPane.showMessageDialog(null, "Verify internet connection.", "Alert", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    //CN Check to see if a customer name already exists within the customer table
    public ResultSet checkCustomer(String customer) {
        ResultSet rs = null;
        try{
            makeConnection();
                try{
                    Statement stmt = connect.createStatement();
                    rs = stmt.executeQuery("SELECT customerId FROM customer WHERE customerName = \"" + customer + "\"");
                    return rs;
                }catch(SQLException e){
                    JOptionPane.showMessageDialog(null, "Unable to verify customer in the database.", "Alert", JOptionPane.ERROR_MESSAGE);
                }
            closeConnection();
        }catch(SQLException | ClassNotFoundException e){
            JOptionPane.showMessageDialog(null, "Verify internet connection.", "Alert", JOptionPane.ERROR_MESSAGE);
        }
        return rs;
    }
    
    //CN Save a customer name to the customer table
    public void saveCustomer(String customer) {
        try{
            makeConnection();
            try{
                Statement stmt = connect.createStatement();
                stmt.execute("INSERT INTO customer (customerName, addressId, active, createDate, createdBy, lastUpdateBy) "
                        + "VALUES (" + customer + ")");
            }catch(SQLException e){
                JOptionPane.showMessageDialog(null, "There is a problem adding the customer to the database.", "Alert", JOptionPane.ERROR_MESSAGE);
            }
            closeConnection();
        }catch(SQLException | ClassNotFoundException e){
            JOptionPane.showMessageDialog(null, "Verify internet connection.", "Alert", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    //CN Modify an existing customer in the customer table
    public void modifyCustomerById(String cust, int custId){
        try{
            makeConnection();
            try{
                Statement stmt = connect.createStatement();
                stmt.execute("UPDATE customer SET " + cust + " WHERE customerId = \"" + custId + "\"");
            }catch(SQLException e){
                JOptionPane.showMessageDialog(null, "There is a problem modifying the customer in the database.", "Alert", JOptionPane.ERROR_MESSAGE);
            }
            closeConnection();
        }catch(SQLException | ClassNotFoundException e){
            JOptionPane.showMessageDialog(null, "Verify internet connection.", "Alert", JOptionPane.ERROR_MESSAGE);
        }
    }
                
    //CN Add a new appointment to the database
    public void addNewAppointment (String appt) {
        try{
            makeConnection();
            try{
                Statement stmt = connect.createStatement();
                stmt.execute("INSERT INTO appointment (customerId, userId, title, description, location, contact, type, "
                        + "url, start, end, createDate, createdBy, lastUpdate, lastUpdateBy) "
                        + "VALUES (" + appt + ")");
            }catch(SQLException e){
                JOptionPane.showMessageDialog(null, "There is a problem adding the appointment to the database.", "Alert", JOptionPane.ERROR_MESSAGE);
            }
            closeConnection();
        }catch(SQLException | ClassNotFoundException e){
            JOptionPane.showMessageDialog(null, "Verify internet connection.", "Alert", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    //CN Modify an existing appointment in the database
    public void modifyAppointment(String appt, int apptId) {
        //System.out.println(appt);
        //System.out.println(apptId);
        
        try{
            makeConnection();
            try{
                Statement stmt = connect.createStatement();
                stmt.execute("UPDATE appointment SET " + appt + " WHERE appointmentId = \"" + apptId + "\"");
            }catch(SQLException e){
                JOptionPane.showMessageDialog(null, "There is a problem modifying the appointment in the database.", "Alert", JOptionPane.ERROR_MESSAGE);
            }
            closeConnection();
        }catch(SQLException | ClassNotFoundException e){
            JOptionPane.showMessageDialog(null, "Verify internet connection.", "Alert", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    //CN Delete an existing appointment in the database
    public void deleteAppointment(int appt) {
        try{
            makeConnection();
            try{
                Statement stmt = connect.createStatement();
                stmt.execute("DELETE FROM appointment WHERE appointmentId = "+ appt);
            }catch(SQLException e){
                JOptionPane.showMessageDialog(null, "Unable to delete appointment.", "Alert", JOptionPane.ERROR_MESSAGE);
            }
            closeConnection();
        }catch(SQLException | ClassNotFoundException e){
            JOptionPane.showMessageDialog(null, "Verify internet connection.", "Alert", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    //CN Retrieve an existing appointment by using the appointmentId
    public ResultSet getAppointmentById (int apptId){
        ResultSet rs = null;
        try{
            makeConnection();
                try{
                    Statement stmt = connect.createStatement();
                    rs = stmt.executeQuery("SELECT title, description, location, contact, type, url, start, end, customer.customerName "
                            + "FROM appointment LEFT JOIN customer ON appointment.customerId = customer.customerId "
                            + "WHERE appointment.appointmentId = \"" + apptId + "\"");
                    return rs;
                }catch(SQLException e){
                    JOptionPane.showMessageDialog(null, "Unable to fetch appointment from database.", "Alert", JOptionPane.ERROR_MESSAGE);
                }
            closeConnection();
        }catch(SQLException | ClassNotFoundException e){
            JOptionPane.showMessageDialog(null, "Verify internet connection.", "Alert", JOptionPane.ERROR_MESSAGE);
        }
        return rs;
    }
    
    //CN Retrieve all appointments of a certain type, and order by type
    public ResultSet appointmentsByType() {
        ResultSet rs = null;
        try{
            makeConnection();
                try{
                    Statement stmt = connect.createStatement();
                    rs = stmt.executeQuery("SELECT type, title, start, userId FROM appointment ORDER BY type");
                    return rs;
                }catch(SQLException e){
                    JOptionPane.showMessageDialog(null, "Unable to verify customer in the database.", "Alert", JOptionPane.ERROR_MESSAGE);
                }
            closeConnection();
        }catch(SQLException | ClassNotFoundException e){
            JOptionPane.showMessageDialog(null, "Verify internet connection.", "Alert", JOptionPane.ERROR_MESSAGE);
        }
        return rs;
    }
    
    //CN Retrieve all appointments by consultant and order by consultant
    public ResultSet appointmentByConsultant(){
        ResultSet rs = null;
        try{
            makeConnection();
                try{
                    Statement stmt = connect.createStatement();
                    rs = stmt.executeQuery("SELECT userId , title, start FROM appointment ORDER BY userId");
                    return rs;
                }catch(SQLException e){
                    JOptionPane.showMessageDialog(null, "Unable to get appointments by consultants.", "Alert", JOptionPane.ERROR_MESSAGE);
                }
            closeConnection();
        }catch(SQLException | ClassNotFoundException e){
            JOptionPane.showMessageDialog(null, "Verify internet connection.", "Alert", JOptionPane.ERROR_MESSAGE);
        }
        return rs;
    }
    
    //CN Retrieve all appointments scheduled for today
    public ResultSet appointmentsForToday(){
        ResultSet rs = null;
        
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	LocalDateTime now = LocalDateTime.now();
        String localDate = dtf.format(now);
        
        String tester = "SELECT title, description, location, type, start FROM appointment WHERE start LIKE \"%" + localDate + "%\" ORDER BY start";
        
        try{
            makeConnection();
                try{
                    Statement stmt = connect.createStatement();
                    rs = stmt.executeQuery("SELECT title, description, start, location, type FROM appointment WHERE start LIKE \"%" + localDate + "%\" ORDER BY start");
                    return rs;
                }catch(SQLException e){
                    JOptionPane.showMessageDialog(null, "Unable to get appointments by date.", "Alert", JOptionPane.ERROR_MESSAGE);
                }
            closeConnection();
        }catch(SQLException | ClassNotFoundException e){
            JOptionPane.showMessageDialog(null, "Verify internet connection.", "Alert", JOptionPane.ERROR_MESSAGE);
        }
        return rs;
    }
    
    //CN Retirve a list of all current appointments
    public ResultSet listAppointments (){
        ResultSet rs = null;
        
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	LocalDateTime now = LocalDateTime.now();
        String localDate = dtf.format(now);
        
        try{
            makeConnection();
                try{
                    Statement stmt = connect.createStatement();
                    rs = stmt.executeQuery("SELECT appointmentId, start, title, description, location, contact, end, url FROM appointment "
                            + "WHERE start >=\"" + localDate + "\" ORDER BY start");
                    return rs;
                }catch(SQLException e){
                    JOptionPane.showMessageDialog(null, "Unable to load appointments to table.", "Alert", JOptionPane.ERROR_MESSAGE);
                }
            closeConnection();
        }catch(SQLException | ClassNotFoundException e){
            JOptionPane.showMessageDialog(null, "Verify internet connection.", "Alert", JOptionPane.ERROR_MESSAGE);
        }
        return rs;
    }
    
    //CN Double check that the appointment being made does not overlap with another appointment in the database already.
    public boolean overlappingAppointment(String startTime) {
        boolean overlapping = false;
        ResultSet rs = null;
            try{
            makeConnection();
                try{
                    Statement stmt = connect.createStatement();
                    rs = stmt.executeQuery("SELECT * FROM appointment WHERE \"" + startTime + "\" BETWEEN  start and end");
                    if (rs.first()){
                        overlapping = true;
                    }
                    return overlapping;
                }catch(SQLException e){
                    JOptionPane.showMessageDialog(null, "Unable to check for overlapping appointment times.", "Alert", JOptionPane.ERROR_MESSAGE);
                }
            closeConnection();
        }catch(SQLException | ClassNotFoundException e){
            JOptionPane.showMessageDialog(null, "Verify internet connection.", "Alert", JOptionPane.ERROR_MESSAGE);
        }
        return overlapping;
    }
    
    //CN Checks to see if there is an appointment in the next 15 minutes and warns user if there is.
    public void apptWarning(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	LocalDateTime now = LocalDateTime.now();
        LocalDateTime plusFifteen = LocalDateTime.now().plusMinutes(15);
        String localDate = dtf.format(now), localToUTC = null;
        String localPlusFifteen = dtf.format(plusFifteen), localPlusFifteenToUTC = null;
        
        Timezone tz = new Timezone();
        try{
            localToUTC = tz.localToUTC(localDate);
            localPlusFifteenToUTC = tz.localToUTC(localPlusFifteen);
        }catch(ParseException e){
            JOptionPane.showMessageDialog(null, "Unable to check appointment in the next 15 minutes", "Alert", JOptionPane.ERROR_MESSAGE);
        }
        
        try{
            makeConnection();
                try{
                    Statement stmt = connect.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * FROM appointment "
                            + "WHERE start BETWEEN \"" + localToUTC + "\" AND \"" + localPlusFifteenToUTC + "\"");
                    if (rs.first()) {
                        JOptionPane.showMessageDialog(null, "You have an appointment scheculed within the next 15 minutes.", "Alert", JOptionPane.ERROR_MESSAGE);
                    }
                }catch(SQLException e){
                    JOptionPane.showMessageDialog(null, "Unable to check appointment in the next 15 minutes", "Alert", JOptionPane.ERROR_MESSAGE);
                }
            closeConnection();
        }catch(SQLException | ClassNotFoundException e){
            JOptionPane.showMessageDialog(null, "Verify internet connection.", "Alert", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    //CN Retrieve all appointments over the next week.
    public ResultSet getWeekCalendar(){
        ResultSet rs = null;
        LocalDate localDate = LocalDate.now();
        LocalDate localPlusWeek = LocalDate.now().plusWeeks(1);
        try{
            makeConnection();
                try{
                    Statement stmt = connect.createStatement();
                    rs = stmt.executeQuery("SELECT customer.customerName, appointment.title, appointment.description, appointment.location, appointment.start \n" +
                                            "FROM appointment LEFT JOIN customer ON appointment.customerId = customer.customerId \n" +
                                            "WHERE start BETWEEN \"" + localDate + "\" AND \"" + localPlusWeek + "\" ORDER BY start;");
                    return rs;
                }catch(SQLException e){
                    JOptionPane.showMessageDialog(null, "Unable to get weekly calendar.", "Alert", JOptionPane.ERROR_MESSAGE);
                }
            closeConnection();
        }catch(SQLException | ClassNotFoundException e){
            JOptionPane.showMessageDialog(null, "Verify internet connection.", "Alert", JOptionPane.ERROR_MESSAGE);
        }
        return rs;
    }
    
    //CN Retrieve all appointments over the next month
    public ResultSet getMonthCalendar(){
        ResultSet rs = null;
        LocalDate localDate = LocalDate.now();
        LocalDate localPlusMonth = LocalDate.now().plusMonths(1);

        try{
            makeConnection();
                try{
                    Statement stmt = connect.createStatement();
                    rs = stmt.executeQuery("SELECT customer.customerName, appointment.title, appointment.description, appointment.location, appointment.start \n" +
                                            "FROM appointment LEFT JOIN customer ON appointment.customerId = customer.customerId \n" +
                                            "WHERE start BETWEEN \"" + localDate + "\" AND \"" + localPlusMonth + "\" ORDER BY start;");
                    return rs;
                }catch(SQLException e){
                    JOptionPane.showMessageDialog(null, "Unable to get monthly calendar.", "Alert", JOptionPane.ERROR_MESSAGE);
                }
            closeConnection();
        }catch(SQLException | ClassNotFoundException e){
            JOptionPane.showMessageDialog(null, "Verify internet connection.", "Alert", JOptionPane.ERROR_MESSAGE);
        }
        return rs;
    }
}
