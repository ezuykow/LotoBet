package ru.ezuykow.lotobet.threads.top3.utils;

/**
 * @author ezuykow
 */
public class Top3Constant {

    public static final String ARCHIVE_URL = "https://www.stoloto.ru/top3/archive";
    public static final String FIRST_ELEM_XPATH = "/html/body/div[1]/div[1]/div[6]/div[2]/div[2]/div[2]/div";
    public static final String SECOND_ELEM_XPATH = "/html/body/div[1]/div[1]/div[6]/div[2]/div[2]/div[3]/div";
    public static final String THIRD_ELEM_XPATH = "/html/body/div[1]/div[1]/div[6]/div[2]/div[2]/div[4]/div";
    public static final String GAME_TIME_XPATH_POSTFIX = "/div[1]";
    public static final String GAME_NUMBER_XPATH_POSTFIX = "/div[2]/a";
    public static final String NUMBERS_SPAN_XPATH_POSTFIX = "/div[3]/div[1]/div[1]/span";
    public static final String TIMER_SPAN_XPATH = "/html/body/div[1]/div[1]/div[8]/div[5]/div[3]/div/div[19]/div[4]/div/span";
    public static final int MAX_GAMES_IN_MEMORY = 10;
    public static final int WAITING_STEP_MINUTES_SHIFT = 3;
    public static final int FIRST_BET = 1000;
    public static final int SECOND_BET_MULTIPLICATION = 3;
    public static final int BANK_DIFFER = 331;

}
