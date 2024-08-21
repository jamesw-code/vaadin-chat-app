FROM openjdk:21

EXPOSE 8080

WORKDIR /applications

COPY target/*.jar /applications/*.jar

ENTRYPOINT ["java","-jar", "*.jar"]