# Expenses Tracker App

------
Bank Expenses Tracker: Parses a CSV transactions file and allow categorization of expenses This is a hobbies project,
only for study some technologies

## Stack/Libraries:

* [Ktor](https://ktor.io/)
* [Exposed](https://github.com/JetBrains/Exposed)
* [Kotlin-CSV](https://github.com/doyaaaaaken/kotlin-csv)
* Gradle
* Docker
* Docker-compose
* Postgres

## Requirements

* Java 8

Create two `.env` files at the docker folder:

postgres.env

```dotenv
POSTGRESQL_USERNAME=<username>
POSTGRESQL_PASSWORD=<password>
POSTGRESQL_DATABASE=expenses_db
```

app.env

```dotenv
DB_CLASS_NAME=org.postgresql.Driver
DB_JDBC_URL=jdbc:postgresql://db:5432/expenses_db
DB_MAX_POOL_SIZE=2
DB_TRANSACTION_ISOLATION=TRANSACTION_REPEATABLE_READ
DB_USERNAME=<username>
DB_PASSWORD=<password>
```

## Running the project

Executing the `build.sh` script will trigger the gradle task to build the project image and docker image After, execute
the `run.sh` script to start the docker-compose stack. The rest api will be available under localhost:8080/api