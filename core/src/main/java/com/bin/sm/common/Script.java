package com.bin.sm.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Script {
    private static Logger logger = LoggerFactory.getLogger(Script.class);

    private static final ConcurrentMap<String, ScriptEngine> ENGINES =
            new ConcurrentHashMap<>();


    private  ScriptEngine engine;


    private CompiledScript function;

    /**
     * (function route(invokers,invocation,context) {
     *       var result = new java.util.ArrayList(invokers.size());
     *       for (i = 0; i < invokers.size(); i ++) {
     *           if ("10.20.3.3".equals(invokers.get(i).getUrl().getHost())) {
     *               result.add(invokers.get(i));
     *           }
     *       }
     *       return result;
     *   } (invokers, invocation, context)); // 表示立即执行方法
     */
    public Script(String rule) {
        engine = getEngine();
        try {
            Compilable compilable = (Compilable) engine;
            function = compilable.compile(rule);
        } catch (ScriptException e) {
            logger.error("script route rule invalid, script route error, rule has been ignored. rule: {}, url: ",rule,
                    e);
        }
    }

    public void doRoute() {
        Bindings bindings = createBindings();
        try {
            Object eval = function.eval(bindings);
        } catch (ScriptException e) {
            logger.error(
                    "Scriptrouter exec script error Script route error, rule has been ignored. rule: {}, method: {}, url: ","rule","method",e);
            return; //
        }
    }


    private ScriptEngine getEngine() {
        String type = "javascript";
        return ENGINES.computeIfAbsent(type, t -> {
            ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName(type);
            if (scriptEngine == null) {
                throw new IllegalStateException("unsupported route engine type: " + type);
            }
            return scriptEngine;
        });
    }

    /**
     * create bindings for script engine
     */
    private Bindings createBindings() {
        Bindings bindings = engine.createBindings();
        // create a new List of invokers
        bindings.put("invokers", new ArrayList<>());
        bindings.put("context", new Object());
        return bindings;
    }
}
