package ru.ezuykow.lotobet.messages;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.*;
import com.pengrad.telegrambot.response.SendResponse;
import org.springframework.stereotype.Component;
import ru.ezuykow.lotobet.configs.Properties;
import ru.ezuykow.lotobet.statistic.StatisticService;

/**
 * @author ezuykow
 */
@Component
public class MessageSender {

    private final long chatId;

    private final TelegramBot bot;
    private final StatisticService statistic;

    private int statisticMsgId;

    public MessageSender(TelegramBot bot, StatisticService statistic, Properties properties) {
        this.bot = bot;
        this.statistic = statistic;
        this.chatId = Long.parseLong(properties.getProperty("telegram.chat.id"));
    }

    //-----------------API START-----------------

    public void send(String msg) {
        bot.execute(new SendMessage(chatId, msg));
    }

    public void sendStats() {
        SendResponse response = bot.execute(new SendMessage(chatId, statistic.createStatsMsg()));
        if (response.isOk()) {
            statisticMsgId = response.message().messageId();
            pinOnlyStatMsg();
        }
    }

    public void editStats() {
        bot.execute(new EditMessageText(chatId, statisticMsgId, statistic.createStatsMsg()));
    }

    public void delete(int msgId) {
        bot.execute(new DeleteMessage(chatId, msgId));
    }

    //-----------------API END-----------------

    private void pinOnlyStatMsg() {
        bot.execute(new UnpinAllChatMessages(chatId));
        bot.execute(new PinChatMessage(chatId, statisticMsgId));
    }
}
