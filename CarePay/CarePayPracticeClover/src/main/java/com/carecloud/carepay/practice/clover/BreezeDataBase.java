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
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_1)
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

    private static final Migration MIGRATION_2_1 = new Migration(2, 1) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS new_CloverQueuePaymentRecord " +
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "patientID TEXT," +
                    "practiceID TEXT," +
                    "practiceMgmt TEXT," +
                    "queueTransition TEXT," +
                    "paymentModelJsonEnc TEXT," +
                    "paymentModelJson TEXT," +
                    "username TEXT)");
            database.execSQL("INSERT INTO new_CloverQueuePaymentRecord (id, patientID, practiceID, practiceMgmt, queueTransition, paymentModelJsonEnc, paymentModelJson, username) " +
                    "SELECT id, patientID, practiceID, practiceMgmt, queueTransition, paymentModelJsonEnc, paymentModelJson, username FROM CloverQueuePaymentRecord");
            database.execSQL("DROP TABLE CloverQueuePaymentRecord");
            database.execSQL("ALTER TABLE new_CloverQueuePaymentRecord RENAME TO CloverQueuePaymentRecord");
        }
    };

    public abstract CloverPaymentQueueRecordDao getCloverPaymentDao();
}
