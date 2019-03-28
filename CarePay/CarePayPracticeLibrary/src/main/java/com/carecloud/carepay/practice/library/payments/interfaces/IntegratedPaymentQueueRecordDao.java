package com.carecloud.carepay.practice.library.payments.interfaces;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.carecloud.carepay.practice.library.payments.models.IntegratedPaymentQueueRecord;

import java.util.List;

/**
 * @author pjohnson on 2/27/19.
 */
@Dao
public interface IntegratedPaymentQueueRecordDao {

    @Insert
    void insert(IntegratedPaymentQueueRecord record);

    @Delete
    void delete(IntegratedPaymentQueueRecord record);

    @Query("SELECT * from IntegratedPaymentQueueRecord")
    List<IntegratedPaymentQueueRecord> getAllRecords();
}
