package astar.smartfitness;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A placeholder fragment containing a simple view.
 */
public class PreSignupFragment extends Fragment {

    public PreSignupFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_presignup, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.caregiver_button)
    public void gotoSignup(View view) {
        //TODO Transition to signup

        // role is either <code>patient</code> or <code>caregiver</code>
        String role = (String) view.getTag();
    }
}
