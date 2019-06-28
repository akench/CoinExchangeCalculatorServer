package com.atharvak.coinexchangecalculatorserver.utils;

import com.atharvak.coinexchangecalculatorserver.models.MoneyPouch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChangeMaker {

    public static void main(String[] args) {

        List<Integer> denoms = new ArrayList<>();
        denoms.add(1);
        denoms.add(2);
        denoms.add(5);

        List<Integer> freqs = new ArrayList<>();
        freqs.add(1);
        freqs.add(100);
        freqs.add(4);

        MoneyPouch mp = new MoneyPouch(denoms, freqs);

        MoneyPouch resPouch = getMinCoinsLimited(10001, mp);


        System.out.println("bal: " + resPouch.getBalance());
    }

    // wrapper method
    static MoneyPouch getMinCoinsLimited(int amount, MoneyPouch mp) {
        CoinMapTuple tuple = getMinCoinsLimited(amount, 0, mp.denoms, mp.freqs);
        return new MoneyPouch(tuple.map);
    }

    /*
    Gets the coins needed to make change for an amount with a limited number
    of coins for each denomination

    stores which coins are needed and how many of them, in a hashmap

    return a tuple of the min coins and the hashmap of coin freqs
     */
    private static CoinMapTuple getMinCoinsLimited(int amount,
                                                   int denomIndex,
                                                   List<Integer> denoms,
                                                   List<Integer> freqs) {
        // base case
        if (amount <= 0)
            return new CoinMapTuple();

        if (denomIndex >= denoms.size())
            return new CoinMapTuple(Integer.MAX_VALUE - 1, new HashMap<>());

        CoinMapTuple minPicked = new CoinMapTuple(Integer.MAX_VALUE - 1, new HashMap<>());
        CoinMapTuple minNotPicked;
        // pick
        // if can pick
        int denom = denoms.get(denomIndex);
        if (amount - denom >= 0 && freqs.get(denomIndex) > 0) {

            // make a new list with that frequency decremented
            List<Integer> newFreqs = new ArrayList<>(freqs);
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
            int coinVal = denoms.get(denomIndex);

            // frequency of that value in the hashmap
            int freq = minPicked.map.containsKey(coinVal) ? minPicked.map.get(coinVal) : 0;

            // increment that frequency because we just picked this coin again
            minPicked.map.put(coinVal, freq + 1);

            return minPicked;
        }
        // if not picking that coin gave least number of coins
        else {
            return minNotPicked;
        }

    }


    static class CoinMapTuple {
        int numCoins;
        Map<Integer, Integer> map;

        public CoinMapTuple() {
            numCoins = 0;
            map = new HashMap<>();
        }

        public CoinMapTuple(int n, Map<Integer, Integer> m) {
            numCoins = n;
            map = m;
        }
    }


////////////////////////////////////////////////////////////////////////

    private static int getMinCoinsBasic(int amount, ArrayList<Integer> denoms) {

        // base case
        if (amount <= 0)
            return 0;

        if (denoms.size() == 0)
            return Integer.MAX_VALUE - 1;

        int minPicked = Integer.MAX_VALUE - 1;
        int minNotPicked;
        // pick
        // if can pick
        int denom = denoms.get(0);
        if (amount - denom >= 0) {
            minPicked = 1 + getMinCoinsBasic(amount - denom, denoms);
        }

        // not pick
        ArrayList<Integer> newDenoms = new ArrayList<>(denoms);
        newDenoms.remove(0);
        minNotPicked = getMinCoinsBasic(amount, newDenoms);

        // take min of both options
        return Math.min(minNotPicked, minPicked);
    }
}