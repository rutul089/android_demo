package app.trirail.com.myapplication.model.location;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LocationResponseModel {
    @SerializedName("Data")
    @Expose
    private List<LocationList> data = null;

    public List<LocationList> getData() {
        return data;
    }

    public void setData(List<LocationList> data) {
        this.data = data;
    }

}
