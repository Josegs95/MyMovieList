
# STAGE: BUILD
FROM maven:3.9-eclipse-temurin-23-alpine AS build

WORKDIR /app

COPY pom.xml ./
COPY client/pom.xml ./client/pom.xml
COPY core/pom.xml ./core/pom.xml
COPY server/pom.xml ./server/pom.xml

RUN mvn dependency:go-offline

COPY client/src ./client/src
COPY core/src ./core/src
COPY server/src ./server/src

RUN mvn -pl server -am clean package -DskipTests

# STAGE: RUNTIME
FROM eclipse-temurin:23-jre-alpine

WORKDIR /app

COPY --from=build /app/server/target/*jar-with-dependencies.jar app.jar

EXPOSE 7776

ENTRYPOINT ["java", "-jar", "app.jar"]