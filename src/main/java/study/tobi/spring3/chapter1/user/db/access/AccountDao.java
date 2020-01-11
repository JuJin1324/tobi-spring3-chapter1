package study.tobi.spring3.chapter1.user.db.access;

import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.sql.DataSource;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2019-09-12
 */

@NoArgsConstructor
@Setter
public class AccountDao {

    private DataSource dataSource;
}
