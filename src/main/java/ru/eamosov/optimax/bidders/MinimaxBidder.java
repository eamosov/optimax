package ru.eamosov.optimax.bidders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by fluder on 19/05/2018.
 */
public class MinimaxBidder extends AbstractBidder {

    private static final Logger log = LoggerFactory.getLogger(MinimaxBidder.class);

    private static class GameState {

        final AbstractBidder bidder;

        boolean filtered = false;

        double maxAvrScore;
        List<Integer> maxAvrScoreBids = null;

        double maxMinScore;
        List<Integer> maxMinScoreBids = null;

        GameState(final AbstractBidder bidder) {
            this.bidder = bidder;
        }

        GameState(final AbstractBidder bidder, double score) {
            this.bidder = bidder;
            this.maxAvrScore = score;
            this.maxMinScore = score;
        }

        @Override
        public String toString() {
            return "GameState{" +
                "maxAvrScore=" + maxAvrScore +
                ", maxAvrScoreBids=" + maxAvrScoreBids +
                ", maxMinScore=" + maxMinScore +
                ", maxMinScoreBids=" + maxMinScoreBids +
                '}';
        }
    }

    //Decision tree
    private Map<AbstractBidder, GameState> memo = new HashMap<>();

    private int quantityLimit = 10;

    private int levelLimit = 3;

    private GameState compute(AbstractBidder bidder, int level) {
        GameState m = memo.get(bidder);
        if (m != null) {
            return m;
        }
        m = doCompute(bidder, level);
        memo.put(bidder, m);
        return m;
    }

    private GameState doCompute(AbstractBidder bidder, int level) {

        if (bidder.heuristicIsExact()) {
            return new GameState(bidder, bidder.heuristicScore());
        }

        if (level > levelLimit && bidder.quantity > quantityLimit) {
            return new GameState(bidder, bidder.heuristicScore());
        }

        final GameState gameState = new GameState(bidder);
        gameState.maxAvrScoreBids = new ArrayList<>();
        gameState.maxMinScoreBids = new ArrayList<>();

        final Map<Integer, Map<Integer, GameState>> gameStates = new TreeMap<>();
        final Map<Integer, Map<Integer, GameState>> invertedGameStates = new TreeMap<>();

        //Build possible bids
        final Set<Integer> bids = new HashSet<>();
        bids.add(0);
        bids.add(1);
        for (int i = 1; i <= 4; i++) {
            bids.add(i * startCash / startQuantity - 1);
            bids.add(i * startCash / startQuantity);
            bids.add(i * startCash / startQuantity + 1);
        }
        bids.add(bidder.ownCash);
        bids.add(bidder.otherCash);

        //Make all possible combinations from this state
        for (int ownBid : bids) {
            for (int otherBid : concat(otherBids, bids)) {
                if (bidder.ownCash >= ownBid && bidder.otherCash >= otherBid) {
                    final AbstractBidder nextBidder = new AbstractBidder(bidder);
                    nextBidder.bids(ownBid, otherBid);
                    final GameState child = new GameState(nextBidder, nextBidder.heuristicScore());
                    gameStates.computeIfAbsent(ownBid, k -> new TreeMap<>()).put(otherBid, child);
                    invertedGameStates.computeIfAbsent(otherBid, k -> new TreeMap<>()).put(ownBid, child);
                }
            }
        }

        double currentHeuristic = bidder.heuristicScore();

        //Remove bad own bids
        for (Map.Entry<Integer, Map<Integer, GameState>> ownEntry : gameStates.entrySet()) {
            boolean filtered = false;

            for (Map.Entry<Integer, GameState> otherEntry : ownEntry.getValue().entrySet()) {
                if (otherEntry.getValue().bidder.heuristicScore() < (currentHeuristic - 1.0)) {
                    filtered = true;
                    break;
                }
            }

            if (filtered) {
                for (Map.Entry<Integer, GameState> otherEntry : ownEntry.getValue().entrySet()) {
                    otherEntry.getValue().filtered = true;
                }
            }
        }

        //Remove bad opponents' bids
        for (Map.Entry<Integer, Map<Integer, GameState>> otherEntry : invertedGameStates.entrySet()) {
            boolean filtered = true;

            for (Map.Entry<Integer, GameState> ownEntry : otherEntry.getValue().entrySet()) {
                if (!ownEntry.getValue().filtered && ownEntry.getValue().bidder.heuristicScore() < currentHeuristic) {
                    filtered = false;
                    break;
                }
            }

            if (filtered) {
                for (Map.Entry<Integer, GameState> ownEntry : otherEntry.getValue().entrySet()) {
                    ownEntry.getValue().filtered = true;
                }
            }
        }

        //Calculate maxMinScore and maxAvrScore
        for (Map.Entry<Integer, Map<Integer, GameState>> ownEntry : gameStates.entrySet()) {

            int ownBid = ownEntry.getKey();

            double avrScore = 0;
            double minScore = Double.MAX_VALUE;

            double sum = 0;

            for (Map.Entry<Integer, GameState> otherEntry : ownEntry.getValue().entrySet()) {

                int otherBid = otherEntry.getKey();
                GameState otherGameState = otherEntry.getValue();

                double k = 0.0;

                if (!otherGameState.filtered) {
                    otherGameState = compute(otherGameState.bidder, level + 1);

                    if (bidder.otherBids.contains(otherBid)) {
                        k = 1.0;
                    } else {
                        k = 0.3;
                    }

                    avrScore += k * otherGameState.maxAvrScore;
                    sum += k;

                }

                if (otherGameState.maxMinScore < minScore) {
                    minScore = otherGameState.maxMinScore;
                }
            }

            avrScore = (sum != 0) ? avrScore / sum : minScore;

            if (avrScore >= gameState.maxAvrScore) {
                if (avrScore > gameState.maxAvrScore) {
                    gameState.maxAvrScoreBids.clear();
                }
                gameState.maxAvrScore = avrScore;
                gameState.maxAvrScoreBids.add(ownBid);
            }

            if (minScore >= gameState.maxMinScore) {
                if (minScore > gameState.maxMinScore) {
                    gameState.maxMinScoreBids.clear();
                }
                gameState.maxMinScore = minScore;
                gameState.maxMinScoreBids.add(ownBid);
            }

        }

        if (gameState.maxAvrScore == 0) {
            gameState.maxAvrScore = gameState.maxMinScore;
        }

        return gameState;
    }

    private GameState findBestBid(AbstractBidder bidder) {
        return compute(bidder, 0);
    }

    @Override
    public int placeBid() {

        if (ownCash == 0) {
            return 0;
        }

        if (otherCash == 0) {
            return 1;
        }

        if (turns() == 0) {
            return 0;
        }

        //memo.clear();

        //remove old items from decision tree
        final Iterator<Map.Entry<AbstractBidder, GameState>> it = memo.entrySet().iterator();
        while (it.hasNext()) {
            final Map.Entry<AbstractBidder, GameState> e = it.next();
            if (e.getKey().quantity > quantityLimit + levelLimit * 2) {
                it.remove();
            }
        }

        final GameState gameState = findBestBid(this);


        if (quantity <= quantityLimit) {
            //trust maxMinScoreBids only for full decision tree
            final List<Integer> best = intersection(gameState.maxMinScoreBids, gameState.maxAvrScoreBids);

            if (!best.isEmpty()) {
                return best.get(0);
            }

            if (gameState.maxMinScore > startQuantity / 2) {
                return gameState.maxMinScoreBids.get(0);
            }
        }

        //choose bid with best scores
        if (!gameState.maxAvrScoreBids.isEmpty()) {
            return gameState.maxAvrScoreBids.get(0);
        }

        return gameState.maxMinScoreBids.get(0);

    }

    @Override
    public String name() {
        return "MinimaxBidder()";
    }

    @Override
    public void init(int quantity, int cash) {
        super.init(quantity, cash);
        memo.clear();
    }

    @Override
    public void bids(int own, int other) {
        super.bids(own, other);
        otherBids.add(other);
    }

    /**
     * Set quantity limit for building full decision tree
     *
     * @param quantityLimit
     */
    public void setQuantityLimit(int quantityLimit) {
        this.quantityLimit = quantityLimit;
    }

    /**
     * Set decision tree height
     *
     * @param levelLimit
     */
    public void setLevelLimit(int levelLimit) {
        this.levelLimit = levelLimit;
    }

    private static <E> List<E> intersection(final List<E> list1, final List<E> list2) {
        final ArrayList<E> result = new ArrayList<>();

        for (E o : list2) {
            if (list1.contains(o)) {
                result.add(o);
            }
        }

        return result;
    }

    private static <E> Collection<E> concat(Collection<E> a, E... b) {
        return Stream.concat(a.stream(), Arrays.stream(b)).collect(Collectors.toList());
    }

    private static <E> Collection<E> concat(Collection<E> a, Collection<E> b) {
        return Stream.concat(a.stream(), b.stream()).collect(Collectors.toList());
    }

}
