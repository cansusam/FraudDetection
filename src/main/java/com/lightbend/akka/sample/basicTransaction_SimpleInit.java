package com.lightbend.akka.sample;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.lightbend.akka.sample.Constants.*;

public class basicTransaction_SimpleInit {

    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("transactions");
        try {
            //list of transactions
            final ActorRef transactions = system.actorOf(TransactionList.props());
            // TODO cards and terminals can be created with another actor
            final List<ActorRef> terminalList = new ArrayList<ActorRef>();
            final List<ActorRef> cardList = new ArrayList<ActorRef>();

            generateTerminals(terminalList,system);
            generateCards(cardList,transactions,system);

            //#main-send-messages
//            do {
            for (int i=0; i<numberOfTransactions; i++) {
                Random rn = new Random();
                int randomCard = rn.nextInt(cardNumber);
                int randomAmount = rn.nextInt(amountList.length);
                int randomTerminal = rn.nextInt(terminalNumber);
                int randomDuration = rn.nextInt(schedulingDurationsMS.length);
                system.scheduler().scheduleOnce(Duration.ofMillis(schedulingDurationsMS[randomDuration]),
                        new Runnable() {
                            @Override
                            public void run() {
                                cardList.get(randomCard).tell(new Card_SimpleInit.Transaction(amountList[randomAmount],terminalList.get(randomTerminal),transactions),ActorRef.noSender());
                            }
                        }, system.dispatcher());
                //
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

    /**
     * Creating terminals for simulation.
     *
     * Maximum ATM number is 81. Until ATM number reach to its limit,
     * proportion possibility of ATM/POS will be 1/1;
     *
     * Terminal IDs for
     *  - ATMs will be in [0,80] range
     *  - POS devices will be higher than 80
     * @param terminalList
     * @param system
     */
    public static void generateTerminals(List<ActorRef> terminalList, ActorSystem system){
        int atmNumber = 0;
        int posNumber = 0;
        int terminalID;
        for (int i = 0; i < terminalNumber; i++) {
            Kind.terminalKind type;
            if ((Math.random() < 0.5) && atmNumber < atmLimit) {
                type = Kind.terminalKind.ATM;
                terminalID = atmNumber;
                atmNumber++;
            } else {
                type = Kind.terminalKind.POS;
                terminalID = posNumber + atmLimit;
                posNumber++;
            }
            terminalList.add(system.actorOf(Terminal_idInit.props(terminalID,type)));
        }
    }

    /**
     * Creating cards for simulation.
     *
     * @param cardList
     * @param transactions
     * @param system
     */
    public static void generateCards(List<ActorRef> cardList, ActorRef transactions, ActorSystem system){
        for (int i = 0; i < cardNumber; i++) {
            cardList.add(system.actorOf(Card_SimpleInit.props()));
            cardList.get(i).tell(new Card_SimpleInit.recordToList(transactions),ActorRef.noSender());
        }
    }

}
