package com.lightbend.akka.sample;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.lightbend.akka.sample.Card.Transaction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class basicTransaction_SimpleInit {
    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("transactions");
        try {

            final List<ActorRef> terminalList = new ArrayList<ActorRef>();
            final List<ActorRef> cardList = new ArrayList<ActorRef>();

            int terminalNumber = 4; // TODO adapt for the tasks
            for(int i=0; i<terminalNumber;i++)
                terminalList.add(system.actorOf(Terminal_idInit.props(i)));

            int cardNumber = 10; // TODO adapt for the tasks
            for (int i = 0; i<cardNumber; i++)
                cardList.add(system.actorOf(Card_SimpleInit.props()));


            int amountList[] = {1,10,100,1000};
            //#create-actors

            //#main-send-messages
            do {
//            int numberOfTransactions = 20;
//            for (int i=0; i<numberOfTransactions; i++) {
                Random rn = new Random();
                int randomCard = rn.nextInt(cardNumber);
                int randomAmount = rn.nextInt(amountList.length);
                int randomTerminal = rn.nextInt(terminalNumber);
                cardList.get(randomCard).tell(new Card_SimpleInit.Transaction(amountList[randomAmount],terminalList.get(randomTerminal)),ActorRef.noSender());
//            }

            }while (System.in.available() == 0);
            //#main-send-messages

            System.out.println(">>> Press ENTER to exit <<<");
            System.in.read();
        } catch (IOException ioe) {
        } finally {
            system.terminate();
        }
    }
}
