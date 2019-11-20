package com.carecloud.carepay.practice.library.base;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.carecloud.carepay.practice.library.payments.interfaces.IntegratedPaymentQueueRecordDao;
import com.carecloud.carepay.practice.library.payments.models.IntegratedPaymentQueueRecord;


/**
 * @author pjohnson on 2/27/19.
 */
@Database(entities = {IntegratedPaymentQueueRecord.class}, version = 1, exportSchema = false)
public abstract class BreezeDataBase extends RoomDatabase {

    private static volatile BreezeDataBase INSTANCE;

    public static BreezeDataBase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (BreezeDataBase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            BreezeDataBase.class, "practice_breeze_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract IntegratedPaymentQueueRecordDao getIntegratedAndroidPayDao();
}
