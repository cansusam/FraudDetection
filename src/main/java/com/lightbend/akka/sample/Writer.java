package com.lightbend.akka.sample;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

// TODO This class will be responsible of writing transactions to the .csv

//#writerTransactions-messages
public class Writer extends AbstractActor {
    //#writerTransactions-messages
    static public Props props() {
        return Props.create(Writer.class, () -> new Writer());
    }

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    static private String fileName;

    /**
     * Add new transaction info as a line into the csv file
     */
    static public class transactionAddLine{
        public transactionAddLine(TransactionListElement lineInfo) throws IOException {

            String line = lineInfo.cardID
                    + "," + lineInfo.terminalID
                    + "," + lineInfo.amount
                    + "," + lineInfo.balance
                    + "," + lineInfo.remaining
                    + "," + lineInfo.validity
                    + "," + lineInfo.date;

            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
            writer.append("\n"+line);
            writer.close();
        }
    }

    /**
     * Create transaction log file .csv
     */
    static public class transactionFileCreate {
        public transactionFileCreate() throws IOException {
            // 1th file is transaction list
            // CSV file headlines
            String headlines = "CardID,TerminalID,Amount,Balance,Remaining,Validity,Date";
            // File name with date/time
            DateFormat df = new SimpleDateFormat("yyyyMMdd-HHmmss");
            df.setTimeZone(TimeZone.getTimeZone("GMT+3"));
            String date = df.format(new Date());
            fileName = "transactions-" + date + ".csv";
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(headlines);
            writer.close();
        }
    }

    public Writer() {
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(transactionFileCreate.class, create -> {
                    log.info("file created");
                })
                .build();
    }
//#writerTransactions-messages
}
//#writerTransactions-messages
