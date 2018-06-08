package com.lightbend.akka.sample;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.lightbend.akka.sample.Terminal_idInit.receivedAmount;
import com.lightbend.akka.sample.TransactionList.receivedCardInitialization;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.text.SimpleDateFormat;
import java.util.Random;

//#Cart-messages
public class Card_SimpleInit extends AbstractActor {
    // used to catch unknown messages sent to this actor
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    static public Props props() {
        return Props.create(Card_SimpleInit.class, () -> new Card_SimpleInit());
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

    private static Integer idCounter = 0;
    private final Integer id;
    private final Integer limit;
    private final Kind.cardKind type;
    private final Integer homeLocation;
    private Integer[] limitPool = {5000,10000,20000,30000};
    private Integer[] limitRatios = {4,3,2,1};

    public Card_SimpleInit() {
        this.id = idCounter; // each Card will have unique ID
        idCounter++;

        // kind of the card, 50/50
        if(Math.random() < 0.5) {
            this.type = Kind.cardKind.Debit;
        }else
            this.type = Kind.cardKind.Credit;

        if(type == Kind.cardKind.Debit)
            this.limit = 1000; // Daily limit
        else
            this.limit = limitPool[limitSelection()]; // Monthly limit

        Random rn = new Random();
        this.homeLocation = rn.nextInt(82); // 81 for International
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
        for (int i = 0; i < limitPool.length; i++) {
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

        Double ratiosTotal = 0.0; //
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
                    //String timeStamp = new SimpleDateFormat("HHmmss").format(new java.util.Date());
                    String timeStamp = TimeConverter.returnTime(System.currentTimeMillis());
                    list.transactionList.tell(new receivedCardInitialization(limit,id,timeStamp),getSelf());
                })
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }
//#Card-messages
}
//#Card-messages
