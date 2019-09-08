package study.tobi.spring3.chapter1;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Yoo Ju Jin(yjj@hanuritien.com)
 * Created Date : 08/09/2019
 */

@Data
@NoArgsConstructor
public class User {
    String id;
    String name;
    String password;
}
