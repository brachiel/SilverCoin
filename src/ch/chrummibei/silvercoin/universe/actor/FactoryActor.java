package ch.chrummibei.silvercoin.universe.actor;

import ch.chrummibei.silvercoin.universe.Universe;
import ch.chrummibei.silvercoin.universe.credit.Price;
import ch.chrummibei.silvercoin.universe.item.Recipe;
import ch.chrummibei.silvercoin.universe.space.Position;
import ch.chrummibei.silvercoin.universe.trade.Factory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A factors that ticks every now and then and produces products. It also offers to buy its ingredients
 * on the market to the last seen price. Also makes a spread profit with it.
 */
public class FactoryActor extends Factory implements PositionedActor, TimeStepActor {
    Position position;

    private final HashMap<Consumer<Long>, Timekeeper> timedActions = new HashMap<>();
    private double productionSpreadFactor = 1.2; // 20 Percent between ingredient costs and sell price
    private double ingredientOverBuyFactor = 1.5; // We're willing to buy 50% more per ingredient for the price


    public FactoryActor(Recipe recipe, int goalStock) {
        super(recipe, goalStock);
        addAction(this::buyIngredientsAndProduceProduct, Universe.getRandomInt(5000, 9000));
    }

    public void buyIngredientsAndProduceProduct(long timeDiffMillis) {
        offersPresentAtMarkets.forEach(market -> buyIngredients(market));
        produceProduct(timeDiffMillis);
    }

    @Override
    public void addProductToTradeOffer(int amount, Price price) {
        super.addProductToTradeOffer(amount, price.multiply(productionSpreadFactor));
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public void setPosition(Position position) {
        this.position = position;
    }

    // TimeStepActor methods
    @Override
    public void addAction(Consumer<Long> action, long periodicity) {
        timedActions.put(action, new Timekeeper(periodicity));
    }

    @Override
    public Map<Consumer<Long>, Timekeeper> getTimedActions() {
        return timedActions;
    }

    public void setSpread(double spread) {
        productionSpreadFactor = spread;
    }
}
