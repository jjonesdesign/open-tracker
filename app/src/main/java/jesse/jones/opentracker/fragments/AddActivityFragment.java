package jesse.jones.opentracker.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jesse.jones.opentracker.R;
import jesse.jones.opentracker.events.NewActivityEntryEvent;
import jesse.jones.opentracker.events.UpdatedActivityEntryEvent;
import jesse.jones.opentracker.network.GoogleCityNameService;
import jesse.jones.opentracker.network.entity.GetGoogleLocationNameResponse;
import jesse.jones.opentracker.network.entity.LocationNameResult;
import jesse.jones.opentracker.utils.DatabaseHelper;
import jesse.jones.opentracker.utils.entity.local.ActivityEntry;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by admin on 2/26/17.
 */

public class AddActivityFragment extends DialogFragment {


    @BindView(R.id.locationNameDisplay)
    TextView mLocationNameDisplay;

    @BindView(R.id.closeAddActivityButton)
    ImageView mCloseDialogButton;

    @BindView(R.id.createActivityButton)
    ImageView mCreateActivityButton;

    @BindView(R.id.activity_name_input)
    EditText mActivityNameInput;

    @BindView(R.id.activity_description_input)
    EditText mActivityDesriptionInput;

    Retrofit mRetrofit;
    GoogleCityNameService mGoogleCityNameService;
    private final String GOOGLE_MAPS_URL = "https://maps.googleapis.com/";

    String mLocationCords;
    private final String KEY_LOCATION = "location";
    String mLatitude;
    private final String KEY_LATITUDE = "latitude";
    String mLongitude;
    private final String KEY_LONGITUDE= "longitude";

    int mId = 0;
    String mName;
    String mDescription;
    private final String KEY_ID = "id";
    private final String KEY_NAME = "name";
    private final String KEY_DESCRIPTION = "description";

    ActivityEntry mEntry;

    DatabaseHelper mDatabaseHelper;

    public static AddActivityFragment getInstance() {

        return new AddActivityFragment();
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
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_add_activity, container, false);
        ButterKnife.bind(this, view);

        mRetrofit = new Retrofit.Builder()
                .baseUrl(GOOGLE_MAPS_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Bundle arguments = getArguments();

        if(arguments != null){
            mId = getArguments().getInt(KEY_ID);
            mLocationCords = getArguments().getString(KEY_LOCATION);
            mLatitude = getArguments().getString(KEY_LATITUDE);
            mLongitude = getArguments().getString(KEY_LONGITUDE);
        }

        mDatabaseHelper = new DatabaseHelper(getContext());

        if(mId > 0){
            mEntry = mDatabaseHelper.getActivityEntry(mId);
            mName = mEntry.getName();
            mActivityNameInput.setText(mName);
            mDescription = mEntry.getDescription();
            mActivityDesriptionInput.setText(mDescription);

        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        mGoogleCityNameService = mRetrofit.create(GoogleCityNameService.class);

        Call<GetGoogleLocationNameResponse> cityName = mGoogleCityNameService.getCityName(mLocationCords);
        cityName.enqueue(new Callback<GetGoogleLocationNameResponse>() {

            @Override
            public void onResponse(Call<GetGoogleLocationNameResponse> call, Response<GetGoogleLocationNameResponse> response) {
                GetGoogleLocationNameResponse responseBody = response.body();
                List<LocationNameResult> LocationNameResults = responseBody.getResults();
                String formattedAddress = "";

                for(int i=0; i < LocationNameResults.size();i++){
                    LocationNameResult locationNameResult = LocationNameResults.get(i);
                    formattedAddress += locationNameResult.getFormattedAddress();

                }

                mLocationNameDisplay.setText(formattedAddress);

            }

            @Override
            public void onFailure(Call<GetGoogleLocationNameResponse> call, Throwable t) {

            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    @OnClick(R.id.closeAddActivityButton)
    public void closeDialogButtonClicked() {
        this.dismiss();
    }

    @OnClick(R.id.createActivityButton)
    public void addActivityButtonClicked() {

        if(mId > 0){
            mEntry.setName(mActivityNameInput.getText().toString());
            mEntry.setDescription(mActivityDesriptionInput.getText().toString());

            mDatabaseHelper.updateActivtyEntry(mEntry);

            EventBus.getDefault().post(new UpdatedActivityEntryEvent());

            Toast.makeText(getContext(), getString(R.string.toast_activity_updated), Toast.LENGTH_SHORT).show();
        }else {

            ActivityEntry newActivityEntry = new ActivityEntry();
            newActivityEntry.setName(mActivityNameInput.getText().toString());
            newActivityEntry.setDescription(mActivityDesriptionInput.getText().toString());
            newActivityEntry.setLatitude(mLatitude);
            newActivityEntry.setLongitude(mLongitude);
            newActivityEntry.setStatus(1);

            mDatabaseHelper.createActivityEntry(newActivityEntry);

            EventBus.getDefault().post(new NewActivityEntryEvent());

            Toast.makeText(getContext(), getString(R.string.toast_activity_created), Toast.LENGTH_SHORT).show();
        }

        mActivityNameInput.setText("");
        mActivityDesriptionInput.setText("");

        this.dismiss();
    }
}