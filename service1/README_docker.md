
개발한 서비스를 도커 이미지로 만들어보자

이미지를 만들려면 Dockerfile을 만들어야 한다
`new > File > Dockerfile`

```
FROM openjdk:8-jdk-alpine

ADD build/libs/*.jar service2.jar 

ENTRYPOINT ["java", "-jar", "service2.jar"]
```

이미지를 빌드해보자
`$ docker build . -t service2`


이미지가 생성됐는지 확인해보자 
```
$ docker images                                                                                              ✔  929  16:53:57
REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE
service2            latest              f556485c6cd2        52 seconds ago      137MB
mongo               latest              3f3daf863757        3 days ago          388MB
openjdk             8-jdk-alpine        a3562aa0b991        11 months ago       105MB
```

이제 돌려보자
`$ docker run -d -p8080:8080 service2`

컨테이너를 멈추려면 `docker kill [CONTAINER ID]` 이다.

로그를 보려면 `docker logs 99ce51857c34` 이다. 기다리려면 `-f` 를 추가하면 된다. 

#

도커 이미지를 Docker Hub에 publish 해보자.

```
$ docker login
...
$ docker build . -t kkwonsy/service2
...
$ docker images
REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE
kkwonsy/service2    latest              832d98604931        9 minutes ago       127MB
service2            latest              832d98604931        9 minutes ago       127MB
...
$ docker push kkwonsy/service2
// (docker search kkwonsy/service2 로는 바로 안나오네.. 좀 걸리는 듯? dockerhub.com에서는 확인이 가능함)
```
간단한 내용만 다루었으므로 자세한 내용은 다른 문서를 찾아보면 좋을 것. 

#
이제 확장에 대해 알아보자. 

수직적 확장은 비싸기 때문에 수평적 확장이 효율적이다. 

각 컴포넌트는 독립적으로 확장이 가능해야 좋다. 필요하지 않은데도 같이 확장하는 경우는 비효율적이다.  

축소도 가능해야 한다. 

...

#

도커 스웜을 알아보자. 

쿠버네티스와 비슷한 도구이며 도커가 제공한다. 

`$ Docker swarm init`

`$ docker info` 를 해보면 확인해볼 수 있다. 
#

서비스를 추가해보자. 서비스는 도커를 관리하기 위한 도커 이미지의 인스턴스 집합이다.

```
// 도커 알파인 이미지를 기반으로 하는 서비스 생성
$ docker service create --replicas 1 --name helloworld alpine ping google.com

// 서비스 확인
$  docker service ls                                                                                         ✔  994  19:07:24
ID                  NAME                MODE                REPLICAS            IMAGE               PORTS
va3nclhtv98c        helloworld          replicated          1/1                 alpine:latest

// 상세 확인
$ docker service inspect --pretty helloworld                                                            125 ↵  997  19:09:28
  
  ID:             va3nclhtv98cu60zavds930z6
  Name:           helloworld
  Service Mode:   Replicated
   Replicas:      1
  Placement:
  UpdateConfig:
   Parallelism:   1
   On failure:    pause
   Monitoring Period: 5s
   Max failure ratio: 0
   Update order:      stop-first
  RollbackConfig:
   Parallelism:   1
   On failure:    pause
   Monitoring Period: 5s
   Max failure ratio: 0
   Rollback order:    stop-first
  ContainerSpec:
   Image:         alpine:latest@sha256:9a839e63dad54c3a6d1834e29692c8492d93f90c59c978c1ed79109ea4fb9a54
   Args:          ping google.com 
   Init:          false
  Resources:
  Endpoint Mode:  vip    
```

```
// 서비스의 로그확인
$ docker service logs -f helloworld                                                                         ✔  998  19:09:41
 helloworld.1.jsoluezy2ydk@docker-desktop    | PING google.com (172.217.175.46): 56 data bytes

// 서비스 삭제
$ docker service rm helloworld

// 확인
$ docker service ls
```

#
마이크로 서비스를 서비스로 퍼블리시 해보자

도커 이미지를 개발 장비에 만들면 스웜의 마스터 노드에서는 사용할 수 없다. 따라서 서비스를 만들 때, 이미지를 배포할 레지스트리 서비스가 필요하다. 

레지스트리를 만들자
```
$ docker service create --name registry --publish 5000:5000 registry

$ docker service ls                                                                                       ✔  1013  14:56:23
ID                  NAME                MODE                REPLICAS            IMAGE               PORTS
rd35cc81cs0v        registry            replicated          1/1                 registry:latest     *:5000->5000/tcp
```

http://localhost:5000/v2/_catalog 호출해보면 
```
{"repositories":[]}
```

마이크로 서비스를 만들자. service3 참고.

service3에 도커를 만들자. (Dockerfile)
```
FROM openjdk:8-jdk-alpine

ADD build/libs/*.jar service3.jar

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "service3.jar"]
```

```
// 도커 생성
$ docker build . -t service3

// 레지스트리 서비스의 이미지에 태그 지정
$ docker tag service3 localhost:5000/service3

// 공유 레지스트리 서비스로 푸시
$ docker push localhost:5000/service3

// 8080포트로 서비스하는 마이크로서비스를 스웜에 서비스로 추가
$ docker service create --name service3-service --publish 8080:8080 localhost:5000/service3 
```

http://localhost:8888/application/default 요청하면 이전과 동일한 결과를 얻는데 지금은 드커 스웜에서 서비스가 실행중인 것이다. 

#
```
$ docker service ls                                                                                       ✔  1019  15:12:20
ID                  NAME                MODE                REPLICAS            IMAGE                            PORTS
rd35cc81cs0v        registry            replicated          1/1                 registry:latest                  *:5000->5000/tcp
4hsd9brnlk6a        service3-service    replicated          1/1                 localhost:5000/service3:latest   *:8080->8080/tcp

// replica를 늘릴 수 있다.
$ docker service scale service3-service=3

$ docker service ls                                                                                       ✔  1019  15:12:20
ID                  NAME                MODE                REPLICAS            IMAGE                            PORTS
rd35cc81cs0v        registry            replicated          1/1                 registry:latest                  *:5000->5000/tcp
4hsd9brnlk6a        service3-service    replicated          3/3                 localhost:5000/service3:latest   *:8080->8080/tcp
```

cURL을 사용해 요청을 반복해보자
```
$ for i in `seq 1 21`; do curl http://localhost:8080/hello; echo . ; done
hello I am 31cb9912-55e4-4efa-8a0a-2af7f7a5f2d4 and I have been called 8 times.
hello I am 601f2a9b-21c1-4dbd-9041-025f6f8607d6 and I have been called 11 times.
hello I am fd30fba7-5f67-425a-8aff-16e76580ebed and I have been called 9 times.
hello I am 31cb9912-55e4-4efa-8a0a-2af7f7a5f2d4 and I have been called 9 times.
hello I am 601f2a9b-21c1-4dbd-9041-025f6f8607d6 and I have been called 12 times.
hello I am fd30fba7-5f67-425a-8aff-16e76580ebed and I have been called 10 times.
hello I am 31cb9912-55e4-4efa-8a0a-2af7f7a5f2d4 and I have been called 10 times.
hello I am 601f2a9b-21c1-4dbd-9041-025f6f8607d6 and I have been called 13 times.
hello I am fd30fba7-5f67-425a-8aff-16e76580ebed and I have been called 11 times.
hello I am 31cb9912-55e4-4efa-8a0a-2af7f7a5f2d4 and I have been called 11 times.
hello I am 601f2a9b-21c1-4dbd-9041-025f6f8607d6 and I have been called 14 times.
hello I am fd30fba7-5f67-425a-8aff-16e76580ebed and I have been called 12 times.
hello I am 31cb9912-55e4-4efa-8a0a-2af7f7a5f2d4 and I have been called 12 times.
hello I am 601f2a9b-21c1-4dbd-9041-025f6f8607d6 and I have been called 15 times.
hello I am fd30fba7-5f67-425a-8aff-16e76580ebed and I have been called 13 times.
hello I am 31cb9912-55e4-4efa-8a0a-2af7f7a5f2d4 and I have been called 13 times.
hello I am 601f2a9b-21c1-4dbd-9041-025f6f8607d6 and I have been called 16 times.
hello I am fd30fba7-5f67-425a-8aff-16e76580ebed and I have been called 14 times.
hello I am 31cb9912-55e4-4efa-8a0a-2af7f7a5f2d4 and I have been called 14 times.
hello I am 601f2a9b-21c1-4dbd-9041-025f6f8607d6 and I have been called 17 times.
hello I am fd30fba7-5f67-425a-8aff-16e76580ebed and I have been called 15 times.
```

모든 인스턴스를 중지하려면 아래 명령어
```
$ docker service scale service3-service=0
```

로그 관련
```
$ docker service logs service3-service

$ docker service logs -f service3-service

// 특정 인스턴스
$ docker service logs -f sqmo44alskd
```

서비스 제어
```
$  docker service ps service3-service                                                                      ✔  1029  15:22:01
ID                  NAME                 IMAGE                            NODE                DESIRED STATE       CURRENT STATE         ERROR               PORTS
0rbe1l1fucng        service3-service.1   localhost:5000/service3:latest   docker-desktop      Running             Running 2 hours ago                       
u5ncuxbqr532        service3-service.2   localhost:5000/service3:latest   docker-desktop      Running             Running 2 hours ago                       
yvkg7noot0co        service3-service.3   localhost:5000/service3:latest   docker-desktop      Running             Running 2 hours ago

// shell 접속
$ docker exec -it service3-service.2.0rbe1l1fucng /bin/sh

// 서비스 삭제
$ docker service rm service3-service
```






