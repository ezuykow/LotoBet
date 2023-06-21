package ru.ezuykow.lotobet.processor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ezuykow.lotobet.messages.MessageSender;
import ru.ezuykow.lotobet.statistic.Statistic;
import ru.ezuykow.lotobet.stealer.PageStealer;
import ru.ezuykow.lotobet.threads.top3.Top3Thread;

/**
 * @author ezuykow
 */
@Component
@RequiredArgsConstructor
public class Processor {

    private final MessageSender msgSender;
    private final Statistic statistic;
    private final PageStealer pageStealer;

    private boolean isStarted = false;
    private Top3Thread top3Thread;

    //-----------------API START-----------------

    public void startProcessor() {
        if (!isStarted) {
            top3Thread = new Top3Thread(msgSender, statistic, pageStealer);

            top3Thread.start();

            isStarted = true;
        }
    }

    public void stopProcessor() {
        if (isStarted) {
            top3Thread.interrupt();

            isStarted = false;
        }
    }

    //-----------------API END-----------------

}
