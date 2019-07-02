package com.atharvak.coinexchangecalculatorserver.controllers;

import com.atharvak.coinexchangecalculatorserver.models.GiverAndReceiverCoins;
import com.atharvak.coinexchangecalculatorserver.models.MoneyPouch;
import com.atharvak.coinexchangecalculatorserver.utils.CoinExchanger;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

// giverAndReceiverCoins={"transferAmount":51,"giverCoins":[{"denom":100,"freq":1}],"receiverCoins":[{"denom":20,"freq":2},{"denom":10,"freq":2},{"denom":1,"freq":10}]}
@RestController
public class CoinChangeCalculatorController {

    @Autowired
    private CoinExchanger coinExchanger;

    @CrossOrigin
    @RequestMapping("/giveMoney")
    public GiverAndReceiverCoins giverAndReceiverCoins(
            @RequestParam(value="giverAndReceiverCoins", defaultValue = "{}")
            final String giverAndReceiverCoinsJson) throws IOException {

        // convert JSON string to java object
        final GiverAndReceiverCoins giverAndReceiverCoins =
                new ObjectMapper().readValue(giverAndReceiverCoinsJson, GiverAndReceiverCoins.class);

        // convert the object into money pouches which are needed for computation
        final MoneyPouch giverMoneyPouch = getGiverMoneyPouch(giverAndReceiverCoins);
        final MoneyPouch receiverMoneyPouch = getReceiverMoneyPouch(giverAndReceiverCoins);

        // calculate the money pouches that represent how much money
        // each person will transfer to each other
        final MoneyPouch[] transferPouches = coinExchanger.giveMoney(
                giverAndReceiverCoins.getTransferAmount(),
                giverMoneyPouch,
                receiverMoneyPouch
        );

        // convert these money pouches to the response object
        return new GiverAndReceiverCoins(
                giverAndReceiverCoins.getTransferAmount(),
                transferPouches
        );
    }

    private MoneyPouch getGiverMoneyPouch(final GiverAndReceiverCoins giverAndReceiverCoins) {
        return new MoneyPouch(giverAndReceiverCoins.getGiverCoins());
    }

    private MoneyPouch getReceiverMoneyPouch(final GiverAndReceiverCoins giverAndReceiverCoins) {
        return new MoneyPouch(giverAndReceiverCoins.getReceiverCoins());
    }

}
