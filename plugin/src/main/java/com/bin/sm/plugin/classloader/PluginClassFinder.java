package com.bin.sm.plugin.classloader;

import com.bin.sm.plugin.Plugin;
import com.bin.sm.plugin.common.LoggerFactory;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

public class PluginClassFinder {
    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger();

    private final Map<String, PluginClassLoader> pluginClassLoaderMap = new HashMap<>();

    /**
     * Cache pluginClassLoader
     *
     * @param plugin plugin
     */
    public void addPluginClassLoader(Plugin plugin) {
        pluginClassLoaderMap.put(plugin.getName(), plugin.getPluginClassLoader());
    }

    /**
     * Remove pluginClassLoader
     *
     * @param plugin plugin
     */
    public void removePluginClassLoader(Plugin plugin) {
        pluginClassLoaderMap.remove(plugin.getName());
    }

    /**
     * Load the corresponding class name under the Sermant search path
     *
     * @param name class name
     * @return Class<?>
     * @throws ClassNotFoundException If the class is not found in any of pluginClassLoaders, an exception is thrown
     * that the class is not found
     */
    public Class<?> loadSermantClass(String name) throws ClassNotFoundException {
        for (PluginClassLoader pluginClassLoader : pluginClassLoaderMap.values()) {
            try {
                Class<?> clazz = pluginClassLoader.loadSermantClass(name);
                if (clazz != null) {
                    return clazz;
                }
            } catch (ClassNotFoundException e) {
                // Class not found, ignored, exception thrown later
            }
        }
        throw new ClassNotFoundException("Can not load class in pluginClassLoaders: " + name);
    }

    /**
     * Find the resource corresponding to the resource path under the Sermant search path
     *
     * @param path resource path
     * @return URL of the resource
     */
    public Optional<URL> findSermantResource(String path) {
        for (PluginClassLoader pluginClassLoader : pluginClassLoaderMap.values()) {
            URL url = pluginClassLoader.findResource(path);
            if (url != null) {
                return Optional.of(url);
            }
        }
        return Optional.empty();
    }
}
