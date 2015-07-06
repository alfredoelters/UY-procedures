package uy.edu.ucu.android.tramitesuy.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import uy.edu.ucu.android.tramitesuy.R;
import uy.edu.ucu.android.tramitesuy.activities.MainActivity;

/**
 * Created by alfredo on 05/07/15.
 */
public class LoginFragment extends Fragment {
    private LoginButton mLoginButton;
    private CallbackManager callbackManager;
    private TextView mNoThanksTextView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // Other app specific specialization
        callbackManager = CallbackManager.Factory.create();
        mLoginButton = (LoginButton) view.findViewById(R.id.login_button);
        mLoginButton.setReadPermissions("user_friends");
        // If using in a fragment
        mLoginButton.setFragment(this);
        // Callback registration
        mLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                startHomeActivity();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(getActivity(), getString(R.string.login_error),
                        Toast.LENGTH_LONG).show();
            }
        });
        mNoThanksTextView = (TextView) view.findViewById(R.id.no_thanks);
        mNoThanksTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startHomeActivity();
            }
        });
        if (isLoggedIn()) {
            startHomeActivity();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Method to start the homeActivity and finish the splashScreenActivity
     */
    private void startHomeActivity() {
        // Start the next activity
        Intent mainIntent = new Intent(getActivity(), MainActivity.class);
        startActivity(mainIntent);
        // Close the activity so the user won't able to go back this
        // activity pressing Back button
        getActivity().finish();
    }

    public boolean isLoggedIn() {
        return AccessToken.getCurrentAccessToken() != null;
    }
}
