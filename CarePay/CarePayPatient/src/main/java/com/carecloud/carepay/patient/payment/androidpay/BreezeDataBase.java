package com.carecloud.carepay.patient.payment.androidpay;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
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
                            .addMigrations(MIGRATION_2_1)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    //This is not a realistic situation. Nobody in production will pass from a version 2 to 1.
    //This is for dev and qa purposes.
    private static final Migration MIGRATION_2_1 = new Migration(2, 1) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Create the new table
            database.execSQL("DROP TABLE AppointmentCalendarEvent");
        }
    };

    public abstract AndroidPayQueuePaymentRecordDao getAndroidPayDao();
}
