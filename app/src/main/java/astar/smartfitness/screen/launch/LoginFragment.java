package astar.smartfitness.screen.launch;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import astar.smartfitness.R;
import astar.smartfitness.util.Utils;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * A placeholder fragment containing a simple view.
 */
public class LoginFragment extends Fragment implements Validator.ValidationListener {

    @Email
    @Bind(R.id.email_edit_text)
    EditText emailEditText;

    @Bind(R.id.email_text_input_layout)
    TextInputLayout emailTextInputLayout;

    @Password(min = 6, message = "Enter at least 6 characters.")
    @Bind(R.id.password_edit_text)
    EditText passwordEditText;

    @Bind(R.id.password_text_input_layout)
    TextInputLayout passwordTextInputLayout;

    private Validator validator;

    public LoginFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof LaunchActivity)) {
            throw new ClassCastException("Activity must be LaunchActivity");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        validator = new Validator(this);
        validator.setValidationListener(this);

        emailEditText.setTag(emailTextInputLayout);
        passwordEditText.setTag(passwordTextInputLayout);


    }

    @OnClick(R.id.login_button)
    public void login() {
        Utils.hideKeyboard(getActivity());
        emailTextInputLayout.setErrorEnabled(false);
        passwordTextInputLayout.setErrorEnabled(false);
        validator.validate();
    }

    @OnClick(R.id.signup_button)
    public void gotoPreSignup() {
        ((LaunchActivity) getActivity()).gotoPresignup();
    }

    @Override
    public void onValidationSucceeded() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        ParseUser.logInInBackground(email, password, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e == null) {
                    // Check if e-mail is verified
                    if (!parseUser.getBoolean("emailVerified")) {
                        ParseUser.logOut();
                        ParseException error = new ParseException(ParseException.OTHER_CAUSE, "Please verify your e-mail");
                        handleError(error, parseUser);
                    } else {
                        onLoginSucceeded(parseUser);
                    }
                } else {
                    handleError(e, parseUser);
                }
            }
        });
    }

    private void onLoginSucceeded(ParseUser user) {
        ((LaunchActivity) getActivity()).onLoginSuccess();
    }

    private void handleError(final Exception e, final ParseUser user) {
        if (e instanceof ParseException) {
            ParseException error = (ParseException) e;
            int errorCode = error.getCode();

            Timber.e(errorCode + ": " + e.getMessage());

            switch (errorCode) {
                case ParseException.EMAIL_NOT_FOUND:
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            emailTextInputLayout.setErrorEnabled(true);
                            emailTextInputLayout.setError("E-mail not found");
                        }
                    });
                    break;
                case ParseException.OBJECT_NOT_FOUND:
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            emailTextInputLayout.setErrorEnabled(true);
                            emailTextInputLayout.setError("Invalid login credentials");
                        }
                    });
                    break;
                case ParseException.OTHER_CAUSE:
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            emailTextInputLayout.setErrorEnabled(true);
                            emailTextInputLayout.setError(e.getMessage());

                            //TODO: Check if e-mail resend really working
                            Snackbar.make(getView(), "Click here to resend verification e-mail", Snackbar.LENGTH_LONG)
                                    .setAction("Resend", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            user.setEmail(user.getEmail());
                                            user.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if (e == null) {
                                                        Timber.d("E-mail sent to %s", user.getEmail());
                                                    } else {
                                                        Timber.e(e.getCode() + " " + e.getMessage());
                                                    }
                                                }
                                            });
                                        }
                                    })
                                    .show();
                        }
                    });
                    break;
                default:

            }
        } else {
            e.printStackTrace();
        }
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {

        int l = errors.size();

        for (int i = 0; i < l; i++) {
            ValidationError error = errors.get(i);
            TextInputLayout textInputLayout = (TextInputLayout) error.getView().getTag();
            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError(error.getCollatedErrorMessage(getActivity()));
        }
    }
}
