package com.bin.sm.plugin.classloader;

import com.bin.sm.plugin.common.BootArgsIndexer;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;


public class ServiceClassLoader extends URLClassLoader {
    /**
     * Manages the loaded classes in the ServiceClassLoader
     */
    private final Map<String, Class<?>> serviceClassMap = new HashMap<>();

    /**
     * Constructor.
     *
     * @param urls Url of plugin service package
     * @param parent parent classloader
     */
    public ServiceClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    /**
     * Load and maintain the classes in the plugin service package
     *
     * @param name class full qualified name
     * @return Class object
     */
    private Class<?> loadServiceClass(String name) {
        if (!serviceClassMap.containsKey(name)) {
            try {
                serviceClassMap.put(name, findClass(name));
            } catch (ClassNotFoundException ignored) {
                serviceClassMap.put(name, null);
            }
        }
        return serviceClassMap.get(name);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return this.loadClass(name, false);
    }

    @Override
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            Class<?> clazz = loadServiceClass(name);
            if (clazz == null) {
                clazz = super.loadClass(name, resolve);

                // Put the classes loaded from itself into the cache
                if (clazz != null && clazz.getClassLoader() == this) {
                    serviceClassMap.put(name, clazz);
                }
            }
            if (resolve) {
                resolveClass(clazz);
            }
            return clazz;
        }
    }

    @Override
    public URL getResource(String name) {
        URL url = null;

        // Customize the getResource method for the log configuration file. First get agent/config/logback.xml,
        // then logback.xml in the resource file under the PluginClassloader
        if ("logback.xml".equals(name)) {
            File logSettingFile = BootArgsIndexer.getLogSettingFile();
            if (logSettingFile.exists() && logSettingFile.isFile()) {
                try {
                    url = logSettingFile.toURI().toURL();
                } catch (MalformedURLException e) {
                    url = findResource(name);
                }
            } else {
                url = findResource(name);
            }
        }
        if (url == null) {
            url = super.getResource(name);
        }
        return url;
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }
}
