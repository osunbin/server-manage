package com.bin.sm.router;

import com.bin.sm.context.NodeAdapter;
import com.bin.sm.router.matcher.ConditionRule;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class RouterChain {

    public static final RouterChain DEFAULT = new RouterChain();

    public <N extends NodeAdapter> List<N> route(RouterContext routerContext, List<N> nodes) {

        nodes = FilterRouter.instance.filter(nodes);
        nodes = EnvRouter.instance.router(routerContext.localEnv(),nodes);

        nodes = ZoneRouter.instance.router(routerContext.localZone(),nodes);

        List<String> tags = routerContext.getTag();

        for (String tag : tags) {
            Set<String> addressesByTag = routerContext.getAddressesByTag(tag);
            nodes = TagRouter.instance.router(tag,addressesByTag,nodes);
        }


        ConditionRouter<RouterContext> conditionRouter = ConditionRouterFactory.INSTANCE.getConditionRouter(routerContext.getRpcType());
        LinkedList<ConditionRule> conditionRules = routerContext.getConditionRules();
        while (!conditionRules.isEmpty()) {
            ConditionRule conditionRule = conditionRules.pollFirst();
            nodes = conditionRouter.router(routerContext,nodes,conditionRule);
        }
        return nodes;
    }
}
