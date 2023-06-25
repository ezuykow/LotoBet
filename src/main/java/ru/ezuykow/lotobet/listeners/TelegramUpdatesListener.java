package ru.ezuykow.lotobet.listeners;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ezuykow.lotobet.messages.MessageSender;
import ru.ezuykow.lotobet.processor.Processor;
import ru.ezuykow.lotobet.statistic.StatisticService;

import java.util.List;

/**
 * @author ezuykow
 */
@Service
@AllArgsConstructor
public class TelegramUpdatesListener implements UpdatesListener {

    private final TelegramBot bot;
    private final Processor processor;
    private final MessageSender msgSender;
    private final StatisticService statisticService;

    @PostConstruct
    public void init() {
        bot.setUpdatesListener(this);
    }

    //-----------------API START-----------------

    @Override
    public int process(List<Update> updates) {
        updates.forEach(this::performUpdate);
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    //-----------------API END-----------------

    private void performUpdate(Update update) {
        if (update.message() != null && update.message().text() != null) {
            String text = update.message().text();
            switch (text) {
                case "/start" -> {
                    msgSender.delete(update.message().messageId());
                    processor.startProcessor();
                }
                case "/stop" -> {
                    msgSender.delete(update.message().messageId());
                    processor.stopProcessor();
                }
                case "/stat" -> {
                    msgSender.delete(update.message().messageId());
                    msgSender.sendStats();
                }
                case "/clearstat" -> {
                    msgSender.delete(update.message().messageId());
                    statisticService.clearStatistics();
                }
            }
        }
    }
}
