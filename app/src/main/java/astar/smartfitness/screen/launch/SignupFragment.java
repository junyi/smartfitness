package astar.smartfitness.screen.launch;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.mobsandgeeks.saripaar.annotation.Size;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRole;
import com.parse.ParseUser;

import java.util.List;

import astar.smartfitness.R;
import astar.smartfitness.model.User;
import astar.smartfitness.util.Utils;
import astar.smartfitness.validation.Nric;
import bolts.Capture;
import bolts.Continuation;
import bolts.Task;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import timber.log.Timber;

public class SignupFragment extends Fragment implements Validator.ValidationListener {
    public final static String ARG_ROLE = "role";

    private String role;

    @NotEmpty
    @Bind(R.id.first_name_edit_text)
    EditText firstNameEditText;

    @Bind(R.id.first_name_text_input_layout)
    TextInputLayout firstNameTextInputLayout;

    @NotEmpty
    @Bind(R.id.last_name_edit_text)
    EditText lastNameEditText;

    @Bind(R.id.last_name_text_input_layout)
    TextInputLayout lastNameTextInputLayout;

    @Nric
    @Bind(R.id.nric_edit_text)
    EditText nricEditText;

    @Bind(R.id.nric_text_input_layout)
    TextInputLayout nricTextInputLayout;

    @NotEmpty
    @Bind(R.id.phone_edit_text)
    EditText phoneEditText;

    @Bind(R.id.phone_text_input_layout)
    TextInputLayout phoneTextInputLayout;

    @Size(min = 6, max = 6, message = "Postal code must be 6 digits long")
    @Bind(R.id.postal_code_edit_text)
    EditText postalCodeEditText;

    @Bind(R.id.postal_code_text_input_layout)
    TextInputLayout postalCodeInputLayout;

    @Email
    @Bind(R.id.email_edit_text)
    EditText emailEditText;

    @Bind(R.id.email_text_input_layout)
    TextInputLayout emailTextInputLayout;

    @Password(min = 6, message = "Enter at least 6 characters")
    @Bind(R.id.password_edit_text)
    EditText passwordEditText;

    @Bind(R.id.password_text_input_layout)
    TextInputLayout passwordTextInputLayout;

    @ConfirmPassword
    @Bind(R.id.confirm_password_edit_text)
    EditText confirmPasswordEditText;

    @Bind(R.id.confirm_password_text_input_layout)
    TextInputLayout confirmPasswordTextInputLayout;

    @Bind(R.id.progress)
    FrameLayout progress;

    @Bind(R.id.signup_button)
    Button signUpButton;

    private Validator validator;

    public SignupFragment() {
    }

    public static SignupFragment newInstance(String role) {

        Bundle args = new Bundle();
        args.putString(ARG_ROLE, role);

        SignupFragment fragment = new SignupFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            role = args.getString(ARG_ROLE, User.ROLE_PATIENT);
        }

        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        validator = new Validator(this);
        validator.setValidationListener(this);
        Validator.registerAnnotation(Nric.class);

        firstNameEditText.setTag(firstNameTextInputLayout);
        lastNameEditText.setTag(lastNameTextInputLayout);
        nricEditText.setTag(nricTextInputLayout);
        phoneEditText.setTag(phoneTextInputLayout);
        postalCodeEditText.setTag(postalCodeInputLayout);
        emailEditText.setTag(emailTextInputLayout);
        passwordEditText.setTag(passwordTextInputLayout);
        confirmPasswordEditText.setTag(confirmPasswordTextInputLayout);

    }

    @OnLongClick(R.id.signup_button)
    public boolean populateWithMockData() {
        firstNameEditText.setText("Jun Yi");
        lastNameEditText.setText("Hee");
        nricEditText.setText("S2963740G");
        phoneEditText.setText("83876831");
        postalCodeEditText.setText("123456");
        emailEditText.setText("junyi.hjy@gmail.com");
        passwordEditText.setText("heejunyi");
        confirmPasswordEditText.setText("heejunyi");

        return true;
    }

    @OnClick(R.id.signup_button)
    public void clientValidate() {
        signUpButton.setEnabled(false);

        firstNameTextInputLayout.setErrorEnabled(false);
        lastNameTextInputLayout.setErrorEnabled(false);
        nricTextInputLayout.setErrorEnabled(false);
        phoneTextInputLayout.setErrorEnabled(false);
        postalCodeInputLayout.setErrorEnabled(false);
        emailTextInputLayout.setErrorEnabled(false);
        passwordTextInputLayout.setErrorEnabled(false);
        confirmPasswordTextInputLayout.setErrorEnabled(false);
        Utils.hideKeyboard(getActivity());
        validator.validate();
    }

    @Override
    public void onValidationSucceeded() {
        Timber.d("Validation succeeded");
        showProgress();
        serverValidate();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        signUpButton.setEnabled(true);

        int l = errors.size();

        for (int i = 0; i < l; i++) {
            ValidationError error = errors.get(i);
            TextInputLayout textInputLayout = (TextInputLayout) error.getView().getTag();
            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError(error.getCollatedErrorMessage(getActivity()));
        }
    }

    private void serverValidate() {
        String email = emailEditText.getText().toString();
        String nric = nricEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String postalCode = postalCodeEditText.getText().toString();

        final User user = new User();
        user.setUsername(email);
        user.setEmail(email);
        user.setNric(nric);
        user.setPassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhone(phone);
        user.setPostalCode(postalCode);
        user.addRole(role);

        final ParseQuery<ParseRole> roleQuery = ParseRole.getQuery().whereEqualTo("name", role);

        final ParseQuery<ParseUser> uniqueNricQuery = ParseUser.getQuery().whereEqualTo("nric", nric);

        final Capture<ParseRole> capture = new Capture<>();

        roleQuery.getFirstInBackground().continueWithTask(new Continuation<ParseRole, Task<List<ParseUser>>>() {
            @Override
            public Task<List<ParseUser>> then(Task<ParseRole> task) throws Exception {
                if (task.isFaulted()) {
                    handleError(task.getError());
                } else {
                    Timber.d(task.getResult().getName());
                    capture.set(task.getResult());
                    return uniqueNricQuery.findInBackground();
                }

                return Task.cancelled();
            }
        }).continueWithTask(new Continuation<List<ParseUser>, Task<Void>>() {
            @Override
            public Task<Void> then(Task<List<ParseUser>> task) throws Exception {
                if (task.isCancelled()) {
                    Timber.d("Task is cancelled");
                    handleError(new Exception("Task is cancelled"));
                } else if (task.isFaulted()) {
                    handleError(task.getError());
                } else if (task.getResult().size() > 0) {
                    ParseException e = new ParseException(ParseException.DUPLICATE_VALUE, "NRIC already existed");
                    handleError(e);
                } else {
                    return user.signUpInBackground();
                }

                return Task.cancelled();
            }
        }).continueWithTask(new Continuation<Void, Task<Void>>() {
            @Override
            public Task<Void> then(Task<Void> task) throws Exception {
                if (task.isCancelled()) {
                    Timber.d("Task is cancelled");
                    handleError(new Exception("Task is cancelled"));
                } else if (task.isFaulted()) {
                    Timber.e("Error!");
                    handleError(task.getError());
                } else {
                    ParseRole role = capture.get();
                    role.getUsers().add(user);
                    return role.saveInBackground();
                }
                return Task.cancelled();
            }
        }).onSuccess(new Continuation<Void, Void>() {
            @Override
            public Void then(Task<Void> task) throws Exception {
                if (task.isCancelled()) {
                    Timber.d("Task is cancelled");
                    handleError(new Exception("Task is cancelled"));
                } else if (task.isFaulted()) {
                    handleError(task.getError());
                } else {
                    signupSuccess();
                }
                return null;
            }
        }, Task.UI_THREAD_EXECUTOR);

    }

    private void signupSuccess() {
        hideProgress();
        ((LaunchActivity) getActivity()).fromSignupSuccess();
    }

    private void handleError(final Exception e) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideProgress();
                signUpButton.setEnabled(true);
            }
        });

        if (e instanceof ParseException) {
            final ParseException error = (ParseException) e;
            int errorCode = error.getCode();

            Timber.e(e.getMessage());

            switch (errorCode) {
                case ParseException.USERNAME_TAKEN:
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            emailTextInputLayout.setErrorEnabled(true);
                            emailTextInputLayout.setError("This e-mail has already been registered");
                        }
                    });
                    break;
                case ParseException.DUPLICATE_VALUE:
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            nricTextInputLayout.setErrorEnabled(true);
                            nricTextInputLayout.setError(error.getMessage());
                        }
                    });
                    break;
                default:

            }
        } else {
            e.printStackTrace();
        }
    }

    private void showProgress() {
        progress.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        progress.setVisibility(View.GONE);
    }
}
