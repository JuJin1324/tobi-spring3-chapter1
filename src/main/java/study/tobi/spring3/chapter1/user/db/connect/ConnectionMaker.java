package study.tobi.spring3.chapter1.user.db.connect;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Yoo Ju Jin(yjj@hanuritien.com)
 * Created Date : 09/09/2019
 */
public interface ConnectionMaker {

    Connection makeConnection() throws ClassNotFoundException, SQLException;
}
