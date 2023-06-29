package ru.ezuykow.lotobet.statistic;

import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author ezuykow
 */
@Component
@Log
public class StatisticService {

    public enum StatisticName {
        GENERAL,
        THEORY_A
    }

    private static final String STATISTIC_FILE_NAME = "statistic.txt";

    private final File statisticFile;
    private final Map<String, StatisticModel> statistics;

    public StatisticService() {
        statisticFile = new File(STATISTIC_FILE_NAME);
        statistics = new LinkedHashMap<>();

        setUpStatistics();
        fillFields();
    }

    //-----------------API START-----------------

    public void setStatistic(StatisticName statisticName, int betsShift, int loosesShift, int bankShift) {
        refreshStatistic(statistics.get(statisticName.name()), betsShift, loosesShift, bankShift);
        refreshStatistic(statistics.get(StatisticName.GENERAL.name()), betsShift, loosesShift, bankShift);
        writeToFile();
    }

    public String createStatsMsg() {
        StringBuilder sb = new StringBuilder();
        statistics.values().forEach(s -> sb.append(s.toString()));
        return sb.toString();
    }

    public void clearStatistics() {
        statistics.values().forEach(s -> s.setStatistic(0, 0, 0));
        writeToFile();
    }

    //-----------------API END-----------------

    private void setUpStatistics() {
        for (StatisticName v : StatisticName.values()) {
            statistics.put(v.name(), new StatisticModel(v.name()));
        }
    }

    private void fillFields() {
        checkFile();
        readFromFile();
    }

    private void checkFile() {
        try {
            if (statisticFile.createNewFile()) {
                clearStatistics();
            }
        } catch (IOException e) {
            log.warning("Statistic file error");
            throw new RuntimeException(e);
        }
    }

    private void readFromFile() {
        try (var br = new BufferedReader(new FileReader(statisticFile))) {
            while (true) {
                String statisticName = br.readLine();
                if (statisticName != null) {
                    readStats(statisticName, br);
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            log.warning("Statistic file error");
            throw new RuntimeException(e);
        }
    }

    private void readStats(String statisticName, BufferedReader br) throws IOException {
        try {
            StatisticName targetStatisticName = StatisticName.valueOf(statisticName);
            StatisticModel targetStatistic = statistics.get(targetStatisticName.name());
            targetStatistic.setStatistic(
                    Integer.parseInt(br.readLine()),
                    Integer.parseInt(br.readLine()),
                    Integer.parseInt(br.readLine())
            );
        } catch (IllegalArgumentException e) {
            log.warning("Skipping unknown statistic");
        }
    }

    private void refreshStatistic(StatisticModel statistic, int betsShift, int loosesShift, int bankShift) {
        statistic.setStatistic(
                statistic.getBetsCount() + betsShift,
                statistic.getLoosesCount() + loosesShift,
                statistic.getBankStatus() + bankShift
        );
    }

    private void writeToFile() {
        try (var bw = new BufferedWriter(new FileWriter(statisticFile, false))) {
            for (StatisticModel s : statistics.values()) {
                bw.write(s.forFile());
            }
            bw.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
