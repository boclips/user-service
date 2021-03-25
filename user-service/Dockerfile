FROM openjdk:8-jre-slim
COPY build/libs/user-service-*.jar /opt/app.jar
COPY cloud_sql_proxy /opt/cloud_sql_proxy
WORKDIR /opt
CMD ["java", "-jar", "app.jar"]
