
# STAGE: BUILD
FROM eclipse-temurin:23-jdk-alpine AS build

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./

RUN ./mvnw dependency:go-offline

COPY src ./src

RUN ./mvnw package -DskipTests

# STAGE: RUNTIME
FROM eclipse-temurin:23-jre-alpine

WORKDIR /app

COPY --from=build /app/target/*-jar-with-dependencies.jar app.jar

EXPOSE 7776

ENTRYPOINT ["java", "-jar", "app.jar"]