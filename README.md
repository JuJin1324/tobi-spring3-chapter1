# tobi-spring3-chapter1
토비의 스프링 3.1 Vol.1 스프링 이해와 원리

### DB Table
* USERS 테이블
```mysql
create table users(
    id varchar(10) primary key ,
    name varchar(20) not null ,
    password varchar(10) not null
);
```
### Class.forName()
* 인터페이스 드라이버(interface driver)를 구현(implements)하는 작업으로,   
Class 클래스의 forName() 메소드를 사용해서 드라이버를 로드한다.   
forName(String className) 메소드는 문자열로 주어진 클래스나 인터페이스 이름을  
객체로 리턴한다.  
* MySQL 드라이버 로딩
```java
Class.forName("com.mysql.jdbc.Driver");
```
