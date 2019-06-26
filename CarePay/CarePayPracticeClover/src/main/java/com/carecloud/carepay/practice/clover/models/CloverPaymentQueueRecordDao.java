package com.carecloud.carepay.practice.clover.models;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

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
