FROM amazoncorretto:21-alpine

WORKDIR /root

COPY pom.xml /root/

COPY .mvn /root/.mvn

COPY mvnw /root/

RUN chmod +x mvnw       

RUN ./mvnw dependency:go-offline

COPY ./src /root/src

RUN ./mvnw clean install -DskipTests

EXPOSE 8050

ENTRYPOINT ["java", "-jar", "/root/target/DTT-MS-CATALOG-V2-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=docker"]