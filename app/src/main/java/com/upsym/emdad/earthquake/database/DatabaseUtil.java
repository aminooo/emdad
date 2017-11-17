package com.upsym.emdad.earthquake.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

/**
 *
 * Created by Karo on 11/16/2017.
 */

@Database(entities = {Person.class, EventPoint.class, ValidPackage.class}, version = 5)
public abstract class DatabaseUtil extends RoomDatabase {

    private static DatabaseUtil INSTANCE;

    public abstract PersonDao getPersonDao();

    public abstract EventPointDao getEventPointDao();

    public abstract ValidPackageDao getValidPackageDao();

    public static DatabaseUtil getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), DatabaseUtil.class, "earthquake_db")
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                            .allowMainThreadQueries()
                            .build();
        }
        return INSTANCE;
    }

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE person_tbl "
                    + " ADD COLUMN token TEXT");
        }
    };

    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE valid_package (" +
                    "u_id INTEGER NOT NULL, " +
                    "title TEXT, " +
                    "cpp TEXT, " +
                    "PRIMARY KEY(u_id))");
        }
    };

    private static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE point_event_tbl "
                    + " ADD COLUMN count INTEGER");
            database.execSQL("ALTER TABLE point_event_tbl "
                    + " ADD COLUMN cpp TEXT");
        }
    };

    private static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE point_event_tbl "
                    + " ADD COLUMN package_id INTEGER NOT NULL DEFAULT 0");
        }
    };
}
