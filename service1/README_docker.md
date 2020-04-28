
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







