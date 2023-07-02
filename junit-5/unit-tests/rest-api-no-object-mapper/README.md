# How to Write MockMvc Tests Without ObjectMapper

This example demonstrates how you can write unit tests for a Spring MVC REST API by using JUnit 5 and the Spring
MVC Test framework. Also, this example doesn't use ObjectMapper when it creates the request body that's send to 
the system under test.

## Running Unit Tests With Maven

You can run unit tests with Maven by running the following command at command prompt:

    mvn clean test