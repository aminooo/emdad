package com.upsym.emdad.earthquake.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by Karo on 11/16/2017.
 */

@Dao
public interface PersonDao {
    @Query("SELECT * FROM person_tbl WHERE u_id = 1 LIMIT 1")
    Person getPerson();

    @Insert(onConflict = REPLACE)
    void insert(Person person);

    @Delete
    void delete(Person person);
}
