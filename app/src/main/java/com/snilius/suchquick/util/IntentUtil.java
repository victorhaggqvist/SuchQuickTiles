package com.snilius.suchquick.util;

import android.content.ComponentName;
import android.content.Intent;

/**
 * @author Victor Häggqvist
 * @since 11/2/15
 */
public class IntentUtil {
    public static Intent getIntentByComponentInfo(String packageName, String className) {
        return new Intent().setComponent(new ComponentName(packageName, className));
    }
}
