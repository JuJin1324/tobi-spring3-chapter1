package study.tobi.spring3.chapter1.user.dao;

import study.tobi.spring3.chapter1.user.User;

import java.sql.*;

/**
 * Created by Yoo Ju Jin(yjj@hanuritien.com)
 * Created Date : 08/09/2019
 */

public abstract class UserDao {
//    private static final String MYSQL_URL = "jdbc:mysql://192.168.0.4:3306/spring3";
    protected static final String MYSQL_URL = "jdbc:mysql://localhost:3306/spring3?useSSL=false";

    public void add(User user) throws ClassNotFoundException, SQLException {
        Connection c = getConnection();

        PreparedStatement ps = c.prepareStatement("insert into users(id, name, password) values (?, ?, ?)");
        ps.setString(1, user.getId());
        ps.setString(2, user.getName());
        ps.setString(3, user.getPassword());

        ps.executeUpdate();

        ps.close();
        c.close();
    }

    public abstract Connection getConnection() throws ClassNotFoundException, SQLException;
//    private Connection getConnection() throws ClassNotFoundException, SQLException {
//        Class.forName("com.mysql.jdbc.Driver");
//        return DriverManager.getConnection(MYSQL_URL, "scott", "tiger");
//    }

    public User get(String id) throws ClassNotFoundException, SQLException {
        Connection c = getConnection();
        PreparedStatement ps = c.prepareStatement("select id, name, password from users where id = ?");
        ps.setString(1, id);

        ResultSet rs = ps.executeQuery();
        rs.next();
        User user = new User();
        user.setId(rs.getString("id"));
        user.setName(rs.getString("name"));
        user.setPassword(rs.getString("password"));

        rs.close();
        ps.close();
        c.close();

        return user;
    }
}
