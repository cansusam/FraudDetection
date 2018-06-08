package com.lightbend.akka.sample.primitive;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.lightbend.akka.sample.Card_SimpleInit;

import java.io.IOException;

public class basicTransaction {
    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("transactions");
        try {

            final ActorRef terminalActor = system.actorOf(Terminal.props());
            final ActorRef cardActor = system.actorOf(Card.props(100,terminalActor));
            final ActorRef cardActor1 = system.actorOf(Card.props(200,terminalActor));
            final ActorRef cardActor2 = system.actorOf(Card.props(300,terminalActor));
            final ActorRef cardActor3 = system.actorOf(Card_SimpleInit.props());
            //#create-actors

            //#main-send-messages
//            do {
                cardActor.tell(new Card.Transaction(), ActorRef.noSender());
                cardActor1.tell(new Card.Transaction(), ActorRef.noSender());
                cardActor2.tell(new Card.Transaction(), ActorRef.noSender());
                cardActor.tell(new Card.Transaction(), ActorRef.noSender());
                cardActor2.tell(new Card.Transaction(), ActorRef.noSender());
//                cardActor3.tell(new Card_SimpleInit.Transaction(500,terminalActor),ActorRef.noSender());
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
