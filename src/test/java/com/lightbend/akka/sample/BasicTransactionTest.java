package com.lightbend.akka.sample;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import com.lightbend.akka.sample.Card.*;
import com.lightbend.akka.sample.Terminal.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BasicTransactionTest {
    static ActorSystem system;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create();
    }

    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void testCardRequestTransaction() {
        final TestKit testProbe = new TestKit(system);
        final ActorRef card1 = system.actorOf(Card.props(100, testProbe.getRef()));
        //card1.tell(new whichTerminal(1), ActorRef.noSender());
        card1.tell(new Transaction(), ActorRef.noSender());
        receivedAmount terminal1 = testProbe.expectMsgClass(receivedAmount.class);
        assertEquals(new Integer(100), terminal1.amount);
    }

}
