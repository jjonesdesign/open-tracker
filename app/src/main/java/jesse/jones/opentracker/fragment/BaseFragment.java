package jesse.jones.opentracker.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import jesse.jones.opentracker.R;

public class BaseFragment extends Fragment{

    public void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content, fragment);
        //transaction.addToBackStack("");
        transaction.commit();
    }

    public void addFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content, fragment);
        transaction.addToBackStack(fragment.getClass().getSimpleName());
        transaction.commit();
    }

    public void cleanBackstack(){
        FragmentManager manager = getFragmentManager();
        FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
        manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

}
