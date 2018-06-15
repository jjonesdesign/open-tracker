package jesse.jones.opentracker.entity;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class ActivityItem {

    public String uid;
    public String name;
    public String description;
    public String longitude;
    public String lattitude;

    public ActivityItem() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public String getUid(){
        return this.uid;
    }
    public void setUid(String uid){
        this.uid = uid;
    }

    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }

    public String getDescription(){
        return this.description;
    }
    public void setDescription(String description){
        this.description = description;
    }

    public String getLong(){
        return this.longitude;
    }
    public void setLong(String longitude){
        this.longitude = longitude;
    }

    public String getLat(){
        return this.lattitude;
    }
    public void setLat(String lattitude){
        this.lattitude = lattitude;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("name", name);
        result.put("description", description);
        result.put("longitude", longitude);
        result.put("lattitude", lattitude);

        return result;
    }
}
