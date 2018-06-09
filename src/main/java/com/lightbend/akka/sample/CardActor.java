package com.lightbend.akka.sample;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.lightbend.akka.sample.TerminalActor.receivedAmount;
import com.lightbend.akka.sample.TransactionList.receivedCardInitialization;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.util.Random;

import static com.lightbend.akka.sample.Constants.*;

//#Cart-messages
public class CardActor extends AbstractActor {
    // used to catch unknown messages sent to this actor
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    static public Props props(Integer cardID) {
        return Props.create(CardActor.class, () -> new CardActor(cardID));
    }

    /**
     * Message: Sending transaction information to the terminal
     */
    static public class Transaction {
        private Integer amount;
        private ActorRef terminalActor;
        private ActorRef transactionListActor;

        /**
         * Payment amount with using which device
         * @param amount
         * @param terminalActor
         */
        public Transaction(Integer amount, ActorRef terminalActor, ActorRef transactionListActor) {
            this.amount = amount;
            this.terminalActor = terminalActor;
            this.transactionListActor = transactionListActor;
        }
    }

    /**
     * Message: Record cards to the list in transactionlist actor right after initializing the card.
     * This list only keeps all card information with their balance value.
     */
    static public class recordToList {
        private ActorRef transactionList;
        public recordToList(ActorRef transactionList) {
            this.transactionList = transactionList;
        }
    }

    public final Integer id;
    public final Integer limit;
    public final Kind.cardKind type;
    public final Integer homeLocation;
    public final Integer statementDate; // day of the month (for credit cards)

    public CardActor(Integer cardID) {
        this.id = cardID;

        // kind of the card, 50/50
        if(Math.random() < 0.5) {
            this.type = Kind.cardKind.Debit;
        }else
            this.type = Kind.cardKind.Credit;

        Random rn = new Random();
        if (type == Kind.cardKind.Debit) {
            this.limit = debitLimit; // Daily limit
            this.statementDate = 1; // not important
        }
        else{
            this.limit = cardLimitPool[limitSelection()]; // Monthly limit
            this.statementDate = rn.nextInt(maxValueOfStatementDay) + 1;
        }
        this.homeLocation = rn.nextInt(atmLimit); // 81 for International
    }

    /**
     * Select limit according to determined ratios
     */
    private int limitSelection(){
        Random rn = new Random(); // assign random limit
        double rand;
        int limitIndex = 0;
        Double[] limitProbs = calculateLimitProbs();
        //Calculate
        rand = rn.nextFloat();
        for (int i = 0; i < cardLimitPool.length; i++) {
            if (rand < limitProbs[i]) {
                limitIndex = i;
                break;
            }
        }
        return limitIndex;
    }

    /**
     * Probabilistic distribution of card limits
     * @return
     */
    private Double[] calculateLimitProbs(){
        Double[] probArray = new Double[limitRatios.length];
        Double totalProb = 0.0;
        Double ratiosTotal = 0.0;
        for (int i = 0; i < limitRatios.length; i++) {
            ratiosTotal += limitRatios[i];
        }
        for (int i = 0; i < limitRatios.length; i++) {
            probArray[i] = totalProb + limitRatios[i]/ratiosTotal;
            totalProb = probArray[i];
        }
        return probArray;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Transaction.class, x -> {
                    // Send message of transaction to the related terminal
                    x.terminalActor.tell(new receivedAmount(x.amount,id,x.transactionListActor), getSelf());
                })
                .match(recordToList.class, list ->{
                    String timeStamp = TimeConverter.returnTime(System.currentTimeMillis());
                    list.transactionList.tell(new receivedCardInitialization(limit,id,timeStamp,type,statementDate,
                            homeLocation),getSelf());
                })
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }
//#Card-messages
}
//#Card-messages