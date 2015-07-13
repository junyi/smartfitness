package astar.smartfitness.screen.launch;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import astar.smartfitness.R;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof LaunchActivity)) {
            throw new ClassCastException("Activity must be LaunchActivity");
        }
    }

    @OnClick({R.id.caregiver_button, R.id.patient_button})
    public void gotoSignup(View view) {
        // role is either <code>patient</code> or <code>caregiver</code>
        String role = (String) view.getTag();

        ((LaunchActivity) getActivity()).gotoSignup(role);
    }
}
