package com.bin.sm.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Configs {

    protected Map<String, String> source;

    protected  String localAppName;

    protected String localEnv = "local";

    protected String localZone = "defaultZone";

    public Configs(Map<String, String> source) {
        this.source = new TreeMap<>(source);
    }

    public void updateConfigs(Map<String, String> changes) {
        this.source.putAll(changes);
    }



    public Map<String, String> getConfigs() {
        return new TreeMap<>(this.source);
    }

    public boolean hasConfig(String key) {
        return this.source.containsKey(key);
    }


    public String getString(String name) {
        return this.source.get(name);
    }


    public String getString(String name, String defVal) {
        String val = this.source.get(name);

        return val == null ? defVal : val;
    }

    public Integer getInt(String name) {
        String value = this.source.get(name);
        if (value == null) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return null;
        }
    }


    public Integer getInt(String name, int defValue) {
        Integer anInt = getInt(name);
        if (anInt == null) {
            return defValue;
        }
        return anInt;
    }

    public Boolean getBooleanNullForUnset(String name) {
        String value = this.source.get(name);
        if (value == null) {
            return null;
        }
        return value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("true");
    }

    public Boolean getBoolean(String name) {
        String value = this.source.get(name);
        if (value == null) {
            return false;
        }
        return value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("true");
    }


    public Boolean getBoolean(String name, boolean defValue) {
        Boolean aBoolean = getBooleanNullForUnset(name);
        if (aBoolean == null) {
            return defValue;
        }
        return aBoolean;
    }

    public Double getDouble(String name) {
        String value = this.source.get(name);
        if (value == null) {
            return null;
        }
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return null;
        }
    }


    public Double getDouble(String name, double defValue) {
        Double aDouble = getDouble(name);
        if (aDouble == null) {
            return defValue;
        }
        return aDouble;
    }

    public Long getLong(String name) {
        String value = this.source.get(name);
        if (value == null) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            return null;
        }
    }


    public Long getLong(String name, long defValue) {
        Long aLong = getLong(name);
        if (aLong == null) {
            return defValue;
        }
        return aLong;
    }

    public List<String> getStringList(String name) {
        String value = this.source.get(name);
        if (value == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(value.split(",")).filter(Objects::nonNull).collect(Collectors.toList());
    }



    public String getLocalAppName() {
        String appName = SystemEnv.get("appName");
        return localAppName;
    }

    public void setLocalAppName(String localAppName) {
        this.localAppName = localAppName;
    }

    public String getLocalEnv() {
        return localEnv;
    }

    public void setLocalEnv(String localEnv) {
        this.localEnv = localEnv;
    }

    public String getLocalZone() {
        return localZone;
    }

    public void setLocalZone(String localZone) {
        this.localZone = localZone;
    }
}
