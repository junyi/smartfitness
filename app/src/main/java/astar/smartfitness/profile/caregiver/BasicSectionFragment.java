package astar.smartfitness.profile.caregiver;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.appyvet.rangebar.RangeBar;

import astar.smartfitness.R;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class BasicSectionFragment extends Fragment {

    @Bind(R.id.select_location_button)
    Button selectLocationButton;

    @Bind(R.id.selected_locations)
    TextView selectedLocationsTextView;

    @Bind(R.id.rangebar)
    RangeBar rangeBar;

    public BasicSectionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_caregiver_profile_basic, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @OnClick(R.id.location_textview)
    public void showTextSize() {
//        Timber.d("Bounds width: " + rangeBar.getLeftPin().mBounds.width());
    }


    @OnClick({R.id.select_location_button, R.id.selected_locations})
    public void onSelectLocation() {
        new MaterialDialog.Builder(getActivity())
                .title("Select location")
                .items(R.array.location_list)
                .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog materialDialog, Integer[] integers, CharSequence[] charSequences) {
                        int length = charSequences.length;

                        if (length == 0) {
                            selectedLocationsTextView.setVisibility(View.GONE);
                            selectLocationButton.setVisibility(View.VISIBLE);
                        } else {
                            StringBuilder sb = new StringBuilder();
                            for (int i = 0; i < length; i++) {
                                sb.append(charSequences[i]);
                                if (length > 1 && i < length - 1)
                                    sb.append(", ");
                            }

                            selectedLocationsTextView.setText(sb.toString());
                            selectedLocationsTextView.setVisibility(View.VISIBLE);
                            selectLocationButton.setVisibility(View.GONE);
                        }

                        return false;
                    }
                })
                .positiveText("Confirm")
                .show();
    }
}
