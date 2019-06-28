package com.atharvak.coinexchangecalculatorserver.utils;

import com.atharvak.coinexchangecalculatorserver.models.MoneyPouch;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


/**
 * Class which handles logic of how two people
 * should exchange money so that person1 can give some
 * amount of money to person2.
 *
 * person A has some denominations of money with limited number
 * person B has some denominations of money with limited number
 *
 * person A wants to give $X to person B
 *
 * find transactions needed to make this possible
 */
@RequiredArgsConstructor
public class CoinExchanger {

    // dependency which handles how to make change out of a certain amount
    @NonNull
    private ChangeMaker changeMaker;


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

    /**
     * do coin problem with A's initial state, to make X change
     * if not possible, take one currency from B, and add it to A's state
     *
     * example:
     * A has one $10
     * B has two $5
     *
     * A must give $5 to B
     * initial A balance: 10
     * initial B balance 10
     *
     * final A balance: 5
     * final B balance 15
     *
     *
     * step 1: B needs to gain 5. Find a way to make $5 change with A
     *        not possible so transfer one currency to A
     * step 2: B->A $5
     *
     * A = {10: 1, 5: 1}
     * B = {5: 1}              needs to gain 10 (needs to gain 5 and lost 5)
     *
     * step 3: find way to make $10 change with A
     * step 4: A->B $10
     *
     * A = {5: 1}              bal = 5
     * B = {10: 1, 5: 1}       bal = 15
     *
     * returns
     *     money pouch representing the coins A must give B
     *     money pouch rep. coins B must give A
     *
     * @param transferAmount amount to transfer from giver to receiver
     * @param giverPouch giver's money
     * @param receiverPouch receiver's money
     * @return two money pouches representing how much each person transfer
     * @throws IllegalArgumentException
     */
    public MoneyPouch[] giveMoney(final int transferAmount,
                                  final MoneyPouch giverPouch,
                                  final MoneyPouch receiverPouch) throws IllegalArgumentException {

        if (transferAmount < 0) {
            throw new IllegalArgumentException("transfer amount cannot be negative");
        } else if (transferAmount == 0) {
            return new MoneyPouch[]{new MoneyPouch(), new MoneyPouch()};
        }

        final int receiverTargetBal = transferAmount + receiverPouch.getBalance();

        // initially, check if we can make change with A's pouch without
        // needing to transfer
        final MoneyPouch changeNoTransfer = changeMaker.getMinCoinsLimited(
                receiverTargetBal - receiverPouch.getBalance(),
                giverPouch);

        // able to make change without transfer
        if (changeNoTransfer.size() > 0) {
            return new MoneyPouch[]{changeNoTransfer, new MoneyPouch()};
        }

        // we need to transfer stuff from B to A, to be able to make the change
        // repeat for every possible transfer for B->A
        for (final MoneyPouch transferToGiverAmount : receiverPouch.getSubsets()) {
            receiverPouch.transferTo(giverPouch, transferToGiverAmount);

            final MoneyPouch transferToReceiver = changeMaker.getMinCoinsLimited(
                    receiverTargetBal - receiverPouch.getBalance(),
                    giverPouch);

            if (transferToReceiver.size() > 0) {
                // can make change after transferring... success
                return new MoneyPouch[]{transferToReceiver, transferToGiverAmount};
            } else {
                // failure, must revert the transfer and try another subset of receiver next loop
                giverPouch.transferTo(receiverPouch, transferToGiverAmount);
            }
        }

        // no sol. found using all available subsets
        throw new IllegalArgumentException("Unable to exchange the specified amount");
    }
}
