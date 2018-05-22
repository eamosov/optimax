package ru.eamosov.optimax.bidders.trivial;

import ru.eamosov.optimax.bidders.AbstractBidder;

import java.util.Random;

/**
 * Created by fluder on 19/05/2018.
 */
public class RandomBidder extends AbstractBidder {

    private Random rnd = new Random();
    private int min;
    private int max;

    public RandomBidder(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public int placeBid() {
        return Math.min(ownCash, min + rnd.nextInt(max - min + 1));
    }

    @Override
    public String name() {
        return String.format("RandomBidder(%d,%d)", min, max);
    }
}
