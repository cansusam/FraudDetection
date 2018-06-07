package com.lightbend.akka.sample;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.lightbend.akka.sample.Greeter.*;

import java.io.IOException;

import static java.sql.Types.NULL;

public class basicTransaction {
    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("helloakka");
        try {

            final ActorRef terminalActor = system.actorOf(Terminal.props(), "terminalActor");
            final ActorRef cardActor = system.actorOf(Card.props(100,terminalActor), "cardActor");
            //#create-actors

            //#main-send-messages
            do {
                cardActor.tell(new Card.Transaction(), ActorRef.noSender());
            }while (System.in.available() == 0);
            //#main-send-messages

            System.out.println(">>> Press ENTER to exit <<<");
            System.in.read();
        } catch (IOException ioe) {
        } finally {
            system.terminate();
        }
    }
}
