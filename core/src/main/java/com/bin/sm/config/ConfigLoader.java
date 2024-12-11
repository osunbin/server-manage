package com.bin.sm.config;

import com.bin.sm.shutdown.signal.OperateSignal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConfigLoader {

    private final static Logger logger = LoggerFactory.getLogger(OperateSignal.class);


    // agent.yaml   agent.properties
    private static boolean checkYaml(String filename) {
        return filename.endsWith(".yaml") || filename.endsWith(".yml");
    }

    static Configs loadFromFile(String fileName) {
        File file = new File(fileName);
        try (FileInputStream in = new FileInputStream(file)) {
            return ConfigLoader.loadFromStream(in, file.getAbsolutePath());
        } catch (IOException e) {
            logger.warn("Load config file failure: {}", file.getAbsolutePath());
        }
        return new Configs(Collections.emptyMap());
    }

    static Configs loadFromStream(InputStream in, String filename) throws IOException {
        if (in != null) {
            Map<String, String> map;
            if (checkYaml(filename)) {
                map = YamlReader.load(in);
            } else {
                map = extractPropsMap(in);
            }
            return new Configs(map);
        } else {
            return new Configs(Collections.emptyMap());
        }
    }

    private static HashMap<String, String> extractPropsMap(InputStream in) throws IOException {
        Properties properties = new Properties();
        properties.load(in);
        HashMap<String, String> map = new HashMap<>();
        for (String one : properties.stringPropertyNames()) {
            map.put(one, properties.getProperty(one));
        }
        return map;
    }

    static Configs loadFromClasspath(ClassLoader classLoader, String file) {
        try (InputStream in = classLoader.getResourceAsStream(file)) {
            return ConfigLoader.loadFromStream(in, file);
        } catch (IOException e) {
            logger.warn("Load config file:{} by classloader:{} failure: {}", file, classLoader.toString(), e);
        }

        return new Configs(Collections.emptyMap());
    }
}
