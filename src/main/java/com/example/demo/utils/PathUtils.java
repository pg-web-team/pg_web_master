/**
 * 
 */
package com.example.demo.utils;

import java.nio.charset.StandardCharsets;

/**
 * @author Wang,Jingzhu
 *
 */
public class PathUtils {

    /**
     * App path
     * 
     * @param args
     */
    public static String getAppPath() {
        String realPath =
                PathUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath();

//        System.out.println("realPath" + realPath);
        int pos = realPath.indexOf("file:");
        if (pos > -1)
            realPath = realPath.substring(pos + 5);
        if (realPath.endsWith("!"))
            realPath = realPath.substring(0, realPath.lastIndexOf("/"));
        try {
            realPath = java.net.URLDecoder.decode(realPath, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return realPath;

    }

    public static void main(String[] args) {
        System.out.println(getAppPath());
    }

}
