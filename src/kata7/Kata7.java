package kata7;


import java.sql.SQLException;
import service.Server;
public class Kata7 {

    public static void main(String[] args) throws SQLException {
        Server server = new Server(8080);
        server.start();
    }
    
}
