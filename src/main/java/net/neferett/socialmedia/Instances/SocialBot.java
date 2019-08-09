package net.neferett.socialmedia.Instances;

import net.neferett.tradingplugin.Trade.Price.PriceAction;
import net.neferett.tradingplugin.Trade.Trade;

public interface SocialBot {

    void sendTrade(Trade trade);

    void sendPriceHit(Trade trade, PriceAction action);

    void sendCurrentResults(Trade trade, int id);

    void sendMessage(String message);

    void selfRegister();

}
