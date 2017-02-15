package ch.chrummibei.silvercoin.universe.actor;

import ch.chrummibei.silvercoin.universe.credit.Price;
import ch.chrummibei.silvercoin.universe.item.Recipe;
import ch.chrummibei.silvercoin.universe.space.Position;
import ch.chrummibei.silvercoin.universe.trade.Factory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by brachiel on 15/02/2017.
 */
public class FactoryActor extends Factory implements PositionedActor, TimeStepActionActor {
    Position position;

    private HashMap<Consumer<Long>, Timekeeper> timedActions = new HashMap<>();
    private double productionSpreadFactor = 1.2; // 20 Percent between ingredient costs and sell price
    private double ingredientOverBuyFactor = 1.5; // We're willing to buy 50% more per ingredient for the price


    public FactoryActor(Recipe recipe, int goalStock) {
        super(recipe, goalStock);
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
}