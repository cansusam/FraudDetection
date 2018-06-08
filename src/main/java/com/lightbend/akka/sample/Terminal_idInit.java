package com.lightbend.akka.sample;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.lightbend.akka.sample.transactionList.receivedTransaction;

import java.text.SimpleDateFormat;

//#Cart-messages
public class Terminal_idInit extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    //#Cart-messages
    static public Props props(Integer id) {
        return Props.create(Terminal_idInit.class, () -> new Terminal_idInit(id));
    }

    /**
     * Card request receiver.
     */
    static public class receivedAmount {
        public final Integer amount;
        public final Integer cardID;
        private ActorRef transactionList;

        /**
         * Amount and ID is needed to keep log of transactions
         * @param amount
         * @param cardID
         */
        public receivedAmount(Integer amount, Integer cardID, ActorRef transactionList) {
            this.amount = amount;
            this.cardID = cardID;
            this.transactionList = transactionList;
        }
    }

//    private static Integer idCounter = 0;
    private final Integer id;
    /**
     * During static ip creation at actor initialization, some of the actors had same id.
     * Id assignment with argument is a temporary solution.
     * @param id
     */
    public Terminal_idInit(Integer id) {
        this.id = id;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(receivedAmount.class, received -> {
                    //log.debug("\n#Received: Terminal " + id + " - Amount : " + received.amount.toString() + " - CardID : " + received.cardID.toString());
                    //log.info("#Received: Terminal " + id + " - Amount : " + received.amount.toString() + " - CardID : " + received.cardID.toString());
                    String timeStamp = new SimpleDateFormat("HHmmss").format(new java.util.Date());
                    // Direct to the transactionList // possible to do it directly from card
                    received.transactionList.tell(new receivedTransaction(received.amount,received.cardID,id,timeStamp), getSelf());


                }) // when request received, this message triggered
                .matchAny(o -> log.info("received unknown message")) // if non of the messages match
                .build();
    }
//#Card-messages
}
//#Card-messages
