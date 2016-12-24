package com.outlook.sftjun.greendao.db;

import android.content.Context;

import com.outlook.sftjun.greendao.gen.DaoMaster;
import com.outlook.sftjun.greendao.gen.DaoSession;

/**
 * Created by j.yin on 2016/12/22
 */

public class DBHelper {

    private static DaoMaster daoMaster;
    private static DaoSession daoSession;

    /**
     * 取得DaoMaster
     *
     * @param context
     * @return
     */
    private static DaoMaster getDaoMaster(Context context,String DBName) {
        if (daoMaster == null) {
            DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(context,
                    DBName, null);
            daoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return daoMaster;
    }

    /**
     * 取得DaoSession
     *
     * @param context
     * @return
     */
    public static DaoSession getDaoSession(Context context,String DBName) {
        if (daoSession == null) {
            if (daoMaster == null) {
                daoMaster = getDaoMaster(context,DBName);
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }
}