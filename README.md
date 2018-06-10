# Tazi Project

The goal is to create time series of transactions using a simulated world.  
These are credit or debit card transactions on terminals.  
The ratio of terminals to cards is 1 to 1000.

## Details

A card has the following properties:  
-Kind: Debit or Credit  
-Limit:
  * If debit, max daily transaction 1000 TL  
  * If credit, monthly limit is one of 5000, 10000, 20000, 30000 TL  
  * where lower limits are more common  

-Home location: One of 80 unique values plus a value representing International  

A terminal has the following properties:  
-Kind: POS or ATM  
-Location: One of 80 unique values plus a value representing International and a value representing Internet (POS only)  
-Merchant Category: (POS only) One of 5 unique values representing the type of business  

A transaction is represented by the following:  
* DateTime (minute resolution)  
* CardID  
* TerminalID
* Amount

## Task 1

Implement this world using Akka Actors.  
Your code must be able to generate an infinite stream of transaction data, in real or accelerated time.  
Make sure that your code is documented.  
Test your code using test cases.

## Task 2

Describe features that could help you detect payment fraud on a credit card.

## Task 3

Implement the features and a simple prediction model.

## Test Cases

[DONE]
* Single terminal, single card
* Single terminal, multiple cards (100000)
* Multiple terminal (1000), multiple cards (100000)
* Artificial Training Dataset Generation (Uniform/Gaussian/DiscreteMix)

[WAITING]
* Scheduled initialization of cards/terminals.

## Built With

* [Akka](https://akka.io) - Actor Model
* [Maven](https://maven.apache.org/) - Dependency Management

## Author

* **Ömercan Susam** 
