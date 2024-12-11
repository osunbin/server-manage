package com.bin.sm.plugin.utils;


import java.io.IOException;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

public class JarFileUtils {
    private JarFileUtils() {
    }

    /**
     * Gets the manifest internal attributes
     *
     * @param jarFile jar package name
     * @param key manifest key
     * @return attributes of manifest
     * @throws IOException can not find manifest
     */
    public static Object getManifestAttr(JarFile jarFile, String key) throws IOException {
        return jarFile.getManifest().getMainAttributes().get(new Attributes.Name(key));
    }

    /**
     * Gets the URL of the jar package the class is in
     *
     * @param cls class
     * @return url
     */
    public static URL getJarUrl(Class<?> cls) {
        return cls.getProtectionDomain().getCodeSource().getLocation();
    }
}
