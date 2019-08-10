package net.neferett.socialmedia.Manager;

import lombok.Data;
import lombok.experimental.Delegate;
import net.neferett.coreengine.CoreEngine;
import net.neferett.redisapi.RedisAPI;
import net.neferett.socialmedia.Instances.SocialBot;
import net.neferett.socialmedia.SocialMedia;
import net.neferett.tradingplugin.Trade.Price.PriceAction;
import net.neferett.tradingplugin.Trade.Trade;

import java.util.ArrayList;
import java.util.List;

@Data
public class BotsManager {

    @Delegate
    private List<SocialBot> bots = new ArrayList<>();

    private RedisAPI redisAPI = CoreEngine.getInstance().getRedisAPI();

    public static BotsManager getInstance() {
        return SocialMedia.getInstance().getBotsManager();
    }

    public Object getDataChat(Class clazz, String id) {
        return this.redisAPI.deSerialize(clazz, id);
    }

    public void sendTrade(Trade trade) {
        this.bots.forEach(e -> this.redisAPI.serialize(e.sendTrade(trade), trade.getUuid().toString()));
    }

    public void stopTrade(Trade trade) {
        this.bots.forEach(e -> this.redisAPI.serialize(e.sendTrade(trade), trade.getUuid().toString()));
    }

    public void updatePrice(Trade trade) {
        this.bots.forEach(e -> e.updatePrice(trade));
    }

    public void sendMessage(String message) {
        this.bots.forEach(e -> e.sendMessage(message));
    }

    public void registerBots() {
        this.bots.forEach(SocialBot::selfRegister);
    }
}
