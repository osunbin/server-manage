package com.bin.sm.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

public class OsUtil {

    private static final String osName = System.getProperty("os.name");

    public static boolean isWindows() {
        return osName.toLowerCase().contains("window");
    }

    public static boolean isMac() {
        return osName.toLowerCase().contains("mac");
    }

    public static boolean isLinux() {
        return osName.toLowerCase().contains("nux");
    }

    public static double getCpuUsage() {
        if (isLinux()) {
            Process process = null;
            BufferedReader reader = null;
            try {
                ProcessBuilder processBuilder = new ProcessBuilder();
                processBuilder.command("top -n 1 -b");
                process = processBuilder.start();

                reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while (true) {

                    if (!((line = reader.readLine()) != null)) break;

                    if (line.startsWith("%Cpu(s)")) {
                        String[] tokens = line.split("\\s+");
                        String cpuUsage = tokens[1];

                    }
                }

                process.waitFor();
                process.destroy();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }  finally {
                if (reader != null) {
                    try { reader.close();} catch (IOException e) {}
                }
                if (process != null)
                      process.destroy();

            }

        } else if (isWindows()) {
            OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
            return operatingSystemMXBean.getSystemLoadAverage();

        }
        return 0;
    }
}
