FROM openjdk

WORKDIR /profiler
RUN cd /profiler
EXPOSE 8082
COPY /target/tng-analytics-engine-0.0.1-SNAPSHOT.jar  .

#ENV MONGO_DB mongo

CMD ["java","-jar","tng-analytics-engine-0.0.1-SNAPSHOT.jar"]
