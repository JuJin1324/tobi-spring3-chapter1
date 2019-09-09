package study.tobi.spring3.chapter1;

import study.tobi.spring3.chapter1.user.User;
import study.tobi.spring3.chapter1.user.dao.DUserDao;
import study.tobi.spring3.chapter1.user.dao.UserDao;

import java.sql.SQLException;

/**
 * Created by Yoo Ju Jin(yjj@hanuritien.com)
 * Created Date : 08/09/2019
 */
public class Main {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
//        UserDao dao = new UserDao();
        UserDao dao = new DUserDao();

        User user = new User();
        user.setId("whiteship");
        user.setPassword("1234");
        user.setName("백기선");

        dao.add(user);

        System.out.println(user.getId() + " 등록 성공");

        User user2 = dao.get(user.getId());
        System.out.println("user2.getId() = " + user2.getId());
        System.out.println("user2.getPassword() = " + user2.getPassword());
        System.out.println("user2.getName() = " + user2.getName());

        System.out.println(user2.getId() + " 조회 성공");
    }
}
