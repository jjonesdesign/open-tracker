
package jesse.jones.opentracker.network.entity;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetGoogleLocationNameResponse {

    @SerializedName("results")
    @Expose
    private List<LocationNameResult> results = null;
    @SerializedName("status")
    @Expose
    private String status;

    public List<LocationNameResult> getResults() {
        return results;
    }

    public void setResults(List<LocationNameResult> results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
