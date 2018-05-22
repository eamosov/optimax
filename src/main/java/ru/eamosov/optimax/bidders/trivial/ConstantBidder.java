package ru.eamosov.optimax.bidders.trivial;

import ru.eamosov.optimax.bidders.AbstractBidder;

/**
 * Created by fluder on 19/05/2018.
 */
public class ConstantBidder extends AbstractBidder {

    private int bid;

    public ConstantBidder(int bid) {
        this.bid = bid;
    }

    @Override
    public int placeBid() {
        return Math.min(ownCash, bid);
    }

    @Override
    public String name() {
        return "ConstantBidder(" + bid + ")";
    }
}
