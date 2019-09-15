package study.tobi.spring3.chapter1.user.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Yoo Ju Jin(yjj@hanuritien.com)
 * Created Date : 2019-09-12
 *
 */

@Configuration
public class DaoFactory {

    @Bean
    public UserDao userDao() {
        UserDao userDao = new UserDao();
        userDao.setConnectionMaker(connectionMaker());

        return userDao;
    }

    @Bean
    public AccountDao accountDao() {
        AccountDao accountDao = new AccountDao();
        accountDao.setConnectionMaker(connectionMaker());
        return accountDao;
    }

    @Bean
    public MessageDao messageDao() {
        MessageDao messageDao = new MessageDao();
        messageDao.setConnectionMaker(connectionMaker());
        return messageDao;
    }

    @Bean
    public ConnectionMaker connectionMaker() {
        return new DConnectionMaker();
    }
}
