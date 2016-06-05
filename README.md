# Transactionservice Api

## Building The Project

The project compiles with JDK 1.7 and Maven as build tool and to also manage dependencies.

Import as an existing Maven Project

Use Maven Build -> clean install 

Start apache server and make it point to http://localhost:8080 then change the server.xml and put the 
<Context> path to "/" to make possibile calls like: 
* http://localhost:8080/transactionservice/types/cars

Add the project to the server instance and restart

## Description

This is RESTful web service that stores some transactions in memory and returns information about those transactions. 

The transactions have a type and an amount. 
The service support return of all transactions of a type.
Also, transactions can be linked to each other (using a "parent_id") and the service is able to calculate the total amount involved for all transactions linked to a particular transaction.

In detail the api spec looks like the following:

> PUT /transactionservice/transaction/$transaction_id

Body:

{ "amount":double,"type":string,"parent_id":long }

where:

* transaction_id is a long specifying a new transaction

* amount is a double specifying the amount

* type is a string specifying a type of the transaction.

* parent_id is an optional long that may specify the parent transaction of this transaction.

> GET /transactionservice/transaction/$transaction_id * 

Returns:
{ "amount":double,"type":string,"parent_id":long }

> GET /transactionservice/types/$type *

Returns: 
[ long, long, .... ]

A json list of all transaction ids that share the same type $type.

> GET /transactionservice/sum/$transaction_id *

Returns : 
{ "sum", double }

A sum of all transactions that are transitively linked by their parent_id to $transaction_id.

Some simple examples would be:

* PUT /transactionservice/transaction/10 => { "amount": 5000, "type":"cars" } => { "status": "ok" }

* PUT /transactionservice/transaction/11 =>{ "amount": 10000, "type": "shopping", "parent_id": 10 } => { "status": "ok" }

* GET /transactionservice/types/cars => [10]

* GET /transactionservice/sum/10 => {"sum":15000}

* GET /transactionservice/sum/11 => {"sum":10000}


