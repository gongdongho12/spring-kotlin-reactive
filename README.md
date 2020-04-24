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

 

 