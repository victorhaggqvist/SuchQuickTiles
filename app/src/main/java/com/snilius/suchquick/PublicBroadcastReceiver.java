package com.snilius.suchquick;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.snilius.suchquick.data.DbConnection;
import com.snilius.suchquick.data.TileType;
import com.snilius.suchquick.entity.DaoSession;
import com.snilius.suchquick.entity.IntentExtra;
import com.snilius.suchquick.entity.Launcher;
import com.snilius.suchquick.entity.LauncherDao;
import com.snilius.suchquick.entity.Shortcut;
import com.snilius.suchquick.entity.ShortcutDao;
import com.snilius.suchquick.entity.Tile;
import com.snilius.suchquick.entity.TileDao;

import java.util.List;

public class PublicBroadcastReceiver extends BroadcastReceiver {

//    public static final String ACTION_CLICK = "com.snilius.suchquick.ACTION_CLICK";
    public static final String ACTION_SHORTCUT = "com.snilius.suchquick.ACTION_SHORTCUT";
    public static final String ACTION_LAUNCHER = "com.snilius.suchquick.ACTION_LAUNCHER";

    public static final String EXTRA_SHORTCUT_ID = "com.snilius.suchquick.EXTRA_SHORTCUT_ID";
    public static final String EXTRA_TILE_ID = "com.snilius.suchquick.EXTRA_TILE_ID";
    public static final String EXTRA_IS_LONG_CLICK = "com.snilius.suchquick.EXTRA_IS_LONG_CLICK";

    private static final String TAG = PublicBroadcastReceiver.class.getSimpleName();

    public PublicBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        Log.i(TAG, "Called");
        Log.d(TAG, "action " + action);

        int isLongClick = intent.getIntExtra(EXTRA_IS_LONG_CLICK, -1);
        Log.d(TAG, "isLongClick " + isLongClick);

        if (action.equals(ACTION_SHORTCUT)) {
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

        } else if (action.equals(ACTION_LAUNCHER)) {
            DbConnection.setContext(context);
            DaoSession session = DbConnection.getSession();

            long tileId = intent.getLongExtra(EXTRA_TILE_ID, -1);
            if (tileId != -1) {
                TileDao tileDao = session.getTileDao();
                Tile tile = tileDao.load(tileId);

                if (tile.getClickType().equals(TileType.LAUNCHER)) {
                    LauncherDao launcherDao = session.getLauncherDao();
                    Launcher launcher = launcherDao.load(tile.getClickActionId());
                    String packageName = launcher.getPackageName();

                    Intent clickAction = context.getPackageManager().getLaunchIntentForPackage(packageName);

                    context.startActivity(clickAction);
                    context.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)); // ie close notification panel
                }
            }
        }
        else {
            Log.i(TAG, "Called with no action");
        }
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");
    }
}
