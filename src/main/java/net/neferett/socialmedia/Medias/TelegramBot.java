package net.neferett.socialmedia.Medias;

import lombok.SneakyThrows;
import net.neferett.socialmedia.Instances.SocialBot;
import net.neferett.socialmedia.Instances.TelegramChat;
import net.neferett.socialmedia.Manager.BotsManager;
import net.neferett.socialmedia.SocialMedia;
import net.neferett.tradingplugin.Manager.TradeManager;
import net.neferett.tradingplugin.Trade.Enums.PriceEnum;
import net.neferett.tradingplugin.Trade.Enums.TradeStatus;
import net.neferett.tradingplugin.Trade.Price.PriceAction;
import net.neferett.tradingplugin.Trade.Trade;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.text.DecimalFormat;
import java.text.NumberFormat;
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
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery() && update.getCallbackQuery().getData().contains("trade_button")) {
            String uuid = update.getCallbackQuery().getData().split(" ")[1];
            Trade trade = TradeManager.getInstance().retrieveTrade(uuid);

            this.sendCurrentResults(trade, update.getCallbackQuery().getFrom().getId());
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

    private String getDeltaFormatted(double delta) {
        return (delta > 0 ? "+" : "") + new DecimalFormat("##.##").format(delta) + "%";
    }

    private String getTradeHeader(Trade trade) {
        return "================\n" +
                "Trade: " + trade.getUuid() + "\n\n" +
                "Asset: " + trade.getAsset() + "\n\n" +
                "Type: " + trade.getType() + "\n" +
                (trade.getStatus() == TradeStatus.CLOSED ?
                        "Result: " + trade.getState() + " | " +
                                this.getDeltaFormatted(trade.calculateFinalDelta())
                                 + "\n" : "") +
                "Status: " + trade.getStatus() + "\n\n" +
                "Pair: " + trade.getPair() + "\n\n";
    }

    private String tradeToString(Trade trade){
        StringBuilder builder = new StringBuilder();

        trade.getActions().forEach(e ->
                builder.append(e.getType().getName())
                        .append(" : ").append(e.getPrice()).append(" ")
                        .append(e.getType() == PriceEnum.OPEN ? "" : "    |    " + this.getDeltaFormatted(e.getDelta()))
                        .append(e.getType() != PriceEnum.OPEN ? e.isHit() ? "   ✔" : "  ✖" : "")
                        .append("\n\n")
        );

        return this.getTradeHeader(trade) + builder.toString() + "================";
    }

    private SendMessage sendText(String text) {
        return new SendMessage().setChatId(this.chatId).setText(text);
    }

    private InlineKeyboardMarkup getMessagedButton(String id, String buttonText) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        rowInline.add(new InlineKeyboardButton().setText(buttonText).setCallbackData("trade_button " + id));
        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }

    private EditMessageText editMessage(TelegramChat chat, String text) {
        return new EditMessageText()
                .setChatId(chat.getChatId())
                .setMessageId(chat.getMessageId())
                .setText(text);
    }

    private InlineKeyboardMarkup getKeyboard(Trade trade) {
        return this.getMessagedButton(trade.getUuid().toString(), "Get technical analysis");
    }

    private SendPhoto loadPhoto(String photo, long id) {
        return new SendPhoto()
            .setChatId(id)
            .setPhoto(photo);
    }

    @Override
    @SneakyThrows
    public TelegramChat sendTrade(Trade trade) {
        SendMessage send = this.sendText(this.tradeToString(trade));

        send.setReplyMarkup(this.getKeyboard(trade));

        Message msg = this.execute(send);

        return new TelegramChat(msg.getMessageId(), send.getChatId());
    }



    @Override
    @SneakyThrows
    public void sendCurrentResults(Trade trade, int id) {
        SendMessage send = this.sendText(this.tradeToString(trade));
        send.setChatId((long) id);

        this.execute(send);
        this.execute(this.loadPhoto(trade.getPhoto(), id));
    }

    @Override
    @SneakyThrows
    public void sendMessage(String message) {
        this.execute(this.sendText(message));
    }

    @Override
    public void updatePrice(Trade trade) {
        TelegramChat telegramChat = (TelegramChat) BotsManager.getInstance().getDataChat(TelegramChat.class, trade.getUuid().toString());

        try {
            this.execute(
                    this.editMessage(telegramChat, this.tradeToString(trade))
                            .setReplyMarkup(this.getKeyboard(trade))
            );
        } catch(Exception ignored) { }
    }

    @Override
    public void selfRegister() {
        try {
            this.api.registerBot(this);
        } catch (Exception ignored) { }
    }
}
