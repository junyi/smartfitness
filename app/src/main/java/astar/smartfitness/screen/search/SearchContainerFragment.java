package astar.smartfitness.screen.search;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.HashMap;

import astar.smartfitness.screen.MainActivity;
import astar.smartfitness.R;
import astar.smartfitness.screen.profile.caregiver.SectionFragment;
import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchContainerFragment extends Fragment {

    public enum PageState {BASIC, SKILLS, SERVICES}

    @Bind(R.id.back_button)
    Button backButton;

    @Bind(R.id.next_button)
    Button nextButton;

    @Bind(R.id.current_page_text_view)
    TextView currentPageTextView;

    private PageState currentState = PageState.BASIC;
    private HashMap<PageState, Bundle> dataMap = new HashMap<>();

    public SearchContainerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_caregiver_edit_profile, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((MainActivity) getActivity()).getSupportActionBar().hide();

    }




    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof MainActivity)) {
            throw new ClassCastException("Activity must be MainActivity");
        }
    }

    private void replaceFragment(SectionFragment f, PageState state) {
        replaceFragment(f, state, true);
    }

    private void replaceFragment(SectionFragment f, PageState state, boolean leftToRight) {
        replaceFragment(f, state, true, leftToRight);
    }

    private void replaceFragment(SectionFragment f, PageState state, boolean addToBackStack, boolean leftToRight) {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        if (leftToRight)
            ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left);
        else
            ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);

        FrameLayout container = (FrameLayout) getView().findViewById(R.id.container);

        if (container.getChildCount() == 0)
            ft.add(R.id.container, f, String.valueOf(state.ordinal()));
        else
            ft.replace(R.id.container, f, String.valueOf(state.ordinal()));
        if (addToBackStack)
            ft.addToBackStack(null);
        ft.commit();
    }

}
