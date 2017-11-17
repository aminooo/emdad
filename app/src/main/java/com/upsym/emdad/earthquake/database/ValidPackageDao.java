package com.upsym.emdad.earthquake.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by Karo on 11/16/2017.
 */

@Dao
public interface ValidPackageDao {

    @Insert(onConflict = REPLACE)
    void insertAll(List<ValidPackage> list);

    @Query("SELECT * FROM valid_package")
    List<ValidPackage> getAll();
}
