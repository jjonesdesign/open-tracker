package jesse.jones.opentracker;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jesse.jones.opentracker.adapter.ActivitiesAdapter;
import jesse.jones.opentracker.events.NewActivityEntryEvent;
import jesse.jones.opentracker.events.UpdatedActivityEntryEvent;
import jesse.jones.opentracker.utils.DatabaseHelper;
import jesse.jones.opentracker.utils.ItemClickSupport;
import jesse.jones.opentracker.utils.entity.local.ActivityEntry;

/**
 * Created by admin on 2/27/17.
 */

public class ListActivitiesFragment extends DialogFragment implements ItemClickSupport.OnItemClickListener, ItemClickSupport.OnItemLongClickListener, MenuItem.OnMenuItemClickListener {

    @BindView(R.id.closeActivitiesListButton)
    ImageView mCloseActivitiesListButton;

    DatabaseHelper mDatabaseHelper;

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    ActivitiesAdapter mActivitiesAdapter;
    List<ActivityEntry> mActivityEntries;

    ActivityEntry mLongPressedSelectedItem;

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

        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(this);

        mActivitiesAdapter = new ActivitiesAdapter();

        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(this);
        ItemClickSupport.addTo(mRecyclerView).setOnItemLongClickListener(this);
        mRecyclerView.setAdapter(mActivitiesAdapter);

        registerForContextMenu(view);

        mDatabaseHelper = new DatabaseHelper(getContext());

        mActivityEntries = mDatabaseHelper.getActivityEntries();
        mActivitiesAdapter.setContent(mActivityEntries);

        EventBus.getDefault().register(this);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }



    @Override
    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
        ActivityEntry entry = mActivitiesAdapter.getLocation(position);

        AddActivityFragment addActivityFragment = new AddActivityFragment();
        Bundle bundle = new Bundle();

        bundle.putInt("id", entry.getId());
        String locationString = entry.getLatitude() + "," + entry.getLongitude();
        bundle.putString("location", locationString);
        bundle.putString("latitude", String.valueOf(entry.getLatitude()));
        bundle.putString("longitude", String.valueOf(entry.getLongitude()));


        addActivityFragment.setArguments(bundle);
        addActivityFragment.show(getActivity().getSupportFragmentManager(), addActivityFragment.getClass().getSimpleName());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.list_options, menu);


        menu.getItem(0).setOnMenuItemClickListener(this);
        menu.getItem(1).setOnMenuItemClickListener(this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_list_edit:
                ActivityEntry entry = mLongPressedSelectedItem;

                AddActivityFragment addActivityFragment = new AddActivityFragment();
                Bundle bundle = new Bundle();

                bundle.putInt("id", entry.getId());
                String locationString = entry.getLatitude() + "," + entry.getLongitude();
                bundle.putString("location", locationString);
                bundle.putString("latitude", String.valueOf(entry.getLatitude()));
                bundle.putString("longitude", String.valueOf(entry.getLongitude()));


                addActivityFragment.setArguments(bundle);
                addActivityFragment.show(getActivity().getSupportFragmentManager(), addActivityFragment.getClass().getSimpleName());
                break;
            // action with ID action_settings was selected
            case R.id.action_list_delete:
                if (mLongPressedSelectedItem != null) {
                    try {
                        mDatabaseHelper.deleteActivityEntry(mLongPressedSelectedItem);
                        mActivityEntries.remove(mLongPressedSelectedItem);
                        mActivitiesAdapter.setContent(mActivityEntries);
                        Toast.makeText(getContext(), "Item Deleted", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
        mLongPressedSelectedItem = mActivitiesAdapter.getLocation(position);
        //showMenu();
        return false;
    }

    @OnClick(R.id.closeActivitiesListButton)
    public void closeActivityButtonClicked(ImageView imageView) {
        this.dismiss();
    }


    // Get called when an event is added up updated.
    //Refresh adapter data
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(NewActivityEntryEvent event){
        mActivityEntries = mDatabaseHelper.getActivityEntries();
        mActivitiesAdapter.setContent(mActivityEntries);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UpdatedActivityEntryEvent event){
        mActivityEntries = mDatabaseHelper.getActivityEntries();
        mActivitiesAdapter.setContent(mActivityEntries);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
