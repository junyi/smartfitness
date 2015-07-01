package astar.smartfitness;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
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

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
        validator.validate(true);
    }

    @OnClick(R.id.signup_button)
    public void gotoPreSignup() {
        //TODO Transition to pre-signup view
    }

    @Override
    public void onValidationSucceeded() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        ParseUser.logInInBackground(email, password, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                //TODO Transition to view after login
            }
        });
        Snackbar.make(getView(), "Yay! No error!", Snackbar.LENGTH_SHORT).show();
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
