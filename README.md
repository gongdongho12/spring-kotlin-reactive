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


 

 