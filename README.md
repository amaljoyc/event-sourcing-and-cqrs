# es-boilerplate
- boilerplate with various usecases we need to cover or evaluate
- tech stack - kotlin, springboot, axon

## project structure
we have the following packages,
- command
  - includes all the Aggregates (ie. AggregateRoot) and Entities and services related to command side
- query
  - includes all the projections (ie, read models) and the services doing the projections
- coreapi
  - includes all the commands, events and queries
- controller
  - rest apis (for various commands & queries)
- config
  - all custom configurations
- external
  - includes event handlers and dispatchers for Integration Events and other external system calls
- replay
  - includes a service to showcase the replay mechanism
- saga
  - includes a sample saga implementation

## usecases
- command handling
  - eg, see CommandController & Customer aggregate (command and event handler methods in it)
- query handling
  - eg, see QueryController & CustomerProjector (query and event handler methods in it)
- past event replays
  - see ReplayController & ReplayService - it replays the events from the beginning
- querying the event store directly (without using a separate read model or projection)
  - see AggregateReader.fetchCustomer and AggregateReader.fetchAllEventsForCustomer
- versioning of read model to resolve eventual consistency issues
  - see CustomerProjector's CustomerCreatedEvent handler (@SequenceNumber)
- handling events outside aggregate to avoid replay of events causing side effects
  - see eg, SideEffectEventsHandler
- events creating another events
  - see Customer aggregate's CustomerCreatedEvent handler creating extra event CustomerActivatedEvent
- events creating other commands
  - check out CustomerAccountCommsHandler's CustomerActivatedEvent handler
- aggregate to aggregate flow
  - check Customer to Account flow via CustomerAccountCommsHandler (is a simple form of Saga)
- logging interceptors
  - either use a global config like LoggingInterceptorConfig or have separate @MessageHandlerInterceptor in each of your classes to intercept
- snapshotting events for faster replays
  - see SnapshotConfig + the @Aggregate annotation in Customer
  - Note that snapshotting is not used when we replay `Projections` (eg. in ReplayService), but rather it replays all the events
  - However, the snapshotting is used when an `Aggregate` does replays for calculating its latest state (eg. in AggregateReader.fetchAllEventsForCustomer and AggregateReader.fetchCustomer)  
- axon's Saga feature to cover more complicated scenarios of long running processes or distributed transactions
  - refer to CustomerDeletionSaga
- axon's convention for making external system calls
  - The advice is to call external services in event handlers - which are present outside of the aggregate.
  - see the EmailTriggerRequestedEvent handling in SideEffectEventsHandler
- use rabbitmq (instead of in-memory event bus)
  - refer to AmqpConfig and amqp/rabbitmq related properties in application.yml 
- axon's convention for handling domain events vs integration events
  - refer to IntegrationEventsDispatcher and the configs done in AmqpConfig (especially the CustomRoutingKeyResolver)
- axon's convention for handling incoming events from external services
  - refer to IntegrationEventsHandler and the configs done in AmqpConfig
- use postgres (instead of H2)
  - @TODO
- handling of migration / versioning of events
  - @TODO (also check how versioning would affect Projection replays, eg, if you want to replay old versions?)

## good reads
- tactical ddd
  - https://vaadin.com/learn/tutorials/ddd/tactical_domain_driven_design
- ES vs CDC + outbox
  - https://debezium.io/blog/2020/02/10/event-sourcing-vs-cdc/
- axon reference
  - https://docs.axoniq.io/reference-guide/
- dealing with external systems in axon
  - https://groups.google.com/g/axonframework/c/7pURn8XboSc
- vaughn vernon's advice on ES
  - https://kalele.io/to-event-source-or-not/
- why saga's shouldn't query the read side?
  - https://stackoverflow.com/questions/34284697/why-cant-sagas-query-the-read-side
