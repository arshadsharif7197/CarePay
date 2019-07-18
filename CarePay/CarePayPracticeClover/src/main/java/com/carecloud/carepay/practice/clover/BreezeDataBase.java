package com.carecloud.carepay.practice.clover;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import android.content.Context;

import com.carecloud.carepay.practice.clover.models.CloverPaymentQueueRecordDao;
import com.carecloud.carepay.practice.clover.models.CloverQueuePaymentRecord;


/**
 * @author pjohnson on 2/27/19.
 */
@Database(entities = {CloverQueuePaymentRecord.class}, version = 2, exportSchema = false)
public abstract class BreezeDataBase extends RoomDatabase {

    private static volatile BreezeDataBase INSTANCE;

    public static BreezeDataBase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (BreezeDataBase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            BreezeDataBase.class, "clover_breeze_database")
                            .addMigrations(MIGRATION_1_2)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE CloverQueuePaymentRecord"
                    + " ADD COLUMN patientUsername TEXT");
        }
    };

    public abstract CloverPaymentQueueRecordDao getCloverPaymentDao();
}
