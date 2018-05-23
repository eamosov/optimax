Main Bidder is ru.eamosov.optimax.bidders.MinimaxBidder , it implements auction.Bidder interface and can be used
just after constructing the object in any potential test without additional turning. Also, the project contains
tests which create competitions between MinimaxBidder and trivial bot’s implementations (ConstantBidder, RandomBidder, RandomListBidder).

The main idea for this bot is accommodate the well-known Minimax algorithm. There is a heuristic score function AbstractBidder.heuristicScore()
which computes the relative position of bidder (probable wins count at the end of auction). Minimax algorithm is implemented in MinimaxBidder.
It tries to choose that bid, which maximise average profit for any opponent’s bid. Also, it computes the minimum possible profit which can be achieved.
If bot finds such bid which leads to win, it uses it. Otherwise it uses the bid with maximum average profit. Algorithm doesn’t check all possible bids,
but just 0, 1, k * MU/AU +/- 1, and bids are made by opponent before. Algorithm filter combinations which lead to decreasing
my profit or opponent’s profit in any case and doesn't use them in calculation of average. Also bot uses increased weight
for calculating average for bids which exist in the auction history.

One of the main drawback of this realisation - is it's speed. It can be optimised but I decided it isn't in the context of my task, 
as I don't know any restrictions on MU and AU and don't know the results of competition between my realisation and other bots. 
Also I approached this task creatively and payed attention more on logic then on other formal things,
as I am interested in such algorithms  and I wanted to create really strong bot than beautiful code for the time I was recommended to spend on this task.
