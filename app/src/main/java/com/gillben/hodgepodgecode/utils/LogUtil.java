package com.gillben.hodgepodgecode.utils;

import android.util.Log;

public final class LogUtil {

    private static final boolean LOG_DEBUG = false;


    public static void logInfo(String tag, String content) {
        if (LOG_DEBUG) {
            Log.e(tag, content);
        }
    }

    public static void logDebug(String tag, String content) {
        if (LOG_DEBUG) {
            Log.d(tag, content);
        }
    }

    public static void logWarn(String tag, String content) {
        if (LOG_DEBUG) {
            Log.w(tag, content);
        }
    }

    public static void logError(String tag, String content) {
        if (LOG_DEBUG) {
            Log.e(tag, content);
        }
    }


}
