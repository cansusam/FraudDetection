package com.lightbend.akka.sample;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class TerminalCreator extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    //#Cart-messages
    static public Props props(Integer terminalNumber) {
        return Props.create(TerminalCreator.class, () -> new TerminalCreator(terminalNumber));
    }

    private final Integer terminalNumber;

    public TerminalCreator(Integer terminalNumber) {
        this.terminalNumber = terminalNumber;
    }

    @Override
    public void preStart(){

    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }
}
