package com.bin.sm.plugin.classloader;

import com.bin.sm.plugin.agent.config.AgentConfig;
import com.bin.sm.plugin.common.LoggerFactory;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class PluginClassLoader extends URLClassLoader {
    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger();

    private final HashMap<Long, ClassLoader> localLoader = new HashMap<>();

    /**
     * Whether to use context classLoader
     */
    private final boolean useContextLoader;

    /**
     * Manages the loaded classes in the classLoader
     */
    private final Map<String, Class<?>> pluginClassMap = new HashMap<>();

    /**
     * constructor
     *
     * @param urls The URL of the lib where the class is to be loaded by the classloader
     * @param parent parent classLoader
     */
    public PluginClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
        useContextLoader = AgentConfig.INSTANCE.isUseContextLoader();
    }

    /**
     * Load the plugin class and cache it
     *
     * @param name fully qualified name
     * @return class object
     */
    private Class<?> loadPluginClass(String name) {
        if (!pluginClassMap.containsKey(name)) {
            try {
                pluginClassMap.put(name, findClass(name));
            } catch (ClassNotFoundException ignored) {
                pluginClassMap.put(name, null);
            }
        }
        return pluginClassMap.get(name);
    }

    /**
     * Adds the search path for the class to the classloader
     *
     * @param url search path
     */
    public void appendUrl(URL url) {
        this.addURL(url);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return this.loadClass(name, false);
    }

    @Override
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            Class<?> clazz = loadPluginClass(name);

            // If the class cannot be loaded on its own, it is loaded in the Sermant search path
            if (clazz == null) {
                try {
                    clazz = super.loadClass(name, resolve);
                } catch (ClassNotFoundException e) {
                    // Catch the exception that the class cannot be found. The next step is to load the class by
                    // the localLoader
                    // ignored
                    LOGGER.log(Level.FINE, "Load class failed, msg is {0}", e.getMessage());
                }
            }

            // If the class cannot be found from the Sermant search path, it is attempted to be loaded via the
            // thread-bound localClassLoader
            if (clazz == null) {
                clazz = getClassFromLocalClassLoader(name);
            }

            // If the class cannot be found, an exception is thrown
            if (clazz == null) {
                throw new ClassNotFoundException("Sermant pluginClassLoader can not load class: " + name);
            }

            // Parse the class if necessary
            if (resolve) {
                resolveClass(clazz);
            }
            return clazz;
        }
    }

    private Class<?> getClassFromLocalClassLoader(String name) {
        ClassLoader loader = localLoader.get(Thread.currentThread().threadId());

        if (loader == null && useContextLoader) {
            loader = Thread.currentThread().getContextClassLoader();
        }
        Class<?> clazz = null;

        // Make sure the localClassLoader is not the current classLoader or ServiceClassLoader, otherwise it
        // will cause stackoverflow
        if (loader != null && !this.equals(loader) && !(loader instanceof ServiceClassLoader)) {
            try {
                clazz = loader.loadClass(name);
            } catch (ClassNotFoundException e) {
                // Class not found, ignored, exception thrown later
                LOGGER.log(Level.FINE, "Load class failed, msg is {0}", e.getMessage());
            }
        }
        return clazz;
    }

    /**
     * Load classes only through Sermant's own search path, not using localClassLoader, which would otherwise cause
     * stackoverflow
     *
     * @param name class name
     * @return Class<?>
     * @throws ClassNotFoundException class not found
     */
    public Class<?> loadSermantClass(String name) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            Class<?> clazz = loadPluginClass(name);

            if (clazz == null) {
                try {
                    clazz = super.loadClass(name, false);
                } catch (ClassNotFoundException e) {
                    // Class not found, ignored, exception thrown later
                }
            }

            // If the class cannot be found, an exception is thrown
            if (clazz == null) {
                throw new ClassNotFoundException("Sermant pluginClassLoader can not load class: " + name);
            }
            return clazz;
        }
    }

    /**
     * Set up the localClassLoader
     *
     * @param loader classLoader
     */
    public void setLocalLoader(ClassLoader loader) {
        localLoader.put(Thread.currentThread().threadId(), loader);
    }

    /**
     * Clear the localClassLoader
     */
    public void removeLocalLoader() {
        localLoader.remove(Thread.currentThread().threadId());
    }
}