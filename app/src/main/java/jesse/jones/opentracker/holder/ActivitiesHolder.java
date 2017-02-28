package jesse.jones.opentracker.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import jesse.jones.opentracker.R;

/**
 * Created by admin on 2/28/17.
 */

public class ActivitiesHolder extends RecyclerView.ViewHolder {
    TextView mName;

    public ActivitiesHolder(View itemView) {
        super(itemView);
        mName = (TextView) itemView.findViewById(R.id.activityName);
    }

    public void setName(String name) {
        mName.setText(name);
    }


    public void clear() {
        mName.setText("");
    }
}
