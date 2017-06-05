package com.icbcasia.icbcamvtmclientapp.bak;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Chance on 2017/5/15.
 */

public class EncodeTest {

    public static void main(String[] args) {
        try {
            File file = new File("E:\\test");
            InputStream input = new FileInputStream(file);
            StringBuffer buffer = new StringBuffer();
            byte[] bytes = new byte[1024];
            for (int n; (n = input.read(bytes)) != -1; ) {
                buffer.append(new String(bytes, 0, n));
//                buffer.append(new String(bytes,0,n,"UTF-8"));
            }
            System.out.println(getEncoding(buffer.toString()) + " - " + buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getEncoding(String str) {
        String encode = "GB2312";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s = encode;
                System.out.println(new String(str.getBytes(encode), "utf-8"));
                return s;
            }
        } catch (Exception exception) {
        }
        encode = "ISO-8859-1";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s1 = encode;
                System.out.println(new String(str.getBytes(encode), "utf-8"));
                return s1;
            }
        } catch (Exception exception1) {
        }
        encode = "UTF-8";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s2 = encode;
                System.out.println(new String(str.getBytes(encode), "utf-8"));
                return s2;
            }
        } catch (Exception exception2) {
        }
        encode = "GBK";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s3 = encode;
                System.out.println(new String(str.getBytes(encode), "utf-8"));
                return s3;
            }
        } catch (Exception exception3) {
        }
        return "";
    }
}
