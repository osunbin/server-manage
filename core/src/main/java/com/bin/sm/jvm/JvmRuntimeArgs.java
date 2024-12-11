package com.bin.sm.jvm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JvmRuntimeArgs {

    static Logger logger = LoggerFactory.getLogger(JvmRuntimeArgs.class);


    public static void genArgs() {
        StringBuilder sb = new StringBuilder();
        List<String> jvmArgs = ManagementFactory.getRuntimeMXBean().getInputArguments();
        for (String arg : jvmArgs) {
            sb.append(arg).append(" ");
        }
        System.out.println(sb.toString());
    }


    private static String getVmStartTime() {
        long startTime = ManagementFactory.getRuntimeMXBean().getStartTime();
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(startTime));
    }

    private static List<String> getLibJarNames() {
        List<URL> classLoaderUrls = loadClassLoaderUrls();
        return extractLibJarNamesFromURLs(classLoaderUrls);
    }
    private static Set<ClassLoader> CURRENT_URL_CLASSLOADER_SET = new HashSet<>();


    private static List<URL> loadClassLoaderUrls() {
        List<URL> classLoaderUrls = new ArrayList<>();
        for (ClassLoader classLoader : CURRENT_URL_CLASSLOADER_SET) {
            try {
                URLClassLoader webappClassLoader = (URLClassLoader) classLoader;
                URL[] urls = webappClassLoader.getURLs();
                classLoaderUrls.addAll(Arrays.asList(urls));
            } catch (Exception e) {
                logger.warn("Load classloader urls exception: {}", e.getMessage());
            }
        }
        return classLoaderUrls;
    }

    private static List<String> extractLibJarNamesFromURLs(List<URL> urls) {
        Set<String> libJarNames = new HashSet<>();
        for (URL url : urls) {
            try {
                String libJarName = extractLibJarName(url);
                if (libJarName.endsWith(".jar")) {
                    libJarNames.add(libJarName);
                }
            } catch (Exception e) {
                logger.warn("Extracting library name exception: {}", e.getMessage());
            }
        }
        List<String> sortedLibJarNames = new ArrayList<>(libJarNames.size());
        if (!libJarNames.isEmpty()) {
            sortedLibJarNames.addAll(libJarNames);
            Collections.sort(sortedLibJarNames);
        }
        return sortedLibJarNames;
    }

    private static String extractLibJarName(URL url) {
        String protocol = url.getProtocol();
        if (protocol.equals("file")) {
            return extractNameFromFile(url.toString());
        } else if (protocol.equals("jar")) {
            return extractNameFromJar(url.toString());
        } else {
            return "";
        }
    }

    private static String extractNameFromFile(String fileUri) {
        int lastIndexOfSeparator = fileUri.lastIndexOf(File.separator);
        if (lastIndexOfSeparator < 0) {
            return fileUri;
        } else {
            return fileUri.substring(lastIndexOfSeparator + 1);
        }
    }

    private static String extractNameFromJar(String jarUri) {
        String uri = jarUri.substring(0, jarUri.lastIndexOf("!"));
        return extractNameFromFile(uri);
    }


}
