package com.upsym.emdad.earthquake.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.ArrayList;
import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 *
 * Created by Karo on 11/16/2017.
 */

@Dao
public interface EventPointDao {

    @Insert
    void insert(EventPoint events);

    @Delete
    void delete(EventPoint events);

    @Insert(onConflict = REPLACE)
    void insertAll(List<EventPoint> events);

    @Delete
    void deleteAll(List<EventPoint> events);

    @Query("SELECT MIN(u_id) FROM point_event_tbl LIMIT 1")
    long getMinId();

    @Query("SELECT * FROM point_event_tbl")
    LiveData<List<EventPoint>> getAllLiveData();

    @Query("SELECT * FROM point_event_tbl WHERE u_id = :id")
    EventPoint getEvent(Long id);

    @Query("SELECT * FROM point_event_tbl WHERE u_id < 0")
    List<EventPoint> getAllLocal();
}
