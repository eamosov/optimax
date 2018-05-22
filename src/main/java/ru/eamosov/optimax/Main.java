package ru.eamosov.optimax;

import ru.eamosov.optimax.bidders.MinimaxBidder;
import ru.eamosov.optimax.bidders.trivial.ConstantBidder;

/**
 * Created by fluder on 19/05/2018.
 */
public class Main {

    public static void main(String argv[]) {

        //        WiseBidder w = new WiseBidder();
        //        w.init(10,10);
        //        w.bids(3,4);
        //
        //        //bidder1:{cash=7, wins=0, metrica=5.0}, bidder2:{cash=6, wins=2, metrica=5.0}
        //        System.out.println(w);

        //        WiseBidder w2 = new WiseBidder();
        //        w2.init(10,100);
        //        w2.otherCash = 4;
        //        w2.otherWins = 2;
        //        w2.ownCash = 0;
        //        w2.ownWins = 4;
        //        w2.turns = 2;
        //        System.out.println(w2.metrica());

        int wins = 0;
        int fails = 0;
        int draws = 0;
        for (int i = 0; i < 10; i++) {
            final Auction auction = new Auction();
            MinimaxBidder wise = new MinimaxBidder();
            //WiseBidder2 wise2 = new WiseBidder2();
            //wise.setLevelLimit(3);
            //wise.setQuantityLimit(16);
            //wise2.setLevelLimit(3);
            //wise.setUseHisto(false);
            //wise.setBidsSizeLimit(100);
            //wise.setLevelLimit(1);
            //auction.doAuction(100, 200, wise, wise2);
            auction.doAuction(200, 400, wise, new ConstantBidder(4));
            //auction.doAuction(10, 20, wise, new ConstantBidder(3));
            //WiseBidder3 wise3 = new WiseBidder3();
            //wise3.setUseHisto(false);
            //auction.doAuction(100, 200, wise, wise3);
            //auction.doAuction(10, 200, wise, new ConstantBidder(79));

            //            System.out.println(wise.histo);
            System.out.println(String.format("%d/%d", wise.getOwnWins(), wise.getOtherWins()));

            if (wise.getOwnWins() > wise.getOtherWins()) {
                wins++;
            } else if (wise.getOwnWins() < wise.getOtherWins()) {
                fails++;
            } else {
                draws++;
            }

            System.out.println(String.format("wins/fails/draws = %d/%d/%d", wins, fails, draws));
        }
    }
}
