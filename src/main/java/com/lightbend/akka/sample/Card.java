package com.lightbend.akka.sample;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.lightbend.akka.sample.Terminal.receivedAmount;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.util.Random;

//#Cart-messages
public class Card extends AbstractActor {
    /**
     * whichTerminal: Transaction request receiver, terminalID
     * Transaction: instruction to execute transaction
     * Greeting: message containing the greeting
     */


    // used to catch unknown messages sent to this actor
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private static Integer idCounter = 0;

    //#Cart-messages
    static public Props props(Integer amount, ActorRef terminalActor) {
        return Props.create(Card.class, () -> new Card(amount, terminalActor));
    }

    //#transaction request receiver
    static public class whichTerminal {
        public final Integer terminalID;

        public whichTerminal(Integer terminalID) {
            this.terminalID = terminalID;
        }
    }


    static public class Transaction {
        public Transaction() {
        }
    }
    //#Card-messages

    private final Integer amount;
    private final ActorRef terminalActor;
    private final Integer id;
    private final Integer limit;

    private Integer[] limitPool = {5000,10000,20000,30000};

    public Card(Integer amount, ActorRef terminalActor) {
        this.amount = amount;
        this.terminalActor = terminalActor;

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
                    //#Card-send-message
                    terminalActor.tell(new receivedAmount(amount,id), getSelf());
                    //#Card-send-message
                })
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }
//#Card-messages
}
//#Card-messages
