package com.atharvak.coinexchangecalculatorserver.utils;

import com.atharvak.coinexchangecalculatorserver.models.MoneyPouch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to calculate how to make change for an amount using
 * a limited number of coins in a money pouch.
 */
public class ChangeMaker {

    /**
     * Get a money pouch representing the best way (min number of coins)
     * to make change for an amount using an input money pouch.
     * @param amount make change for this amount
     * @param mp available coins in money pouch
     * @return money pouch showing how to make change
     */
    public MoneyPouch getMinCoinsLimited(final int amount, final MoneyPouch mp) {
        final CoinMapTuple tuple = getMinCoinsLimited(amount, 0, mp.getDenoms(), mp.getFreqs());
        return new MoneyPouch(tuple.map);
    }


    /**
     * Gets the coins needed to make change for an amount with a limited number
     * of coins for each denomination
     *
     * stores which coins are needed and how many of them, in a hashmap
     * return a tuple of the min coins and the hashmap of coin freqs
     *
     * @param amount amount to make change
     * @param denomIndex which denomination we are currently procecssing
     * @param denoms list of denomination
     * @param freqs number of coins for that denomination
     * @return minimum number of coins, and hashmap of denom->freq
     */
    private CoinMapTuple getMinCoinsLimited(final int amount,
                                            final int denomIndex,
                                            final List<Integer> denoms,
                                            final List<Integer> freqs) {
        // base case
        if (amount <= 0)
            return new CoinMapTuple();

        if (denomIndex >= denoms.size())
            return new CoinMapTuple(Integer.MAX_VALUE - 1, new HashMap<>());

        CoinMapTuple minPicked = new CoinMapTuple(Integer.MAX_VALUE - 1, new HashMap<>());
        CoinMapTuple minNotPicked;
        // pick
        // if can pick
        final int denom = denoms.get(denomIndex);
        if (amount - denom >= 0 && freqs.get(denomIndex) > 0) {

            // make a new list with that frequency decremented
            final List<Integer> newFreqs = new ArrayList<>(freqs);
            newFreqs.set(denomIndex, newFreqs.get(denomIndex) - 1); //decrement

            // recursive call with a smaller amount
            // add 1 to number of coins because we picked the current coin
            minPicked = getMinCoinsLimited(amount - denom, denomIndex, denoms, newFreqs);
            minPicked.numCoins += 1;
        }

        // never pick the current coin!
        // increment denomIndex because we will never pick that coin
        minNotPicked = getMinCoinsLimited(amount, denomIndex + 1, denoms, freqs);

        // if picking that coin gave least number of coins,
        // so increment frequency of that in hashmap
        if (minPicked.numCoins < minNotPicked.numCoins) {

            // get value of picked coin
            final int coinVal = denoms.get(denomIndex);

            // frequency of that value in the hashmap
            final int freq = minPicked.map.getOrDefault(coinVal, 0);

            // increment that frequency because we just picked this coin again
            minPicked.map.put(coinVal, freq + 1);

            return minPicked;
        }
        // if not picking that coin gave least number of coins
        else {
            return minNotPicked;
        }
    }

    /**
     * Simple tuple class
     */
    class CoinMapTuple {
        int numCoins;
        Map<Integer, Integer> map;

        CoinMapTuple() {
            numCoins = 0;
            map = new HashMap<>();
        }

        CoinMapTuple(int n, Map<Integer, Integer> m) {
            numCoins = n;
            map = m;
        }
    }
}
