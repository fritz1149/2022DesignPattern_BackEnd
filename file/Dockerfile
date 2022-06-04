#基础镜像通过java8来的
FROM java:8
VOLUME /tmp
COPY target/file-0.0.1-SNAPSHOT.jar /service.jar
#暴露端口
EXPOSE ${SERVER_PORT}
#执行命令java  -jar
ENTRYPOINT ["java","-jar","/service.jar"]