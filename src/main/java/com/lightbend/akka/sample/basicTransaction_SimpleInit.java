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
            //simulation start time recorded
            TimeConverter.startTime = System.currentTimeMillis();

            //list of transactions
            final ActorRef transactions = system.actorOf(TransactionList.props());
            // TODO cards and terminals can be created with another actor
            final List<ActorRef> terminalList = new ArrayList<ActorRef>();
            final List<ActorRef> cardList = new ArrayList<ActorRef>();

            generateTerminals(terminalList,transactions,system);
            generateCards(cardList,transactions,system);

            if(endlessSimulationON)
                endlessSimulation(system,cardList,terminalList,transactions);
            else
                limitedTransactions(system,cardList,terminalList,transactions);

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
    public static void generateTerminals(List<ActorRef> terminalList, ActorRef transactions, ActorSystem system){
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
            terminalList.get(i).tell(new Terminal_idInit.recordToList(transactions),ActorRef.noSender());
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
            cardList.add(system.actorOf(Card_SimpleInit.props(i)));
            cardList.get(i).tell(new Card_SimpleInit.recordToList(transactions),ActorRef.noSender());
        }
    }

    /**
     * Scheduling a transaction for a randomly selected card and terminal with random amount selected from predefined
     * array.
     * Scheduling duration can be selected either from predefined array or randomly with setting
     * "schedulingWithRandomIntervalON" constant on/off.
     *
     * @param system
     * @param cardList
     * @param terminalList
     * @param transactions
     * @throws IOException
     */
    public static void schedulingTransactions(ActorSystem system, List<ActorRef> cardList, List<ActorRef> terminalList,
                                              ActorRef transactions){
        Random rn = new Random();
        int randomCard = rn.nextInt(cardNumber);
        int randomAmount = rn.nextInt(amountList.length);
        int randomTerminal = rn.nextInt(terminalNumber);
        int randomDuration;
        if (schedulingWithRandomIntervalON)
            randomDuration = rn.nextInt(schedulingDurationsInterval);
        else
            randomDuration = schedulingDurationsMS[rn.nextInt(schedulingDurationsMS.length)];
        system.scheduler().scheduleOnce(Duration.ofMillis(randomDuration),
                () -> cardList.get(randomCard).tell(new Card_SimpleInit.Transaction(amountList[randomAmount],
                        terminalList.get(randomTerminal),transactions),ActorRef.noSender()), system.dispatcher());
    }

    /**
     * Simulation continues until key press.
     * @param system
     * @param cardList
     * @param terminalList
     * @param transactions
     * @throws IOException
     */
    public static void endlessSimulation(ActorSystem system, List<ActorRef> cardList, List<ActorRef> terminalList,
                                         ActorRef transactions) throws IOException{
        do {
            schedulingTransactions(system,cardList,terminalList,transactions);
        }while (System.in.available() == 0);
    }

    /**
     * Simulation with limited number of transactions
     * @param system
     * @param cardList
     * @param terminalList
     * @param transactions
     */
    public static void limitedTransactions(ActorSystem system, List<ActorRef> cardList, List<ActorRef> terminalList,
                                           ActorRef transactions){
        for (int i=0; i<numberOfTransactions; i++) {
            schedulingTransactions(system,cardList,terminalList,transactions);
        }
    }

}
