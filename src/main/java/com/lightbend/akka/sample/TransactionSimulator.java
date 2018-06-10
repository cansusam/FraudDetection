package com.lightbend.akka.sample;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;
import org.apache.commons.math3.distribution.IntegerDistribution;

import static com.lightbend.akka.sample.Constants.*;

/**
 * Main file for simulation.
 * Actor system and its three main actor types (transactions actor, terminal actors and card actors)
 * are created in this file.
 */
public class TransactionSimulator {

    /**
     * System time recorded to be used for time acceleration calculations.
     * @param args
     */
    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("transactions");
        try {
            //simulation start time recorded
            TimeConverter.startTime = System.currentTimeMillis();

            //list of transactions
            final ActorRef transactions = system.actorOf(TransactionList.props());
            // TODO cards and terminals can be created with another actor
            final List<ActorRef> terminalList = new ArrayList<ActorRef>();
            final List<ActorRef> cardList = new ArrayList<ActorRef>();

            generateTerminals(terminalList,transactions,system);
            generateCards(cardList,transactions,system);

            if(endlessSimulationON)
                endlessSimulation(system,cardList,terminalList,transactions);
            else
                limitedTransactions(system,cardList,terminalList,transactions);

            System.out.println(">>> Press ENTER to exit <<<");
            System.in.read();
        } catch (IOException ioe) {
        } finally {
            system.terminate();
        }
    }

    /**
     * Creating terminals for simulation.
     *
     * Maximum ATM number is 81. Until ATM number reach to its limit,
     * proportion possibility of ATM/POS will be 1/1;
     *
     * Terminal IDs for
     *  - ATMs will be in [0,80] range
     *  - POS devices will be higher than 80
     * @param terminalList
     * @param system
     */
    public static void generateTerminals(List<ActorRef> terminalList, ActorRef transactions, ActorSystem system){
        int atmNumber = 0;
        int posNumber = 0;
        int terminalID;
        for (int i = 0; i < terminalNumber; i++) {
            Kind.terminalKind type;
            if ((Math.random() < 0.5) && atmNumber < atmLimit) {
                type = Kind.terminalKind.ATM;
                terminalID = atmNumber;
                atmNumber++;
            } else {
                type = Kind.terminalKind.POS;
                terminalID = posNumber + atmLimit;
                posNumber++;
            }
            terminalList.add(system.actorOf(TerminalActor.props(terminalID,type)));
            terminalList.get(i).tell(new TerminalActor.recordToList(transactions),ActorRef.noSender());
        }
    }

    /**
     * Creating cards for simulation.
     *
     * @param cardList
     * @param transactions
     * @param system
     */
    public static void generateCards(List<ActorRef> cardList, ActorRef transactions, ActorSystem system){
        for (int i = 0; i < cardNumber; i++) {
            cardList.add(system.actorOf(CardActor.props(i)));
            cardList.get(i).tell(new CardActor.recordToList(transactions),ActorRef.noSender());
        }
    }

    /**
     * Scheduling a transaction for a randomly selected card and terminal with random amount selected from predefined
     * array.
     * Scheduling duration can be selected either from predefined array or randomly with setting
     * "schedulingWithRandomIntervalON" constant on/off.
     *
     * @param system
     * @param cardList
     * @param terminalList
     * @param transactions
     */
    public static void schedulingTransactions(ActorSystem system, List<ActorRef> cardList, List<ActorRef> terminalList,
                                              ActorRef transactions){
        Random rn = new Random();
        int randomCard = rn.nextInt(cardNumber); // cards selected uniformly
        int randomDuration;
        int randomAmount = amountList[rn.nextInt(amountList.length)];
        int randomTerminal = rn.nextInt(terminalNumber);
        if (schedulingWithRandomIntervalON)
            randomDuration = rn.nextInt(schedulingDurationsInterval);
        else
            randomDuration = schedulingDurationsMS[rn.nextInt(schedulingDurationsMS.length)];

        system.scheduler().scheduleOnce(Duration.ofMillis(randomDuration),
                () -> cardList.get(randomCard).tell(new CardActor.Transaction(randomAmount,
                        terminalList.get(randomTerminal),transactions),ActorRef.noSender()), system.dispatcher());
    }

    /**
     * List elements
     * 1-amountList
     * 2-schedulingList
     * 3-terminalArray
     */
    private static HashMap<Integer, List<IntegerDistribution>> cardFeatureDistributions = new HashMap<>();
    private static ValidDistributionRanges validRanges = new ValidDistributionRanges();
    private static int terminalArray[] = new int[terminalNumber];

    /**
     *
     */
    public static void rangeGeneration() {
        for(int i=0;i<terminalNumber;i++)
            terminalArray[i] = i;
        validRangeCreate(amountList,validRanges.amountLowerLimits,validRanges.amountUpperLimits);
        validRangeCreate(schedulingDurationsMS,validRanges.schedulingLowerLimits,validRanges.schedulingUpperLimits);
        validRangeCreate(terminalArray,validRanges.terminalLowerLimits,validRanges.terminalUpperLimits);
    }

    /**
     * For separable(valid/nonValid) data generation, ranges for each feature are created.
     *
     * @param featureLowerLimits
     * @param featureUpperLimits
     */
    public static void validRangeCreate(int[] array, Integer[] featureLowerLimits,Integer[] featureUpperLimits){
        Random rn = new Random();
        int length = array.length;
        int windowSize = (int)Math.floor(length*windowRatio);
        int randNumber;
        for (int i = 0; i <cardNumber ; i++) {
            randNumber =  rn.nextInt(length-windowSize);
            featureLowerLimits[i] = array[randNumber];
            featureUpperLimits[i] = array[randNumber+windowSize];
        }
    }

    /**
     * For each user, different feature distributions are generated.
     */
    public static void recordFeatureDistributions() {
        for (int j = 0; j < distributionNumber; j++) {
            List<IntegerDistribution> featureDistributions = new ArrayList<IntegerDistribution>();
            featureDistributions.add(generateDistributions(amountList));
            featureDistributions.add(generateDistributions(schedulingDurationsMS));
            featureDistributions.add(generateDistributions(terminalArray));
            cardFeatureDistributions.put(j,featureDistributions);
        }
    }

    /**
     * Feature are distributed according to generated distributions
     * @param feature
     * @return
     */
    public static IntegerDistribution generateDistributions(int[] feature){
        Random rn = new Random();
        double probs[] = new double[feature.length];
        IntegerDistribution dist;
        //IntStream a = new Random().ints(amountList.length, 0, amountList.length)
        for (int i = 0; i < feature.length; i++) {
            probs[i] = (double) rn.nextInt(feature.length);
        }
        dist = new EnumeratedIntegerDistribution(feature,probs);

        System.out.print("");
        return dist;
    }

    /**
     * Discrete probability distribution used for amount, scheduletime and terminal
     * @param system
     * @param cardList
     * @param terminalList
     * @param transactions
     */
    public static void discreteSchedulingTransactions(ActorSystem system, List<ActorRef> cardList, List<ActorRef> terminalList,
                                              ActorRef transactions){
        Random rn = new Random();
        int randomCard = rn.nextInt(cardNumber); // cards selected uniformly

        int randomDistribution =  randomCard%distributionNumber;

        int randomAmount = cardFeatureDistributions.get(randomDistribution).get(0).sample();
        int randomDuration = cardFeatureDistributions.get(randomDistribution).get(1).sample();
        int randomTerminal = cardFeatureDistributions.get(randomDistribution).get(2).sample();

        int valid;
        valid = isTransactionValid(randomDistribution,randomAmount,randomDuration,randomTerminal);

        system.scheduler().scheduleOnce(Duration.ofMillis(randomDuration),
                () -> cardList.get(randomCard).tell(new CardActor.ValidTransaction(randomAmount,
                        terminalList.get(randomTerminal),transactions,valid),ActorRef.noSender()), system.dispatcher());
    }

    /**
     * Check if the transaction is valid (in the range of predetermined distribution)
     * @param amount
     * @param duration
     * @param terminal
     * @return
     */
    public static int isTransactionValid(int distribution, int amount, int duration,int terminal) {
        if(validRanges.amountLowerLimits[distribution] <= amount
                && validRanges.amountUpperLimits[distribution] >= amount
                && validRanges.schedulingLowerLimits[distribution] <= duration
                && validRanges.schedulingUpperLimits[distribution] >= duration
                && validRanges.terminalLowerLimits[distribution] <= terminal
                && validRanges.terminalUpperLimits[distribution] >= terminal)
            return 1;
        return 0;

    }

    /**
     *
     * @param system
     * @param cardList
     * @param terminalList
     * @param transactions
     */
    public static void gaussSchedulingTransactions(ActorSystem system, List<ActorRef> cardList, List<ActorRef> terminalList,
                                              ActorRef transactions){
        Random rn = new Random();
        int randomCard = rn.nextInt(cardNumber); // cards selected uniformly
        int randomDuration;

        int randomAmount = amountList[rn.nextInt(amountList.length)];
//        int amountMax = amountList[amountList.length-1];
//        int randomAmount = generateGaussValue(amountMax,10,randomCard,1);

        int randomTerminal = generateGaussValue(terminalNumber,0,randomCard,1);

        int durationMax = schedulingDurationsMS[schedulingDurationsMS.length-1];
        int durationMin = schedulingDurationsMS[0];
        randomDuration = generateGaussValue(durationMax,durationMin,randomCard,1);

        system.scheduler().scheduleOnce(Duration.ofMillis(randomDuration),
                () -> cardList.get(randomCard).tell(new CardActor.Transaction(randomAmount,
                        terminalList.get(randomTerminal),transactions),ActorRef.noSender()), system.dispatcher());
    }

    /**
     * Normally distributed numbers returned according to given parameters for features.
     * @param upperLimit
     * @param lowerLimit
     * @param user
     * @param feature
     * @return
     */
    static private int generateGaussValue(int upperLimit, int lowerLimit, int user, int feature){
        Random rn = new Random();
        double gvalue;
        int result;
        int mean = (upperLimit+lowerLimit)/2;
        int std = (user+feature)%(mean/2); // for user and feature specific variations
        do {
            gvalue = rn.nextGaussian()*std + mean;
            result = (int) Math.round(gvalue);
        } while (result < lowerLimit || result > upperLimit);

        return result;
    }

    /**
     * Simulation continues until key press.
     * @param system
     * @param cardList
     * @param terminalList
     * @param transactions
     * @throws IOException
     */
    public static void endlessSimulation(ActorSystem system, List<ActorRef> cardList, List<ActorRef> terminalList,
                                         ActorRef transactions) throws IOException{
        do {
            if(discreteDistroON){
                rangeGeneration();
                recordFeatureDistributions();
                discreteSchedulingTransactions(system,cardList,terminalList,transactions);
            }
            else if(!gaussDistroON)
                schedulingTransactions(system,cardList,terminalList,transactions);
            else
                gaussSchedulingTransactions(system,cardList,terminalList,transactions);
        }while (System.in.available() == 0);
    }

    /**
     * Simulation with limited number of transactions
     * @param system
     * @param cardList
     * @param terminalList
     * @param transactions
     */
    public static void limitedTransactions(ActorSystem system, List<ActorRef> cardList, List<ActorRef> terminalList,
                                           ActorRef transactions){
        for (int i=0; i<numberOfTransactions; i++) {
            if(discreteDistroON){
                rangeGeneration();
                recordFeatureDistributions();
                discreteSchedulingTransactions(system,cardList,terminalList,transactions);
            }
            else if(!gaussDistroON)
                schedulingTransactions(system,cardList,terminalList,transactions);
            else
                gaussSchedulingTransactions(system,cardList,terminalList,transactions);
        }
    }

}
