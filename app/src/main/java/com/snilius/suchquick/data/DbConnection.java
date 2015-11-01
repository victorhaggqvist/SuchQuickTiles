package com.snilius.suchquick.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.snilius.suchquick.BuildConfig;
import com.snilius.suchquick.entity.DaoMaster;
import com.snilius.suchquick.entity.DaoSession;

import de.greenrobot.dao.query.QueryBuilder;
import timber.log.Timber;

/**
 * @author Victor Häggqvist
 * @since 10/31/15
 */
public class DbConnection {

    private static Context context;

    public static DaoSession getSession() {
        if (Holder.session == null) {
            // enable some debugging
            if (BuildConfig.DEBUG) {
                QueryBuilder.LOG_SQL = true;
                QueryBuilder.LOG_VALUES = true;
            }

            Timber.i("creating new db session");
            DaoMaster.DevOpenHelper dbHelper = new DaoMaster.DevOpenHelper(context, "quick-db", null);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            DaoMaster daoMaster = new DaoMaster(db);
            Holder.session = daoMaster.newSession();
        } else {
            Timber.i("using existing db session");
        }

        return Holder.session;
    }

    public static void setContext(Context context) {
        DbConnection.context = context;
    }

    private static class Holder {
        private static DaoSession session = getSession();
    }
}