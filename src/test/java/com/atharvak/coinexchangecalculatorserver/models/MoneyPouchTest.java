package com.atharvak.coinexchangecalculatorserver.models;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MoneyPouchTest {

    @Test
    public void subsetTest() {
        final MoneyPouch mp = new MoneyPouch(
                ImmutableMap.of(
                        1, 1,
                        2, 2,
                        3, 3
                )
        );

        final Set<MoneyPouch> actualSubsets = mp.getSubsets();
        System.out.println(actualSubsets.size());
    }

    private void verifyMoneyPouch(final Map<Integer, Integer> expected, final MoneyPouch actualPouch) {

        final Map<Integer, Integer> actualMap = new HashMap<>();
        for (int i = 0; i < actualPouch.getDenoms().size(); i++) {
            final int denom = actualPouch.getDenoms().get(i);
            final int freq = actualPouch.getFreqs().get(i);

            actualMap.put(denom, freq);
        }

        Assert.assertEquals(expected, actualMap);
    }
}
