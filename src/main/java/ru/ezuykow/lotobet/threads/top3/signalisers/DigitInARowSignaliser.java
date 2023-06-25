package ru.ezuykow.lotobet.threads.top3.signalisers;

import lombok.extern.java.Log;
import ru.ezuykow.lotobet.messages.MessageSender;
import ru.ezuykow.lotobet.statistic.StatisticService;
import ru.ezuykow.lotobet.threads.top3.utils.Top3Game;

import java.util.*;

import static ru.ezuykow.lotobet.statistic.StatisticService.StatisticName.THEORY_A;
import static ru.ezuykow.lotobet.threads.top3.utils.Top3Constant.*;

/**
 * @author ezuykow
 */
@Log
public class DigitInARowSignaliser {

    private final MessageSender msgSender;
    private final StatisticService statistic;

    private final Map<Integer, Integer> digitsInRow;

    public DigitInARowSignaliser(MessageSender msgSender, StatisticService statistic) {
        this.msgSender = msgSender;
        this.statistic = statistic;

        digitsInRow = new HashMap<>();
    }

    //-----------------API START-----------------

    public void checkNewGameDigits(List<Top3Game> games, long waitingTimeMillis) {
        Top3Game lastGame = games.get(games.size() - 1);
        Set<Integer> lastGameUniqueNums = new HashSet<>(List.of(lastGame.getNumbers()));
        log.info("Checking Top3 game digits " + lastGameUniqueNums);

        checkDigitsInRow(lastGameUniqueNums);
        checkPreviousBets(lastGameUniqueNums);
        checkAndSendNewBets(lastGame, waitingTimeMillis);
    }

    //-----------------API END-----------------

    private void checkDigitsInRow(Set<Integer> lastGameUniqueNums) {
        for (Integer digit : lastGameUniqueNums) {
            if (digitsInRow.containsKey(digit)) {
                digitsInRow.put(digit, digitsInRow.get(digit) + 1);
            } else {
                digitsInRow.put(digit, 1);
            }
        }
    }

    private void checkPreviousBets(Set<Integer> lastGameUniqueNums) {
        List<Integer> digitsToDelete = new ArrayList<>();
        for (Map.Entry<Integer, Integer> d : digitsInRow.entrySet()) {
            if (!lastGameUniqueNums.contains(d.getKey())) {
                if (d.getValue() > 2) {
                    switch (d.getValue()) {
                        case 3 -> statistic.setStatistic(THEORY_A, 1, 0,
                                BANK_DIFFER);
                        case 4 -> statistic.setStatistic(THEORY_A, 1, 1,
                                (SECOND_BET_MULTIPLICATION * BANK_DIFFER - FIRST_BET));
                    }
                    msgSender.editStats();
                }
                digitsToDelete.add(d.getKey());
            }
        }
        for (Integer i : digitsToDelete) {
            digitsInRow.remove(i);
        }
    }

    private void checkAndSendNewBets(Top3Game lastGame, long waitingTimeMillis) {
        for (Map.Entry<Integer, Integer> e : digitsInRow.entrySet()) {
            switch (e.getValue()) {
                case 5:
                    statistic.setStatistic(THEORY_A, 1, 2,
                            -(SECOND_BET_MULTIPLICATION * FIRST_BET + FIRST_BET));
                    digitsInRow.put(e.getKey(), 3);
                case 3:
                    msgSender.send(
                        "Game: Top3 №" + (lastGame.getGameNumber() + 1) + "\n" +
                                "When: in " + waitingTimeMillis / 60_000 + " minutes\n" +
                                "Event: digit " + e.getKey() + " fall out - NO\n" +
                                "Coefficient: " + DIGIT_NOT_FALL_OUT_COEF + "\n" +
                                "Bet: 1 x nominal");
                    break;
                case 4:
                    msgSender.send(
                        "Game: Top3 №" + (lastGame.getGameNumber() + 1) + "\n" +
                                "When: in " + waitingTimeMillis / 60_000 + " minutes\n" +
                                "Event: digit " + e.getKey() + " fall out - NO\n" +
                                "Coefficient: " + DIGIT_NOT_FALL_OUT_COEF + "\n" +
                                "Bet: " + SECOND_BET_MULTIPLICATION + " x nominal");
            }
        }
    }
}
