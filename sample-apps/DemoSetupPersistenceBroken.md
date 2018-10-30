AppSensor Demo Setup
=========

These instructions will help you get setup to run a demo similar to what was done for AppSecUSA 2015. (video here: https://www.youtube.com/watch?v=1imlD1O4HrY)

1. **Install Java 8**

  - follow OS-specific install process
  
2. **Get AppSensor Code**

  - clone the repo fork and switch to the appsensor-nodejs-test branch
  
    ```
    git clone https://github.com/tghosth/appsensor.git
    cd appsensor
    git checkout juice-shop-test
    ```
3. **Install MySQL**

  - follow OS-specific install process (this demo assumes localhost, [Problems have been noted](https://github.com/jtmelton/appsensor/issues/87) with MySQL 8.x but MySQL 5.7.x should work fine)  
  
4. **Load Data into MySQL**

  - Run commands in this file: [../appsensor-ui/src/main/resources/db/scripts/V1_Initial_Schema_Creation.sql](../appsensor-ui/src/main/resources/db/scripts/V1_Initial_Schema_Creation.sql) (including commented lines - uncomment and run)
  - Run commands in this file: [appsensor-ws-rest-server-with-websocket-mysql-boot/create.sql](appsensor-ws-rest-server-with-websocket-mysql-boot/create.sql)

5. **Start REST / WebSocket Server**

  - go to this directory: [appsensor-ws-rest-server-with-websocket-mysql-boot](appsensor-ws-rest-server-with-websocket-mysql-boot)
  - run this command:
  
  ```
  mvn spring-boot:run -DAPPSENSOR_WEB_SOCKET_HOST_URL=ws://localhost:8085/dashboard
  ```

6. **Start AppSensorUI**

  - go to this directory: [../appsensor-ui](../appsensor-ui)
  - run this command:
  
  ```
  mvn spring-boot:run -DAPPSENSOR_REST_REPORTING_ENGINE_URL=http://localhost:8085 -DAPPSENSOR_CLIENT_APPLICATION_ID_HEADER_NAME=X-Appsensor-Client -DAPPSENSOR_CLIENT_APPLICATION_ID_HEADER_VALUE=clientui -DAPPSENSOR_WEB_SOCKET_HOST_URL=ws://localhost:8085/dashboard -Dspring.datasource.url=jdbc:mysql://localhost/appsensorjuiceshop -Dspring.datasource.username=appsensor_user2 -Dspring.datasource.password=appsensor_pass2
  ```
  
7. **Login**

  - open your browser to : http://localhost:8084
  - When prompted login with user ```uberuser``` and password ```uberuser```.
  
  

This set of instructions should get the demo going for you.
