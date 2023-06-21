package ru.ezuykow.lotobet.threads.top3.signalisers;

import lombok.extern.java.Log;
import ru.ezuykow.lotobet.messages.MessageSender;
import ru.ezuykow.lotobet.statistic.Statistic;
import ru.ezuykow.lotobet.threads.top3.utils.Top3Constant;
import ru.ezuykow.lotobet.threads.top3.utils.Top3Game;

import java.util.*;

/**
 * @author ezuykow
 */
@Log
public class DigitInARowSignaliser {

    private final MessageSender msgSender;
    private final Statistic statistic;

    private final Map<Integer, Integer> digitsInRow;

    public DigitInARowSignaliser(MessageSender msgSender, Statistic statistic) {
        this.msgSender = msgSender;
        this.statistic = statistic;

        digitsInRow = new HashMap<>();
    }

    //-----------------API START-----------------

    public void checkNewGameDigits(List<Top3Game> games, long waitingTimeMillis) {
        Top3Game lastGame = games.get(games.size() - 1);
        Set<Integer> lastGameUniqueNums = new HashSet<>(List.of(lastGame.getNumbers()));
        log.info("Checking Top3 game digits " + lastGameUniqueNums);

        for (Integer digit : lastGameUniqueNums) {
            if (digitsInRow.containsKey(digit)) {
                digitsInRow.put(digit, digitsInRow.get(digit) + 1);
            } else {
                digitsInRow.put(digit, 1);
            }
        }

        List<Integer> digitsToDelete = new ArrayList<>();
        for (Map.Entry<Integer, Integer> d : digitsInRow.entrySet()) {
            if (!lastGameUniqueNums.contains(d.getKey())) {
                if (d.getValue() > 2) {
                    switch (d.getValue()) {
                        case 3 -> statistic.setStatistic(1, 0, Top3Constant.BANK_DIFFER);
                        case 4 -> statistic.setStatistic(1, 1, (- 1000 + 3 * Top3Constant.BANK_DIFFER));
                        default -> statistic.setStatistic(1, 1, -3000);
                    }
                    msgSender.sendStats();
                }
                digitsToDelete.add(d.getKey());
            }
        }
        for (Integer i : digitsToDelete) {
            digitsInRow.remove(i);
        }

        for (Map.Entry<Integer, Integer> e : digitsInRow.entrySet()) {
            switch (e.getValue()) {
                case 3 -> msgSender.send("Digit " + e.getKey() + " dropped 3 times in a row. Bet 1 x nominal to game "
                        + (lastGame.getGameNumber() + 1) + " in " + waitingTimeMillis/60_000 + " minutes");
                case 4 -> msgSender.send("Digit " + e.getKey() + " dropped 4 times in a row. Bet 3 x nominal to game "
                        + (lastGame.getGameNumber() + 1) + " in " + waitingTimeMillis/60_000 + " minutes");
                case 5 -> msgSender.send("Digit " + e.getKey() + " dropped 5 times in a row. DONT TOUCH THIS EVIL DIGIT");
            }
        }
    }

    //-----------------API END-----------------

}
