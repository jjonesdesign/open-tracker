package jesse.jones.opentracker;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jesse.jones.opentracker.adapter.ActivitiesAdapter;
import jesse.jones.opentracker.utils.DatabaseHelper;
import jesse.jones.opentracker.utils.ItemClickSupport;
import jesse.jones.opentracker.utils.entity.local.ActivityEntry;

/**
 * Created by admin on 2/27/17.
 */

public class ListActivitiesFragment extends DialogFragment implements ItemClickSupport.OnItemClickListener,View.OnClickListener {

    @BindView(R.id.closeActivitiesListButton)
    ImageView mCloseActivitiesListButton;

    DatabaseHelper mDatabaseHelper;

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    ActivitiesAdapter mActivitiesAdapter;

    public static ListActivitiesFragment getInstance() {

        return new ListActivitiesFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);

        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.activities_list_fragment, container, false);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.list);
        mLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mActivitiesAdapter = new ActivitiesAdapter();

        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(this);
        mRecyclerView.setAdapter(mActivitiesAdapter);


        mDatabaseHelper = new DatabaseHelper(getContext());

        List<ActivityEntry> activities = mDatabaseHelper.getActivityEntries();
        mActivitiesAdapter.setContent(activities);

        Toast.makeText(getContext(), String.valueOf(activities.size()), Toast.LENGTH_SHORT).show();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        ButterKnife.bind(this, view);


        super.onViewCreated(view, savedInstanceState);
    }


    @OnClick(R.id.closeActivitiesListButton)
    public void addActivityButtonClicked(ImageView imageView) {
        this.dismiss();
    }



    @Override
    public void onItemClicked(RecyclerView recyclerView, int position, View v) {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {

            //case R.id.new_unit_button:

              //  break;
            }
        }

}
