# chapter1
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
## MySQL 서버 설치 및 실행
### 옵션1. Docker를 이용한 mysql 서버 생성 및 실행
* mysql 생성 - $ `docker run --name mysql-db -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root -d mysql`
* mysql 접속 - $ `docker exec -it [컨테이너명] bash`
* 이후 `MySQL 계정 및 스키마 생성`을 따르면 됨.

### 옵션2. 패키지 관리자를 이용한 mysql 서버 생성 및 실행
* 서버 설치 
    - macOS - $ `brew install mysql`
    - ubuntu - $ `sudo apt-get install -y mysql-server mysql-client`
* 이후 `MySQL 계정 및 스키마 생성`을 따르면 됨.

### MySQL 계정 및 스키마 생성
```bash
## mysql 접속 
$ mysql -u root -p

## 계정 생성
mysql> create user 'scott'@'%' identified by 'tiger';

## 권한 주기
mysql> grant all privileges on *.* to 'scott'@'%';
mysql> flush privileges;

## spring3 스키마 생성
mysql> create database spring3;

## 생성 확인
mysql> show databases;
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

## 1 오브젝트와 의존관계
### 오브젝트의 생명주기(Life Cycle)
* 오브젝트 생성 -> 다른 오브젝트와 관계 맺음 -> 사용 -> 소멸의 과정에 대한 생각 필요 ==> 객체지향 설계
* 오브젝트를 목적에 따라 재활용하기 위한 설계 방법 => 디자인 패턴
* 소스코드를 개선/수정 해나가는 작업 => 리팩토링

## 1.1 DAO
* Data Access Object
* DB 테이블의 칼럼을 멤버변수로 선언하여 해당 테이블의 ROW를 가져오기 위한 객체

### 자바 빈(Java Bean)
* 자바 빈은 파라미터가 없는 디폴트 생성자를 가지고 있어야 한다.   
툴이나 프레임워크에서 리플렉션을 이용해 오브젝트를 생성하기 때문에 필요하다.
* 리플렉션 - 구체적인 클래스 타입을 알지 못해도, 그 클래스의 메소드, 타입, 변수들에 접근할 수 있도록 해주는 자바 API  
예시) 
* 아래 자바 리플렉션에서 사용할 User 클래스
```java
public class User {
    String id;
    String name;
    String password;
}
```
* 자바 리플렉션을 사용한 User 클래스의 필드 타입 및 필드 명 출력하기
```java
Class<?> clazz = User.class;  // Article의 Class를 가져온다.
Field[] fields = clazz.getDeclaredFields(); // Article의 모든 필드를 가져온다.

for (final Field field : fields) { // field의 type, name 출력
    System.out.printf("%s %s\n", field.getType(), field.getName());
}
```
* 출력 결과
```text
class java.lang.String id
class java.lang.String name
class java.lang.String password
```

### Class.forName()
* 인터페이스 드라이버(interface driver)를 구현(implements)하는 작업으로, Class 클래스의 `forName()` 메소드를 사용해서 드라이버를 로드한다. 
`forName(String className)` 메소드는 문자열로 주어진 클래스나 인터페이스 이름을 객체로 리턴한다.  
* MySQL 드라이버 로딩
```java
Class.forName("com.mysql.jdbc.Driver");
```

## 1.2 관심사의 분리
* 소프트웨어를 개발하면서 오브젝트에 대한 설계와 이를 구현하는 코드는 항상 변한다. 그래서 객체를 설계할 때 가장 염두해야할 사항은 미래의 변화를 대비하는 것이다.  
* 객체지향 기술은 추상화를 보다 효과적으로 구성할 수 있으며, 이를 자유롭고 편리하게 <b>변경, 발전, 확장</b>시킬 수 있다.
* 프로그래밍 기초 개념 중에 <b>관심사의 분리(Separation of Concerns)</b>라는 게 있다. 이를 객체지향에 적용해보면, 관심이 같은 것끼리는 하나의 객체 안으로 또는 친한 객체로 모이게 하고, 
관심이 다른 것은 간으한 한 따로 떨어져서 서로 영향을 주지 않도록 분리하는 것이라고 생각할 수 있다.

### DAO 코드를 통한 예시
```java
public void add(User user) throws ClassNotFoundException, SQLException {
     /* 관심사1 : DB 연결 */
     Class.forName("com.mysql.jdbc.Driver");
     Connection c = DriverManager.getConnection(MYSQL_URL, "scott", "tiger");

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
public User get(String id) throws SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        Connection c = DriverManager.getConnection(MYSQL_URL, "scott", "tiger");

        PreparedStatement ps = c.prepareStatement("select id, name, password from users where id = ?");
        ps.setString(1, id);

        ResultSet rs = ps.executeQuery();
        rs.next();
        User user = new User();
        user.setId(rs.getString("id"));
        user.setName(rs.getString("name"));
        user.setPassword(rs.getString("password"));

        rs.close();
        ps.close();
        c.close();

        return user;
    }
```
* 위의 코드에서 DAO의 주요 관심사는 `관심사2 : 쿼리를 통한 DB처리`임으로 `관심사1 : DB연결` 분리에 중점

### 처리 1 : 공통 부분 메서드로 분리
* add 메서드와 get 메서드 모두 Class.forName 메서드를 통해서 인터페이스 드라이버 구현과 Connection 객체를 만드는 부분이 공통됨으로 메서드로 분리한다.
* 리팩토링 : 외부에서 코드를 사용하는 방식에는 변화를 주지 않으며 내부 로직을 고치는 작업

### 처리 2 : 분리한 메서드 상속
* N사와 D사가 사용하는 DB가 원하는 DB가 다름으로 '관심사1 : DB연결' 부분(getConnection 메서드)를 추상메서드로 변경 이후 상속을 통해서 각 DB에 맞게 N사와 D사가 구현할 수 있도록한다.
* 추상 메서드나 오버라이딩이 가능한 protected 메서드 등으로 만든 뒤 서브클래스에서 필요에 맞게 구현해서 사용하도록 하는 방법을 `템플릿 메서드 패턴`이라고 한다.
* UserDao의 get, add 메서드 처럼 추상메서드나 protected 메서드를 이용하여 동작 로직을 정의해놓은 메서드를 `탬플릿 메서드`라고 한다.
* protected로 선언하여 디폴트 기능을 정의하거나 비워두고 하위 클래스가 선택적으로 오버라이드하도록 만든 메서드를 `훅 메서드`라고도 한다. 
* 서브 클래스에서 구체적인 오브젝트 생성 방법을 결정하게 하는 것을 `팩토리 메서드 패턴`이라고 한다.
* 이렇게 분리된 UserDao는 Connection 인터페이스 타입의 객체의 `기능을 사용하는데만 관심이 있고` NUserDao, DUserDao는 Connection 객체를 `제공하는 것에만 관심`을 둔다.
* 상속함으로써의 장점? - get 및 add 메서드를 `탬플릿 메서드`로 만듬으로 인하여 해당 메서드 자체의 로직이 변경되는 것이 아닌 외부 요인(ex)DB Connection 연결 방식 변화)으로 인한 코드 수정이 사라진다.

## 1.3 처리 3 : 분리한 메서드를 상속이 아닌 외부 클래스로 분리
* 상속으로 인한 단점(다중상속 불가, 부모클래스와의 관계가 깊어 부모클래스의 기능 추가하거나 수정할 시에 자식 클래스 역시 수정해야함)  
으로 인해 '관심사1 : DB연결' 기능을 클래스로 분리
```java
public class SimpleConnectionMaker {
    public Connection makeNewConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/spring3?useSSL=false", "scott", "tiger");
    }
}
```

### 처리 4
* 처리 3의 문제점 : 클래스로 분리한 경우 N사와 D사의 DB에 맞게 처리하기 위해서는 UserDao 클래스를 일일히 수정해야한다. 
* 클래스 사이의 관계(`UserDao`(class) -> `SimpleConnectionMaker`(class)에서는 UserDao가 어떤 클래스를 사용해야하며 클래스가 변경됨에 따라 어떤 메서드를 사용해야하는지 일일이 알아야한다. 
따라서 메서드 분리했던 것과 별반 다를 것이 없이 UserDao가 SimpleConnetionMaker 라는 특정 클래스에 종속되어 있다.
* 클래스 사이의 관계 -> 오브젝트 사이의 관계로 변경을 위한 인터페이스 도입
```text
UserDao`(class) -> `ConnectionMaker`(interface)  ├─> `DConnectionMaker`(class) 
                                                 ├─> `NConnectionMaker`(class) 
```
* DB Connection 클래스 -> 인터페이스로 변경
* `DAO 의 생성자` <- `DB Connection 인터페이스 구현 오브젝트`를 받아 처리함으로써 `DAO` <~~~> `DB Connection 관심사`와 분리
* UserDao에서 Connection 담당하는 객체는 인터페이스를 구현하는 객체여야하며, 인터페이스에 선언된 메서드이름 및 형식으로 해당 기능을 제공해야한다.
* 서비스 : 기능 제공, 클라이언트 : 기능을 사용하는 사용자(사용 클래스)   
예시) main 메서드에서 UserDao 객체 생성 및 add, get 메서드 호출 => 클라이언트 : main 메서드(UserDaoTest 메서드) / 서비스 메서드 : add, get / 서비스 : UserDao 
* 클래스와 클래스의 관계 : 정적 관계 실제 클래스 코드 내부에서 특정 클래스 객체 선언 및 사용
* 오브젝트와 오브젝트의 관계 : 동적 관계, 클래스 코드 내부에 인터페이스 객체 선언 및 사용

### 문제점
* 클라이언트인 `UserDaoTest` 의 관심사는 `UserDao`가 정상 동작하는지 테스트하는 것에 있지만 
현재는 `UserDao` 와 `ConnectionMaker` 가 연결돼서 사용될 수 있도록 관계를 맺어주는 관심사가 추가로 있다.

### 팩토리
* 팩토리 클래스는 객체의 생성 방법을 결정하고 그렇게 만들어진 오브젝트를 돌려주는 것

### 높은 응집도
* 응집도가 높다 = 하나의 모듈, 클래스가 하나의 책임 또는 관심사에만 집중되어 있다.

### 낮은 결합도
* 결합도가 낮다 = 책임과 관심사가 다른 오브젝트 또는 모듈과 느스하게 연결된 형태를 유지한다.(클래스 사이의 관계보다는 클래스와 인터페이스 관계)

### 전략 패턴
* 전략 패턴이란 자신의 <b>기능 맥락(context)</b>에서 필요에 따라 <b>변경이 필요한 알고리즘을 인터페이스</b>를 통해  
통째로 외부로 분리시킴, 이를 구현한 <b>구체적인 알고리즘 클래스</b>를 필요에 따라 바꿔서 사용할 수 있게 하는 디자인패턴이며
<b>클라이언트</b>에서 기능 맥락에 구체적인 알고리즘 클래스를 주입한다.  
* 기능 맥락(context) : `UserDao`
* 변경이 필요한 알고리즘 인터페이스 : `ConnectionMaker`
* 구체적인 알고리즘 클래스 : `DConnectionMaker`, `NConnectionMaker`
* 클라이언트 : `UserDaoTest`

## 1.4 Ioc(제어의 역전)
### 일반적 프로그래밍 구조
* main 메서드와 같이 프로그램이 시작되는 지점에서 사용할 오브젝트를 결정 및 생성 후 만들어진 오브젝트의 메서드를 사용하는 식의 작업이 반복  
* 모든 오브젝트가 자신이 사용할 클래스를 결정하고 언제 어떻게 오브젝트를 만들지 스스로 관장

### 설계도로서의 팩토리
* `UserDao` 및 `ConnectionMaker`는 각각 데이터 로직과 기술 로직 담당 - 애플리케이션을 구성하는 <b>컴포넌트</b>
* `DaoFactory`는 애플리케이션의 오브젝트들을 구성하고 그 관계를 정의하는 책임을 맡고있다. - 구조와 관계를 정의한 <b>설계도</b>

### 제어의 역전
* 프로그램의 시작을 담당하는 main 메서드와 같은 엔트리 포인트를 제외하고 모든 오브젝트는 위임받은 제어 권한을 갖는 특별한 오브젝트에 의해 결정되고 만들어진다.  
여기서는 `DaoFactory` 클래스의 오브젝트에 의해서 `UserDao` 클래스가 사용할 `ConnectionMaker`를 결정하며  
`UserDaoTest`의 main 메서드에서 `UserDao`를 생성할 때도 `DaoFactory` 클래스를 통해서 만들어야한다.

* 프레임워크는 라이브러리의 다른 이름이 <b>아니다</b>.
* 프레임워크와 라이브러리는 다르며, 라이브러리를 사용하는 애플리케이션 코드는 애플리케이션 흐름을 직접 제어하지만,   
프레임워크는 거꾸로 애플리케이션 코드가 프레임워크에 의해 사용된다. 보통 프레임워크 위에 개발한 클래스를 등록하고, 프레임워크가 흐름을 주도하는 중에   
개발자가 만든 애플리케이션 코드를 사용하도록 만드는 방식이다.

## 1.5 스프링의 IoC
* 스프링의 핵심 - <b>빈 팩토리</b> 또는 <b>애플리케이션 컨텍스트</b>

### 1.5.1 오브젝트 팩토리를 이용한 스프링 IoC
* 애플리케이션 컨텍스트와 설정정보
    - 스프링이 제어권을 가지고 직접 만들고 관계를 부여하는 오브젝트를 <b>빈(bean)</b>이라고 부른다.
    - 스프링에서 빈의 생성과 관계설정 같은 제어를 담당하는 IoC 오브젝트를 <b>빈 팩토리</b>라고 부른다.
    - 빈 팩토리 = 빈의 생성을 담당하는 클래스
    - 애플리케이션 컨텍스트(문맥, 흐름) = 빈 팩토리 + 스프링에서 제공하는 추가 기능, 다른 이름으로 컨테이너, IoC 컨테이너 또는 스프링 컨테이너라고도 부름
    - 애플리케이션 컨텍스트(문맥, 흐름) = 컴포넌트들을 관리하는 설계도

* DaoFactory를 사용하는 애플리케이션 컨텍스트
    - `@Configuration` : 클래스 위에 붙여서 해당 클래스가 애플리케이션 컨텍스트 또는 빈 팩토리가 사용할 설정정보라는 표시
    - `@Bean` : 메서드 위에 붙여서 해당 메서드가 리턴하는 오브젝트가 스프링에서 관리하는 빈 오브젝트가 된다는 것을 표시
    - Bean 메서드 이름 = Bean 이름 (Default)

### 1.5.2 애플리케이션 컨텍스트의 동작방식
* ApplicationContext 는 BeanFactory 를 상속받았다.
* <b>DaoFactory</b>는 UserDao를 비롯한 DAO 오브젝트들의 생성과 관계 설정을 담당하며 <b>ApplicationContext</b>는 
DaoFactory를 설정정보로 사용하여 Spring 컨테이너(스프링 엔진)을 통하여 해당 오브젝트들을 직접 생성하고 가져다 쓸 수 있도록 해준다.
* DaoFactory -> ApplicationContext 변경시 장점
    - 클라이언트는 구체적인 팩토리 클래스를 알 필요가 없다. -> 나중에 XML 만들면서 말하는 장점인듯
    - 애플리케이션 컨텍스트는 종합 IoC 서비스를 제공해준다. -> 스프링 엔진 사용으로 인한 여러가지 부가기능 제공(솔직히 완벽히는 이해 못했음..)
    - 애플리케이션 컨텍스트는 빈을 검색하는 다양한 방법을 제공한다. -> 기본적으로는 getBean 메서드는 빈의 이름을 이용해서 빈을 찾아주지만 타입만으로도 찾아줄 수 있으며, 특별한 애노테이션 설정이 되어 있는 빈을 찾을 수도 있다.

### 1.5.3 스프링 IoC 용어 정리
* 빈(bean)
    - 빈 또는 빈 오브젝트는 스프링이 IoC(제어의 역전) 방식으로 관리하는 오브젝트, 제어를 사용자가 아닌 스프링에 위임하였음으로 제어의 역전  
    - 스프링이 직접 그 생성과 제어를 담당하는 오브젝트만이 빈(bean)
* 빈 팩토리(bean factory)
    - 스프링의 IoC를 담당하는 핵심 컨테이너
    - 빈의 등록, 생성, 조회 및 돌려주는 기능 담당
* 애플리케이션 컨텍스트(application context)

* 설정정보/설정 메타정보(configuration metadata)

* 컨테이너 또는 IoC 컨테이너

* 스프링 프레임워크

## 1.6 싱글톤 레지스트리와 오브젝트 스코프
### 싱글톤
* 싱글톤 패턴 오브젝트의 단점 - 
  - private 생성자톨 갖고 있기 때문에 상속할 수 없다.
  - 싱글톤은 테스트하기가 힘들다.
  - 서버환경에서는 싱글톤이 하나만 만들어지는 것을 보장하지 못한다.
  - 싱글톤의 사용은 전역(Global) 상태를 만들 수 있기 때문에 바람직하지 못하다.  
  
* 애플리케이션 컨텍스트는 빈을 싱글톤으로 저장하고 관리하는 <b>싱글톤 레지스트리</b>이다.  
단, 애플리케이션 컨텍스트의 장점은 스태틱 메소드와 private 생성지를 시용해야 하는  
비정상적인 클래스가 아니라 평범한 자바 클래스를 싱글톤으로 활용하게 해준다는 점이다.  
스프링이 빈을 싱글톤으로 만드는 것은 결국 오브젝트의 생성 방법을 제어하는 IoC 컨테이너로서의 역할이다.

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
빈 setter 메서드 | setFieldName(fieldObject); | `<property name="fieldName" ref="fieldObject">`

* Property 즉 클래스의 멤버변수(Field)의 이름은 바뀔 수 있는 실제 의존하는 클래스의 이름보다는   
인터페이스의 이름을 따르는 것이 좋다.

## 정리

### 개방 폐쇠 원칙
* 자신의 책임 자체가 변경되는 경우 외에는 불필요한 변화가 발생하지 않도록 막아주고(폐쇠),  
자신이 사용하는 외부 오브젝트의 기능은 자유롭게 확장하거나 변경할 수 있게 한다.(개방)

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

