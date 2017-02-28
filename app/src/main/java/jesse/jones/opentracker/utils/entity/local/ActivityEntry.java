package jesse.jones.opentracker.utils.entity.local;

/**
 * Created by admin on 2/27/17.
 */

public class ActivityEntry {

    int mId;
    String mName;
    String mDescription;
    String mLatitude;
    String mLongitude;
    int mStatus;
    String mCreatedAt;

    //Constructors
    public ActivityEntry(){

    }
    public ActivityEntry(int id, String name, String description, String latitude, String longitude, int status, String createdAt){
        mId = id;
        mName = name;
        mDescription = description;
        mLatitude = latitude;
        mLongitude = longitude;
        mStatus = status;
        mCreatedAt = createdAt;
    }

    //Setters
    public void setId(int id) { this.mId = id; }
    public void setName(String name) { this.mName = name; }
    public void setDescription(String description) { this.mDescription = description; }
    public void setLatitude(String latitude) { this.mLatitude = latitude; }
    public void setLongitude(String longitude) { this.mLongitude = longitude; }
    public void setStatus(int status){
        this.mStatus = status;
    }
    public void setCreatedAt(String createdAt){
        this.mCreatedAt = createdAt;
    }


    //Getters
    public int getId() { return this.mId; }
    public String getName() { return this.mName; }
    public String getDescription() { return this.mDescription; }
    public String getLatitude() { return this.mLatitude; }
    public String getLongitude() { return this.mLongitude; }
    public int getStatus() {return this.mStatus; }
    public String getCreatedAt() { return this.mCreatedAt; }

}
