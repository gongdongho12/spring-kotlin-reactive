# spring-kotlin-reactive

## WebFlux
Netty 라는 애플리케이션 서버를 사용해 reactive microservice를 만들 수 있는 새로운 컴포넌트이다.

reactive stream pattern 구현을 위해 reactor framework를 광범위하게 사용한다. 

#
## Netty
원래 Netty는 Non-blocking IO 작업을 수행할 수 있게 하는 Client-Server 프레임워크를 만들려는 JBoss에 의해 개발됐다.

Reactor 패턴의 Message 기반 구현을 사용한다. 

HTTP, SSL/TLS, DNS 같은 주요 알고리즘 및 프로토콜을 지원하며
HTTP/2, WebSocket, Google Protocol Buffer 같은 최신 프로토콜도 지원한다.

Spring boot는 2.0부터 Non Blocking IO 기능의 Reactive 서비스를 위해 Netty를 선택했다. 

따라서 스프링 웹 선택시 Tomcat이 구동되고, reactive 웹 선택시에는 Netty가 구동된다.

NodeJS, Nginx, Apache Mina, Vert.X, Akka 등도 참고 하자. 


#
### 정적 컨텐츠 제공
resources/public/index.html 파일 추가

reactive하게 처리된다.

`Blocking IO` 방식에서는 서버의 페이지를 요청하면 **모든 페이지 내용을 읽고** 
이를 요청한 클라이언트로 페이지를 전송하기 시작한다. 

파일을 읽는 동안 이 오퍼레이션은 차단된다. 

`Non Blocking IO` 방식에서는 서버가 페이지를 읽기 시작하고 **데이터를 가져오는 즉시 정보를 보낸다.**
그 다음 데이터를 더 읽고 클라이언트로 데이터를 다시 보낸다. 


#
### 구독자와 게시자
리액티브 프로그래밍의 핵심 구성 요소는 `Subscribe/Publish` 이다. 

일련의 `이벤트가 감지`되면 필요한 `사용자에게 전송`되는 Event model 매커니즘이다. 

거의 모든 UI 프레임워크에서 처리하는 방법을 보면 쉽게 이해가 된다.

사용자가 버튼을 누르면? --> event

이벤트를 발생시키는 책임 --> Publisher

이벤트를 수신하는 책임 --> Subscriber

1. 프레임워크 시작
2. mouse.click 이벤트 정의
3. mouseClick을 Subscriber로 등록
4. mouseHandler를 Publisher로 등록
5. 애플리케이션 시작
6. button1.click 이벤트 정의
7. Action1을 Subscriber로 등록
8. Button1Trigger를 Publisher로 등록
9. 사용자가 button1 클릭!!
10. MouseHandler가 mouse.click 이벤트를 게시!
11. MouseClick이 mouse.click 이벤트를 수신!
12. MouseClick이 button1을 인식
13. MouseClick이 Button1Trigger에게 대리
14. Button1Trigger가 button1.click 이벤트를 게시!
15. Action1이 button1.click 이벤트를 수신!

사용자는 여러가지 버튼들을 누르면서 여러 이벤트를 발생시킬 수 있으며 이 이벤트들은 서로 블로킹하지 않는다.

#
### Mono
Reactor는 모노(`Mono`) 클래스를 통해 `reactive publisher를 정의`하는 방법을 제공한다.

`하나의 결과`만 보낼 수 있다. 

```
val customerMono: Mono<Customer> = Mono.just(Customer(1, "Mono"))

val customerMono: Mono<Customer> = Customer(1, "Mono").toMono()

val customerMono = Customer(1, "Mono").toMono()
```
위에서 Mono<Customer>는 Customer 인스턴스가 아니다. 

앞으로 얻으려고 하는 것에 대한 약속이다. 

이 게시자가 Customer를 게시할 것임을 나타낼 뿐이다. 

#
### Flux
0에서 무한대의 요소를 가진 Publisher를 만들 수 있는 클래스이다. 
```
val customerFlux = Flux.fromIterable(listOf(Customer(1, "One"),Customer(2, "Two"))

val customerFlux = listOf(Customer(1, "One"),Customer(2, "Two")).toFlux()
```     

#
```
override fun createCustomer(customerMono: Mono<Customer>): Mono<*> {
    return customerMono
            .subscribe { customers[it.id] = it }
            .toMono()
}
```
create를 하면 아래처럼 출력되는데, 이는 subscribe 메소드가 Disposable 객체를 반환하기 때문이다. 
```
{
    "disposed": false,
    "scanAvailable": true
}
```
map으로 변경하면 
```
override fun createCustomer(customerMono: Mono<Customer>): Mono<*> {
    return customerMono
            .map { customers[it.id] = it }
            .toMono()
}
``` 
아래처럼 빈객체를 반환한다. 
```
{}
```
이는 모노에서 Mapper를 사용해 변환하기 때문이다.
아래처럼 it을 추가해보자
그럼 추가한 내용을 결과로 내보낼 수도 있다.  
```
override fun createCustomer(customerMono: Mono<Customer>): Mono<*> {
    return customerMono
            .map {
                customers[it.id] = it
                it
            }
            .toMono()
}
...
{
    "id": 5,
    "name": "kkwonsy",
    "telephone": null
}
```
빈 객체를 추가할 수도 있다. 참고로 Any는 코틀린의 최상위 객체이다. 자바의 Object와 유사.  
```
override fun createCustomer(customerMono: Mono<Customer>): Mono<*> {
    return customerMono
            .map {
                customers[it.id] = it
                Mono.empty<Any>()
            }
            .toMono()
}
...
{
    "scanAvailable": true
}
```


#
## 함수형 웹프로그래밍
지금까지 애노테이션 기반 구문을 사용해 리액티브 마이크로 서비스를 만들었다면 이번엔 함수형 프로그래밍을 사용해본다. 
```
@Component
class CustomerRouter {
    @Bean
    fun customerRoutes(): RouterFunction<*> = router {
        "/functional".nest {
            "/customer".nest {
                GET("/") {
                    ServerResponse.ok().body("hello world".toMono(), String::class.java)
                }
            }
        }
    }
}
```
컨트롤러 대신 RouterFunction을 사용한다. 

컴포넌트로 생성했기 때문에 빈이 노출되면 컴포넌트 스캔을 통해 RouterFunction을 만들고 웹 어플리케이션의 경로를 정의할 수 있다. 

/functional 경로에 중첩된 GET 요청으로 /customer 경로를 요청하면 200OK 를 응답하게 된다.  

ServerResponse.ok는 응답을 만드는 메소드인 ServerResponse.Builder이며, 결국 Mono<ServerResponse>를 만든다. 

그 응답에는 Mono<String> 객체가 포함된 또 다른 모노가 들어있다. 

타입추론과 static import를 통해 코드를 간단하게 했다.  
```
@Bean
fun customerRoutes() = router {
    "/functional".nest {
        "/customer".nest {
            GET("/") {
                ok().body("hello world".toMono(), String::class.java)
            }
        }
    }
}
```
#
### 핸들러 만들기
람다를 자세히 보자. 
```
@Bean
fun customerRoutes() = router {
    "/functional".nest {
        "/customer".nest {
            GET("/") { 
                it: ServerRequest ->
                ok().body(Customer(1, "functional web").toMono(), String::class.java)
            }
        }
    }
}
```
람다에는 ServerRequest 클래스의 객체인 매개 변수가 하나 있다. `it: ServerRequest`

이 객체에는 매개변수, 요청, 요청 본문 등 모든 세부 정보가 포함된다. 

예제에서는 아무것도 처리할 필요가 없어서 생략됐다. 

클래스를 하나 생성해보자. 
```
@Component
class CustomerHandler {
    fun get(serverRequest: ServerRequest): Mono<ServerResponse> =
            ok().body(Customer(1, "kkwonsy").toMono(), Customer::class.java)
}
``` 
get 함수는 람다에서 사용할 수 있다. 
CustomerRouter를 변경해보자. 
```
...
GET("/") {
    it: ServerRequest -> customerHandler.get(it)
}
...
```
람다가 새 핸들러 함수에 매개 변수를 보내기 때문에 method reference를 사용할 수 있다. 
```
GET("/", customerHandler::get)
```
아재 customerService로 바꿔보자
```
@Component
class CustomerHandler(private val customerService: CustomerService) {
    fun get(serverRequest: ServerRequest): Mono<ServerResponse> =
            ok().body(customerService.getCustomer(1))
}
```
(nullable 문제로 컴파일 오류가 날 수 있으니 잘 대응할 것)
body함수에서 클래스를 지정할 필요가 없어 생략이 가능하다. 


이제 기존 REST API 처럼 작성할건데 자세한건 git 코드를 보자.

```
// 존재하지 않는 고객일 때 ?
// ok().body() 는 Mono가 필요한데.. 
// 일단 서비스 호출한 뒤 flatMap을 통해
// 값이 있든 없든 fromValue를 통해 Mono<Customer>를 생성해준다. 
fun get(serverRequest: ServerRequest): Mono<ServerResponse> =
        customerService.getCustomer(serverRequest.pathVariable("id").toInt())
                .flatMap { ok().body(fromValue(it))}
                .switchIfEmpty(notFound().build())
```

#
#
지금까지 Non Blocking Reactive Microservice 생성하는 방법을 배웠으나
Blocking operation을 사용해 데이터를 쿼리하는 경우는 reactive의 이점을 잃는 거다. 

현재 스프링 데이터의 Reactive 구현은 mongodb, cassandra, redis 중에서 선택 가능하다.

여기서는 mongodb를 활용해본다. 

#
몽고DB를 설치하자

https://docs.mongodb.com/manual/tutorial/install-mongodb-on-os-x/

```
$ brew tap mongodb/brew
$ brew install mongodb-community
$ brew services start mongodb-community
$ ps aux | grep -v grep | grep mongod
$ mongo
>
```

#
어떤 데이터 베이스가 있는지 본다
```
> shpw dbs  
admin   0.000GB
config  0.000GB
local   0.000GB
```    
기본적으로 admin, local이 있다. 
admin은 보안 및 관리 정보를, local은 몽고DB 서버의 인스턴스에 필요한 데이터를 저장한다.

#
사용할 데이터베이스를 만들어보고 다뤄보자 
```
> use kotlinspring
switched to db kotlinspring

> db.createCollection("Customers")
{ "ok" : 1 }

> db.Customers.insertOne({"name": "spring"})
{
	"acknowledged" : true,
	"insertedId" : ObjectId("5ea527b61886e930c082bd57")
}

> db.Customers.insertMany([{"name": "reactive"},{"name": "microsevices"}])
{
	"acknowledged" : true,
	"insertedIds" : [
		ObjectId("5ea527e51886e930c082bd58"),
		ObjectId("5ea527e51886e930c082bd59")
	]
}

> db.Customers.find()
{ "_id" : ObjectId("5ea527b61886e930c082bd57"), "name" : "spring" }
{ "_id" : ObjectId("5ea527e51886e930c082bd58"), "name" : "reactive" }
{ "_id" : ObjectId("5ea527e51886e930c082bd59"), "name" : "microsevices" }

> db.Customers.update({"_id": ObjectId("5ea527e51886e930c082bd59")}, {"name": "kotlin"})
WriteResult({ "nMatched" : 1, "nUpserted" : 0, "nModified" : 1 })

> db.Customers.find()
{ "_id" : ObjectId("5ea527b61886e930c082bd57"), "name" : "spring" }
{ "_id" : ObjectId("5ea527e51886e930c082bd58"), "name" : "reactive" }
{ "_id" : ObjectId("5ea527e51886e930c082bd59"), "name" : "kotlin" }

> db.Customers.remove({"_id" : ObjectId("5ea527e51886e930c082bd58")})
WriteResult({ "nRemoved" : 1 })

> db.Customers.find()
{ "_id" : ObjectId("5ea527b61886e930c082bd57"), "name" : "spring" }
{ "_id" : ObjectId("5ea527e51886e930c082bd59"), "name" : "kotlin" }

> db.Customers.find({"name": "spring"})
{ "_id" : ObjectId("5ea527b61886e930c082bd57"), "name" : "spring" }
```     
#

gradle에 의존성을 추가하자. 
```
implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
```

#
application.yml 설정해보자
```
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017
      database: kotlinspring

```

#

mongodb에 명령어를 보내보자
```
@Component
class DatabaseInitializer {

    @Autowired
    lateinit var mongoOperations: ReactiveMongoOperations

    @PostConstruct
    fun initData() {

        // 컬렉션을 생성하라는 명령을 보내놓고 구독함
        mongoOperations.collectionExists("Customers").subscribe {
            if (it != true) {
                mongoOperations.createCollection("Customers").subscribe {
                    println("Customers collections created")
                }
            } else {
                println("Customers collections already exist")
            }
        }
    }
}

...
Customers collections already exist
```
이전에 collection을 만들었기 때문에 존재한다고 출력됐다. 

#
이번에는 repository를 생성해보자
```
@Document(collection = "Customers")
data class Customer(var id: Int = 0, val name: String = "", val telephone: Telephone? = null) {
    data class Telephone(var countryCode: String = "", var telephoneNumber: String = "")
}

interface CustomerRepository : ReactiveCrudRepository<Customer, Int>{
}
```
Crud는 database 종류에 상관없이 CRUD 인터페이스를 구성하고 있다. 

@Document 애너테이션을 통해 컬렉션으로 만들었다. 

#
사용해보자
```
@Component
class DatabaseInitializer {

    @Autowired
    lateinit var customerRepository: CustomerRepository

    @PostConstruct
    fun initData() {
        mongoOperations.collectionExists("Customers").subscribe {
            ...
            if (it != true) {
            ...
            } else {
                ...
                customerRepository.save(Customer(1, "spring")).subscribe {
                    println("Default customers created")
                }
...
Customers collections already exist
Default customers created
```

실제로 저장되었는지 볼까?
```
> show collections Customers
Customers
> db.Customers.find()
{ "_id" : ObjectId("5ea527b61886e930c082bd57"), "name" : "spring" }
{ "_id" : ObjectId("5ea527e51886e930c082bd59"), "name" : "kotlin" }
{ "_id" : 1, "name" : "spring", "_class" : "com.kkwonsy.spring.kotlin.reactive.Customer" }
```
(_class 라는 특별한 값을 저장하고 있다.)

#
사실 스프링이 제공하는 repository를 사용하면 collection을 만들 필요가 없기 때문에 삭제하면 된다. 

#
Mono, Flux를 쓰기 위해 ReactiveMongoTemplate으로 변경하고 DatabaseInitializer도 합친다. 
```
@Repository
class CustomerRepository(private val template: ReactiveMongoTemplate) {
    fun create(customer: Mono<Customer>): Mono<Customer> = template.save(customer)
    

    companion object {
        val initialCustomers = listOf(
                Customer(1, "Kotlin")
                , Customer(2, "Java")
                , Customer(3, "Javascript", Customer.Telephone("+81", "1231092718"))
                , Customer(4, "Python", Customer.Telephone("+82", "1029301928")))
    }

    @PostConstruct
    fun initData() = initialCustomers.map(Customer::toMono).map(this::create).map(Mono<Customer>::subscribe)

    // 헷갈려서 위 initDate를 풀어봤음
    fun initData2(): List<Disposable> {
        val toMono: List<Mono<Customer>> = initialCustomers.map { customer -> customer.toMono() }
        val resultOfCreates: List<Mono<Customer>> = toMono.map { this.create(it) }
        return resultOfCreates.map { mono: Mono<Customer> -> mono.subscribe() }
    }
}
```

#
기존의 CustomerService도 수정하자
```
@Component
class CustomerServiceImpl : CustomerService {

    @Autowired
    lateinit var customerRepository: CustomerRepository

    override fun getCustomer(id: Int) = customerRepository.findById(id)
```

Telephone은 없는 경우가 있으니 Jackson  null 설정을 추가하자
```
spring:
  jackson:
    default-property-inclusion: non_null
```

나머지는 코드 참고..

#

  


  
 