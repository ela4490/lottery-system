FROM amazoncorretto:17.0.5
MAINTAINER com.bynder

COPY target/*.jar lottery.jar

ENTRYPOINT ["java", "--add-opens", "java.base/java.lang=ALL-UNNAMED", "-Duser.timezone=Etc/UTC", "-jar", "lottery.jar"]
