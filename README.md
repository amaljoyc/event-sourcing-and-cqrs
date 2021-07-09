# event-sourcing-and-cqrs

Some POC projects for ES and CQRS. Each of the projects are under the following dirs,
- axon
- no-framework
- axon-multi-service
- no-framework-multi-service

## Domain Intro
A simple bank application with two Aggregates
- Customer (with an Address entity)
- Account

Customer aggregate handle following commands
- createCustomer
- addAddress
- changeAddress
- createSavingsAccount

Account aggregate handle following commands
- createAccount
- creditAmount
- debitAmount

## axon
built using,
- axon-framework with springboot
- command & query on same application
- h2 db
- axon event bus

Note: explicitly skipping the usage of axon-server.

## no-framework
built using,
- springboot only (no ES/CQRS framework)
- command & query on same application
- h2 db
- spring application events

## axon-multi-service
built using,
- axon-framework with springboot
- command & query as separate microservice
- postgresql db
- rabbitmq

Note: explicitly skipping the usage of axon-server.

## no-framework-multi-service
built using,
- springboot only (no ES/CQRS framework)
- command & query as separate microservice
- postgresql db
- rabbitmq

## TODO
- try out SAGA pattern (for axon & no-framework projects)