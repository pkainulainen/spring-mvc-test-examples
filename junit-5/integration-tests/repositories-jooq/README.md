# Writing Integration Tests for jOOQ Repositories

This sample project demonstrates how you can write integration tests for
Spring powered jOOQ repositories.

## Running Tests

If you want to run unit tests, you have to run the following command at
command prompt:

    mvn clean test -P unit-tests

If you want to run integration tests, you have to run the following command
at command prompt:

    mvn clean verify -P integration-tests

If you want to run both unit and integration tests, you have to run the following 
command at command prompt:

    mvn clean verify -P all-tests

If you want to run the application and skip all tests, you have to run the 
following command at command prompt:

    mvn clean spring-boot:run -P application

## Credits

The Maven build of this example project is based on this excellent blog post by Lukas Eder:
[Using Testcontainers to Generate jOOQ Code](https://blog.jooq.org/using-testcontainers-to-generate-jooq-code/).