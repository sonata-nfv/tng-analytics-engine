FROM maven

WORKDIR /app
ADD . /app
RUN cd /app

RUN mvn clean install

FROM openjdk
EXPOSE 8085
COPY --from=0  /app/target/tng-analytics-engine-0.0.1-SNAPSHOT.jar /app/tng-analytics-engine-0.0.1-SNAPSHOT.jar
WORKDIR /app

#ENV MONGO_DB localhost
#ENV PHYSIOG_URL localhost

ENV MONGO_DB son-mongo
ENV PHYSIOG_URL http://analyticserver



ENV MONITORING_ENGINE http://son-vnv-monitor-manager:8000
ENV PROMETHEUS_URL http://son-monitor-prometheus:9090


#ENV MONITORING_ENGINE http://pre-int-vnv-bcn.5gtango.eu:8000
#ENV PROMETHEUS_URL http://pre-int-vnv-bcn.5gtango.eu:9090




CMD ["java","-jar","tng-analytics-engine-0.0.1-SNAPSHOT.jar"]
