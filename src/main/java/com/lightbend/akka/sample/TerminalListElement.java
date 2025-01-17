package com.lightbend.akka.sample;

import static com.lightbend.akka.sample.Constants.*;

/**
 * Terminal list used only for data record.
 * 1- terminalID
 * 2- terminalType
 * 3- merchantCategory
 */
public class TerminalListElement {


    public Integer terminalID;
    public Kind.terminalKind terminalType;
    public Integer merchantCategory;

    TerminalListElement(Integer terminalID, Kind.terminalKind terminalType, Integer merchantCategory){
        this.terminalID = terminalID;
        this.terminalType = terminalType;
        this.merchantCategory = merchantCategory;
    }

    /**
     * Converts location to string.
     * @return
     */
    public String terminalLocation(){
        if(terminalID == atmLimit-1)
            return "International";
        else if(terminalID > atmLimit-1)
            return "Internet";
        return terminalID.toString();
    }

    /**
     * Converts merchantCategory to string.
     * @return
     */
    public String merchantName(){
        switch (merchantCategory){
            case 0: return "A";
            case 1: return "B";
            case 2: return "C";
            case 3: return "D";
            default: return "E";
        }
    }

}
