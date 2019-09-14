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
        return new UserDao(connectionMaker());
    }

    @Bean
    public AccountDao accountDao() {
        return new AccountDao(connectionMaker());
    }

    @Bean
    public MessageDao messageDao() {
        return new MessageDao(connectionMaker());
    }

    @Bean
    public ConnectionMaker connectionMaker() {
        return new DConnectionMaker();
    }
}
