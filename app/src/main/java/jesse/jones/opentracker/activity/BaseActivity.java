package jesse.jones.opentracker.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RelativeLayout;

import jesse.jones.opentracker.R;

public class BaseActivity extends AppCompatActivity {

    ProgressDialog mProgress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProgress = new ProgressDialog(this);

    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        RelativeLayout base = (RelativeLayout) getLayoutInflater().inflate(R.layout.activity_base, null);

        super.setContentView(base);

        //ButterKnife.bind(this);

        getLayoutInflater().inflate(layoutResID, base, true);
    }

    public void showProgress() {
        showProgress("Processing", "Please Wait");
    }

    public void showProgress(String title, String message) {
        if (mProgress != null) {
            try {
                mProgress.setTitle(title);
                mProgress.setMessage(message);
                mProgress.show();
            } catch (Exception er) {
                String errorString = er.toString();
                Log.e(this.getClass().getSimpleName(), er.toString());
            }
        }
    }

    public void hideProgress() {
        mProgress.hide();
    }

    @Override
    protected void onPause() {
        hideProgress();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProgress != null) {
            mProgress.dismiss();
        }
    }

    public void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content, fragment);
        //transaction.addToBackStack("");
        transaction.commit();
    }

    public void addFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content, fragment);
        transaction.addToBackStack(fragment.getClass().getSimpleName());
        transaction.commit();
    }

    public void clearStack() {
        /*
         * Here we are clearing back stack fragment entries
         */
        int backStackEntry = getSupportFragmentManager().getBackStackEntryCount();

        if (backStackEntry > 0) {
            for (int i = 0; i < backStackEntry; i++) {
                getSupportFragmentManager().popBackStackImmediate();
            }
        }
    }
}