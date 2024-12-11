package com.bin.sm.toolkit.config;

import com.bin.sm.Main;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ConfigLoader {


    static {
        // 获取路径 jar 去jar获取  file
        URL url = Main.class.getResource("");
        String protocol = url.getProtocol();
        if ("file".equals(protocol)) {
            String realPath = getRealPath();

            String config = realPath + File.separator + "agent.yml";
            System.setProperty("configPath", config);
            File file = new File(config);

            System.out.println(file.exists());
            System.out.println("file-"+getRealPath());
        }else if ("jar".equals(protocol)) {
            System.setProperty("configPath", getProjectPath());
            System.out.println("jar-"+getProjectPath());
        }
    }


    /**
     * 获取项目所在路径(包括jar)
     *
     * @return
     */
    public static String getProjectPath() {

        URL url = Main.class.getProtectionDomain().getCodeSource()
                .getLocation();
        String filePath = null;
        try {
            filePath = java.net.URLDecoder.decode(url.getPath(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (filePath.endsWith(".jar"))
            filePath = filePath.substring(0, filePath.lastIndexOf("/") + 1);
        File file = new File(filePath);
        filePath = file.getAbsolutePath();
        return filePath;
    }

    /**
     * 获取项目所在路径
     *
     * @return
     */
    public static String getRealPath() {
        String realPath = Main.class.getClassLoader().getResource("")
                .getFile();
        File file = new File(realPath);
        realPath = file.getAbsolutePath();

        realPath = java.net.URLDecoder.decode(realPath, StandardCharsets.UTF_8);

        return realPath;
    }
}
