# SilverCoin
A space trade simulator

# Why?
I always wanted to do this; I don't know what the result will be - maybe a programming game, maybe just a massive market simulation.

# TODO
* Introduce actual space actors (moving actors).
* Introduce multiple markets.
* Factories that cannot buy due to lacking sell offers should instead place a buy order.
* Use gdx-Ashley's Entity System to clean up Actor-inheritance mess
* Use gdxAI to implement Actor AI-logic.
* Introduce a script language (possibly LUA) to implement behaviour tree leaf tasks.
* Replace Resources class by libGDX AssetManager


# Known Bugs
* Debugging reveals, that factories have inventory items with item=null.

# Screenshot
![SilverCoin screenshot](https://raw.githubusercontent.com/brachiel/SilverCoin/master/SilverCoin.png)
