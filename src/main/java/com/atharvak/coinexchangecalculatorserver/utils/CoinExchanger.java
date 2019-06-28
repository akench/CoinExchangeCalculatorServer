package com.atharvak.coinexchangecalculatorserver.utils;

import com.atharvak.coinexchangecalculatorserver.models.MoneyPouch;

/*
person A has some denominations of money with limited number
person B has some denominations of money with limited number

person A wants to give $X to person B

find transactions needed to make this possible
 */
public class CoinExchanger {

    public static void main(String[] args) {
        MoneyPouch mpA = new MoneyPouch();
        MoneyPouch mpB = new MoneyPouch();

//        mpA.changeCoinFreq(100, 1);
//
//        mpB.changeCoinFreq(20, 2);
//        mpB.changeCoinFreq(10, 2);
//        mpB.changeCoinFreq(1, 20);

        mpA.changeCoinFreq(10, 1);
        mpB.changeCoinFreq(5, 2);
        MoneyPouch[] res = giveMoney(5, mpA, mpB);

        if (res != null) {
            System.out.println("A gives to B " + res[0]);
            System.out.println("B gives to A " + res[1]);
        } else {
            System.out.println("invalid transaction");
        }
    }

    /*
    do coin problem with A's initial state, to make X change
    if not possible, take one currency from B, and add it to A's state

    example:
    A has one $10
    B has two $5

    A must give $5 to B
    initial A balance: 10
    initial B balance 10

    final A balance: 5
    final B balance 15


    step 1: B needs to gain 5. Find a way to make $5 change with A
            not possible so transfer one currency to A
    step 2: B->A $5

    A = {10: 1, 5: 1}
    B = {5: 1}              needs to gain 10 (needs to gain 5 and lost 5)

    step 3: find way to make $10 change with A
    step 4: A->B $10

    A = {5: 1}              bal = 5
    B = {10: 1, 5: 1}       bal = 15

    returns
        money pouch representing the coins A must give B
        money pouch rep. coins B must give A

    */
    public static MoneyPouch[] giveMoney(int transferAmount, MoneyPouch pouchA, MoneyPouch pouchB) throws IllegalArgumentException {

        if (transferAmount < 0) {
            throw new IllegalArgumentException("transfer amount cannot be negative");
        } else if (transferAmount == 0) {
            return new MoneyPouch[]{new MoneyPouch(), new MoneyPouch()};
        }

        int targetBalanceB = transferAmount + pouchB.getBalance();

        // initially, check if we can make change with A's pouch without
        // needing to transfer
        MoneyPouch changeNoTransfer = ChangeMaker.getMinCoinsLimited(
                targetBalanceB - pouchB.getBalance(),
                pouchA);

        // able to make change without transfer
        if (changeNoTransfer.size() > 0) {
            return new MoneyPouch[]{changeNoTransfer, new MoneyPouch()};
        }

        // we need to transfer stuff from B to A, to be able to make the change
        // repeat for every possible transfer for B->A
        for (MoneyPouch transferToA : pouchB.getSubsets()) {
            pouchB.transferTo(pouchA, transferToA);

            MoneyPouch transferToB = ChangeMaker.getMinCoinsLimited(
                    targetBalanceB - pouchB.getBalance(),
                    pouchA);

            if (transferToB.size() > 0) {
                // can make change after transferring... success
                return new MoneyPouch[]{transferToB, transferToA};
            } else {
                // failure, must revert the transfer and try another subset of B next loop
                pouchA.transferTo(pouchB, transferToA);
            }
        }

        // no sol. found using all available subsets
        throw new IllegalArgumentException("Unable to exchange the specified amount");
    }

}
