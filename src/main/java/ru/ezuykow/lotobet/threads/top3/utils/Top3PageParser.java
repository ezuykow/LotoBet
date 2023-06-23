package ru.ezuykow.lotobet.threads.top3.utils;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import lombok.Getter;
import lombok.extern.java.Log;
import ru.ezuykow.lotobet.messages.MessageSender;
import ru.ezuykow.lotobet.statistic.StatisticService;
import ru.ezuykow.lotobet.threads.top3.signalisers.DigitInARowSignaliser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

/**
 * @author ezuykow
 */
@Log
public class Top3PageParser {

    private final DigitInARowSignaliser digitInARowSignaliser;

    private boolean isFirstParsing;
    private boolean newGameAdded;
    private boolean numbersParsed;
    @Getter
    private List<Top3Game> games;
    @Getter
    private long millisToSleep;

    public Top3PageParser(MessageSender msgSender, StatisticService statistic) {
        isFirstParsing = true;
        digitInARowSignaliser = new DigitInARowSignaliser(msgSender, statistic);
    }

    //-----------------API START-----------------

    public long parsePage(HtmlPage page) {
        millisToSleep = parseMillisToSleep(page);
        if (isFirstParsing) {
            firstParse(page);
        }
        parseGame(page, Top3Constant.FIRST_ELEM_XPATH);
        checkGameAdded();
        return millisToSleep;
    }

    //-----------------API END-----------------

    private void firstParse(HtmlPage page) {
        games = new LinkedList<>();
        parseGame(page, Top3Constant.THIRD_ELEM_XPATH);
        digitInARowSignaliser.checkNewGameDigits(games, millisToSleep);
        parseGame(page, Top3Constant.SECOND_ELEM_XPATH);
        digitInARowSignaliser.checkNewGameDigits(games, millisToSleep);
        isFirstParsing = false;
    }

    private void parseGame(HtmlPage page, String gameXPath) {
        HtmlDivision elemDiv = page.getFirstByXPath(gameXPath);
        int gameNumber = parseGameNumber(gameXPath, elemDiv);

        newGameAdded = false;
        if (games.isEmpty() || games.get(games.size() - 1).gameNumber != gameNumber) {
            Top3Game newGame = new Top3Game(
                    gameNumber,
                    parseNumbers(gameXPath, elemDiv),
                    parseDateTime(gameXPath, elemDiv)
            );
            if (numbersParsed) {
                games.add(newGame);
                newGameAdded = true;
                log.info("New Top3 game parsed: " + newGame);
            }
        } else {
            log.info("Repeated Top3 game " + gameNumber);
        }
    }

    private int parseGameNumber(String gameXPath, HtmlDivision elemDiv) {
        HtmlAnchor anchor = elemDiv.getFirstByXPath(gameXPath + Top3Constant.GAME_NUMBER_XPATH_POSTFIX);
        return Integer.parseInt(anchor.getVisibleText());
    }

    private Integer[] parseNumbers(String gameXPath, HtmlDivision elemDiv) {
        Integer[] numbers = new Integer[3];

        HtmlSpan span = elemDiv.getFirstByXPath(gameXPath + Top3Constant.NUMBERS_SPAN_XPATH_POSTFIX);
        if (span == null) {
            numbersParsed = false;
        } else {
            String text = span.asNormalizedText();
            numbers[0] = Integer.parseInt(text.substring(0, 1));
            numbers[1] = Integer.parseInt(text.substring(2, 3));
            numbers[2] = Integer.parseInt(text.substring(4, 5));
            numbersParsed = true;
        }

        return numbers;
    }

    private LocalDateTime parseDateTime(String elemXPath, HtmlDivision elemDiv) {
        HtmlDivision div = elemDiv.getFirstByXPath(elemXPath + Top3Constant.GAME_TIME_XPATH_POSTFIX);
        return LocalDateTime.parse(div.getVisibleText(), DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
    }

    private long parseMillisToSleep(HtmlPage page) {
        HtmlSpan timeToNextGameSpan = page.getFirstByXPath(Top3Constant.TIMER_SPAN_XPATH);
        String timeToNextGameText = timeToNextGameSpan.asNormalizedText();
        if (timeToNextGameText.contains("--")) {
            return 60_000;
        } else {
            String[] parts = timeToNextGameText.split(":");
            int secondsToNextGame = Integer.parseInt(parts[parts.length - 1])
                    + 60 * ((parts.length == 2)
                        ? Integer.parseInt(parts[parts.length - 2])
                        : Integer.parseInt(parts[parts.length - 2]) + 60 * Integer.parseInt(parts[parts.length - 3]));
            return secondsToNextGame * 1_000L;
        }
    }

    private void checkGameAdded() {
        if (newGameAdded) {
            trimList();
            digitInARowSignaliser.checkNewGameDigits(games, millisToSleep);
            millisToSleep += (Top3Constant.WAITING_STEP_MINUTES_SHIFT * 60_000);
        }/* else {
            millisToSleep = 60_000;
        }*/
        log.info("Sleep " + (millisToSleep/60_000) + " mins");
    }

    private void trimList() {
        if (games.size() > Top3Constant.MAX_GAMES_IN_MEMORY) {
            games.remove(0);
            log.info("Removed far element from games");
        }
    }
}
