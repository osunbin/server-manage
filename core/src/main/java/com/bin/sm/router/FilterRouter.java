package com.bin.sm.router;

import com.bin.sm.context.NodeAdapter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FilterRouter {

    public static final FilterRouter instance = new FilterRouter();

    private boolean filterEnabled = false;
    private Set<String> blacklist = new HashSet<>();
    private Set<String> whitelist = new HashSet<>();
    /**
     *  黑白名单
     */
    public <N extends NodeAdapter> List<N> filter(List<N> nodes) {
        if (!filterEnabled)
            return nodes;

        nodes.removeIf(node -> blacklist.contains(node.getAddressStr()));


        nodes.removeIf(node -> !whitelist.contains(node.getAddressStr()));

        return nodes;
    }
}
