package com.lightbend.akka.sample;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.lightbend.akka.sample.Writer.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TransactionList extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    static public Props props() {
        return Props.create(TransactionList.class, () -> new TransactionList());
    }

    /**
     * Transactions received by terminals are kept in the list.
     */
    static public class receivedTransaction {
        public final Integer amount;
        public final Integer cardID;
        public final Integer terminalID;
        public final String date;

        /**
         *
         * @param amount
         * @param cardID
         * @param terminalID
         * @param date
         */
        public receivedTransaction(Integer amount, Integer cardID, Integer terminalID, String date) {
            this.amount = amount;
            this.cardID = cardID;
            this.terminalID = terminalID;
            this.date = date;
        }
    }

    /**
     * balanceList keeps cards balance list.
     * Wrapper can be used instead of List.
     */
    private final HashMap<Integer, BalanceListElement> balanceList = new HashMap<Integer, BalanceListElement>();
    /**
     * TransactionList keeps all transaction logs weather it is valid or not.
     */
    private final List<TransactionListElement> transactionList = new ArrayList<>();

    /**
     * Transactions received by terminals are kept in the list.
     */
    static public class receivedCardInitialization {
        public final Integer limit;
        public final Integer cardID;
        public final String date;


        public receivedCardInitialization(Integer limit, Integer cardID, String date) {
            this.limit = limit;
            this.cardID = cardID;
            this.date = date;
        }
    }

    // It is not necessary
//    static public class printBalanceList{
//
//    }

    // constructor
    public TransactionList() {

    }

    /**
     * Creates its own writerTransactions for transactions
     */
    final ActorRef writerTransactions = getContext().system().actorOf(Writer.props());
    @Override
    public void preStart() throws IOException {
        writerTransactions.tell(new transactionFileCreate(),getSelf());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(receivedTransaction.class, received -> {
                    BalanceListElement cardValues = balanceList.get(received.cardID);
                    int balance = cardValues.balance;
                    int validity = 1;
                    int newBalance = balance;
                    if (balance >= received.amount) {
                        // valid
                        newBalance = balance - received.amount;
                        // log.info("\n#Valid Transaction Received: Terminal " + received.terminalID + " - Amount : " + received.amount + " - Previous Balance : " + balance + " - Remaining : " + newBalance + " - CardID : " + received.cardID);
                        System.out.println("#Valid Transaction Received: Terminal " + received.terminalID + " - Amount : " + received.amount + " - Previous Balance : " + balance + " - Remaining : " + newBalance + " - CardID : " + received.cardID);
                        cardValues.balance = newBalance;
                        balanceList.put(received.cardID,cardValues);
                    } else {
                        // not valid
                        validity = 0;
                        //log.info("\n#Non-Valid Transaction Received: Terminal " + received.terminalID + " - Amount : " + received.amount + " - Balance : " + balance + " - CardID : " + received.cardID);
                        System.out.println("#Non-Valid Transaction Received: Terminal " + received.terminalID + " - Amount : " + received.amount + " - Balance : " + balance + " - CardID : " + received.cardID);
                    }

                    TransactionListElement transactionValues = new TransactionListElement(received.cardID,
                            received.terminalID,received.amount,balance,newBalance,validity,received.date);

                    transactionList.add(transactionValues);
                    writerTransactions.tell(new transactionAddLine(transactionValues),getSelf());

                })
                .match(receivedCardInitialization.class, received -> {
                    // TODO type of card, house location should be added
                    BalanceListElement cardValues = new BalanceListElement(received.cardID,
                            received.limit,received.date,received.limit);
                    balanceList.put(received.cardID,cardValues);
                    //log.info("\nCard information received from Card ID : " + received.cardID + " Card Limit: " + received.limit + " at " + received.date + " - Card initialized.");
                    System.out.println("Card information received from Card ID : " + received.cardID + " Card Limit: " + received.limit + " at " + received.date + " - Card initialized.");
                })
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }
//#Card-messages
}
//#Card-messages
