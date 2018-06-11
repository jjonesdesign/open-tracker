package jesse.jones.opentracker.entity;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {

    public String uid;
    public String email;
    public List<ActivityItem> activities;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public String getUid(){
        return this.uid;
    }
    public void setUid(String uid){
        this.uid = uid;
    }

    public String getEmail(){
        return this.email;
    }
    public void setEmail(String email){
        this.email = email;
    }

    public List<ActivityItem> getActivities(){
        return this.activities;
    }
    public void setActivities(List<ActivityItem> activities){
        this.activities = activities;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("email", email);
        result.put("activities", activities);

        return result;
    }
}
