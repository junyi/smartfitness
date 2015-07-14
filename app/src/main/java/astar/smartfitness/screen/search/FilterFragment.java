package astar.smartfitness.screen.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.appyvet.rangebar.RangeBar;

import java.util.ArrayList;

import astar.smartfitness.R;
import astar.smartfitness.screen.MainActivity;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FilterFragment extends BaseSearchFragment {
    public static final String ARG_SERVICES = "services";
    public static final String ARG_BUDGET = "budget";

    private ArrayList<Integer> servicesResult = new ArrayList<>();
    private int[] budgetResult = new int[]{0, 1000};

    private Bundle result = new Bundle();

    @Bind(R.id.services)
    TextView servicesTextView;

    @Bind(R.id.budget_text_view)
    TextView budgetTextView;

    @Bind(R.id.rangebar)
    RangeBar rangeBar;

    public static FilterFragment newInstance(Bundle data) {
        FilterFragment fragment = new FilterFragment();
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupBudget();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @OnClick(R.id.container)
    public void showCreateProfileFragment(){
        ((MainActivity)getActivity()).showCreateProfileFragment();
    }

    private void setupBudget() {
        rangeBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int startIndex, int endIndex, String start, String end) {
                budgetTextView.setText(String.format(getResources().getString(R.string.wage_range_formatted_text), start, end));

                int budgetStart = Integer.parseInt(start);
                int budgetEnd = Integer.parseInt(end);

                // Save wage range result
                budgetResult = new int[]{budgetStart, budgetEnd};

                // Notify section change
                notifySectionChanged();
            }
        });

        if (budgetResult[0] == 0 && budgetResult[0] == budgetResult[1]) {
            budgetResult[0] = (int) rangeBar.getTickStart();
            budgetResult[1] = (int) rangeBar.getTickEnd();
        } else {
            rangeBar.setRangePinsByValue(budgetResult[0], budgetResult[1]);
        }

        budgetTextView.setText(String.format(getResources().getString(R.string.wage_range_formatted_text), budgetResult[0], budgetResult[1]));
    }


    @OnClick(R.id.services)
    public void showServicesDialog() {
        int selectedIndex = servicesResult.size() == 0 ? 0 : servicesResult.get(0);

        new MaterialDialog.Builder(getActivity())
                .backgroundColorRes(R.color.bg_dark_blue)
                .title("Select services")
                .items(R.array.service_list)
                .itemsCallbackSingleChoice(selectedIndex, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                        servicesTextView.setText(charSequence);
                        return false;
                    }
                })
                .positiveText("OK")
                .negativeText("Cancel")
                .show();
    }

    @OnClick(R.id.search_button)
    public void onSearchButtonClicked() {
        getParentContainer().passSearchResults();
    }

    @Override
    public void saveSection(Bundle data) {
        data.putIntegerArrayList(ARG_SERVICES, servicesResult);
        data.putIntArray(ARG_BUDGET, budgetResult);
    }

    @Override
    public void restoreSection(Bundle data) {


    }

    private void notifySectionChanged() {

    }
}
