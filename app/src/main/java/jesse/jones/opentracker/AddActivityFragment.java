package jesse.jones.opentracker;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jesse.jones.opentracker.interfaces.NewActivityAdded;
import jesse.jones.opentracker.network.GoogleCityNameService;
import jesse.jones.opentracker.network.GooglePlacesService;
import jesse.jones.opentracker.network.entity.AddressComponent;
import jesse.jones.opentracker.network.entity.GetGoogleLocationNameResponse;
import jesse.jones.opentracker.network.entity.GetGooglePlacesResponse;
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

    String mLocationCords;
    String mLatitude;
    String mLongitude;

    DatabaseHelper mDatabaseHelper;

    NewActivityAdded mNewActivityAddedInterface;

    public static AddActivityFragment getInstance() {

        return new AddActivityFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Dialog dialog = super.onCreateDialog(savedInstanceState);
        Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        //dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.add_activity_fragment, container, false);


        mRetrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mLocationCords = getArguments().getString("location");
        mLatitude = getArguments().getString("latitude");
        mLongitude = getArguments().getString("longitude");

        mDatabaseHelper = new DatabaseHelper(getContext());

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        ButterKnife.bind(this, view);

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

                //String zip = response.body().getResults().get(0).getAddressComponents().get(7).getLongName();
                //String city = response.body().getResults().get(0).getAddressComponents().get(3).getLongName();
                //String state = response.body().getResults().get(0).getAddressComponents().get(5).getShortName();
                //String country = response.body().getResults().get(0).getAddressComponents().get(6).getShortName();
                //mLocationNameDisplay.setText(city + " " + state + " " + zip + ", " + country);
                mLocationNameDisplay.setText(formattedAddress);

            }

            @Override
            public void onFailure(Call<GetGoogleLocationNameResponse> call, Throwable t) {

            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    @OnClick(R.id.closeAddActivityButton)
    public void closeDialogButtonClicked(ImageView imageView) {
        this.dismiss();
    }

    @OnClick(R.id.createActivityButton)
    public void addActivityButtonClicked(ImageView imageView) {


        ActivityEntry newActivityEntry = new ActivityEntry();
        newActivityEntry.setName(mActivityNameInput.getText().toString());
        newActivityEntry.setDescription(mActivityDesriptionInput.getText().toString());
        newActivityEntry.setLatitude(mLatitude);
        newActivityEntry.setLongitude(mLongitude);
        newActivityEntry.setStatus(1);

        long results = mDatabaseHelper.createActivityEntry(newActivityEntry);

        Toast.makeText(getContext(), "createActivityEntry Result: " + results, Toast.LENGTH_SHORT).show();

        mNewActivityAddedInterface.notifyNewActivityAdded();
        mActivityNameInput.setText("");
        mActivityDesriptionInput.setText("");

        this.dismiss();
    }

    public void setNewActivityAddedListener(NewActivityAdded newActivityAdded){
        mNewActivityAddedInterface = newActivityAdded;
    }
}
