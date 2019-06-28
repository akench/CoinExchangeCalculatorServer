package com.atharvak.coinexchangecalculatorserver.utils;

import com.atharvak.coinexchangecalculatorserver.models.MoneyPouch;
import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = CoinExchangeCalculatorConfig.class)
public class CoinExchangerTest {

//    @Autowired
//    private CoinExchanger coinExchanger;

    @Test(expected = IllegalArgumentException.class)
    public void emptyPouches() {
        final MoneyPouch mpGiver = new MoneyPouch();
        final MoneyPouch mpReceiver = new MoneyPouch();

        CoinExchanger.giveMoney(1, mpGiver, mpReceiver);
    }

    @Test(expected = IllegalArgumentException.class)
    public void giverEmpty() {
        final MoneyPouch mpGiver = new MoneyPouch();
        final MoneyPouch mpReceiver = new MoneyPouch(
                ImmutableMap.of(
                        10, 2,
                        20, 1
                )
        );

        CoinExchanger.giveMoney(2, mpGiver, mpReceiver);
    }

    @Test
    public void receiverEmpty() {
        final MoneyPouch mpGiver = new MoneyPouch(
                ImmutableMap.of(
                        10, 2
                )
        );
        final MoneyPouch mpReceiver = new MoneyPouch();

        final MoneyPouch[] res = CoinExchanger.giveMoney(10, mpGiver, mpReceiver);

        verifyMoneyPouch(
                ImmutableMap.of(10, 1),
                res[0]
        );

        verifyMoneyPouch(ImmutableMap.of(), res[1]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeTransfer() {
        final MoneyPouch mpGiver = new MoneyPouch();
        final MoneyPouch mpReceiver = new MoneyPouch();

        mpGiver.changeCoinFreq(100, 1);

        mpReceiver.changeCoinFreq(20, 2);
        mpReceiver.changeCoinFreq(10, 2);
        mpReceiver.changeCoinFreq(1, 20);

        CoinExchanger.giveMoney(-1, mpGiver, mpReceiver);
    }

    @Test
    public void zeroTransfer() {
        final MoneyPouch mpGiver = new MoneyPouch();
        final MoneyPouch mpReceiver = new MoneyPouch();

        mpGiver.changeCoinFreq(100, 1);

        mpReceiver.changeCoinFreq(20, 2);
        mpReceiver.changeCoinFreq(10, 2);
        mpReceiver.changeCoinFreq(1, 20);

        final MoneyPouch[] res = CoinExchanger.giveMoney(0, mpGiver, mpReceiver);
        verifyMoneyPouch(ImmutableMap.of(), res[0]);
        verifyMoneyPouch(ImmutableMap.of(), res[1]);
    }

    @Test
    public void test1() {
        final MoneyPouch mpGiver = new MoneyPouch();
        final MoneyPouch mpReceiver = new MoneyPouch();

        mpGiver.changeCoinFreq(100, 1);

        mpReceiver.changeCoinFreq(20, 2);
        mpReceiver.changeCoinFreq(10, 2);
        mpReceiver.changeCoinFreq(1, 20);

        final MoneyPouch[] res = CoinExchanger.giveMoney(51, mpGiver, mpReceiver);

        verifyMoneyPouch(
                ImmutableMap.of(100, 1),
                res[0]
        );

        verifyMoneyPouch(
                ImmutableMap.of(
                        20, 2,
                        1, 9
                ),
                res[1]
        );
    }

    @Test
    public void test2() {
        final MoneyPouch mpGiver = new MoneyPouch();
        final MoneyPouch mpReceiver = new MoneyPouch();

        mpGiver.changeCoinFreq(10, 1);

        mpReceiver.changeCoinFreq(5, 2);

        final MoneyPouch[] res = CoinExchanger.giveMoney(5, mpGiver, mpReceiver);

        verifyMoneyPouch(
                ImmutableMap.of(10, 1),
                res[0]
        );

        verifyMoneyPouch(
                ImmutableMap.of(5, 1),
                res[1]
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidTest() {
        final MoneyPouch mpGiver = new MoneyPouch();
        final MoneyPouch mpReceiver = new MoneyPouch();

        mpGiver.changeCoinFreq(100, 1);

        mpReceiver.changeCoinFreq(20, 2);
        mpReceiver.changeCoinFreq(10, 2);
        mpReceiver.changeCoinFreq(1, 20);

        CoinExchanger.giveMoney(19, mpGiver, mpReceiver);
    }

    private void verifyMoneyPouch(final Map<Integer, Integer> expected, final MoneyPouch actualPouch) {

        final Map<Integer, Integer> actualMap = new HashMap<>();
        for (int i = 0; i < actualPouch.denoms.size(); i++) {
            final int denom = actualPouch.denoms.get(i);
            final int freq = actualPouch.freqs.get(i);

            actualMap.put(denom, freq);
        }

        Assert.assertEquals(expected, actualMap);
    }
}
