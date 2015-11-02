package com.snilius.suchquick.background;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import com.kcoppock.broadcasttilesupport.BroadcastTileIntentBuilder;
import com.snilius.suchquick.PublicBroadcastReceiver;
import com.snilius.suchquick.data.DbConnection;
import com.snilius.suchquick.data.TileType;
import com.snilius.suchquick.entity.DaoSession;
import com.snilius.suchquick.entity.Launcher;
import com.snilius.suchquick.entity.Shortcut;
import com.snilius.suchquick.entity.Tile;
import com.snilius.suchquick.entity.TileDao;
import com.snilius.suchquick.util.BitmapUtil;

import java.util.List;

import cyanogenmod.app.CMStatusBarManager;
import cyanogenmod.app.CustomTile;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = BootReceiver.class.getSimpleName();

//    public BootReceiver() {
//    }

    @Override
    public void onReceive(Context context, Intent intent) {

        DbConnection.setContext(context);
        DaoSession session = DbConnection.getSession();

        List<Tile> tiles = session.getTileDao()
                .queryBuilder()
                .where(TileDao.Properties.Enabled.eq(true))
                .build().list();

        for (Tile t:tiles) {
            Intent clickAction = null;
            Intent longClickAction = null;
            Drawable icon = null;
            int iconResource = 0;


            if (null == t.getClickType()) {
                clickAction = new Intent();
                if (!t.getIconIsPackageDrawable()) {
                    iconResource = t.getIconResource();
                } else {
                    t.setIconResource(-1);
                }

            } else if (t.getClickType().equals(TileType.LAUNCHER)) {
                Launcher launcher = session.getLauncherDao().load(t.getClickActionId());
                Intent launcherIntent = context.getPackageManager().getLaunchIntentForPackage(launcher.getPackageName());

                clickAction = new Intent(PublicBroadcastReceiver.ACTION_LAUNCHER);
                clickAction.putExtra(PublicBroadcastReceiver.EXTRA_TILE_ID, t.getId());

                if (t.getIconIsPackageDrawable()) {
                    try {
                        icon = context.getPackageManager().getActivityIcon(launcherIntent);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                } else if (t.getIconResource() != null) {
                    iconResource = t.getIconResource();
                }

            } else if (t.getClickType().equals(TileType.SCHORTCUT)) {
                Shortcut shortcut = session.getShortcutDao().load(t.getClickActionId());
                clickAction = new Intent(PublicBroadcastReceiver.ACTION_SHORTCUT);
                clickAction.putExtra(PublicBroadcastReceiver.EXTRA_SHORTCUT_ID, t.getClickActionId());

                if (t.getIconIsPackageDrawable()) {
                    Intent iconFetchIntent = new Intent()
                            .setComponent(new ComponentName(
                                    shortcut.getPackageName(),
                                    shortcut.getClassName()
                            ));
                    try {
                        icon = context.getPackageManager().getActivityIcon(iconFetchIntent);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                } else if (t.getIconResource() != null) {
                    iconResource = t.getIconResource();
                }
            }

            if (null == t.getLongClickType()) {
                longClickAction = new Intent();
            } else if (t.getLongClickType().equals(TileType.LAUNCHER)) {
                longClickAction = new Intent(PublicBroadcastReceiver.ACTION_LAUNCHER);
                longClickAction.putExtra(PublicBroadcastReceiver.EXTRA_TILE_ID, t.getId());
                longClickAction.putExtra(PublicBroadcastReceiver.EXTRA_IS_LONG_CLICK, 1);
            } else if (t.getLongClickType().equals(TileType.SCHORTCUT)) {
                Shortcut shortcut = session.getShortcutDao().load(t.getLongClickActionId());
                longClickAction = new Intent(PublicBroadcastReceiver.ACTION_SHORTCUT);
                longClickAction.putExtra(PublicBroadcastReceiver.EXTRA_SHORTCUT_ID, shortcut.getId());
                longClickAction.putExtra(PublicBroadcastReceiver.EXTRA_IS_LONG_CLICK, 1);
            }

            if(cyanogenmod.os.Build.CM_VERSION.SDK_INT >= cyanogenmod.os.Build.CM_VERSION_CODES.BOYSENBERRY) {
                Log.d(TAG, "Publishing CM tile for "+t.getLabel());

                PendingIntent onClickPendingIntent = PendingIntent.getBroadcast(
                        context, 0, clickAction, PendingIntent.FLAG_CANCEL_CURRENT);

                PendingIntent onLongClickPendingIntent = PendingIntent.getBroadcast(
                        context, 0, longClickAction, PendingIntent.FLAG_CANCEL_CURRENT);

                CustomTile.Builder builder = new CustomTile.Builder(context)
                        .setOnClickIntent(onClickPendingIntent)
                        .setContentDescription("Nope nothing so see here. Just click it.")
                        .setLabel(t.getLabel())
                        .shouldCollapsePanel(true);

                if (icon != null) {
                    builder.setIcon(BitmapUtil.drawableToBitmap(icon));
                } else {
                    builder.setIcon(iconResource);
                }

                int id = Integer.parseInt(t.getId().toString());
                CMStatusBarManager.getInstance(context).publishTile(id, builder.build());
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.d(TAG, "Publishing AOSP tile for " + t.getLabel());

                Intent build = new BroadcastTileIntentBuilder(context, t.getSystemTileName())
                        .setOnClickBroadcast(clickAction)
                        .setOnLongClickBroadcast(longClickAction)
                        .setIconResource(iconResource)
                        .setVisible(true)
                        .setLabel(t.getLabel())
                        .setContentDescription(t.getSystemTileName())
                        .build();

                context.sendBroadcast(build);
            }
        }
    }
}
