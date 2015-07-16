package astar.smartfitness.screen.search;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.flipboard.bottomsheet.BottomSheetLayout;

import java.util.HashMap;

import astar.smartfitness.R;
import astar.smartfitness.screen.MainActivity;
import astar.smartfitness.util.OnBackPressedListener;
import butterknife.ButterKnife;

public class SearchContainerFragment extends Fragment implements OnBackPressedListener {
    public enum PageType {FILTER, RESULTS}

    private PageType currentType = PageType.FILTER;
    private HashMap<PageType, Bundle> dataMap = new HashMap<>();

    public SearchContainerFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        View view = inflater.inflate(R.layout.fragment_search_container, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity) getActivity()).getSupportActionBar().hide();
        replaceFragment(PageType.FILTER);
    }

    private BaseSearchFragment getFragment(PageType type) {
        BaseSearchFragment f = (BaseSearchFragment) getChildFragmentManager().findFragmentByTag(String.valueOf(type.ordinal()));

        Bundle data = dataMap.get(type);

        if (f != null) {
            return f;
        }


        switch (type) {
            default:
            case FILTER:
                f = FilterFragment.newInstance(data);
                break;
            case RESULTS:
                f = SearchResultsFragment.newInstance(data);
                break;
        }


        return f;
    }

    public void passSearchResults() {
        saveFragment(currentType);
        switch (currentType) {
            case FILTER:
                replaceFragment(PageType.RESULTS);
                break;
            case RESULTS:
                replaceFragment(PageType.FILTER);
                break;
        }
    }

    private void saveFragment(PageType pageType) {
        Bundle data = new Bundle();

        switch (pageType) {
            case FILTER:
                getFragment(PageType.FILTER).saveSection(data);
                getFragment(PageType.RESULTS).restoreSection(data);
                dataMap.put(PageType.FILTER, data);
                dataMap.put(PageType.RESULTS, data);
                break;
            case RESULTS:
                getFragment(PageType.RESULTS).saveSection(data);
                getFragment(PageType.FILTER).restoreSection(data);
                dataMap.put(PageType.FILTER, data);
                dataMap.put(PageType.RESULTS, data);
                break;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof MainActivity)) {
            throw new ClassCastException("Activity must be MainActivity");
        }
    }

    private void replaceFragment(PageType type) {
        replaceFragment(type, true);
    }

    private void replaceFragment(PageType type, boolean leftToRight) {
        Fragment f = getFragment(type);

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();

        if (leftToRight)
            ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left);
        else
            ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);

        FrameLayout container = (FrameLayout) getView().findViewById(R.id.container);
        if (container.getChildCount() == 0)
            ft.add(R.id.container, f, String.valueOf(type.ordinal()));
        else
            ft.replace(R.id.container, f, String.valueOf(type.ordinal()));

        ft.addToBackStack(null);

        currentType = type;

        ft.commit();
    }

    public boolean onBackPressed() {
        BottomSheetLayout bottomSheet = getFragment(currentType).getBottomSheet();
        if (bottomSheet != null && bottomSheet.isSheetShowing()) {
            if (bottomSheet.getState() == BottomSheetLayout.State.EXPANDED) {
                bottomSheet.peekSheet();
            } else {
                bottomSheet.dismissSheet();
            }
        } else if (currentType == PageType.FILTER) {
            return true;
        } else {
            saveFragment(currentType);
            replaceFragment(PageType.FILTER, false);
            return false;
        }

        return true;
    }

}
