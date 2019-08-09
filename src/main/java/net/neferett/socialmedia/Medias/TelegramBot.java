package net.neferett.socialmedia.Medias;

import lombok.SneakyThrows;
import net.neferett.socialmedia.Instances.SocialBot;
import net.neferett.socialmedia.SocialMedia;
import net.neferett.tradingplugin.Trade.Enums.PriceEnum;
import net.neferett.tradingplugin.Trade.Price.PriceAction;
import net.neferett.tradingplugin.Trade.Trade;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class TelegramBot extends TelegramLongPollingBot implements SocialBot {

    private String token;
    private long chatId;
    private TelegramBotsApi api;

    public TelegramBot() {
        ApiContextInitializer.init();

        this.api = new TelegramBotsApi();
        this.token = System.getenv("bot_token");
        this.chatId = SocialMedia.getInstance().getConfigFile().getChanelId();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery() && update.getCallbackQuery().getData().equalsIgnoreCase("trade_button")) {
            this.sendCurrentResults(null, update.getCallbackQuery().getFrom().getId());
        }
    }

    @Override
    public String getBotUsername() {
        return "TradingBot";
    }

    @Override
    public String getBotToken() {
        return this.token;
    }

    private String getTradeHeader(Trade trade) {
        return "==========================================\nTrade: " + trade.getUuid() + "\n" + "\n" + "Asset: " + trade.getAsset() + "\n" + "Type: " + trade.getType() + "\n\n" + "Pair: " + trade.getPair() + "\n\n";
    }

    private String tradeToString(Trade trade){
        StringBuilder builder = new StringBuilder();

        trade.getActions().forEach(e ->
                builder.append(e.getType().getName()).append(" : ").append(e.getPrice()).append(" ").append(e.getType() == PriceEnum.OPEN ? "" : "    |    " + e.getDelta() + "%").append("\n")
        );

        return this.getTradeHeader(trade) + builder.toString() + "==========================================";
    }

    private SendMessage sendText(String text) {
        return new SendMessage().setChatId(this.chatId).setText(text);
    }

    private InlineKeyboardMarkup getMessagedButton(String buttonText) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        rowInline.add(new InlineKeyboardButton().setText(buttonText).setCallbackData("trade_button"));
        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }

    @Override
    public void sendTrade(Trade trade) {
        SendMessage send = this.sendText(this.tradeToString(trade));

        send.setReplyMarkup(getMessagedButton("See current trade results"));

        try {
            this.execute(send);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @SneakyThrows
    public void sendPriceHit(Trade trade, PriceAction action) {
        this.execute(this.sendText("=========== UPDATE ===========\n\n" + this.getTradeHeader(trade) +
                action.getType().getName() + "hit at " + action.getPrice() + "\n" + "Delta: " + action.getDelta() + "\n" + "=========== UPDATE ==========="));
    }

    @Override
    @SneakyThrows
    public void sendCurrentResults(Trade trade, int id) {
        SendMessage send = this.sendText(this.tradeToString(trade));

        send.setChatId((long) id);

        this.execute(send);
    }

    @Override
    @SneakyThrows
    public void sendMessage(String message) {
        this.execute(this.sendText(message));
    }

    @Override
    public void selfRegister() {
        try {
            this.api.registerBot(this);
        } catch (Exception ignored) { }
    }
}
