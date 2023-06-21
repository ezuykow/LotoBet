package ru.ezuykow.lotobet.configs;

import com.pengrad.telegrambot.TelegramBot;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ezuykow
 */
@Configuration
@AllArgsConstructor
public class AppConfig {

    private final Properties properties;

    @Bean
    public TelegramBot telegramBot() {
        return new TelegramBot(properties.getProperty("telegram.bot.token"));
    }

}
