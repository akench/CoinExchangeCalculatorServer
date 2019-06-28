package com.atharvak.coinexchangecalculatorserver.utils;

import com.atharvak.coinexchangecalculatorserver.models.MoneyPouch;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = CoinExchangeCalculatorConfig.class)
public class CoinExchangerTest {

//    @Autowired
//    private CoinExchanger coinExchanger;

    @Test
    public void basicTest() {
        MoneyPouch mpA = new MoneyPouch();
        MoneyPouch mpB = new MoneyPouch();

        mpA.changeCoinFreq(100, 1);

        mpB.changeCoinFreq(20, 2);
        mpB.changeCoinFreq(10, 2);
        mpB.changeCoinFreq(1, 20);

        MoneyPouch[] res = CoinExchanger.giveMoney(51, mpA, mpB);

        if (res != null) {
            System.out.println("A gives to B " + res[0]);
            System.out.println("B gives to A " + res[1]);
        } else {
            System.out.println("invalid transaction");
        }
    }
}
