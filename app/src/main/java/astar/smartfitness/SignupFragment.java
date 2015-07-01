package astar.smartfitness;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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
import com.parse.SaveCallback;

import java.util.List;

import astar.smartfitness.model.User;
import bolts.Capture;
import bolts.Continuation;
import bolts.Task;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class SignupFragment extends Fragment implements Validator.ValidationListener {
    public final static String ARG_ROLE = "role";
    public final static String ROLE_PATIENT = "Patient";
    public final static String ROLE_CAREGIVER = "Caregiver";

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


    @Bind(R.id.phone_edit_text)
    EditText phoneEditText;

    @Bind(R.id.phone_text_input_layout)
    TextInputLayout phoneTextInputLayout;

    @Size(min = 6, max = 6, message = "Postal code must be 6 digits long.")
    @Bind(R.id.postal_code_edit_text)
    EditText postalCodeEditText;

    @Bind(R.id.postal_code_text_input_layout)
    TextInputLayout postalCodeInputLayout;

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

    @ConfirmPassword
    @Bind(R.id.confirm_password_edit_text)
    EditText confirmPasswordEditText;

    @Bind(R.id.confirm_password_text_input_layout)
    TextInputLayout confirmPasswordTextInputLayout;

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
            role = args.getString(ARG_ROLE, ROLE_PATIENT);
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

        firstNameEditText.setTag(firstNameTextInputLayout);
        lastNameEditText.setTag(lastNameTextInputLayout);
        phoneEditText.setTag(phoneTextInputLayout);
        postalCodeEditText.setTag(postalCodeInputLayout);
        emailEditText.setTag(emailTextInputLayout);
        passwordEditText.setTag(passwordTextInputLayout);
        confirmPasswordEditText.setTag(confirmPasswordTextInputLayout);

        if (firstNameEditText.requestFocus())
            Utils.showKeyboard(getActivity());
    }

    @OnLongClick(R.id.signup_button)
    public boolean populateWithMockData() {
        firstNameEditText.setText("Jun Yi");
        lastNameEditText.setText("Hee");
        phoneEditText.setText("83876831");
        postalCodeEditText.setText("123456");
        emailEditText.setText("junyi.hjy@gmail.com");
        passwordEditText.setText("heejunyi");
        confirmPasswordEditText.setText("heejunyi");

        return true;
    }

    @OnClick(R.id.signup_button)
    public void clientValidate() {
        firstNameTextInputLayout.setErrorEnabled(false);
        lastNameTextInputLayout.setErrorEnabled(false);
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
        Log.d("Smartfitness", "Validation succeeded");
        serverValidate();
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

    private void serverValidate() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String postalCode = postalCodeEditText.getText().toString();

        final User user = new User();
        user.setUsername(email);
        user.setEmail(email);
        user.setPassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhone(phone);
        user.setPostalCode(postalCode);

        final ParseQuery<ParseRole> roleQuery = ParseRole.getQuery().whereEqualTo("name", role);

        final Capture<ParseRole> capture = new Capture<>();

        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e!=null)
                    e.printStackTrace();
            }
        });
//
//        roleQuery.getFirstInBackground().continueWith(new Continuation<ParseRole, Object>() {
//            @Override
//            public Object then(Task<ParseRole> task) throws Exception {
//                if (task.isFaulted())
//                    throw task.getError();
//                if (task.isCancelled()) {
//                    // the save was cancelled.
//                } else if (task.isFaulted()) {
//                    // the save failed.
//                    Exception error = task.getError();
//                    Log.e("Signup error", error.getMessage());
//                }
//                Log.d("Smartfitness", "inside role");
//
//                Log.d("Smartfitness", task.getResult().getName());
//
//                capture.set(task.getResult());
//                return user.saveInBackground();
//            }
//        }).continueWith(new Continuation<Object, Object>() {
//            @Override
//            public Object then(Task<Object> task) throws Exception {
//                if (task.isFaulted())
//                    throw task.getError();
//                Log.d("Smartfitness", user.getObjectId());
//                ParseRole role = capture.get();
//                role.getUsers().add(user);
//                return role.saveInBackground();
//            }
//        }).continueWith(new Continuation<Object, Object>() {
//            @Override
//            public Object then(Task<Object> task) throws Exception {
//                if (task.isCancelled()) {
//                    // the save was cancelled.
//                } else if (task.isFaulted()) {
//                    // the save failed.
//                    Exception error = task.getError();
//                    Log.e("Signup error", error.getMessage());
//                } else {
//                    signupSuccess();
//                }
//                return null;
//            }
//        }, Task.UI_THREAD_EXECUTOR);

    }

    private void signupSuccess() {
        ((LaunchActivity) getActivity()).fromSignupSuccess();
    }
}
