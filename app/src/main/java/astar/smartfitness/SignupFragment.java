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
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.mobsandgeeks.saripaar.annotation.Size;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A placeholder fragment containing a simple view.
 */
public class SignupFragment extends Fragment implements Validator.ValidationListener {

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
    }

    @OnClick
    public void signup() {
        Utils.hideKeyboard(getActivity());
        validator.validate();
    }

    @Override
    public void onValidationSucceeded() {
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
