package ru.ezuykow.lotobet.threads.top3.utils;

import lombok.*;

import java.time.LocalDateTime;

/**
 * @author ezuykow
 */
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
public class Top3Game {

    @EqualsAndHashCode.Include
    int gameNumber;
    Integer[] numbers;
    LocalDateTime dateTime;

}
