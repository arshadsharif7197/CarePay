package com.carecloud.carepay.patient.payment.androidpay;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.carecloud.carepay.patient.payment.androidpay.models.AndroidPayQueuePaymentRecord;


/**
 * @author pjohnson on 2/27/19.
 */
@Database(entities = {AndroidPayQueuePaymentRecord.class}, version = 1)
public abstract class BreezeDataBase extends RoomDatabase {

    private static volatile BreezeDataBase INSTANCE;

    public static BreezeDataBase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (BreezeDataBase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            BreezeDataBase.class, "breeze_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract AndroidPayQueuePaymentRecordDao getAndroidPayDao();
}
