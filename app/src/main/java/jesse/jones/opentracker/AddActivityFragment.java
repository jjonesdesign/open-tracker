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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jesse.jones.opentracker.network.GoogleCityNameService;
import jesse.jones.opentracker.network.GooglePlacesService;
import jesse.jones.opentracker.network.entity.AddressComponent;
import jesse.jones.opentracker.network.entity.GetGoogleLocationNameResponse;
import jesse.jones.opentracker.network.entity.GetGooglePlacesResponse;
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

    Retrofit mRetrofit;
    GoogleCityNameService mGoogleCityNameService;

    String mLocationCords;

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
                String zip = response.body().getResults().get(0).getAddressComponents().get(7).getLongName();
                String city = response.body().getResults().get(0).getAddressComponents().get(3).getLongName();
                String state = response.body().getResults().get(0).getAddressComponents().get(5).getShortName();
                String country = response.body().getResults().get(0).getAddressComponents().get(6).getShortName();

                mLocationNameDisplay.setText(city + " " + state + " " + zip + ", " + country);

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

}
