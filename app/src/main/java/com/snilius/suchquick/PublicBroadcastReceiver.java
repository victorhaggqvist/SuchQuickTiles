package com.snilius.suchquick;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.snilius.suchquick.data.DbConnection;
import com.snilius.suchquick.entity.DaoSession;
import com.snilius.suchquick.entity.IntentExtra;
import com.snilius.suchquick.entity.Shortcut;
import com.snilius.suchquick.entity.ShortcutDao;

import java.lang.reflect.Method;
import java.util.List;

public class PublicBroadcastReceiver extends BroadcastReceiver {

    public static final String ACTION_CLICK = "com.snilius.suchquick.ACTION_CLICK";

    public static final String EXTRA_SHORTCUT_ID = "com.snilius.suchquick.EXTRA_SHORTCUT_ID";

    private static final String TAG = PublicBroadcastReceiver.class.getSimpleName();

    public PublicBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        Log.i(TAG, "Called");

        if (action.equals(ACTION_CLICK)) {
            DbConnection.setContext(context);
            DaoSession session = DbConnection.getSession();

            long shortcutId = intent.getLongExtra(EXTRA_SHORTCUT_ID, -1);
            if (shortcutId != -1) {
                ShortcutDao shortcutDao = session.getShortcutDao();
                Shortcut shortcut = shortcutDao.load(shortcutId);

                Intent sIntent = new Intent();
                sIntent.setComponent(new ComponentName(shortcut.getPackageName(), shortcut.getClassName()));

                List<IntentExtra> extraList = shortcut.getIntentExtraList();
                for (IntentExtra ext: extraList) {
                    String key = ext.getKeyName();
                    String stringProp = ext.getStringProp();
                    if (!stringProp.equals(null)) {
                        sIntent.putExtra(key, stringProp);
                    } else {
                        Integer intProp = ext.getIntProp();
                        if (!intProp.equals(null)) {
                            sIntent.putExtra(key, intProp);
                        }
                    }
                }

                sIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(sIntent);

                context.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)); // ie close notification panel
            } else {
                Log.i(TAG, "Called with no extra");
            }

        } else {
            Log.i(TAG, "Called with no action");
        }
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");
    }
}
