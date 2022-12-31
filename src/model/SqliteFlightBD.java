
package model;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.time.DayOfWeek;
import java.time.LocalTime;
import static java.util.Collections.*;
import java.util.Iterator;


public class SqliteFlightBD implements FlightIterable  {
    
    public final Connection conn;
    public final utils utils;
    
    public SqliteFlightBD(File file) throws SQLException {
        this.conn = DriverManager.getConnection("jdbc:sqlite:"+file.getAbsolutePath());
        this.utils = new utils();
    }
    
    private Flight getData(ResultSet rs) {
        if (!utils.next(rs)) return null;
        return new Flight(
            DayOfWeek.of(utils.getInt(rs,"DAY_OF_WEEK")),
            utils.timeOf(utils.getInt(rs,"DEP_TIME")),
            utils.getInt(rs,"DEP_DELAY"),
            utils.timeOf(utils.getInt(rs,"ARR_TIME")),
            utils.getInt(rs,"ARR_DELAY"),
            utils.getInt(rs,"AIR_TIME"),
            utils.getInt(rs,"DISTANCE"),
            utils.getInt(rs,"CANCELLED") == 1,
            utils.getInt(rs,"DIVERTED") == 1
        );

    }

    @Override
    public Iterable<Flight> flights() {
        return new Iterable<Flight>() {
            
            @Override
            public Iterator<Flight> iterator() {
                try {
                    return createIterator();
                }
                catch (SQLException EX) {
                    return emptyIterator();
                }
            }

            private Iterator<Flight> createIterator() throws SQLException {
                ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM flights");
                return new Iterator<Flight>(){
                    Flight firstFlight = getData(rs);
                    
                    @Override
                    public boolean hasNext(){
                        return firstFlight!=null;
                    }
                    
                    @Override
                    public Flight next(){
                        Flight flight= firstFlight;
                        firstFlight = getData(rs);
                        return flight;
                    }                
                };
                        
            };
        };
    }
    

    private class utils{
        
        private int getInt(ResultSet rs, String field) {
            try {
                return rs.getInt(field);
            } catch (SQLException ex) {
                return 0;
            }

        }

        private LocalTime timeOf(int d) {
            return LocalTime.of(d / 100 % 24, d%100);
        }

        private boolean next(ResultSet rs) {
            try {
                boolean next = rs.next();
                if (next) return true;
                rs.close();            
            } catch (SQLException ex) {
            }
            return false;
        }
    
    }

}
