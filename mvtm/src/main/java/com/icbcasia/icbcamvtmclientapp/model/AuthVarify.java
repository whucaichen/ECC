package com.icbcasia.icbcamvtmclientapp.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AuthVarify {
    private static final Map<String, String> SECRET_KEY = new HashMap();

    static {
        SECRET_KEY.put("V1.0", "ICBC");
    }

    public AuthVarify() {
    }

    public static Boolean mVTMAuthVerifyData(String authData, String authVersion) {
        if (authData != null && authData.length() != 0) {
            String dataHeader = (String) SECRET_KEY.get(authVersion);
            if (dataHeader == null) {
                return Boolean.valueOf(false);
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
                Calendar c = Calendar.getInstance();
                c.setTime(new Date());

                for (int i = 0; i <= 30; ++i) {
                    Date temp = c.getTime();
                    String varifyData = encryptToSHA(dataHeader + sdf.format(temp));
                    System.out.println(varifyData);
                    if (varifyData.equals(authData)) {
                        return Boolean.valueOf(true);
                    }

                    c.add(Calendar.SECOND, -1);
                }

                return Boolean.valueOf(false);
            }
        } else {
            return Boolean.valueOf(false);
        }
    }

    private static String encryptToSHA(String info) {
        byte[] digesta = null;

        try {
            MessageDigest rs = MessageDigest.getInstance("SHA-1");
            rs.update(info.getBytes());
            digesta = rs.digest();
        } catch (NoSuchAlgorithmException var3) {
            var3.printStackTrace();
        }

        String rs1 = byte2hex(digesta);
        return rs1;
    }

    private static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";

        for (int n = 0; n < b.length; ++n) {
            stmp = Integer.toHexString(b[n] & 255);
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }

        return hs;
    }

    public static void main(String[] args) {
        System.out.println(mVTMAuthVerifyData("11feb954ef6c9ff94a10a8f1a917105458015487", "V1.0"));
    }
}
