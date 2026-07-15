
package com.ai.weather.util;

import java.util.HashMap;
import java.util.Map;

public class WeatherCodeMapper {

    private static final Map<Integer, String> codeMap = new HashMap<>();

    static {
        codeMap.put(0, "晴朗");
        codeMap.put(1, "大部晴朗");
        codeMap.put(2, "局部多云");
        codeMap.put(3, "阴天");
        codeMap.put(45, "雾");
        codeMap.put(48, "冻雾");
        codeMap.put(51, "毛毛雨");
        codeMap.put(53, "毛毛雨");
        codeMap.put(55, "毛毛雨");
        codeMap.put(56, "冻毛毛雨");
        codeMap.put(57, "冻毛毛雨");
        codeMap.put(61, "小雨");
        codeMap.put(63, "中雨");
        codeMap.put(65, "大雨");
        codeMap.put(66, "冻雨");
        codeMap.put(67, "冻雨");
        codeMap.put(71, "小雪");
        codeMap.put(73, "中雪");
        codeMap.put(75, "大雪");
        codeMap.put(77, "雪粒");
        codeMap.put(80, "阵雨");
        codeMap.put(81, "强阵雨");
        codeMap.put(82, "暴雨");
        codeMap.put(85, "阵雪");
        codeMap.put(86, "强阵雪");
        codeMap.put(95, "雷阵雨");
        codeMap.put(96, "雷阵雨伴冰雹");
        codeMap.put(99, "强雷阵雨伴冰雹");
    }

    public static String describe(int code) {
        return codeMap.getOrDefault(code, "未知");
    }

    public enum WeatherCategory {
        SUNNY, CLOUDY, FOGGY, RAINY, SNOWY, THUNDER
    }

    public static WeatherCategory category(int code) {
        if (code == 0 || code == 1) {
            return WeatherCategory.SUNNY;
        } else if (code == 2 || code == 3) {
            return WeatherCategory.CLOUDY;
        } else if (code == 45 || code == 48) {
            return WeatherCategory.FOGGY;
        } else if ((code >= 51 && code <= 67) || (code >= 80 && code <= 82)) {
            return WeatherCategory.RAINY;
        } else if ((code >= 71 && code <= 77) || (code >= 85 && code <= 86)) {
            return WeatherCategory.SNOWY;
        } else if (code >= 95 && code <= 99) {
            return WeatherCategory.THUNDER;
        } else {
            return WeatherCategory.SUNNY;
        }
    }

    public static String uvDescription(double uv) {
        if (uv < 3) {
            return "低";
        } else if (uv < 6) {
            return "中等";
        } else if (uv < 8) {
            return "高";
        } else if (uv < 11) {
            return "很高";
        } else {
            return "极高";
        }
    }
}
