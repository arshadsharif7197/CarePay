package com.carecloud.carepay.patient.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.carecloud.carepay.patient.payment.androidpay.models.AndroidPayQueuePaymentRecord;

import java.util.List;

/**
 * @author pjohnson on 2/27/19.
 */
@Dao
public interface AndroidPayQueuePaymentRecordDao {

    @Insert
    void insert(AndroidPayQueuePaymentRecord record);

    @Delete
    void delete(AndroidPayQueuePaymentRecord record);

    @Query("SELECT * from AndroidPayQueuePaymentRecord")
    List<AndroidPayQueuePaymentRecord> getAllRecords();
}
