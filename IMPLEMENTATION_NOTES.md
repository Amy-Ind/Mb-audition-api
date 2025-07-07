## Additional Implementation Notes

> This section is included as part of the interview requirement to explain how the application can and should be documented.

## API Documentation

This project uses [Springdoc OpenAPI](https://springdoc.org/) to auto-generate Swagger documentation.

- Swagger UI: [http://localhost:8088/swagger-ui.html](http://localhost:8088/swagger-ui.html)
- OpenAPI JSON: [http://localhost:8088/v3/api-docs](http://localhost:8088/v3/api-docs)

No additional setup is needed — the docs are generated at runtime.( Use admin/admin as credentials if required)

## Build the Application

Use the Gradle wrapper to build a runnable JAR: ./gradlew clean bootJar

## Run the Application

Run a runnable JAR: java -jar build/libs/audition-api-0.0.1-SNAPSHOT.jar

## Test Endpoints

We use Bruno to test the endpoints.
A Bruno collection named audition-api-collection is saved under docs.

- Step1: Download free Bruno from [https://www.usebruno.com/downloads](https://www.usebruno.com/downloads).
- Step2: Click "Open Collection" from Bruno, choose docs/audition-api-collection and you will see these 3 endpoints.
- Step3: Click the arrow and run the endpoint you will see the response.
  If required, please set basic Auth with the username "admin" and the password "admin".
