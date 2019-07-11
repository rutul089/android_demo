package app.trirail.com.myapplication.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import app.trirail.com.myapplication.model.location.LocationList;

@Dao
public interface DataItemDao {

    @Query("SELECT COUNT(*) from location_list")
    int locationCount();

    @Insert()
    void insertAllStops(List<LocationList> item);

    @Insert()
    void insertLocation(LocationList... location);

    @Query("SELECT * from location_list")
    List<LocationList> locationList();

}
