package ru.ezuykow.lotobet.statistic;

import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

import java.io.*;

/**
 * @author ezuykow
 */
@Component
@Log
public class Statistic {

    private static final String STATISTIC_FILE_NAME = "statistic.txt";

    private int betsCount;
    private int loosesCount;
    private int bankStatus;

    private final File statisticFile;

    public Statistic() {
        statisticFile = new File(STATISTIC_FILE_NAME);
        fillFields();
    }

    //-----------------API START-----------------

    public void setStatistic(int betsShift, int loosesShift, int bankShift) {
        betsCount += betsShift;
        loosesCount += loosesShift;
        bankStatus += bankShift;
        writeToFile();
    }

    public String createStatsMsg() {
        return "";
    }

    //-----------------API END-----------------

    private void fillFields() {
        checkFile();
        readFromFile();
    }

    private void checkFile() {
        try {
            if (statisticFile.createNewFile()) {
                betsCount = 0;
                loosesCount = 0;
                bankStatus = 0;
                writeToFile();
            }
        } catch (IOException e) {
            log.warning("Statistic file error");
            throw new RuntimeException(e);
        }
    }

    private void readFromFile() {
        try (var br = new BufferedReader(new FileReader(statisticFile))) {
            betsCount = Integer.parseInt(br.readLine());
            loosesCount = Integer.parseInt(br.readLine());
            bankStatus = Integer.parseInt(br.readLine());
        } catch (IOException e) {
            log.warning("Statistic file error");
            throw new RuntimeException(e);
        }
    }

    private void writeToFile() {
        try (var bw = new BufferedWriter(new FileWriter(statisticFile, false))) {
            bw.write(betsCount + "\n" + loosesCount + "\n" + bankStatus);
            bw.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
