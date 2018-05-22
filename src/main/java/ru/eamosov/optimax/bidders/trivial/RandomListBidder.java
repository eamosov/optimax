package ru.eamosov.optimax.bidders.trivial;

import ru.eamosov.optimax.bidders.AbstractBidder;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by fluder on 19/05/2018.
 */
public class RandomListBidder extends AbstractBidder {

    private Random rnd = new Random();
    public final int bids[];

    public RandomListBidder(int[] bids) {
        this.bids = bids;
    }

    @Override
    public int placeBid() {
        return Math.min(ownCash, bids[rnd.nextInt(bids.length)]);
    }

    @Override
    public String name() {
        return String.format("RandomListBidder(%s)", Arrays.toString(bids));
    }
}
