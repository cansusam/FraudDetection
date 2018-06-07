package com.lightbend.akka.sample;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.lightbend.akka.sample.Card.*;
import com.lightbend.akka.sample.Terminal.*;

import java.io.IOException;

import static java.sql.Types.NULL;

public class basicTransaction {
    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("transactions");
        try {

            final ActorRef terminalActor = system.actorOf(Terminal.props());
            final ActorRef cardActor = system.actorOf(Card.props(100,terminalActor));
            final ActorRef cardActor1 = system.actorOf(Card.props(200,terminalActor));
            final ActorRef cardActor2 = system.actorOf(Card.props(300,terminalActor));
            //#create-actors

            //#main-send-messages
//            do {
                cardActor.tell(new Card.Transaction(), ActorRef.noSender());
                cardActor1.tell(new Card.Transaction(), ActorRef.noSender());
                cardActor2.tell(new Card.Transaction(), ActorRef.noSender());
                cardActor.tell(new Card.Transaction(), ActorRef.noSender());
                cardActor2.tell(new Card.Transaction(), ActorRef.noSender());
//            }while (System.in.available() == 0);
            //#main-send-messages

            System.out.println(">>> Press ENTER to exit <<<");
            System.in.read();
        } catch (IOException ioe) {
        } finally {
            system.terminate();
        }
    }
}
