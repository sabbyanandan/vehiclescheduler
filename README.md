# Schedule Car Starts

## Use Case

A car manufacturer wants to provide an option to their mobile-app users to schedule starting the cars in the desired time-window.

## Solution

The prototype includes a Spring Cloud Stream-based source (i.e., [`vehicle`](https://github.com/sabbyanandan/vehiclescheduler/tree/master/vehicle)), 
which simulates the production of the car-schedule events in a random cadence. Downstream from that, a Spring Cloud Stream
processor (i.e., [`schedule`](https://github.com/sabbyanandan/vehiclescheduler/tree/master/schedule)) that computes the
real-time aggregates of all the cars that belong to a specific schedule. The materialized view of the car-schedules
in Kafka Streams can then be handed over to a scheduler system, so it can trigger the car-start launches effectively.
