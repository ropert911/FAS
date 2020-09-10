package com.xq.fin.analyser.util;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    public static String getString(String pstr, String text) {
        final Pattern pattern = Pattern.compile(pstr);
        Matcher m = pattern.matcher(text);
        String str = "";
        if (m.find()) {
            str = m.group(1);
        }
        return str;
    }

    public static List<String> getmutiString(String pstr, String text) {
        final Pattern pattern = Pattern.compile(pstr);
        Matcher m = pattern.matcher(text);
        List<String> r = new ArrayList<>();
        if (m.find()) {
            int n = m.groupCount();
            String str = "";
            for (int i = 1; i <= n; ++i) {
                str = m.group(i);
                r.add(str);
            }
        }
        return r;
    }

    public static String getSCByCode(String code) {
        switch (code.substring(0, 1)) {
            case "6":
                return "SH" + code;
            default:
                return "SZ" + code;
        }
    }

    public static String getCode(String data) {
        return StringUtil.getString("([0-9]{6})", data);
    }

    public static String getTime(String data) {
        List<String> date = StringUtil.getmutiString("([0-9]{4})[/-]([0-9]{1,2})", data);
        return String.format("%s%02d", date.get(0), Long.valueOf(date.get(1)));
    }

    public static double getDouble(String data) {
        if (data.isEmpty()) {
            return 0;
        } else if ("--".equals(data)) {
            return 0;
        } else if (data.contains("亿")) {
            data = data.replace("亿", "");
            return Double.valueOf(data) * 100000000;
        } else if (data.contains("万")) {
            data = data.replace("万", "");
            return Double.valueOf(data) * 10000;
        } else {
            return Double.valueOf(data);
        }
    }

    public static String getLastYearTime(String yearMonth) {
        String year = yearMonth.substring(0, 4);
        String month = yearMonth.substring(4, 6);
        return String.format("%d%02d", Long.valueOf(year) - 1, Long.valueOf(month));
    }

    //    4月底前：一季报公布完毕。一季报基本奠定了一年的基本情况。
//            5-7月是分红最密集的时间段，尤其是6月；可在4月中开始准备进入。
//            7月1日-8月31日：中报披露时间是在半年度结束的两个月内完成。
//            10月底：三季报公布完毕。
//    年报：明年1月中旬起至4月底要公布完毕
    public static String getLastReportTime() {
        LocalDate localDate = LocalDate.now();
        long year = localDate.getYear();
        long month = localDate.getMonthValue();

        //去年报时间 : 明年1月中旬起至4月底要公布完毕
        if (1 <= month && month <= 4) {
            year -= 1;
            month = 12;
        } else if (5 <= month && month <= 6) {
            //4月底前：一季报公布完毕
            month = 3;
        } else if (7 <= month && month <= 9) {
            //7月1日-8月31日：中报披露时间是在半年度结束的两个月内完成
            month = 6;
        } else {
            month = 9;
        }

        return String.format("%04d%02d", year, month);
    }
}
