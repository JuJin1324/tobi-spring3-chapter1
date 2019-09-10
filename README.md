# tobi-spring3-chapter1
토비의 스프링 3.1 Vol.1 스프링 이해와 원리

## 기초 셋팅
### MySQL 계정 및 스키마 생성
```mysql
(도커 사용시 mysql 접속)
> docker exec -it [컨테이너명] bash
> mysql -u root -p

* 계정 생성
> create user 'scott'@'%' identified by 'tiger';

* 권한 주기
> grant all privileges on *.* to 'scott'@'%';
> flush privileges;

* spring3 스키마 생성
> create database spring3;

* 생성 확인
> show databases;
```

### DB Table
* USERS 테이블
```mysql
create table users(
    id varchar(10) primary key ,
    name varchar(20) not null ,
    password varchar(10) not null
);
```

## 배경 지식
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

* 훅(hook) 메서드 : 슈퍼클래스에서 디폴트 기능을 정의해두거나 비워뒀다가 서브클래스에서 선택적으로 오버라이드할 수 있도록 만들어둔 메서드  
예시)
```java
protected void emptiedhookMethod() {}
protected void defaulthookMethod() {
    ...(디폴트 기능)
}
public void abstractMethod(); -> 서브클래스가 반드시 구현해야하는 추상 메서드
```

## 주요 개념 포인트
### 관심사의 분리
* DAO 코드를 통한 예시)
```java
public void add(User user) throws ClassNotFoundException, SQLException {
     /* 관심사1 : DB 연결 */
     Class.forName("com.mysql.jdbc.Driver");
     DriverManager.getConnection(MYSQL_URL, "scott", "tiger");

     /* 관심사2 : 쿼리를 통한 DB처리 */
     PreparedStatement ps = c.prepareStatement("insert into users(id, name, password) values (?, ?, ?)");
     ps.setString(1, user.getId());
     ps.setString(2, user.getName());
     ps.setString(3, user.getPassword());
     ps.executeUpdate();

     /* 관심사3 : 사용한 리소스 닫기 */
     ps.close();
     c.close();
}
```
위의 코드에서 DAO의 주요 관심사는 '관심사2 : 쿼리를 통한 DB처리'임으로 '관심사1 : DB연결' 처리에 중점

### 처리 1
* N사와 D사가 사용하는 DB가 다를 수 있음으로 '관심사1 : DB연결' 부분을 추상메서드로 변경 이후  
상속을 통해서 각 DB에 맞게 N사와 D사가 구현할 수 있도록함.

### 처리 2
* 상속으로 인한 단점(다중상속 불가, 부모클래스와의 관계가 깊어 부모클래스의 기능 추가시 자식 클래스에 영향 미침 등)  
으로 인해 '관심사1 : DB연결' 기능을 클래스로 분리
```java
public class SimpleConnectionMaker {
    public Connection makeNewConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/spring3?useSSL=false", "scott", "tiger");
    }
}
```

### 처리 3
* 처리 2에서 클래스로 분리한 경우 N사와 D사의 DB에 맞게 처리 불가능 = 클래스 사이의 관계  
클래스 사이의 관계 -> 오브젝트 사이의 관계로 변경을 위한 인터페이스 도입
* DB Connection 클래스를 인터페이스로 변경 및 DAO 의 생성자에서  
DB Connection 인터페이스 구현 오브젝트를 받아 처리함으로써 DAO는 DB Connection 관심사와 분리

