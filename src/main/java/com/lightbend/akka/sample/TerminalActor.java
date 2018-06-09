package com.lightbend.akka.sample;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.lightbend.akka.sample.TransactionList.receivedTransaction;

import java.util.Random;

import static com.lightbend.akka.sample.Constants.*;

public class Terminal_idInit extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    static public Props props(Integer id, Kind.terminalKind type) {
        return Props.create(Terminal_idInit.class, () -> new Terminal_idInit(id,type));
    }

    /**
     * Card request receiver.
     * Terminal receives a request from a card.
     * Directs the cards' and its own information to TransactionList,
     * where all transactions are recorded and validity decision made.
     */
    static public class receivedAmount {
        public final Integer amount;
        public final Integer cardID;
        private ActorRef transactionList;

        public receivedAmount(Integer amount, Integer cardID, ActorRef transactionList) {
            this.amount = amount;
            this.cardID = cardID;
            this.transactionList = transactionList;
        }
    }

    /**
     * Message: Record terminals to the list in transactionlist actor right after initializing the terminal.
     * This list only keeps all card information with their balance value.
     */
    static public class recordToList {
        private ActorRef transactionList;
        public recordToList(ActorRef transactionList) {
            this.transactionList = transactionList;
        }
    }

    private final Integer id;
    private final Kind.terminalKind type;
    private final Integer location;
    private final Integer merchantCategory;

    /**
     * During static ip creation at actor initialization, some of the actors had same id.
     * Id assignment with argument is a temporary solution.
     * @param id
     */
    public Terminal_idInit(Integer id, Kind.terminalKind type) {
        this.id = id;
        this.type = type;
        if(type == Kind.terminalKind.ATM)
            this.location = this.id;
        else
            this.location = atmLimit; // all POS devices located in Internet
        Random rn = new Random();
        this.merchantCategory = rn.nextInt(merchantLimit); // assign type of the merchant
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(receivedAmount.class, received -> {
                    //log.debug("\n#Received: Terminal " + id + " - Amount : " + received.amount.toString()
                    // + " - CardID : " + received.cardID.toString());
                    //log.info("#Received: Terminal " + id + " - Amount : " + received.amount.toString() + " - CardID : "
                    // + received.cardID.toString());
                    String timeStamp = TimeConverter.returnTime(System.currentTimeMillis());
                    // Direct to the TransactionList // possible to do it directly from card
                    received.transactionList.tell(new receivedTransaction(received.amount,received.cardID,id,timeStamp),
                            getSelf());
                }) // when request received, this message triggered
                .match(recordToList.class, list ->{
                    TerminalListElement terminalInfo = new TerminalListElement(id,type,merchantCategory);
                    list.transactionList.tell(new TransactionList.receivedTerminalInitialization(terminalInfo),getSelf());
                })
                .matchAny(o -> log.info("received unknown message")) // if non of the messages match
                .build();
    }
}
