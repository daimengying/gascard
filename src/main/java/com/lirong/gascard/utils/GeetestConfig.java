package com.lirong.gascard.utils;

public class GeetestConfig {
    // 填入自己的captcha_id和private_key
    private static final String geetest_id = "4a57bc20acdfee6b178ada43db9e734b";
    private static final String geetest_key = "f49658c761bcece9c8468cd11ce88bb3";
    private static final boolean newfailback = true;

    public static final String getGeetest_id() {
        return geetest_id;
    }

    public static final String getGeetest_key() {
        return geetest_key;
    }

    public static final boolean isnewfailback() {
        return newfailback;
    }
}
