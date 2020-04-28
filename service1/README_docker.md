
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








