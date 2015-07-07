package astar.smartfitness.profile.caregiver;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public abstract class SectionFragment extends Fragment {

    protected OnSectionChangedListener mOnSectionChangedListenerListener;

    public abstract boolean validateSection();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onAttachFragment(getParentFragment());
    }

    public void onAttachFragment(Fragment fragment) {
        try {
            mOnSectionChangedListenerListener = (OnSectionChangedListener) fragment;
        } catch (ClassCastException e) {
            throw new ClassCastException(fragment.toString() + " must implement OnSectionChangedListener");
        }
    }

    public abstract void saveSection(Bundle data);

    public abstract void restoreSection(Bundle data);

    public void notifySectionChanged() {
        if (mOnSectionChangedListenerListener != null) {
            mOnSectionChangedListenerListener.onSectionChanged();
        }
    }

    public void notifyToValidate() {
        ((EditProfileFragment) getParentFragment()).notifyToValidate();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            restoreSection(savedInstanceState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveSection(outState);
    }
}
