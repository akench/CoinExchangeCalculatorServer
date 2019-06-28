package com.atharvak.coinexchangecalculatorserver.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.*;

/**
 * Represents a bunch of coins as one unit.
 */
@Getter
@EqualsAndHashCode
public class MoneyPouch {
    private List<Integer> denoms;
    private List<Integer> freqs;

    public MoneyPouch() {
        denoms = new ArrayList<>();
        freqs = new ArrayList<>();
    }

    public MoneyPouch(final List<CoinFrequency> coinFrequencies) {
        this();
        for(CoinFrequency coinFrequency : coinFrequencies) {
            denoms.add(coinFrequency.getDenom());
            freqs.add(coinFrequency.getFreq());
        }
    }

    public MoneyPouch(final Map<Integer, Integer> map) {
        this();
        for (int denom : map.keySet()) {
            denoms.add(denom);
            freqs.add(map.get(denom));
        }
    }

    /**
     * Gets balance in money pouch
     * @return balance
     */
    public int getBalance() {
        int bal = 0;
        for (int i = 0; i < denoms.size(); i++) {
            bal += (denoms.get(i) * freqs.get(i));
        }
        return bal;
    }

    /**
     * Increases or decreases the frequency of a coin denomination
     *
     * @param denom denomination to modify
     * @param delta how much to inc or dec frequency by
     */
    public void changeCoinFreq(int denom, int delta) {
        int coinIndex = denoms.indexOf(denom);

        if (coinIndex != -1) {
            int newFreq = freqs.get(coinIndex) + delta;

            // if removing coins causes no coins to be left, remove from array
            if (newFreq <= 0) {
                denoms.remove(coinIndex);
                freqs.remove(coinIndex);
            } else {
                freqs.set(coinIndex, newFreq);
            }
        } else if (delta > 0) {
            // if coin denom not found, and are adding coins, then add to list
            denoms.add(denom);
            freqs.add(delta);
        }
        // if trying to remove coin, and coin doesn't exist, don't do anything
    }

    /**
     * Transfer some money from one money pouch to another
     * @param to transfer to this pouch
     * @param transferAmount amount to transfer
     */
    public void transferTo(final MoneyPouch to, final MoneyPouch transferAmount) {
        to.add(transferAmount);
        this.subtract(transferAmount);
    }

    /**
     * Add the contents of mp to this pouch
     * @param mp pouch to add
     */
    private void add(final MoneyPouch mp) {
        this.addOrRemove(mp, 1);
    }

    /**
     * Subtract the contents of mp from this pouch
     * @param mp pouch to subtract
     */
    private void subtract(final MoneyPouch mp) {
        this.addOrRemove(mp, -1);
    }

    /**
     * Helper function to add or remove coins based on a multiplier
     * @param mp target pouch
     * @param multiplier 1 or -1 (add or remove)
     */
    private void addOrRemove(final MoneyPouch mp, final int multiplier) {

        if(mp == null)
            return;

        for (int i = 0; i < mp.freqs.size(); i++) {
            int denom = mp.denoms.get(i);
            int freq = mp.freqs.get(i);
            this.changeCoinFreq(denom, freq * multiplier);
        }
    }

    /*
    10: 1
    20: 2
    30: 3


    10(1)
    20(1)
    20(2)
    30(1)
    30(2)
    30(3)

    10(1) 20(1)
    10(1) 20(2)
    10 30
    20 30

    10 20 30
     */

    /**
     *
     * @return
     */
    public Set<MoneyPouch> getSubsets() {

        final Set<MoneyPouch> set = new HashSet<>();

        // generate all frequency combinations for this money pouch
        final Set<List<Integer>> allFreqCombinations = this.genAllFrequencyCombinations();

        // 2^numelements combinations
        int numCombinations = (1 << denoms.size());
        for (int presentElements = 1; presentElements < numCombinations; presentElements++) {

            // list of indices corresponding to coin denoms in this pouch
            // the current subset of coins will only contains contain coins
            // from these indices
            final List<Integer> usingIndices = new ArrayList<>();

            int mask = 1;
            for (int i = 0; i < denoms.size(); i++) {
                // if not 0, we should add that element
                if ((presentElements & mask) != 0) {
                    usingIndices.add(i); // mark this index as being used
                }
                mask <<= 1; // move onto next element
            }

            // now we have a money pouch with some coins and frequencies
            // must create a new money pouch for every combination of frequencies using
            // these coins, and add to set...
            // the coins that are used are index by usingIndices array
            for (final List<Integer> combination : getFreqCombsForIndices(allFreqCombinations, usingIndices)) {
                // represents a money pouch with a specific frequency of coins which changes each loop
                final MoneyPouch mp = new MoneyPouch();

                for (int i = 0; i < usingIndices.size(); i++) {
                    // get the denomination at one index, and the frequency at that index
                    // in the combination
                    final int denomIndex = usingIndices.get(i);
                    mp.changeCoinFreq(denoms.get(denomIndex), combination.get(i));
                }

                set.add(mp);
            }
        }

        return set;
    }

    /*
    Gets a combination of frequency values only for given indices
    Used when needing to get all combinations of freq values for a subset
     */
    private Set<List<Integer>> getFreqCombsForIndices(final Set<List<Integer>> allFreqCombinations,
                                                      final List<Integer> indices) {

        Collections.sort(indices); // same order everytime
        final Set<List<Integer>> set = new HashSet<>();

        // for every combination, get the values at the given indices
        for (final List<Integer> combination : allFreqCombinations) {

            // add the combination of values at those indices
            final List<Integer> freqs = new ArrayList<>();
            for (final int i : indices) {
                freqs.add(combination.get(i));
            }
            set.add(freqs);
        }

        return set;
    }

    /**
     * Generates all frequency combinations for a base money pouch with max frequencies
     *
     * @return set of list of frequency values, corresponds to the coin denominations list
     */
    private Set<List<Integer>> genAllFrequencyCombinations() {
        final Set<List<Integer>> allFreqCombinations = new HashSet<>();
        genAllFrequencyCombinations(allFreqCombinations, new ArrayList<>(), 0);
        return allFreqCombinations;
    }

    /*
    2 3 4
    =====
    for a in 1..2
        for b in 1..3
            for c in 1..4
                add (a,b,c)

    111,112,113,114,121,122,123,124,131,132,133,134,
    211,212,213,214,221,222,223,224,231,232,233,234
     */
    private void genAllFrequencyCombinations(final Set<List<Integer>> allFreqCombinations,
                                             final List<Integer> prevState,
                                             final int freqIndex) {
        // base case, no more frequencies to add
        if (freqIndex >= freqs.size()) {
            allFreqCombinations.add(prevState);
            return;
        }

        // for every number between 1 and that frequency
        for (int n = 1; n <= freqs.get(freqIndex); n++) {
            final List<Integer> state = new ArrayList<>(prevState);
            state.add(n);

            // recursive call with one possible frequency added
            genAllFrequencyCombinations(allFreqCombinations, state, freqIndex + 1);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (int i = 0; i < denoms.size(); i++) {
            sb.append(denoms.get(i) + ": " + freqs.get(i));
            // only add comma if not the last element in list
            if (i < denoms.size() - 1)
                sb.append(", ");
        }
        sb.append('}');
        return sb.toString();
    }


    /**
     * Number of coins in this pouch
     * @return size int
     */
    public int size() {
        int sum = 0;
        for (int f : freqs) {
            sum += f;
        }
        return sum;
    }
}
