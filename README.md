# tobi-spring3-chapter1
토비의 스프링 3.1 Vol.1 스프링 이해와 원리

## Database
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

