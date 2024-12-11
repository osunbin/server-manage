package com.bin.sm.config;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.parser.ParserException;

import java.io.InputStream;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class YamlReader {


    private static final DumperOptions DUMPER_OPTIONS;

    static {
        DUMPER_OPTIONS = new DumperOptions();
        DUMPER_OPTIONS.setLineBreak(DumperOptions.LineBreak.getPlatformLineBreak());
    }

    public static Map<String, String> load(InputStream in) {
        if (in != null) {
            Map<String, Object> yaml = null;
            try {
                yaml = new Yaml(DUMPER_OPTIONS).load(in);
            } catch (ParserException e) {
                return Collections.emptyMap();
            }
            final Deque<String> keyStack = new LinkedList<>();
            final Map<String, String> resultMap = new HashMap<>();
            compress(yaml, keyStack, resultMap);
            return resultMap;
        }
        return Collections.emptyMap();
    }

    private static void compress(Map<?, Object> result, Deque<String> keyStack, Map<String, String> resultMap) {
        result.forEach((k, v) -> {
            keyStack.addLast(String.valueOf(k));

            if (v instanceof Map) {
                compress((Map<?, Object>) v, keyStack, resultMap);
                keyStack.removeLast();
                return;
            }

            if (v instanceof List) {
                String value = ((List<Object>) v).stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(","));

                resultMap.put(String.join(".", keyStack), value);
                keyStack.removeLast();
                return;
            }

            resultMap.put(String.join(".", keyStack), String.valueOf(v));
            keyStack.removeLast();
        });
    }

}
