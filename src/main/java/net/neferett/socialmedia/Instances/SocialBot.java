package net.neferett.socialmedia.Instances;

import net.neferett.tradingplugin.Trade.Price.PriceAction;
import net.neferett.tradingplugin.Trade.Trade;

public interface SocialBot {

    Object sendTrade(Trade trade);

    void sendCurrentResults(Trade trade, int id);

    void sendMessage(String message);

    void updatePrice(Trade trade);

    void selfRegister();

}
