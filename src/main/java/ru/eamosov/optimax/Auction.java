package ru.eamosov.optimax;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.eamosov.optimax.bidders.AbstractBidder;

/**
 * Created by fluder on 19/05/2018.
 */
public class Auction {

    private static final Logger log = LoggerFactory.getLogger(Auction.class);

    public void doAuction(int quantity, int cash, AbstractBidder bidder1, AbstractBidder bidder2) {

        bidder1.init(quantity, cash);
        bidder2.init(quantity, cash);

        int qu = quantity;
        while (qu >= 2) {
            int bid1 = bidder1.placeBid();
            int bid2 = bidder2.placeBid();
            bidder1.bids(bid1, bid2);
            bidder2.bids(bid2, bid1);
            qu -= 2;
        }
    }
}
