package app.trirail.com.myapplication.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import app.trirail.com.myapplication.model.location.LocationList;

@Database(entities = {LocationList.class}, version = 1)
public abstract class DBHelper extends RoomDatabase {
    private static DBHelper instance;

    public static DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), DBHelper.class, "LOCATION.DB")
                    .allowMainThreadQueries()
                    .setJournalMode(JournalMode.TRUNCATE)
//                    .addMigrations(MIGRATION_FROM_1_2)
                    .build();
        }
        return instance;
    }

    public static void destroyInstance() {
        instance = null;
    }

    public abstract DataItemDao dataItemDao();

}
