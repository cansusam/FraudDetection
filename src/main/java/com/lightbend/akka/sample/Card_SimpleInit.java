package com.lightbend.akka.sample;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.lightbend.akka.sample.Terminal_idInit.receivedAmount;
import akka.event.Logging;
import akka.event.LoggingAdapter;

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

    static public class Transaction {
        private Integer amount;
        private ActorRef terminalActor;
        public Transaction(Integer amount, ActorRef terminalActor) {
            this.amount = amount;
            this.terminalActor = terminalActor;
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
                    //#Card-send-message
                    x.terminalActor.tell(new receivedAmount(x.amount,id), getSelf());
                    //#Card-send-message
                })
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }
//#Card-messages
}
//#Card-messages
