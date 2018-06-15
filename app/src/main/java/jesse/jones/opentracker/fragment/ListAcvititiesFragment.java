package jesse.jones.opentracker.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jesse.jones.opentracker.R;
import jesse.jones.opentracker.adapter.ActivitiesAdapter;
import jesse.jones.opentracker.events.NewActivityEntryEvent;
import jesse.jones.opentracker.events.UpdatedActivityEntryEvent;
import jesse.jones.opentracker.utils.DatabaseHelper;
import jesse.jones.opentracker.utils.ItemClickSupport;
import jesse.jones.opentracker.utils.entity.local.ActivityEntry;

public class ListAcvititiesFragment extends BaseFragment implements ItemClickSupport.OnItemClickListener, ItemClickSupport.OnItemLongClickListener{

    @BindView(R.id.closeActivitiesListButton)
    ImageView mCloseActivitiesListButton;

    DatabaseHelper mDatabaseHelper;

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    ActivitiesAdapter mActivitiesAdapter;
    List<ActivityEntry> mActivityEntries;

    ActivityEntry mLongPressedSelectedItem;

    public static String CORDS_LOCATION = "location";
    public static String CORDS_LATITUDE = "latitude";
    public static String CORDS_LONGITUDE = "longitude";
    public static String CORDS_ID = "id";

    public ListAcvititiesFragment() {
    }

    public static ListAcvititiesFragment newInstance(Bundle bundle) {
        ListAcvititiesFragment fragment = new ListAcvititiesFragment();

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_activities_list, container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(ListAcvititiesFragment.this);

        mActivitiesAdapter = new ActivitiesAdapter();

        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(this);
        ItemClickSupport.addTo(mRecyclerView).setOnItemLongClickListener(this);
        mRecyclerView.setAdapter(mActivitiesAdapter);

        registerForContextMenu(view);

        mDatabaseHelper = new DatabaseHelper(getContext());

        mActivityEntries = mDatabaseHelper.getActivityEntries();
        mActivitiesAdapter.setContent(mActivityEntries);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {

        super.onResume();

    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);

    }


    @Override
    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
        ActivityEntry entry = mActivitiesAdapter.getLocation(position);

        AddActivityFragment addActivityFragment = new AddActivityFragment();
        Bundle bundle = new Bundle();

        bundle.putInt(CORDS_ID, entry.getId());
        String locationString = entry.getLatitude() + "," + entry.getLongitude();
        bundle.putString(CORDS_LOCATION, locationString);
        bundle.putString(CORDS_LATITUDE, String.valueOf(entry.getLatitude()));
        bundle.putString(CORDS_LOCATION, String.valueOf(entry.getLongitude()));

        addActivityFragment.setArguments(bundle);
        addFragment(addActivityFragment);

    }

    @Override
    public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
        mLongPressedSelectedItem = mActivitiesAdapter.getLocation(position);
        //showMenu();
        return false;
    }

    @OnClick(R.id.closeActivitiesListButton)
    public void closeActivityButtonClicked(ImageView imageView) {
        getActivity().onBackPressed();
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