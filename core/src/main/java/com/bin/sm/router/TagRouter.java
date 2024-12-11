package com.bin.sm.router;

import com.bin.sm.context.NodeAdapter;
import com.bin.sm.util.StringUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class TagRouter {

    public static final TagRouter instance = new TagRouter();

    private Set<String> disabledTags = new HashSet<>();
    private boolean noMatchTag = true;





    public <N extends NodeAdapter> List<N> router(String tag, final Set<String> addresses, List<N> nodes) {
        // 黑名单
        List<N> originalNodes = new ArrayList<>(nodes);
        // 忽略  xxxx 会忽略


        // 黑名单
        if (!disabledTags.isEmpty()) {
            originalNodes = filterDisabledTag(originalNodes);
        }

        if (StringUtil.isEmpty(tag)) {
            return originalNodes;
        }


        if (addresses != null) {
            originalNodes.removeIf(node -> !addresses.contains(node.getAddressStr()));
        } else {
            originalNodes.removeIf(node -> !node.existTag(tag));
        }

        if (!originalNodes.isEmpty()) {
            return originalNodes;
        }
        originalNodes = nodes;

        if (noMatchTag) {
            // 没有匹配的tag,那就使用没有tag标记的节点
            originalNodes.removeIf(node -> !node.notTag());
        }
        // 如果都有tag,那么就只忽略 那些需要隔离的tag
        if (originalNodes.isEmpty()) {
            originalNodes = nodes;
            if (!disabledTags.contains(tag)) {
                originalNodes = filterDisabledTag(originalNodes);
            }
        }
        return originalNodes;
    }

    private <N extends NodeAdapter> List<N> filterDisabledTag(List<N> nodes) {
        List<N> targetNodes = new LinkedList<>();
        Set<String> disabledTagSet = disabledTags;
        for (N node : nodes) {
            boolean exist = false;
            for (String tag : disabledTagSet) {
                exist |= node.existTag(tag);
            }
            if (!exist) {
                targetNodes.add(node);
            }
        }
        return targetNodes;
    }
}
