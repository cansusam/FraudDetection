package com.lightbend.akka.sample;


/**
 * Instead of using numbers for each type,
 * card and terminal kinds defined.
 */
public class Kind {
    public enum cardKind {Debit, Credit}
    public enum terminalKind {POS, ATM}
}
