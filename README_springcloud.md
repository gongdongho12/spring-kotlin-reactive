


스프링 클라우드는 어떤 클라우드에서도 서비스를 구축 가능하다. 
스프링 클라우드를 사용하면 클라우드 네이티브 마이크로 서비스를 쉽게 만들 수 있다. 

컴포넌트의 아키텍쳐 패턴을 살펴보자. 

* Config server
* Service discovery
* Gateway
* Circuit breaker

#
### Config server
클라우드가 변경되면 설정도 바뀌어야 하기 때문에 애플리케이션에 설정을 정적으로 할 수는 없다. 서비스에서 요청할 수 있도록 하는 메커니즘이 필요하다. 

Config server는 설정에 대해 쿼리할 수 있는 기능을 제공하여 URL, DB, PW 및 그 밖의 설정값을 검색할 수 있따. 

### Service Discovery
클라우드에서는 다른 서비스를 연결해야 할 수도 있는데, 실제 서비스의 위치, 인스턴스 수 등을 알 수가 없어 이 메커니즘이 필요하다. 

모든 서비스의 인스턴스가 생성되면 Service Discovery에 등록된다. 최신 상태가 유지되어야 하기 때문에 Heart-beat 메커니즘을 사용한다. (5분마다 활성화 상태를 체크)

### Gateway
애플리케이션이 /customers 경로를 사용하면, 서비스 탐색이나 로드 밸런서를 직접 사용할 필요 없이 내부적으로 동작하는 게이트웨이를 통해 호출하도록 간단한 인터페이스를 구성할 수 있다. 

모든 서비스의 진입점이기 때문에 보안, 자격증명, 권한 등을 여기서 해결할 수 있다. 

### Circuit breaker
각 서비스에서 수행한 동작이 실패할 때가 있다. 그럼에도 사용자에게 응답은 필요하며, 나머지 서비스들은 정상 동작해야 한다. 

A, B, C 서비스가 있다. A는 B를 호출해 고객 정보를 얻고 C를 호출해 추천을 받아온다. C가 동작하지 않는다면 A는 매번 C를 호출하기 때문에 결국 A는 오류 내용을 반환하게 된다. 
또한 C에 의해 성능저하도 올 수 있다. 

이러한 문제가 발생하지 않도록 Circuit breaker를 사용해 C 호출 후 작업이 실패하면 일정기간 C 호출 회로를 차단한 뒤, 다시 동작할때 까지는 회로를 열지않고 기본 값을 반환하도록 한다. 

#
## 스프링 클라우드 넷플릭스
넷플릭스 OSS에 클라우드 아키텍쳐 패턴을 구현하는 많은 컴포넌트가 있어 스프링 클라우드와 함께 사용하면 좋다. 
* Eureka: Service discovery
* Ribbon: Load balencer
* Hystrix: Circuit breaker
* Zuul: Gateway server

#
#
## Config server
dependencies: Config Server, Web

컨피그 서버는 기본 포트가 8888이므로 바꾸자
```
server:
  port: 8888
```

@EnableConfigServer 애너테이션을 사용하자
```
@SpringBootApplication
@EnableConfigServer
class ConfigDemoApplication

fun main(args: Array<String>) }
    runApplication<ConfigDemoApplication>(*args)
}
```

설정 파일의 위치 지정이 필요하다. 일단은 Git이나 Database가 아닌 기본 파일시스템을 사용한다. 
```
spring:
  profiles:
    active: native
  cloud:
    config:
      server:
        native:
          search-locations: classpath:config/
```

http://localhost:8888/application/default 요청하면 다음과 같다
```
{
  "name": "application",
  "profiles": [
    "default"
	],
  "label": null,
  "version": null,
  "state": null,
  "propertySources": []
}
```


이제 설정을 추가해보자 (config/application.yml)
```
microservice:
  example:
    greetings: "hello"
```


다시 http://localhost:8888/application/default 요청하면 다음과 같다
```
{
  "name": "application",
  "profiles": [
    "default"
	],
  "label": null,
  "version": null,
  "state": null,
  "propertySources": [
    {
      "name": "classpath:config/application.yml",
      "source": {
        "microservice.example.greetings": "hello"
      }
    }
  ]
}
```
(config server 시작시 잘 안되면 캐시 제거를 위해 gradle clean을 해보자)

#
설정을 받도록 마이크로서비스에 RestController를 추가해서 확인해보자
```
@RestController
class GreetingController {
  @Value("\${microservice.example.greetings}")
  private lateinit var greetings: String

  @GetMapping("/greetings")
  fun greetings() = greetings
}
```
디펜던시도 필요함(책에선 언급이 없네 ㅋ)
```
implementation("org.springframework.cloud:spring-cloud-starter-config")
```
application.yml 설정도 필요함
```
spring:
  cloud:
    config:
      uri: http://localhost:8888
```
http://localhost:8080/greetings 를 요청하면 hello가 출력된다. 

#
이제 설정을 수정해보자
```
microservice:
  example:
    greetings: "hello again!!"
```
컨피그 서버를 시작하고, 마이크로서비스도 다시 시작하면 결과가 바뀐다. 
#

책에서는 언급이 없지만 설정값이 바뀌면 자동으로 refresh가 되야 의미가 있다. 아래 등의 블로그를 참고하자.
https://multifrontgarden.tistory.com/237

#

그런데 마이크로서비스는 여러개가 있기 때문에 각 설정을 개별적으로 하는게 좋다. 

application.yml (마이크로서비스)
```
spring:
  application:
    name: "greetings"
```


resources/config/greetings.yml (Config server)
```
microservice:
  example:
    greetings: "hello greetings!!"
```

http://localhost:8888/greetings/default 요청하면 다음과 같다
```
{
  "name": "greetings",
  "profiles": [
    "default"
	],
  "label": null,
  "version": null,
  "state": null,
  "propertySources": [
    {
      "name": "classpath:config/greetings.yml",
      "source": {
        "microservice.example.greetings": "hello greetings!!"
      }
    },
    {
      "name": "classpath:config/application.yml",
      "source": {
        "microservice.example.greetings": "hello again!!"
      }
    }
  ]
}
```

#
암호화가 필요하다면 /resources/bootstrap.yml (Config server)
```
encrypt.key: "this_is_a_secret"
spring:
  cloud:
    config:
      server:
        encrypt:
          enabled: false
```
여기서는 암호화 때 사용할 암호화키를 지정한다. 그 값은 안전하게 보관되어야 한다. 

URL을 통해 암호화할 수 있다. 

```
$ curl http://localhost:8888/encrypt -d "secret message"
7b310alk2n1lk2j1jf1vjlkvjl1291029
```

이제 이 값을 넣자
resources/config/greetings.yml (Config server)
```
microservice:
  example:
    greetings: "{cipher} 7b310alk2n1lk2j1jf1vjlkvjl1291029"
```
http://localhost:8888/greetings/default 요청하면 암호화 된 것을 볼 수 있다. 

마지막으로 마이크로서비스에도 bootstrap.yml 파일을 추가해 동일한 키를 사용하도록 한다. 

applicaion.yml (마이크로서비스)
```
encrypt.key: "this_is_a_secret"
```

#
프로파일을 사용하려면 `greetings-production.yml` 처럼 파일 이름을 지정하면 된다. 

`java -jar **.jar --spring.cloud.config.profile=production` 

#
#
## 서비스 탐색
mydiscovery project
```
implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-server")
```
```
server:
  port: 8761
spring:
  application:
    name: "discovery-server"
```
```
@SpringBootApplication
@EnableEurekaServer
class MydiscoveryApplication
...
```


#
server1 project
```
implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
```
```
spring:
  application:
    name: "service1"
```

#
http://localhost:8761 에 들어가보면 어떤 인스턴스가 등록되어 있는지 볼 수 있다. 

기본적으로 하트비트를 통해 유레카가 알아서 연결을 끊어주고 연결해주지만 가끔 안될때가 있는데, 그땐 Eureka가 제공하는 API를 통해 해결할 수 있다. 

https://github.com/Netflix/eureka/wiki/Eureka-REST-operations

#
인스턴스 상태 관련해서 스프링 부트 액추에이터가 더 나은 메커니즘을 제공하기 때문에 사용하자
```
implementation("org.springframework.boot:spring-boot-starter-actuator")
```
```
eureka:
  client:
    healthcheck:
      enabled: true
```
http://localhost:8080/actuator/health 가서 확인이 가능하다. 

`{"status":"UP"}`

#
## GateWay

