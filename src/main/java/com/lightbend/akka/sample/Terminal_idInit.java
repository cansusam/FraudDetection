package com.lightbend.akka.sample;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

//#Cart-messages
public class Terminal_idInit extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    //#Cart-messages
    static public Props props(Integer id) {
        return Props.create(Terminal_idInit.class, () -> new Terminal_idInit(id));
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

    public Terminal_idInit(Integer id) {
        this.id = id;
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
