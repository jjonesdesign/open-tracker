package jesse.jones.opentracker.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import jesse.jones.opentracker.R;
import jesse.jones.opentracker.holder.ActivitiesHolder;
import jesse.jones.opentracker.utils.entity.local.ActivityEntry;

/**
 * Created by admin on 2/27/17.
 */

public class ActivitiesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    List<ActivityEntry> mContent;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activities_holder_view,parent,false);
        return new ActivitiesHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ActivitiesHolder activitiesHolder = (ActivitiesHolder) holder;
        activitiesHolder.clear();
        activitiesHolder.setName(mContent.get(position).getName());
    }

    @Override
    public int getItemCount() {
        if (mContent == null){
            return 0;
        }
        return mContent.size();
    }

    public void setContent(List<ActivityEntry> listContent){
        mContent = listContent;
        notifyDataSetChanged();
    }

    public ActivityEntry getLocation(int posistion){
        return mContent.get(posistion);
    }
}
