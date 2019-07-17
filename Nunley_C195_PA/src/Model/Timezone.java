/*
 * CN C195 Performance Assessment
 * Timezone is used to convert all local times to UTC
 * and convert all UTC back to local times.
 */
package Model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.TimeZone;


public class Timezone {
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    //CN Convert local time string to UTC time
    public String localToUTC(String input) throws ParseException {       
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        TimeZone tz = TimeZone.getDefault();
        formatter.setTimeZone(tz);
        
        Date date = formatter.parse(input);        
        
        SimpleDateFormat sdfUTC = new SimpleDateFormat(DATE_FORMAT);
        TimeZone tzUTC = TimeZone.getTimeZone("UTC");
        sdfUTC.setTimeZone(tzUTC);

        String sDateInUTC = sdfUTC.format(date); // Convert to String first

        return sDateInUTC;
    }
    
    //CN Convert all UTC time to local timezone of user
    public String UTCtoLocal(String input) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);        
        TimeZone tz = TimeZone.getTimeZone("UTC");
        formatter.setTimeZone(tz);
        
        Date date = formatter.parse(input);
        
        SimpleDateFormat sdfLocal = new SimpleDateFormat(DATE_FORMAT);                       
        TimeZone tzCurrent = TimeZone.getDefault();
        sdfLocal.setTimeZone(tzCurrent);
        
        String sDateInLocal = sdfLocal.format(date);
        
        return sDateInLocal;
    }
}