package com.lightbend.akka.sample;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.lightbend.akka.sample.Writer.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.lightbend.akka.sample.Constants.*;

public class TransactionList extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    static public Props props() {
        return Props.create(TransactionList.class, () -> new TransactionList());
    }

    /**
     * Transactions received by terminals are kept in the list.
     */
    static public class receivedTransaction {
        public final Integer amount;
        public final Integer cardID;
        public final Integer terminalID;
        public final String date;

        public receivedTransaction(Integer amount, Integer cardID, Integer terminalID, String date) {
            this.amount = amount;
            this.cardID = cardID;
            this.terminalID = terminalID;
            this.date = date;
        }
    }

    /**
     * balanceList keeps cards balance list.
     */
    private final HashMap<Integer, CardBalanceListElement> balanceList = new HashMap<Integer, CardBalanceListElement>();
    /**
     * terminalList keeps list of terminals.
     */
    private final HashMap<Integer, TerminalListElement> terminalList = new HashMap<Integer, TerminalListElement>();
    /**
     * TransactionList keeps all transaction logs weather it is valid or not.
     */
    private final List<TransactionListElement> transactionList = new ArrayList<>();

    /**
     * Last state of cards are kept in the list.
     */
    static public class receivedCardInitialization {
        public final Integer limit;
        public final Integer cardID;
        public final String date;
        public final Kind.cardKind cardType;
        public final Integer statementDate;
        public final Integer homeLocation;


        public receivedCardInitialization(Integer limit, Integer cardID, String date, Kind.cardKind cardType,
                                          Integer statementDate, Integer homeLocation) {
            this.limit = limit;
            this.cardID = cardID;
            this.date = date;
            this.cardType = cardType;
            this.statementDate = statementDate;
            this.homeLocation = homeLocation;
        }
    }

    static public class receivedTerminalInitialization {
        public final TerminalListElement terminalInfo;

        public receivedTerminalInitialization(TerminalListElement terminalInfo) {
            this.terminalInfo = terminalInfo;
         }
    }



    /**
     * Is date1 recent than date2
     * @param date1
     * @param date2
     * @return
     */
    private boolean compareDates(LocalDate date1, LocalDate date2){
        int comparison = date1.getYear() - date2.getYear();
        if(comparison == 0)
            comparison = date1.getMonthValue() - date2.getMonthValue();
        if(comparison == 0)
            comparison = date1.getDayOfMonth() - date2.getDayOfMonth();
        if(comparison > 0)
            return true;
        else return false;
    }

    /**
     * If statementDate passed after last transaction, balance status is updated to the limit again.
     * @param received
     * @param cardValues
     */
    private void checkAndUpdateBalanceStatus(receivedTransaction received, CardBalanceListElement cardValues){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern);
        LocalDate transactionReceivedDate = LocalDate.parse( received.date, formatter);
        LocalDate lastTransactionDate = LocalDate.parse( cardValues.lastUpdate, formatter);

        if (cardValues.cardType == Kind.cardKind.Debit) {
            /**
             * For debit card, if current date is different than last transaction date, daily limit updated
             * TODO date of the balance update should be recorded.
             *      When card has not enough balance even after update, it will update again in the next transaction.
             *      No error, but extra work.
             *      Extra variable can be added which shows last update is balance update or transaction update.
             */
            if (transactionReceivedDate.getDayOfMonth() != lastTransactionDate.getDayOfMonth()) {
            //if (compareDates(transactionReceivedDate, lastTransactionDate)) {
                cardValues.balance = cardValues.limit;
                balanceList.put(cardValues.cardID,cardValues);
                System.out.println("Debit card daily balance update! Card ID : " + cardValues.cardID + " Card Limit: "
                        + cardValues.limit);
            }
        }else{
            /**
             * For credit card, if statement date is recent than current date, continue without update
             */
            LocalDate statementDateForThisMonth = LocalDate.of(transactionReceivedDate.getYear(),
                    transactionReceivedDate.getMonth(),cardValues.statementDate);
            if (!compareDates(statementDateForThisMonth, transactionReceivedDate)) {
                /**
                 * If last transaction date is recent than statement date, do not update
                 */
                if (!compareDates(lastTransactionDate,statementDateForThisMonth)) {
                    cardValues.balance = cardValues.limit;
                    balanceList.put(cardValues.cardID,cardValues);
                    System.out.println("Monthly balance update Card ID : " + cardValues.cardID + " Card Limit: "
                            + cardValues.limit);
                }
            }
        }
    }

    // TODO balance list can be printed to a file before exiting simulation

    // constructor
    public TransactionList() {

    }

    /**
     * Creates its own writerTransactions for transactions
     */
    final ActorRef writerTransactions = getContext().system().actorOf(Writer.props());
    @Override
    public void preStart() throws IOException {
        writerTransactions.tell(new transactionFileCreate(),getSelf());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(receivedTransaction.class, received -> {
                    CardBalanceListElement cardValues = balanceList.get(received.cardID);

                    /**
                     * check for statement date / balance update
                     */
                    checkAndUpdateBalanceStatus(received,cardValues);

                    int balance = cardValues.balance;
                    int validity = 1;
                    int newBalance = balance;
                    if (balance >= received.amount) {
                        // valid
                        newBalance = balance - received.amount;
                        // log.info("\n#Valid Transaction Received: Terminal " + received.terminalID + " - Amount : "
                        // + received.amount + " - Previous Balance : " + balance + " - Remaining : " + newBalance
                        // + " - CardID : " + received.cardID);
                        System.out.println("#Valid Transaction Received: Terminal " + received.terminalID
                                + " - Amount : " + received.amount + " - Previous Balance : " + balance
                                + " - Remaining : " + newBalance + " - CardID : " + cardValues.cardID);
                        cardValues.balance = newBalance;
                        cardValues.lastUpdate = received.date;
                        balanceList.put(cardValues.cardID,cardValues);
                    } else {
                        // not valid
                        validity = 0;
                        //log.info("\n#Non-Valid Transaction Received: Terminal " + received.terminalID + " - Amount : "
                        // + received.amount + " - Balance : " + balance + " - CardID : " + received.cardID);
                        System.out.println("#Non-Valid Transaction Received: Terminal " + received.terminalID
                                + " - Amount : " + received.amount + " - Balance : " + balance + " - CardID : "
                                + cardValues.cardID);
                    }
                    // add received transaction to the list
                    TransactionListElement transactionValues = new TransactionListElement(cardValues.cardID,
                            received.terminalID,received.amount,balance,newBalance,validity,received.date);
                    transactionList.add(transactionValues);

                    writerTransactions.tell(new transactionAddLine(transactionValues,cardValues,terminalList.get(received.terminalID)),getSelf());

                })
                .match(receivedCardInitialization.class, received -> {
                    CardBalanceListElement cardValues = new CardBalanceListElement(received.cardID,
                            received.limit,received.date,received.limit,received.cardType,received.statementDate,
                            received.homeLocation);
                    balanceList.put(received.cardID,cardValues);
                    //log.info("\nCard information received from Card ID : " + received.cardID + " Card Limit: "
                    // + received.limit + " at " + received.date + " - Card initialized.");
                    System.out.println("Card information received from Card ID : " + received.cardID + " Card Limit: "
                            + received.limit + " at " + received.date + " - Card initialized.");
                })
                .match(receivedTerminalInitialization.class, received -> {
                    terminalList.put(received.terminalInfo.terminalID,received.terminalInfo);
                    System.out.println("Terminal information received from Terminal ID : "
                            + received.terminalInfo.terminalID + " - Terminal initialized.");
                })
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }
}
