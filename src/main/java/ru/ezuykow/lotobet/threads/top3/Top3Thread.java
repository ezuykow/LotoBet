package ru.ezuykow.lotobet.threads.top3;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import lombok.extern.java.Log;
import ru.ezuykow.lotobet.messages.MessageSender;
import ru.ezuykow.lotobet.statistic.StatisticService;
import ru.ezuykow.lotobet.stealer.PageStealer;
import ru.ezuykow.lotobet.threads.top3.utils.Top3Constant;
import ru.ezuykow.lotobet.threads.top3.utils.Top3PageParser;

import java.io.IOException;

/**
 * @author ezuykow
 */
@Log
public class Top3Thread extends Thread{

    private final PageStealer stealer;
    private final Top3PageParser parser;

    public Top3Thread(MessageSender msgSender, StatisticService statistic, PageStealer stealer) {
        this.stealer = stealer;

        this.setName("Top3Thread");

        parser = new Top3PageParser(msgSender, statistic);
    }

    //-----------------API START-----------------

    public void run() {
        log.info("Top3 thread started");

        while (true) {
            try {
                HtmlPage page = stealer.stealPage(Top3Constant.ARCHIVE_URL);
                long millisToSleep = parser.parsePage(page);
                sleep(millisToSleep);
            } catch (IOException e) {
                stealer.closeWebClient();
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                break;
            }
        }

        stealer.closeWebClient();
        log.info("Top3 thread stopped");
    }

    //-----------------API END-----------------

}
