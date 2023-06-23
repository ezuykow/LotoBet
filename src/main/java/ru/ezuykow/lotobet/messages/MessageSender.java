package ru.ezuykow.lotobet.messages;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
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
        bot.execute(new SendMessage(chatId, statistic.createStatsMsg()));
    }

    public void delete(int msgId) {
        bot.execute(new DeleteMessage(chatId, msgId));
    }

    //-----------------API END-----------------

}
