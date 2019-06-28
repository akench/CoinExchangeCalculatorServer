package com.atharvak.coinexchangecalculatorserver.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
public class GiverAndReceiverCoins {
    private int transferAmount;
    private List<CoinFrequency> giverCoins;
    private List<CoinFrequency> receiverCoins;

    public GiverAndReceiverCoins(final int transferAmount, final MoneyPouch[] moneyPouches) {
        if(moneyPouches.length != 2) {
            throw new IllegalArgumentException("List must have two elements");
        }

        this.transferAmount = transferAmount;
        this.giverCoins = pouchToCoinFrequencies(moneyPouches[0]);
        this.receiverCoins = pouchToCoinFrequencies(moneyPouches[1]);
    }

    private List<CoinFrequency> pouchToCoinFrequencies(final MoneyPouch mp) {
        final List<CoinFrequency> coinFrequencies = new ArrayList<>();
        for(int i = 0; i < mp.denoms.size(); i++) {
            final int denom = mp.denoms.get(i);
            final int freq = mp.denoms.get(i);
            coinFrequencies.add(new CoinFrequency(denom, freq));
        }

        return coinFrequencies;
    }
}
