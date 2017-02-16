#Notes of Implementing Sudoku

## JavaFX
### JavaFX Threads

* JavaFX initially has two threads, the launcher thread runs the `Application` constructor and `Application.init()`. The thread cannot create `stage`s. The JavaFX application thread runs the `Application.start()` methods and callbacks. 

* `FXMLLoader`, and `stage`s can only be used inside the JavaFX application thread.

* Other thread can run some function on the JavaFX application thread by calling `Platform.runLater(runnable)`

### Binding

* Unidirection
`bind()`

* Bidirection
`bindBidirectional()`

* Expression

* Low-level binding

Extend `XXXBinding` class and implement `computeValue()`.

In the constructor, bind all dependent properties by calling `super.bind()`

### Value Change Listener
* For eager evaluation
* For input validation
* Inside a listener, the property's value cannot be changed. The work arround is to call `Platform.runLater(runnable)`

### Invalidation Listener
* For eagerly update a JavaFX binding value.

### Add/Remove Listener
* `addListener(InvalidationListener)`, `removeListener(InvalidationListener)`
* `addListener(ChangeListener)`, `removeListener(ChangeListener)`
* Forgetting to remove listeners will result in memmory leak.

### CSS 
* Ref [JavaFX CSS Reference](https://docs.oracle.com/javafx/2/api/javafx/scene/doc-files/cssref.html)


## Spring IOC Framework
### XML Configuration
The following xml condiguration enables Spring to search Spring beans in a packages, and do injections with annotations.
```xml
<context:component-scan base-package="lzhou" />
<context:annotation-config />
```

### Annotation for Automatic Bean Detection

* Ref: [Classpath scanning and managed components](https://docs.spring.io/spring/docs/current/spring-framework-reference/html/beans.html#beans-classpath-scanning)

* `@Component`, `@Repository`, `@Service`, `@Controller` tells Spring to manage the components. They are technically identical, but `@Repository` is typically for persistence layer, `@Service` for service layer and `@Controller` for MVC controllers.

### Scope

* Ref [Bean scopes](http://docs.spring.io/spring/docs/3.0.0.M3/reference/html/ch04s04.html)

* Annotate Spring beans with `@Scope`

* Different scopes

| scope value | desciption |
|-------------|------------|
| `prototype`   | Create a new instance each time it is requested from Spring, and is later managed by the application |
| `singleton`   | A single instance for the whole application |
| `request`     | An instance for a HTTP request. Only valid in Spring web applications |
| `session`     | A single bean for the global HTTP session. Only valid in Spring web applications |
| `global session` | A single bean for the global HTTP session. Only valid in Spring web applications  |

### Qualifier

* Ref []()

### Common Exception

* `SAXParseException`

Exception message: The matching wildcard is strict, but no declaration can be found for element.

Reason: There are typos in namespace values or schema locations.

Solution: 

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:context="http://www.springframework.org/schema/context"
	xmlns:tx="http://www.springframework.org/schema/tx"
	xmlns:jpa="http://www.springframework.org/schema/data/jpa"
	xsi:schemaLocation="http://www.springframework.org/schema/beans
    http://www.springframework.org/schema/beans/spring-beans.xsd
    http://www.springframework.org/schema/context
    http://www.springframework.org/schema/context/spring-context.xsd
    http://www.springframework.org/schema/tx
    http://www.springframework.org/schema/tx/spring-tx.xsd
    http://www.springframework.org/schema/data/jpa
    http://www.springframework.org/schema/data/jpa/spring-jpa.xsd">
```

* `ClassNotFound`

Exception message: The matching wildcard is strict, but no declaration can be found for element.

Reason: There are typos in namespace values or schema locations.

Solution: 

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:context="http://www.springframework.org/schema/context"
	xmlns:tx="http://www.springframework.org/schema/tx"
	xmlns:jpa="http://www.springframework.org/schema/data/jpa"
	xsi:schemaLocation="http://www.springframework.org/schema/beans
    http://www.springframework.org/schema/beans/spring-beans.xsd
    http://www.springframework.org/schema/context
    http://www.springframework.org/schema/context/spring-context.xsd
    http://www.springframework.org/schema/tx
    http://www.springframework.org/schema/tx/spring-tx.xsd
    http://www.springframework.org/schema/data/jpa
    http://www.springframework.org/schema/data/jpa/spring-jpa.xsd">
``` 

## Spring Data JPA / Hibernate

### Configuration

* Hibernate requires `persistance.xml`. Spring Data provides another wrapper so that Hibernate can be configured in the spring context xml. 

* Hibernate generate queries and DAO object based on interfaces that extends `Repository<DTOType, KeyType>`. The `<jpa:repositories>` xml tag tells spring to scan the package for such interfaces.

* Hibernate guesses table names, columns, types from entity classes. The locations of these entity classes are configured as the `packagesToScan` property of `LocalContainerEntityManagerFactoryBean`.

```xml
<jpa:repositories base-package="lzhou.javafx.recentfile.dao" />
	<tx:annotation-driven transaction-manager="transactionManager" />
	<bean id="transactionManager" class="org.springframework.orm.jpa.JpaTransactionManager">
		<property name="entityManagerFactory" ref="entityManagerFactory" />
	</bean>
	<bean id="entityManagerFactory"
		class="org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean">
		<property name="dataSource" ref="fileHistoryDs" />
		<property name="packagesToScan" >
            <list>
                <value>lzhou.javafx.recentfile.entity</value>
            </list>
        </property>
		<property name="persistenceUnitName" value="myPersistenceUnit" />
        <property name="jpaVendorAdapter">
            <bean class="org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter">
                <property name="generateDdl" value="true" />
                <property name="showSql" value="true"/>
                <property name="databasePlatform" value="org.hibernate.dialect.DerbyDialect"/>
                <property name="database" value="DERBY"/>
            </bean>
        </property>
	</bean>
```

### Entity Class

* Ref [Hibernate - Annotations](https://www.tutorialspoint.com/hibernate/hibernate_annotations.htm)

* Spring Data guesses table names, and schemas from entity classes. 

* Requirements (JavaBean):
  * Getter, setter for all properties
  * Serializable
  * Marked with `@Entity`
  * A no-arg constructor, for JPA framework
  * A all-arg constructor, for new entities created by programmers.
 
* Annotations
  * `@Entity` 
  * `@Table(name=tablename)`: explicitly sets the table name.
  * `@Id`: the field is the primary key
  * `@GeneratedValue`: the id is generated automatically by the database
  * `@Column(name=colname)`: explicitly sets the column name
  * `@Transient`: the marked property will not be persisted by JPA.

### Repository Interface

* Ref [Repositories Definition](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.definition)
[JPA Repository](http://docs.spring.io/spring-data/jpa/docs/1.4.1.RELEASE/reference/html/jpa.repositories.html#jpa.query-methods.at-query)

* Spring Data generate sql queries from the the method names in repository interfaces. Spring Data recognizes methods with name `find`, `findBy`, `OrderBy`, `TopX`, etc.

* Custom repository interface should extends `Repository<EntiryType, IdType>`.

* `@Query(sql)` annotates a method that runs a custom query. The sql string can have parameter placeholders  specified in named format `:named`, or indexed format `?1`. The method's arguments can be annotated with `@Param(name)` to be used as the parameter.
