package ru.ezuykow.lotobet.statistic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author ezuykow
 */
@AllArgsConstructor
@Getter
@Setter
public class StatisticModel {

    private String name;
    private int betsCount;
    private int loosesCount;
    private int bankStatus;

    public StatisticModel(String name) {
        this.name = name;
    }

    public void setStatistic(int betsCount, int loosesCount, int bankStatus) {
        this.betsCount = betsCount;
        this.loosesCount = loosesCount;
        this.bankStatus = bankStatus;
    }

    public String forFile() {
        return name + "\n" + betsCount + "\n" + loosesCount + "\n" + bankStatus + "\n";
    }

    public String toString() {
        return name + "\n" +
                "Bets count: " + betsCount + "\n" +
                "Looses count: " + loosesCount + "\n" +
                "Bank status: " + ((bankStatus > 0) ? "+" : "") + (bankStatus / 1000d) + " x nominal\n";
    }
}
