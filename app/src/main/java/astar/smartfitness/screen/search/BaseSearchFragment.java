package astar.smartfitness.screen.search;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.View;

import com.flipboard.bottomsheet.BottomSheetLayout;

import astar.smartfitness.screen.MainActivity;

public abstract class BaseSearchFragment extends Fragment {
    public abstract void saveSection(Bundle data);

    public abstract void restoreSection(Bundle data);

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            restoreSection(savedInstanceState);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveSection(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onAttachFragment(getParentFragment());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ActionBar toolbar = ((MainActivity) getActivity()).getSupportActionBar();
        if (toolbar != null) {
            toolbar.hide();
        }
    }

    public void onAttachFragment(Fragment fragment) {
        if (!(fragment instanceof SearchContainerFragment)) {
            throw new ClassCastException("Fragment must be SearchContainerFragment");
        }
    }

    public SearchContainerFragment getParentContainer() {
        return (SearchContainerFragment) super.getParentFragment();
    }

    public abstract BottomSheetLayout getBottomSheet();
}
