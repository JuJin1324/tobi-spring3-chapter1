# 1장 오브젝트와 의존관계
토비의 스프링 3.1 Vol.1 스프링 이해와 원리

## 기초 셋팅
### Maven
```xml
    <properties>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
        <org.springframework-version>3.0.7.RELEASE</org.springframework-version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>${org.springframework-version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-jdbc</artifactId>
            <version>${org.springframework-version}</version>
        </dependency>

        <!-- https://mvnrepository.com/artifact/cglib/cglib -->
        <dependency>
            <groupId>cglib</groupId>
            <artifactId>cglib</artifactId>
            <version>2.1_3</version>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.8</version>
            <scope>provided</scope>
        </dependency>

        <!-- https://mvnrepository.com/artifact/mysql/mysql-connector-java -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.48</version>
        </dependency>
    </dependencies>
```

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

## 관심사의 분리
### DAO 코드를 통한 예시
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
* 위의 코드에서 DAO의 주요 관심사는 '관심사2 : 쿼리를 통한 DB처리'임으로 '관심사1 : DB연결' 처리에 중점

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

## 전략 패턴
* 전략 패턴이란 자신의 기능 맥락(context)에서 필요에 따라 변경이 필요한 알고리즘을 인터페이스를 통해  
통째로 외부로 분리시킴, 이를 구현한 구체적인 알고리즘 클래스를 필요에 따라 바꿔서 사용할 수 있게 하는 디자인패턴  
* 기능 맥락(context) : `UserDao`
* 변경이 필요한 알고리즘 인터페이스 : `ConnectionMaker`
* 구체적인 알고리즘 클래스 : `DConnectionMaker`, `NConnectionMaker`
* 클라이언트 : `UserDaoTest`

### 문제점
* 클라이언트인 `UserDaoTest` 의 관심사는 `UserDao`가 정상 동작하는지 테스트하는 것에 있지만
현재는 `UserDao` 와 `ConnectionMaker` 가 연결돼서 사용될 수 있도록 관계를 맺어주는 관심사가 추가로 있다.

### 팩토리
* 팩토리 클래스는 객체의 생성 방법을 결정하고 그렇게 만들어진 오브젝트를 돌려주는 것

## Ioc(제어의 역전)
### 일반적 프로그래밍 구조
* main 메서드와 같이 프로그램이 시작되는 지점에서 사용할 오브젝트를 결정 및 생성 후 만들어진 오브젝트의 메서드를 사용하는 식의 작업이 반복  
모든 오브젝트가 자신이 사용할 클래스를 결정하고 언제 어떻게 오브젝트를 만들지 스스로 관장

### 제어의 역전
* 프로그램의 시작을 담당하는 main 메서드와 같은 엔트리 포인트를 제외하고 모든 오브젝트는  
위임받은 제어 권한을 갖는 특별한 오브젝트에 의해 결정되고 만들어진다.  
여기서는 `DaoFactory` 클래스의 오브젝트에 의해서 `UserDao` 클래스가 사용할 `ConnectionMaker`를 결정하며  
`UserDaoTest`의 main 메서드에서 `UserDao`를 생성할 때도 `DaoFactory` 클래스를 통해서 만들어야한다.

### 스프링의 IoC
* 스프링이 제어권을 가지고 직접 만들고 관계를 부여하는 오브젝트를 <b>빈(bean)</b>이라고 부른다.
* 스프링에서 빈의 생성과 관계설정 같은 제어를 담당하는 IoC 오브젝트를 <b>빈 팩토리</b>라고 부른다.
* 빈 팩토리 = 빈의 생성을 담당하는 클래스
* 애플리케이션 컨텍스트 = 빈 팩토리 + 스프링에서 제공하는 추가 기능,   
다른 이름으로 컨테이너, IoC 컨테이너 또는 스프링 컨테이너라고도 부름
* Default Bean 메서드 이름 = Bean 이름

### 빈(bean)
* 빈 또는 빈 오브젝트는 스프링이 IoC(제어의 역전) 방식으로 관리하는 오브젝트,  
제어를 사용자가 아닌 스프링에 위임하였음으로 제어의 역전  
스프링이 직접 그 생성과 제어를 담당하는 오브젝트만이 빈(bean)

### 싱글톤
* 싱글톤 패턴 오브젝트의 단점 - 
  - private 생성자톨 갖고 있기 때문에 상속할 수 없다.
  - 싱글톤은 테스트하기가 힘들다.
  - 서버환경에서는 싱글톤이 하나만 만들어지는 것을 보장하지 못한다.
  - 싱글톤의 사용은 전역 상태를 만들 수 있기 때문에 바람직하지 못하다.  
  
* 애플리케이션 컨텍스트는 빈을 싱글톤으로 저장하고 관리하는 <b>싱글톤 레지스트리</b>이다.  
단, 애플리케이션 컨텍스트의 장점 은 스태틱 메소드와 private 생성지를 시용해야 하는  
비정상적인 클래스가 아니라 평범한 자바 클래스를 싱글톤으로 활용하게 해준다는 점이다.  
스프링이 빈을 싱글톤으로 만드 는 것은 결국 오브젝트의 생성 방법을 제어하는 IoC 컨테이너로서의 역할이다.

* 애플리케이션 컨택스트를 통한 싱글톤 빈을 사용시 빈 클래스의 멤버 변수에 상태를 저장하지 않는 무상태 방식으로  
만들어야한다. 멀티스레드 환경시 상태 저장하는 변수를 가질 시에 값이 엉망이 될 수 있기 때문이다.  
자신이 사용하는 다른 싱글톤 빈을 저장하는 용도라면 인스턴스 변수를 사용해도 좋다.  
싱글톤 빈의 경우 한 번 초기화 이후에는 수정되지 않기 때문에 멀티스레드 환경에서 사용해도 문제가 되지 않기때문이다.  

* 스프링 빈의 스코프  
스프링 빈의 기본 스코프는 싱글톤이며 싱글톤 스코프는 컨테이너 내에 한 개의 오브젝트만 만들어져서,  
강제로 제거하지 않는 한 스프링 컨테이너가 존재하는 동안 계속 유지된다.  

### 의존관계 주입(DI : Dependency injection)
* 설계 모델의 관점에서의 의존관계  
`UserDao` -->(의존) `<<interface>>ConnectionMaker`

* 런타임 시에 오브젝트 사이에서 만들어지는 의존관계 : <b>런타임 의존관계</b>  
`UserDao` -->(실제 오브젝트 의존) `DConnectionMaker` (제3자인 `DaoFactory` 개입)  
실제로 의존하는 오브젝트를 <b>의존 오브젝트</b>

* 의존관계 주입의 필요 조건
  - 클래스나 코드 레벨에서 런타임 시점의 의존관계(직접 클래스 주입)가 드러나지 않는다. 
    그러기 위해서는 인터페이스에만 의존하고 있어야한다.
  - 런타임 시점의 의존관계는 컨테이너나 팩토리 같은 제 3의 존재가 결정한다.
  - 의존관계는 사용할 오브젝트에 대한 레퍼런스를 외부에서 제공(주입)해줌으로써 만들어진다.
  
* 정리  
DI(dependency injection) 의존 주입이란 클래스 레벨에서 구체적인 클래스가 아닌 인터페이스와 의존관계를 가지며  
런타임 시점에 실제로 의존하는 의존 오브젝트(구체적인 클래스의 오브젝트)를 컨테이너나 팩토리 같은 제 3의 존재로 주입받는 것

## XML 설정
### Configuration 클래스 XML로 전환
설명       |   클래스    |  XML  
--------- | --------- | ---------
빈 설정파일 | @Configuration | <beans>
빈 선언 | @Bean methodName() | <bean id="methodName"
빈 클래스 | return new BeanClass(); | class="package.name.BeanClass">
빈 setter 메서드 | setFieldName(fieldObject); | <property name="fieldName" ref="fieldObject">

* Property 즉 클래스의 멤버변수(Field)의 이름은 바뀔 수 있는 실제 의존하는 클래스의 이름보다는   
인터페이스의 이름을 따르는 것이 좋다.

## 정리

### 개방 폐쇠 원칙
* 자신의 책임 자체가 변경되는 경우 외에는 불필요한 변화가 발생하지 않도록 막아주고(폐쇠),  
자신이 사용하는 외부 오브젝트의 기능은 자유롭게 확장하거나 별경할 수 있게 한다.(개방)

### 높은 응집도, 낮은 결합도
* 한쪽의 기능 변화가 다른 쪽의 변경을 요구하지 않아도 되면 낮은 결합도,   
자신의 책임과 관심사에만 순수하게 집중하면 높은 응집도

### Bean
* 스프링이 제어권을 가지고 직접 만들고 관계를 부여하는 오브젝트를 <b>빈(bean)</b>이라고 부른다.

### DI(의존성 주입)
* DI(dependency injection) 의존 주입이란 클래스 레벨에서 구체적인 클래스가 아닌 인터페이스와 의존관계를 가지며  
런타임 시점에 실제로 의존하는 의존 오브젝트(구체적인 클래스의 오브젝트)를 컨테이너나 팩토리 같은 제 3의 존재로 주입받는 것

### IoC(제어의 역전)
* 오브젝트가 생성되고 여타 오브젝트와 관계를 맺는 작업의 제어권을 별도의 오브젝트 팩토리를 만들어 넘긴다.
(해당 팩토리 클래스를 IoC 컨테이너, Application Context 혹은 빈 팩토리 라고도 부른다)

### 스프링이란?
* 어떻게 오브젝트가 설계되고, 만들어지고, 어떻게 관계를 맺고 사용되는지에 관심을 갖는 프레임워크
