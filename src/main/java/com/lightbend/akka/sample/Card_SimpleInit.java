package com.lightbend.akka.sample;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.lightbend.akka.sample.Terminal_idInit.receivedAmount;
import com.lightbend.akka.sample.transactionList.receivedCardInitialization;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.text.SimpleDateFormat;
import java.util.Random;

//#Cart-messages
public class Card_SimpleInit extends AbstractActor {
    /**
     * Transaction: instruction to execute transaction
     */


    // used to catch unknown messages sent to this actor
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private static Integer idCounter = 0;

    //#Cart-messages
    static public Props props() {
        return Props.create(Card_SimpleInit.class, () -> new Card_SimpleInit());
    }

    /**
     * Sending transaction information
     */
    static public class Transaction {
        private Integer amount;
        private ActorRef terminalActor;
        private ActorRef transactionListActor;

        /**
         * Payment amount with using which device
         * @param amount
         * @param terminalActor
         */
        public Transaction(Integer amount, ActorRef terminalActor, ActorRef transactionListActor) {
            this.amount = amount;
            this.terminalActor = terminalActor;
            this.transactionListActor = transactionListActor;
        }
    }
    //#Card-messages


    static public class recordToList {
        private ActorRef transactionList;
        public recordToList(ActorRef transactionList) {
            this.transactionList = transactionList;
        }
    }
    //#Card-messages

    private final Integer id;
    private final Integer limit;

    private Integer[] limitPool = {5000,10000,20000,30000};

    public Card_SimpleInit() {


        this.id = idCounter; // each Card will have unique ID
        idCounter++;

        // TODO lower limits should be more common
        Random rn = new Random(); // assign random limit
        int rand = rn.nextInt(limitPool.length);
        this.limit = limitPool[rand];
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Transaction.class, x -> {
                    // Send message of transaction to the related terminal
                    x.terminalActor.tell(new receivedAmount(x.amount,id,x.transactionListActor), getSelf());
                })
                .match(recordToList.class, list ->{
                    String timeStamp = new SimpleDateFormat("HHmmss").format(new java.util.Date());
                    list.transactionList.tell(new receivedCardInitialization(limit,id,timeStamp),getSelf());
                })
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }
//#Card-messages
}
//#Card-messages
