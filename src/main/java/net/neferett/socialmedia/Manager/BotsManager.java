package net.neferett.socialmedia.Manager;

import lombok.Data;
import lombok.experimental.Delegate;
import net.neferett.socialmedia.Instances.SocialBot;
import net.neferett.tradingplugin.Trade.Price.PriceAction;
import net.neferett.tradingplugin.Trade.Trade;

import java.util.ArrayList;
import java.util.List;

@Data
public class BotsManager {

    @Delegate
    private List<SocialBot> bots = new ArrayList<>();

    public void sendTrade(Trade trade) {
        this.bots.forEach(e -> e.sendTrade(trade));
    }

    public void sendPriceHit(Trade trade, PriceAction action) {
        this.bots.forEach(e -> e.sendPriceHit(trade, action));
    }

    public void sendMessage(String message) {
        this.bots.forEach(e -> e.sendMessage(message));
    }

    public void registerBots() {
        this.bots.forEach(SocialBot::selfRegister);
    }
}
