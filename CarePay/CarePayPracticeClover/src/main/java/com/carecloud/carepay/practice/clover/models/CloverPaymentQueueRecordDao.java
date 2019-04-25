package com.carecloud.carepay.practice.clover.models;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * @author pjohnson on 2/27/19.
 */
@Dao
public interface CloverPaymentQueueRecordDao {

    @Insert
    void insert(CloverQueuePaymentRecord record);

    @Delete
    void delete(CloverQueuePaymentRecord record);

    @Query("SELECT * from CloverQueuePaymentRecord")
    List<CloverQueuePaymentRecord> getAllRecords();
}
