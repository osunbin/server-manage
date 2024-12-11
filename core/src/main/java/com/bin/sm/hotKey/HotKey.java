package com.bin.sm.hotKey;

import com.bin.sm.internal.topk.HeavyKeeper;
import com.bin.sm.internal.topk.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 *  做个注解
 */
public class HotKey {

    private static final String RULE_TYPE_KEY = "key";
    private static final String RULE_TYPE_PATTERN = "pattern";

    private HeavyKeeper topK;
    private LocalCache localCache;
    private Builder builder;
    private List<CacheRule> whitelist;
    private List<CacheRule> blacklist;


    public HotKey(Builder builder) {
        this.builder = builder;
        this.whitelist = new ArrayList<>();
        this.blacklist = new ArrayList<>();
        if (builder.getHotKeyCnt() > 0) {
            int factor = (int) Math.log(builder.getHotKeyCnt());
            if (factor < 1) {
                factor = 1;
            }
            this.topK = new HeavyKeeper(builder.getHotKeyCnt(), 1024 * factor, 4, 0.925, builder.getMinCount());
        }
        if (!builder.getWhitelist().isEmpty()) {
            this.whitelist = initCacheRules(builder.getWhitelist());
        }
        if (!builder.getBlacklist().isEmpty()) {
            this.blacklist = initCacheRules(builder.getBlacklist());
        }
        if (builder.isAutoCache() || !whitelist.isEmpty()) {
            if (builder.getLocalCache() != null) {
                this.localCache = builder.getLocalCache();
            } else {
                this.localCache = new LocalCacheDefault(builder.getLocalCacheCnt());
            }
        }
    }

    private List<CacheRule> initCacheRules(List<CacheRuleConfig> rules) {
        List<CacheRule> list = new ArrayList<>();
        for (CacheRuleConfig rule : rules) {
            int ttl = rule.getTtlMs();
            if (ttl == 0) {
                ttl = this.builder.getCacheMs();
            }
            CacheRule cacheRule = new CacheRule();
            cacheRule.setTtl(ttl);

            if (Objects.equals(rule.getMode(), RULE_TYPE_KEY)) {
                cacheRule.setValue(rule.getValue());
            } else if (Objects.equals(rule.getMode(), RULE_TYPE_PATTERN)) {
                Pattern pattern = Pattern.compile(rule.getValue());
                cacheRule.setRegexp(pattern);
            }
            list.add(cacheRule);
        }
        return list;
    }


    public boolean inBlacklist(String key) {
        if (blacklist.isEmpty()) {
            return false;
        }
        for (CacheRule b : blacklist) {
            if (b.getValue().equals(key)) {
                return true;
            }
            if (b.getRegexp() != null && b.getRegexp().matcher(key).matches()) {
                return true;
            }
        }
        return false;
    }

    public long inWhitelist(String key) {
        if (whitelist.isEmpty()) {
            return 0L;
        }
        for (CacheRule b : whitelist) {
            if (b.getValue().equals(key)) {
                return b.getTtl();
            }
            if (b.getRegexp() != null && b.getRegexp().matcher(key).matches()) {
                return b.getTtl();
            }
        }
        return 0L;
    }

    public synchronized boolean add(String key,int incr) {
        HeavyKeeper.Result result = topK.add(key, incr);
        return result.isHotkey();
    }

    public synchronized boolean addWithValue(String key, Object value, int incr) {
        boolean added = false;

        HeavyKeeper.Result result = topK.add(key, incr);

        String expelled = result.getKey();
        added = result.isHotkey();
        // 将旧节点在本地缓存剔除
        if (expelled != null && !expelled.isEmpty() && localCache != null) {
            localCache.remove(expelled);
        }
        // 判断是否要将元素添加进来
        if (builder.isAutoCache() && added) {
            if (!inBlacklist(key)) {
                localCache.add(key, value, builder.getCacheMs());
            }
            return added;
        }

        long whitelistResult = inWhitelist(key);
        if (whitelistResult > 0) {
            localCache.add(key, value, whitelistResult);
        }
        return added;
    }

    public synchronized Object get(String key) {
        return localCache.get(key);
    }

    public synchronized List<Item> listTopK() {
        return topK.list();
    }

    public synchronized List<Object> listCache() {
        return localCache.list();
    }

    public synchronized void delCache(String key) {
        localCache.remove(key);
    }

    public synchronized void fading() {
        topK.fading();
    }


    public static class CacheRule {
        private String value;
        private Pattern regexp;
        private long ttl;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public Pattern getRegexp() {
            return regexp;
        }

        public void setRegexp(Pattern regexp) {
            this.regexp = regexp;
        }

        public long getTtl() {
            return ttl;
        }

        public void setTtl(long ttl) {
            this.ttl = ttl;
        }
    }

    public static class CacheRuleConfig {
        private String mode;
        private String value;
        private int ttlMs;

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getTtlMs() {
            return ttlMs;
        }

        public void setTtlMs(int ttlMs) {
            this.ttlMs = ttlMs;
        }
    }


    public static class Builder {
        private int hotKeyCnt;
        private int localCacheCnt;
        private boolean autoCache;
        private int cacheMs;
        private int minCount;
        private List<CacheRuleConfig> whitelist = new ArrayList<>();
        private List<HotKey.CacheRuleConfig> blacklist = new ArrayList<>();
        private LocalCache localCache;


        public int getHotKeyCnt() {
            return hotKeyCnt;
        }

        public void setHotKeyCnt(int hotKeyCnt) {
            this.hotKeyCnt = hotKeyCnt;
        }

        public int getLocalCacheCnt() {
            return localCacheCnt;
        }

        public void setLocalCacheCnt(int localCacheCnt) {
            this.localCacheCnt = localCacheCnt;
        }

        public boolean isAutoCache() {
            return autoCache;
        }

        public void setAutoCache(boolean autoCache) {
            this.autoCache = autoCache;
        }

        public int getCacheMs() {
            return cacheMs;
        }

        public void setCacheMs(int cacheMs) {
            this.cacheMs = cacheMs;
        }

        public int getMinCount() {
            return minCount;
        }

        public void setMinCount(int minCount) {
            this.minCount = minCount;
        }

        public List<HotKey.CacheRuleConfig> getWhitelist() {
            return whitelist;
        }

        public void setWhitelist(List<HotKey.CacheRuleConfig> whitelist) {
            this.whitelist = whitelist;
        }

        public List<HotKey.CacheRuleConfig> getBlacklist() {
            return blacklist;
        }

        public void setBlacklist(List<HotKey.CacheRuleConfig> blacklist) {
            this.blacklist = blacklist;
        }

        public LocalCache getLocalCache() {
            return localCache;
        }

        public void setLocalCache(LocalCache localCache) {
            this.localCache = localCache;
        }
    }

}
