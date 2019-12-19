package study.tobi.spring3.chapter1.user.db.connect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Yoo Ju Jin(yjj@hanuritien.com)
 * Created Date : 2019-09-09
 */
public class NConnectionMaker implements ConnectionMaker {

    private static final String MYSQL_URL = "jdbc:mysql://192.168.0.95:3306/spring3?useSSL=false";

    public Connection makeConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        return DriverManager.getConnection(MYSQL_URL, "scott", "tiger");
    }
}
