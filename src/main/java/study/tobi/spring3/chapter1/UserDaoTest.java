package study.tobi.spring3.chapter1;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import study.tobi.spring3.chapter1.user.User;
import study.tobi.spring3.chapter1.user.dao.DaoFactory;
import study.tobi.spring3.chapter1.user.dao.UserDao;

import java.sql.SQLException;

/**
 * Created by Yoo Ju Jin(yjj@hanuritien.com)
 * Created Date : 08/09/2019
 */
public class UserDaoTest {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
//        userDaoTestUsingApplicationContext();
        compareDaoCreatedByDaoFactoryDirectly();
        compareDaoCreatedByApplicationContext();
    }

    private static void compareDaoCreatedByApplicationContext() {
        ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);

        UserDao dao3 = context.getBean("userDao", UserDao.class);
        UserDao dao4 = context.getBean("userDao", UserDao.class);

        System.out.println("dao3 = " + dao3);
        System.out.println("dao4 = " + dao4);
        System.out.println("dao3 == dao4 is equal? : " + (dao3 == dao4));
    }

    private static void userDaoTestUsingApplicationContext() throws ClassNotFoundException, SQLException {
        ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);

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

    private static void compareDaoCreatedByDaoFactoryDirectly() {
        DaoFactory daoFactory = new DaoFactory();
        UserDao dao1 = daoFactory.userDao();
        UserDao dao2 = daoFactory.userDao();

        System.out.println("dao1 = " + dao1);
        System.out.println("dao2 = " + dao2);
        System.out.println("dao1 == dao2 is equal? : " + (dao1 == dao2));
    }
}
