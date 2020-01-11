package study.tobi.spring3.chapter1.user.test;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import study.tobi.spring3.chapter1.user.db.entity.User;
import study.tobi.spring3.chapter1.user.db.connect.CountingConnectionMaker;
import study.tobi.spring3.chapter1.user.db.configure.CountingDaoFactory;
import study.tobi.spring3.chapter1.user.db.access.UserDao;

import java.sql.SQLException;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2019-09-15
 */


public class UserDaoConnectionCountingTest {

    /*
     * UserDao 클래스에서
     * ConnectionMaker -> DataSource
     * 변경으로 인해 더이상 사용 안함.
     */
    public static void main(String[] args) throws SQLException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CountingDaoFactory.class);
        UserDao dao = context.getBean("userDao", UserDao.class);

        CountingConnectionMaker ccm = context.getBean("connectionMaker", CountingConnectionMaker.class);
        System.out.println("Connection counter : " + ccm.getCounter());

        User user = dao.get("whiteship");
        System.out.println("user = " + user);
        System.out.println("Connection counter : " + ccm.getCounter());

//        User user1 = new User();
//        user1.setId("test1");
//        user1.setName("테스트");
//        user1.setPassword("test");
//        dao.add(user1);
//        System.out.println("Connection counter : " + ccm.getCounter());

        User test1 = dao.get("test1");
        System.out.println("test1 = " + test1);
        System.out.println("Connection counter : " + ccm.getCounter());

    }
}
