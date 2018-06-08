package com.lightbend.akka.sample;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.dsl.Creators;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class basicTransaction_SimpleInit {
    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("transactions");
        try {

            //list of transactions
            final ActorRef transactions = system.actorOf(transactionList.props());

            // TODO cards and terminals can be created with another actor

            final List<ActorRef> terminalList = new ArrayList<ActorRef>();
            final List<ActorRef> cardList = new ArrayList<ActorRef>();

            int terminalNumber = 4; // TODO adapt for the tasks
            for(int i=0; i<terminalNumber;i++)
                terminalList.add(system.actorOf(Terminal_idInit.props(i)));

            int cardNumber = 10; // TODO adapt for the tasks
            for (int i = 0; i < cardNumber; i++) {
                cardList.add(system.actorOf(Card_SimpleInit.props()));
                cardList.get(i).tell(new Card_SimpleInit.recordToList(transactions),ActorRef.noSender());
            }

            int amountList[] = {100,1000,10000};
            //#create-actors

            //#main-send-messages
//            do {
            int numberOfTransactions = 100;
            for (int i=0; i<numberOfTransactions; i++) {
                Random rn = new Random();
                int randomCard = rn.nextInt(cardNumber);
                int randomAmount = rn.nextInt(amountList.length);
                int randomTerminal = rn.nextInt(terminalNumber);
                cardList.get(randomCard).tell(new Card_SimpleInit.Transaction(amountList[randomAmount],terminalList.get(randomTerminal),transactions),ActorRef.noSender());
            }

//            }while (System.in.available() == 0);
            //#main-send-messages

            System.out.println(">>> Press ENTER to exit <<<");
            System.in.read();
        } catch (IOException ioe) {
        } finally {
            system.terminate();
        }
    }
}
