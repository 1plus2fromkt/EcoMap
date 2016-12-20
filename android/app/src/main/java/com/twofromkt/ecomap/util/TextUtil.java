package com.twofromkt.ecomap.util;

import android.telephony.PhoneNumberUtils;
import android.webkit.URLUtil;

//TODO this work should be removed to server side
public class TextUtil {

    public static String formatPhone(String source) {
        StringBuilder sb = new StringBuilder();
        for (char c : source.toCharArray()) {
            if (Character.isDigit(c) || c == '+') {
                sb.append(c);
            }
        }
        String number = sb.toString();
        if (PhoneNumberUtils.isGlobalPhoneNumber(number)) {
            return number;
        } else {
            return null;
        }
    }

    public static String formatLink(String source) {
        source = source.trim();
        if (source.endsWith(";")) {
            source = source.substring(0, source.length() - 1);
        }
        if (!(URLUtil.isHttpUrl(source) || URLUtil.isHttpsUrl(source))) {
            source = "http://" + source;
        }
        if (URLUtil.isValidUrl(source) && !source.equals("")) {
            return source;
        } else {
            return null;
        }
    }
}
