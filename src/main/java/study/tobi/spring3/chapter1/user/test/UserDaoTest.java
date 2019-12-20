package study.tobi.spring3.chapter1.user.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import study.tobi.spring3.chapter1.user.db.entity.User;
import study.tobi.spring3.chapter1.user.db.configure.DaoFactory;
import study.tobi.spring3.chapter1.user.db.access.UserDao;

import java.sql.SQLException;

/**
 * Created by Yoo Ju Jin(yjj@hanuritien.com)
 * Created Date : 08/09/2019
 */
public class UserDaoTest {

    private static ApplicationContext context;

    public static void main(String[] args) throws SQLException {
//        context = new AnnotationConfigApplicationContext(DaoFactory.class);
        context = new ClassPathXmlApplicationContext("applicationContext.xml");

        userDaoTestUsingApplicationContext();
//        compareDaoCreatedByDaoFactoryDirectly();
        compareDaoCreatedByApplicationContext();
    }

    private static void compareDaoCreatedByApplicationContext() {

        UserDao dao3 = context.getBean("userDao", UserDao.class);
        UserDao dao4 = context.getBean("userDao", UserDao.class);

        System.out.println("dao3 = " + dao3);
        System.out.println("dao4 = " + dao4);
        System.out.println("dao3 == dao4 is equal? : " + (dao3 == dao4));
    }

    private static void userDaoTestUsingApplicationContext() throws SQLException {

        UserDao dao = context.getBean("userDao", UserDao.class);

        User user = new User();
        user.setId("whiteship");
        user.setPassword("1234");
        user.setName("백기선");

//        dao.add(user);
//        System.out.println(user.getId() + " 등록 성공");

        User user2 = dao.get(user.getId());
        System.out.println("user2.getId() = " + user2.getId());
        System.out.println("user2.getPassword() = " + user2.getPassword());
        System.out.println("user2.getName() = " + user2.getName());

        System.out.println(user2.getId() + " 조회 성공");
    }

    private static void compareDaoCreatedByDaoFactoryDirectly() throws SQLException {

        DaoFactory daoFactory = new DaoFactory();
        UserDao dao1 = daoFactory.userDao();
        UserDao dao2 = daoFactory.userDao();

        System.out.println("dao1 = " + dao1);
        System.out.println("dao2 = " + dao2);
        System.out.println("dao1 == dao2 is equal? : " + (dao1 == dao2));

        User user = dao1.get("whiteship");
        System.out.println("user = " + user);
    }
}
