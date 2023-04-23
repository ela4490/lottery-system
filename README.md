# Lottery System

The lottery service includes the following features:

* The service allows anyone to register as a lottery participant.
* Lottery participants are able to submit as many lottery ballots as they want for any lottery that isn’t yet finished.
* Each day at midnight the lottery event will be considered closed and a random lottery winner is selected from all participants for the
day.
* All users are able to check the winning ballot for any specific date.
* The service is persisting the data regarding the lottery.

Domain model of Lottery System is shown below:

![Alt text](data-model.png?raw=true "Lottery Data Model")

## Build/Run Instruction

The Lottery Service is implemented using:
* Java 17
* Spring Boot 2.7.2
* Spring Data JPA
* PostgreSQL database
* Lombok
* Swagger 2

The database and service are started using docker compose.

In the root directory of project run: `./mvnw clean install`

From the lottery folder run: `docker-compose up --build --force-recreate`

After running the service, you can access the database by credentials provided in the application properties file.

The **Lottery Service** contains 8 Rest APIs 
which are documented by Swagger API documentation library. 

The implemented APIs can be accessed through:

[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

Just as an example for reading all defined lotteries, 
you can call:

[http://localhost:8080/lottery?page=0&size=10](http://localhost:8080/lottery?page=0&size=10)

This API is documented under 
[lottery-controller](http://localhost:8080/swagger-ui.html#/lottery-controller).


## List of available APIs 

[lottery-controller](http://localhost:8080/swagger-ui.html#/lottery-controller) contains:

1. Adding new lottery >> _POST: /lottery_
2. Reading all defined lotteries(with pagination) >> _GET: /lottery?page=0&size=10_
3. Finishing a lottery >> _PATCH: /lottery/{id}/finish_

[participant-controller](http://localhost:8080/swagger-ui.html#/participant-controller) contains:

4. Registering a user for a lottery >> _POST: /participant/register_
5. Read all active lotteries user registered for >> _GET: /participant/{ssn}/lotteries_
6. Submit as many ballots as user buys >> _POST: /participant/{ssn}/submit_
7. Read all ballots user submitted for a specific lottery >> _GET: /participant/{ssn}/lottery/{lotteryId}/ballots_

[winner-controller](http://localhost:8080/swagger-ui.html#/winner-controller) contains:

8. Read the detailed info of a winner for specific date >> _GET: /winner/of/{date}_

