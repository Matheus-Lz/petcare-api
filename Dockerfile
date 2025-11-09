FROM maven:3.9.9-eclipse-temurin-21 AS build
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -q -DskipTests dependency:go-offline
COPY src src
RUN --mount=type=cache,target=/root/.m2 mvn -q -DskipTests -DfinalName=app clean package

FROM eclipse-temurin:21-jre-jammy
COPY --from=build /target/app.jar /app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]