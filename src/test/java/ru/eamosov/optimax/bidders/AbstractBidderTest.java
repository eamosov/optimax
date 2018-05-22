package ru.eamosov.optimax.bidders;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by fluder on 20/05/2018.
 */
public class AbstractBidderTest {

    @Test
    public void testBids() {

        final AbstractBidder bidder = new AbstractBidder();
        bidder.init(10, 20);
        Assert.assertEquals(10, bidder.quantity);
        Assert.assertEquals(10, bidder.startQuantity);
        Assert.assertEquals(20, bidder.ownCash);
        Assert.assertEquals(20, bidder.otherCash);
        Assert.assertEquals(0, bidder.ownWins);
        Assert.assertEquals(0, bidder.otherWins);
        Assert.assertEquals(0, bidder.turns());

        bidder.bids(2, 3);
        Assert.assertEquals(8, bidder.quantity);
        Assert.assertEquals(10, bidder.startQuantity);
        Assert.assertEquals(18, bidder.ownCash);
        Assert.assertEquals(17, bidder.otherCash);
        Assert.assertEquals(0, bidder.ownWins);
        Assert.assertEquals(2, bidder.otherWins);
        Assert.assertEquals(1, bidder.turns());

        bidder.bids(5, 4);
        Assert.assertEquals(6, bidder.quantity);
        Assert.assertEquals(10, bidder.startQuantity);
        Assert.assertEquals(13, bidder.ownCash);
        Assert.assertEquals(13, bidder.otherCash);
        Assert.assertEquals(2, bidder.ownWins);
        Assert.assertEquals(2, bidder.otherWins);
        Assert.assertEquals(2, bidder.turns());

        bidder.bids(5, 5);
        Assert.assertEquals(4, bidder.quantity);
        Assert.assertEquals(10, bidder.startQuantity);
        Assert.assertEquals(8, bidder.ownCash);
        Assert.assertEquals(8, bidder.otherCash);
        Assert.assertEquals(3, bidder.ownWins);
        Assert.assertEquals(3, bidder.otherWins);
        Assert.assertEquals(3, bidder.turns());

    }

    @Test
    public void testHeuristicScore() {

        final AbstractBidder bidder = new AbstractBidder();
        bidder.init(10, 20);

        Assert.assertEquals(5.0, bidder.heuristicScore(), 0.0);
        bidder.bids(2, 2);
        Assert.assertEquals(5.0, bidder.heuristicScore(), 0.0);
        bidder.bids(2, 2);
        Assert.assertEquals(5.0, bidder.heuristicScore(), 0.0);
        bidder.bids(2, 2);

        bidder.init(10, 200);
        bidder.bids(79, 79);
        Assert.assertEquals(5.0, bidder.heuristicScore(), 0.0);

        bidder.bids(40, 79);
        Assert.assertEquals(4.95, bidder.heuristicScore(), 0.1);

        bidder.init(10, 20);
        bidder.bids(9, 3);
        Assert.assertEquals(5.1, bidder.heuristicScore(), 0.1);

        bidder.init(10, 20);
        bidder.bids(8, 3);
        Assert.assertEquals(5.3, bidder.heuristicScore(), 0.1);

        bidder.init(10, 20);
        bidder.bids(0, 3);
        Assert.assertEquals(4.32, bidder.heuristicScore(), 0.1);

        bidder.init(10, 20);
        bidder.bids(20, 20);
        Assert.assertEquals(5.0, bidder.heuristicScore(), 0.0);

    }
}