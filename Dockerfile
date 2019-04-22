FROM openjdk

WORKDIR /profiler
RUN cd /profiler
EXPOSE 8082
COPY /target/tng-profiler-0.0.1-SNAPSHOT.jar  .

#ENV MONGO_DB mongo

CMD ["java","-jar","tng-profiler-0.0.1-SNAPSHOT.jar"]
