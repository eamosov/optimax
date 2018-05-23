package ru.eamosov.optimax.bidders;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.eamosov.optimax.Auction;
import ru.eamosov.optimax.bidders.trivial.ConstantBidder;
import ru.eamosov.optimax.bidders.trivial.RandomBidder;
import ru.eamosov.optimax.bidders.trivial.RandomListBidder;

/**
 * Created by fluder on 19/05/2018.
 */
public class MinimaxBidderTest {

    private static final Logger log = LoggerFactory.getLogger(MinimaxBidderTest.class);

    private AbstractBidder makeBidder(){
        final MinimaxBidder bidder =  new MinimaxBidder();
        bidder.setLevelLimit(2);
        return  bidder;
    }

    private void testMultipleTimes(int startQuantity, int startCash, int times, AbstractBidder bidder1, AbstractBidder bidder2, boolean allowEq) {

        final Auction auction = new Auction();

        int wins = 0;
        int fails = 0;
        int draws = 0;

        for (int n = 0; n < times; n++) {
            auction.doAuction(startQuantity, startCash, bidder1, bidder2);

//            log.debug("Auction({},{}) {} / {} = {} / {}", startQuantity, startCash, bidder1.name(), bidder2.name(), bidder1
//                .getOwnWins(), bidder2.getOwnWins());

            if (bidder1.getOwnWins() > bidder2.getOwnWins()) {
                wins++;
            } else if (bidder1.getOwnWins() < bidder2.getOwnWins()) {
                fails++;
            } else {
                draws++;
            }
        }

        log.info("Auction({},{}) stats {} / {} = {} / {} / {}", startQuantity, startCash, bidder1.name(), bidder2.name(), wins, fails, draws);
        MatcherAssert.assertThat(String.format("Auction(%d,%d) stats %s / %s = %d / %d / %d", startQuantity, startCash, bidder1
                                     .name(), bidder2.name(), wins, fails, draws),
                                 wins,
                                 allowEq ? Matchers.greaterThanOrEqualTo(fails): Matchers.greaterThan(fails));
    }

    @org.junit.Test
    public void testWiseBidderAgainstConstantBidder() {
        for (int i = 3; i < 10; i++) {
            testMultipleTimes(10, 20, 2, makeBidder(), new ConstantBidder(i), true);
            testMultipleTimes(100, 200, 2, makeBidder(), new ConstantBidder(i), true);
        }
    }

    @org.junit.Test
    public void testWiseBidderAgainstRandomBidder() {

        for (int i = 0; i < 40; i++) {
            testMultipleTimes(10, 20, 10, makeBidder(), new RandomBidder(0, i), true);
            testMultipleTimes(100, 200, 10, makeBidder(), new RandomBidder(0, i), true);
        }

    }

    @org.junit.Test
    public void testWiseBidderAgainstRandomListBidder_0_1_8() {

        testMultipleTimes(10, 20, 100, makeBidder(), new RandomListBidder(new int[]{0, 1, 8}), true);
        testMultipleTimes(100, 200, 10, makeBidder(), new RandomListBidder(new int[]{0, 1, 8}), true);

    }

    @org.junit.Test
    public void testWiseBidderAgainstRandomListBidder_0_1_8_10() {

        testMultipleTimes(10, 20, 100, makeBidder(), new RandomListBidder(new int[]{0, 1, 8, 10}), true);
        testMultipleTimes(100, 200, 10, makeBidder(), new RandomListBidder(new int[]{0, 1, 8, 10}), true);

    }

    @org.junit.Test
    public void testWiseBidderAgainstRandomListBidder_0_9() {

        testMultipleTimes(10, 20, 100, makeBidder(), new RandomListBidder(new int[]{0, 9}), true);
        testMultipleTimes(100, 200, 10, makeBidder(), new RandomListBidder(new int[]{0, 9}), true);

    }

    @org.junit.Test
    public void testWiseBidder_10_200() {

        for (int n = 0; n < 20; n++) {
            testMultipleTimes(10, 200, 2, makeBidder(), new ConstantBidder(75 + n), true);
        }
    }

    @org.junit.Test
    public void testWiseBidder_2_2() {
        testMultipleTimes(2, 2, 10, makeBidder(), new RandomBidder(0,2), true);
    }

    @org.junit.Test
    public void testWiseBidder_2_2_constant() {
        for (int n = 0; n < 3; n++) {
            testMultipleTimes(2, 2, 1, makeBidder(), new ConstantBidder(n), true);
        }
    }

}