package com.carecloud.carepay.patient.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

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
