FROM anapsix/alpine-java:8
VOLUME /tmp
ADD rno-registry-1.0.0.M1.jar app.jar
ADD wait-for-service.sh wait-for-service.sh
RUN bash -c 'touch /app.jar'
RUN bash -c 'touch /wait-for-service.sh'
RUN bash -c 'chmod +x /wait-for-service.sh'
RUN apk --no-cache add curl
ENTRYPOINT ["java","-jar","/app.jar"]
