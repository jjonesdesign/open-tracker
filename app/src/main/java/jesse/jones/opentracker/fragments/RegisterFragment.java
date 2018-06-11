package jesse.jones.opentracker.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jesse.jones.opentracker.R;
import jesse.jones.opentracker.entity.User;

public class RegisterFragment extends DialogFragment {

    private static final String TAG = "RegisterFragment";

    @BindView(R.id.input_email)
    EditText mEmailInput;

    @BindView(R.id.input_password)
    EditText mPasswordInput;

    @BindView(R.id.input_password_confirm)
    EditText mPasswordConfirmInput;

    @BindView(R.id.button_cancel)
    AppCompatTextView mCancelButton;

    @BindView(R.id.button_register)
    AppCompatTextView mRegisterButton;

    @BindView(R.id.goto_login)
    TextView mGotoLoginButton;

    @BindView(R.id.privacy_policy)
    TextView mPrivacyPolicyButton;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    public static RegisterFragment getInstance() {

        return new RegisterFragment();
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
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_register, container, false);
        ButterKnife.bind(this, view);
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                    // Write a message to the database
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference usersDatabase = database.getReference("Users");

                    User userEntry = new User();
                    userEntry.setUid(user.getUid());
                    userEntry.setEmail(user.getEmail());

                    usersDatabase.child(user.getUid()).setValue(userEntry).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            Toast.makeText(getContext(), "Registration Successfull!", Toast.LENGTH_SHORT).show();
                            RegisterFragment.this.dismiss();
                        }
                    });


                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

    }

    @OnClick(R.id.button_cancel)
    public void onCancelButtonClicked() {
        RegisterFragment.this.dismiss();
    }

    @OnClick(R.id.button_register)
    public void onRegisterButtonClicked() {

        String email = mEmailInput.getText().toString().replace(" ", "");
        String password = mPasswordInput.getText().toString();
        String passwordConfirm = mPasswordConfirmInput.getText().toString();

        if (email.equals("")) {
            Toast.makeText(getContext(), "Email Empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.equals("")) {
            Toast.makeText(getContext(), "Password empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (passwordConfirm.equals("")) {
            Toast.makeText(getContext(), "Password Confirm empty", Toast.LENGTH_SHORT).show();
            return;
        }


        if (password.equals(passwordConfirm)) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Toast.makeText(getContext(), "Failed To Register: " + task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }

}
