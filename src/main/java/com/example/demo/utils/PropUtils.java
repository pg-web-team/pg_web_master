/**
 * 
 */
package com.example.demo.utils;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Wang,Jingzhu
 *
 */
public class PropUtils {

    /**
     * @param args
     */
    public static Map<String, String> load(String file) {
        Properties prop = new Properties();
        Map<String, String> propMap = new HashMap<>();
        try (FileInputStream in = new FileInputStream(file)) {
            prop.load(in);
            for (Entry<Object, Object> entry : prop.entrySet()) {
                String key = StringUtils.trimToNull(String.valueOf(entry.getKey()));
                if (key != null) {
                    propMap.put(StringUtils.upperCase(key), String.valueOf(entry.getValue()));
                }
            }
        } catch (Exception e) {
        }
        return propMap;
    }

}
