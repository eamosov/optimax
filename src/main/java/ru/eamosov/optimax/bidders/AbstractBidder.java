package ru.eamosov.optimax.bidders;

import auction.Bidder;

import java.util.Objects;
import java.util.TreeSet;

/**
 * Created by fluder on 19/05/2018.
 */
public class AbstractBidder implements Bidder {

    /**
     * Current number of auctioned item
     */
    protected int quantity;

    /**
     * Start number of auctioned item
     */
    protected int startQuantity;

    /**
     * Start cash
     */
    protected int startCash;

    /**
     * Current own cash
     */
    protected int ownCash;

    /**
     * Current other's cash
     */
    protected int otherCash;

    /**
     * Number of own winned QU
     */
    protected int ownWins;

    /**
     * Number of ther's winned QU
     */
    protected int otherWins;

    //Any previous opponents' bids
    protected TreeSet<Integer> otherBids = new TreeSet<>();

    public AbstractBidder() {

    }

    public AbstractBidder(AbstractBidder other) {
        other.copy(this);
    }

    /**
     * Copy state to dst
     *
     * @param dst
     */
    void copy(AbstractBidder dst) {
        dst.quantity = quantity;
        dst.startQuantity = startQuantity;
        dst.startCash = startCash;
        dst.ownCash = ownCash;
        dst.otherCash = otherCash;
        dst.ownWins = ownWins;
        dst.otherWins = otherWins;
        dst.otherBids.clear();
        dst.otherBids.addAll(otherBids);
    }

    /**
     * Initializes the bidder with the production quantity and the allowed cash limit.
     *
     * @param quantity the quantity
     * @param cash     the cash limit
     */
    @Override
    public void init(int quantity, int cash) {

        if (quantity < 2 || quantity % 2 != 0) {
            throw new RuntimeException("Invalid quantity: " + quantity);
        }

        this.quantity = quantity;
        this.startQuantity = quantity;
        this.startCash = cash;
        this.ownCash = cash;
        this.otherCash = cash;
        this.ownWins = 0;
        this.otherWins = 0;
        this.otherBids.clear();
    }

    /**
     * Shows the bids of the two bidders.
     *
     * @param own   the bid of this bidder
     * @param other the bid of the other bidder
     */
    @Override
    public void bids(int own, int other) {

        if (own > ownCash || own < 0) {
            throw new RuntimeException("Invalid own: " + own + ", ownCash:" + ownCash);
        }

        if (other > otherCash || other < 0) {
            throw new RuntimeException("Invalid other: " + other + ", otherCash:" + otherCash);
        }

        ownCash -= own;
        otherCash -= other;
        quantity -= 2;
        if (own == other) {
            ownWins += 1;
            otherWins += 1;
        } else if (own > other) {
            ownWins += 2;
        } else {
            otherWins += 2;
        }
    }

    public int getOwnCash() {
        return ownCash;
    }

    public int getOwnWins() {
        return ownWins;
    }

    public int getOtherWins() {
        return otherWins;
    }

    public boolean isEqual() {
        return ownWins == otherWins && ownCash == otherCash;
    }

    public boolean heuristicIsExact() {
        return quantity == 0 || ownCash == 0 || otherCash == 0;
    }

    /**
     * utility function, returning the approximate wins at the end of auction
     *
     * @return
     */
    public double heuristicScore() {

        if (quantity == 0) {
            return ownWins;
        }

        if (isEqual()) {
            return startQuantity / 2.0;
        }

        if (ownCash == 0 && otherCash == 0) {
            return ownWins + quantity / 2.0;
        } else if (ownCash == 0) {
            return ownWins + (quantity - Math.min(otherCash * 2, quantity)) / 2.0;
        } else if (otherCash == 0) {
            return ownWins + Math.min(ownCash * 2, quantity) + (quantity - Math.min(ownCash * 2, quantity)) / 2.0;
        }

        return ownWins + (startQuantity - ownWins - otherWins) * (double) ownCash / (ownCash + otherCash);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AbstractBidder that = (AbstractBidder) o;
        return quantity == that.quantity &&
            startQuantity == that.startQuantity &&
            startCash == that.startCash &&
            ownCash == that.ownCash &&
            otherCash == that.otherCash &&
            ownWins == that.ownWins &&
            otherWins == that.otherWins;
    }

    @Override
    public int hashCode() {
        return Objects.hash(quantity, startQuantity, startCash, ownCash, otherCash, ownWins, otherWins, otherBids);
    }

    @Override
    public String toString() {
        return "AbstractBidder{" +
            "quantity=" + quantity +
            ", startQuantity=" + startQuantity +
            ", startCash=" + startCash +
            ", ownCash=" + ownCash +
            ", otherCash=" + otherCash +
            ", ownWins=" + ownWins +
            ", otherWins=" + otherWins +
            '}';
    }

    public String name() {
        return "AbstractBidder()";
    }

    @Override
    public int placeBid() {
        throw new IllegalStateException();
    }

    public int turns() {
        return (startQuantity - quantity) / 2;
    }
}
