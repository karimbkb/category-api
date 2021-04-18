# Category Api
>  This app is responsible for the category crud operations

[![codecov](https://codecov.io/gh/karimbkb/category-api/branch/master/graph/badge.svg?token=tprxgsRDGg)](https://codecov.io/gh/karimbkb/category-api)
[![Build Status](https://travis-ci.com/karimbkb/category-api.svg?branch=master)](https://travis-ci.com/karimbkb/category-api)
[![made-with-Micronaut](https://img.shields.io/badge/Micronaut-2.3.1-1f425f.svg)](https://micronaut.io/)


## Contents

- [Setup](#setup)
- [Dependencies](#dependencies)
- [Endpoints](#endpoints)
- [Unit Tests](#unit-tests)
- [Code Coverage](#code-coverage)
- [Coding Style](#coding-style)
- [Static Code Analyzer](#static-code-analyzer)

## Setup

Go into the root directory of the application and run

```
docker-compse up -d --build
```

After that the api can be called via `http://localhost:8080/`

## Dependencies

- Java 11
- Gradle 6.7
- JUnit 5
- Micronaut
- Testcontainers

## Endpoints

| Action                       | Endpoint                                                             | Type    | Example                                                                               | Payload                         |
|------------------------------|----------------------------------------------------------------------|---------|---------------------------------------------------------------------------------------|---------------------------------|
| Fetch a category         | `/v1/category/{path}`                         | `GET`   | `/v1/category/bottom-up`             | -                               |
| Remove a category | `/v1/category/{id}`                              | `DELETE`| `/v1/category/3f19324a-39dc-41f4-b88b-b05ff2c38c5a`                   | -      |
| Create a category     | `/v1/category/`                               | `POST`  | `/v1/category`                   | `{"name": "Bottom up", "path": "bottom-up"}`      |
| Update a category     | `/v1/category/{id}`                               | `POST`  | `/v1/category/3f19324a-39dc-41f4-b88b-b05ff2c38c5a`                   | `{"name": "Bottom up", "path": "bottom-up"}`      |


## Unit Tests

To execute Unit Tests run:

```
./gradlew test
```

## Code Coverage

To check if the code coverage ratio was reached run this command:

**INFO: You have to run `./gradlew test` first!**

```
./gradlew jacocoTestCoverageVerification
```

To create a code coverage report run this command:

```
./gradlew jacocoTestReport
```

## Coding Style

Coding style is based on the Google Java Format.

To verify if all java files are formatted right:

```
./gradlew verGJF
```

To format all java files:

```
./gradlew goJF
```

## Static Code Analyzer

Not used
