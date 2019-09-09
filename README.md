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

### 템플릿 메서드 패턴
* 슈퍼클래스에 기본적인 로직의 흐름을 만들고, 그 기능의 일부를 추상 메서드나 오버라이딩 가능한 protected 메서드 등으로  
만들어서 서브클래스에서 필요에 맞게 구현하도록하는 디자인 패턴

* 서브클래스에서 구체적인 오브젝트 생성 방법을 결정하게 하는 것을 <b>팩토리 메서드 패턴</b>이라고 한다.  

