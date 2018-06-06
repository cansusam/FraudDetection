package com.lightbend.akka.sample;

import akka.actor.AbstractActor;
import akka.actor.ActorLogging;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class EnvironmentManager extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    public static Props props() {
        return Props.create(EnvironmentManager.class);
    }

    @Override
    public void preStart() {
        log.info("Simulation Environment started");
    }

    @Override
    public void postStop() {
        log.info("Simulation Environment stopped");
    }

    // No need to handle any messages
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .build();
    }

}