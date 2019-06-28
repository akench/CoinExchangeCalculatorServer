package com.atharvak.coinexchangecalculatorserver.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CoinFrequency {
    public int denom;
    public int freq;
}
