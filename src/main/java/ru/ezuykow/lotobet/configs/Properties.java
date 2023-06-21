package ru.ezuykow.lotobet.configs;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @author ezuykow
 */
@Component
public class Properties {

    private final Environment springEnv;

    public Properties(Environment springEnv) {
        this.springEnv = springEnv;
    }

    //-----------------API START-----------------

    public String getProperty(String key) {
        if (springEnv.containsProperty(key)) {
            return springEnv.getProperty(key);
        }
        return System.getProperty(key.replaceAll("\\.", "_").toUpperCase());
    }

    //-----------------API END-----------------

}
