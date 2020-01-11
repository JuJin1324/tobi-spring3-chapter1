package study.tobi.spring3.chapter1.user.db.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 08/09/2019
 */

@Getter
@Setter
@NoArgsConstructor
public class User {
    String id;
    String name;
    String password;
}
