package com.bin.sm.plugin;

import com.bin.sm.plugin.classloader.PluginClassLoader;
import com.bin.sm.plugin.classloader.ServiceClassLoader;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Plugin {
    /**
     * plugin name
     */
    private String name;

    /**
     * plugin version
     */
    private String version;

    /**
     * plugin path
     */
    private String path;

    /**
     * Dynamic plugin: true means it can be installed dynamically, and false means it cannot be installed dynamically
     */
    private boolean isDynamic;

    /**
     * List of plugin services
     */
    private List<String> services = new ArrayList<>();

    /**
     * Plugin configuration list
     */
    private List<String> configs = new ArrayList<>();

    /**
     * List of plugin interceptors, indexed by adviceKey
     */
    private HashMap<String, Set<String>> interceptors = new HashMap<>();

    /**
     * Set of adviceKey that hold advice lock
     */
    private Set<String> adviceLocks = new HashSet<>();

    /**
     * The classloader used to load the main plugin-module of the plugin
     */
    private PluginClassLoader pluginClassLoader;

    /**
     * The classloader used to load the plugin service module
     */
    private ServiceClassLoader serviceClassLoader;

    /**
     * Resettable class file transformer
     */
    private ResettableClassFileTransformer classFileTransformer;

    /**
     * constructor
     *
     * @param name plugin name
     * @param path plugin path
     * @param isDynamic Whether the plugin is loaded via dynamic installation
     * @param pluginClassLoader PluginClassLoader
     */
    public Plugin(String name, String path, boolean isDynamic, PluginClassLoader pluginClassLoader) {
        this.name = name;
        this.path = path;
        this.isDynamic = isDynamic;
        this.pluginClassLoader = pluginClassLoader;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isDynamic() {
        return isDynamic;
    }

    public void setDynamic(boolean dynamic) {
        this.isDynamic = dynamic;
    }

    public List<String> getServices() {
        return services;
    }

    public void setServices(List<String> services) {
        this.services = services;
    }

    public List<String> getConfigs() {
        return configs;
    }

    public void setConfigs(List<String> configs) {
        this.configs = configs;
    }

    public HashMap<String, Set<String>> getInterceptors() {
        return interceptors;
    }

    public void setInterceptors(HashMap<String, Set<String>> interceptors) {
        this.interceptors = interceptors;
    }

    public Set<String> getAdviceLocks() {
        return adviceLocks;
    }

    public void setAdviceLocks(Set<String> adviceLocks) {
        this.adviceLocks = adviceLocks;
    }

    public PluginClassLoader getPluginClassLoader() {
        return pluginClassLoader;
    }

    public void setPluginClassLoader(PluginClassLoader pluginClassLoader) {
        this.pluginClassLoader = pluginClassLoader;
    }

    public ServiceClassLoader getServiceClassLoader() {
        return serviceClassLoader;
    }

    public void setServiceClassLoader(ServiceClassLoader serviceClassLoader) {
        this.serviceClassLoader = serviceClassLoader;
    }

    public ResettableClassFileTransformer getClassFileTransformer() {
        return classFileTransformer;
    }

    public void setClassFileTransformer(ResettableClassFileTransformer classFileTransformer) {
        this.classFileTransformer = classFileTransformer;
    }
}
