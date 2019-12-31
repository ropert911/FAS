package com.xq.secser.ssbser.utils;

/**
 * @author sk-qianxiao
 * @date 2019/12/31
 */
public class StringUtil {
    public static String getEncoding(String str) {
        String encode = "GB2312";
        try {
            //判断是不是GB2312
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s = encode;
                //是的话，返回“GB2312“，以下代码同理
                return s;
            }
        } catch (Exception exception) {
        }
        encode = "ISO-8859-1";
        try {
            //判断是不是ISO-8859-1
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s1 = encode;
                return s1;
            }
        } catch (Exception exception1) {
        }
        encode = "UTF-8";
        try {
            //判断是不是UTF-8
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s2 = encode;
                return s2;
            }
        } catch (Exception exception2) {
        }
        encode = "GBK";
        try {
            //判断是不是GBK
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s3 = encode;
                return s3;
            }
        } catch (Exception exception3) {
        }
        return "";
    }
}
