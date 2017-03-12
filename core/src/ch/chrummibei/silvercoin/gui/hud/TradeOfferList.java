package ch.chrummibei.silvercoin.gui.hud;

import ch.chrummibei.silvercoin.constants.Messages;
import ch.chrummibei.silvercoin.universe.Universe;
import ch.chrummibei.silvercoin.universe.components.TradeSphereComponent;
import ch.chrummibei.silvercoin.universe.entity_systems.Mappers;
import ch.chrummibei.silvercoin.universe.trade.TradeOffer;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * Created by brachiel on 24/02/2017.
 */
public class TradeOfferList extends Table {
    private final Skin skin;
    private final static ClickListener tradeAcceptListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            if (event.getButton() != Input.Buttons.LEFT) return;

            TextButton clickedButton = (TextButton) event.getListenerActor();
            TradeOffer offer = (TradeOffer) clickedButton.getUserObject();
            offer.accept(Universe.player, offer.getAmount());

            // Deactivate button
            clickedButton.setDisabled(true);
        }
    };
    private final Telegraph telegraph = new Telegraph() {
        @Override
        public boolean handleMessage(Telegram msg) {
            Entity entity;
            TradeOffer offer;

            switch (msg.message) {
                case Messages.PLAYER_BEGINS_SEEING_TRADER:
                    entity = (Entity) msg.extraInfo;
                    seenTraders.add(entity);
                    Mappers.trader.get(entity).getTradeOffers().forEach(ofr -> addTrade(ofr));
                    return true;
                case Messages.PLAYER_ENDS_SEEING_TRADER:
                    entity = (Entity) msg.extraInfo;
                    seenTraders.remove(entity);
                    Mappers.trader.get(entity).getTradeOffers().forEach(ofr -> removeTrade(ofr));
                    return true;
                case Messages.TRADE_OFFER_ADDED:
                    offer = (TradeOffer) msg.extraInfo;
                    if (seenTraders.contains(offer.getOfferingTrader())) {
                        addTrade(offer);
                    }
                    return true;
                case Messages.TRADE_OFFER_FULLY_ACCEPTED:
                case Messages.TRADE_OFFER_REMOVED:
                    offer = (TradeOffer) msg.extraInfo;
                    if (seenTraders.contains(offer.getOfferingTrader())) {
                        removeTrade(offer);
                    }
                    return true;
            }
            return false;
        }
    };

    private final HashSet<Entity> seenTraders =  new HashSet<>();

    class TradeRow {
        TradeOffer offer;
        Label priceLabel;
        Label amountLabel;

        public void update() {
            amountLabel.setText(String.valueOf(
                    (offer.getType() == TradeOffer.TYPE.BUYING) ? offer.getAmount() : -offer.getAmount()
            ));
            priceLabel.setText(String.valueOf(offer.getPrice()));
        }
    }

    private final HashMap<TradeOffer, TradeRow> tradeRows = new HashMap<>();

    public TradeOfferList(Skin skin) {
        super(skin);
        this.skin = skin;

        add("ITEM").pad(0,0,5,5).align(Align.left);
        add("AMOUNT").pad(0,0,5,5).align(Align.right).minWidth(40);
        add("PRICE").pad(0,0,5,5).align(Align.right).minWidth(80);
        add("ACCEPT").pad(0,0,5,0).align(Align.center).minWidth(20);

        createTradeRows();

        Universe.messageDispatcher.addListeners(
                telegraph,
                Messages.PLAYER_BEGINS_SEEING_TRADER,
                Messages.PLAYER_ENDS_SEEING_TRADER,
                Messages.TRADE_OFFER_ACCEPTED,
                Messages.TRADE_OFFER_ADDED,
                Messages.TRADE_OFFER_REMOVED);
    }

    private void addTrade(TradeOffer offer) {
        row();

        add(String.valueOf(offer.getItem())).align(Align.left);

        TradeRow tradeRow = new TradeRow();
        tradeRow.offer = offer;
        tradeRow.amountLabel = add("").align(Align.right).getActor();
        tradeRow.priceLabel = add("").align(Align.right).getActor();
        tradeRow.update();
        tradeRows.put(offer, tradeRow);

        TextButton acceptButton = new TextButton("accept", skin);
        acceptButton.setUserObject(offer);
        acceptButton.addListener(tradeAcceptListener);
        add(acceptButton);
    }

    private void removeTrade(TradeOffer offer) {
    }

    public void createTradeRows() {
        Array<Cell> cells = this.getCells();

        // Remove all cells from row 2
        if (cells.size > 4)
            cells.removeRange(4,cells.size-1);

        if (Universe.player == null) return;
        TradeSphereComponent tradeSphere = Mappers.tradeSphere.get(Universe.player);
        if (tradeSphere == null) return;

        for (TradeOffer offer : tradeSphere.getAllTrades().collect(Collectors.toList())) {
            addTrade(offer);
        }

        invalidate();
    }


    private void updateLabels() {
        tradeRows.values().forEach(row -> row.update());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        updateLabels();
        super.draw(batch, parentAlpha);
    }
}

