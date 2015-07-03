package astar.smartfitness.profile.caregiver;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import astar.smartfitness.R;
import butterknife.ButterKnife;

public class BasicSectionFragment extends Fragment {

    public BasicSectionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_caregiver_profile_basic, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

}
