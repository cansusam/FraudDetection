package com.lightbend.akka.sample;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

//#Cart-messages
public class Terminal extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    //#Cart-messages
    static public Props props() {
        return Props.create(Terminal.class, () -> new Terminal());
    }

    //#printer-messages
    static public class receivedAmount {
        public final Integer amount;
        public final Integer cardID;

        public receivedAmount(Integer amount, Integer cardID) {
            this.amount = amount;
            this.cardID = cardID;
        }
    }

    private static Integer idCounter = 0;
    private final Integer id;

    public Terminal() {
        this.id = idCounter;
        idCounter++;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(receivedAmount.class, received -> {
                    log.info("Terminal " + id + " - Amount : " + received.amount.toString() + " - CardID : " + received.cardID.toString());
                })
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }
//#Card-messages
}
//#Card-messages
