AppSensor WS REST Server with Websocket
==========
Use the websocket server if you want to use appsensor-ui

Generating DDL for MySQL
------------------------
If you want to generate the DDL, you just need to uncomment the lines related to "schema-generation" in the `src/main/resources/application.properties` file.

Running
-------

Before running you must first build AppSensor's modules, follow the directions in README in the project root.

Once you've built AppSensor's modules there are three ways to run the WS REST Server with Websocket

Spring Boot
------------
This will run the application locally 'exploded' allowing static resource to be 'hot reloaded'
```
mvn spring-boot:run -D APPSENSOR_WEB_SOCKET_HOST_URL=ws://localhost:8085/dashboard
```

Fat Jar
------------
This will build a jar that can be transported and run on any Java 8 host
```
mvn package
export APPSENSOR_WEB_SOCKET_HOST_URL=ws://localhost:8085/dashboard
java -jar target/appsensor-ws-rest-server-with-websocket-boot-0.0.1-SNAPSHOT.jar
```

Docker
-----------
This container does not implement TLS and is intended for development. Please harden the image if you intend
to run in a container in production.

Default ENV overridable with -e
APPSENSOR_WEB_SOCKET_HOST_URL=ws://localhost:8085/dashboard
```
mvn package docker:build
docker run -d -p 8085:8085 appsensor/appsensor-ws-rest-server-with-websocket-mysql-boot
```
