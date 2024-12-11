package com.bin.sm.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class JarFileConfigLoader {


    static Configs load(String file) {
        String agentJarPath = "AGENT_JAR_PATH.jar";

        try (JarFile jarFile = new JarFile(new File(agentJarPath))){
            // xxx/xxx/xxx.yml
            ZipEntry zipEntry = jarFile.getEntry(file);
            if (zipEntry == null) {
                return null;
            }
            try (InputStream in = jarFile.getInputStream(zipEntry)) {
                return ConfigLoader.loadFromStream(in, file);
            } catch (IOException ignored) {

            }
        } catch (IOException ignored) {

        }
        return null;
    }
}
