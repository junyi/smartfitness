package astar.smartfitness.screen.search;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.appyvet.rangebar.RangeBar;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import astar.smartfitness.R;
import astar.smartfitness.model.CaregiverProfile;
import astar.smartfitness.model.User;
import astar.smartfitness.util.MarginDecoration;
import astar.smartfitness.util.RecyclerItemClickListener;
import astar.smartfitness.util.Utils;
import astar.smartfitness.widget.EmptyRecyclerView;
import bolts.Continuation;
import bolts.Task;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class FilterFragment extends BaseSearchFragment {
    public static final String ARG_SERVICES = "services";
    public static final String ARG_BUDGET = "budget";

    private ArrayList<Integer> servicesResult = new ArrayList<>();
    private int[] budgetResult = new int[]{0, 1000};

    private Bundle result = new Bundle();
    private SearchResultsRecyclerViewAdapter adapter;

    @Bind(R.id.services_text)
    TextView servicesTextView;

    @Bind(R.id.budget_text_view)
    TextView budgetTextView;

    @Bind(R.id.rangebar)
    RangeBar rangeBar;

    @Bind(R.id.recycler_view)
    EmptyRecyclerView recyclerView;

    @Bind(R.id.bottomsheet)
    BottomSheetLayout bottomSheet;

    final ProfileSheetVH profileSheetVH = new ProfileSheetVH();

    public static FilterFragment newInstance(Bundle data) {
        FilterFragment fragment = new FilterFragment();
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new SearchResultsRecyclerViewAdapter(getActivity(), SearchResultsFragment.SortType.RATING);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new MarginDecoration(getActivity()));
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                profileSheetVH.showProfileSheet(getActivity(), bottomSheet, adapter.getCaregiverProfile(position));
            }
        }));
        recyclerView.setAdapter(adapter);

        executeSearch();

        setupBudget();
        setupServices();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public BottomSheetLayout getBottomSheet() {
        return bottomSheet;
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

    private void setupServices() {
        String formattedText = Utils.getFormattedServiceText(getActivity(), servicesResult);
        servicesTextView.setText(formattedText);
    }

    @OnClick(R.id.services)
    public void showServicesDialog() {
        int selectedIndex = servicesResult.size() == 0 ? 0 : servicesResult.get(0);
        Integer[] selectedIndices = new Integer[servicesResult.size()];
        servicesResult.toArray(selectedIndices);

        new MaterialDialog.Builder(getActivity())
                .backgroundColorRes(R.color.bg_dark_blue)
                .title("Select services")
                .items(R.array.service_list)
                .itemsCallbackMultiChoice(selectedIndices, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog materialDialog, Integer[] integers, CharSequence[] charSequences) {
                        servicesResult.clear();
                        servicesResult.addAll(new ArrayList<>(Arrays.asList(integers)));
                        setupServices();
                        return false;
                    }
                })
                .positiveText("OK")
                .negativeText("Cancel")
                .neutralText("Reset")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onNeutral(MaterialDialog dialog) {
                        super.onNeutral(dialog);
                        servicesResult.clear();
                        setupServices();
                    }
                })
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
        servicesResult = data.getIntegerArrayList(ARG_SERVICES);
        budgetResult = data.getIntArray(ARG_BUDGET);
    }

    private void executeSearch() {
        ParseQuery<CaregiverProfile> query = ParseQuery.getQuery(CaregiverProfile.class);
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.include(CaregiverProfile.KEY_USER_ID);
        query.setLimit(3);

        ParseQuery<User> innerQuery = ParseQuery.getQuery(User.class);
        innerQuery.whereEqualTo(User.KEY_ROLES, User.ROLE_CAREGIVER);

        query.addDescendingOrder(CaregiverProfile.KEY_RATING);

//        if (genderResult != null) {
//            ParseQuery<User> genderQuery = ParseQuery.getQuery(User.class);
//            genderQuery.whereEqualTo(User.KEY_GENDER, genderResult);
//            query.whereMatchesQuery(CaregiverProfile.KEY_USER_ID, genderQuery);
//        }
//
//        if (budgetResult != null) {
//            query.whereGreaterThanOrEqualTo(CaregiverProfile.KEY_WAGE_RANGE_MIN, budgetResult[0]);
//            query.whereLessThanOrEqualTo(CaregiverProfile.KEY_WAGE_RANGE_MAX, budgetResult[1]);
//        }
//
//        if (yearOfExpResult != -1) {
//            query.whereGreaterThanOrEqualTo(CaregiverProfile.KEY_YEAR_OF_EXP, yearOfExpResult);
//        }
//
//        if (servicesResult != null && servicesResult.size() > 0) {
//            query.whereContainedIn(CaregiverProfile.KEY_SERVICES, servicesResult);
//        }
//
//        if (languagesResult != null && languagesResult.size() > 0) {
//            query.whereContainedIn(CaregiverProfile.KEY_LANGUAGES, languagesResult);
//        }

        query.findInBackground().continueWithTask(new Continuation<List<CaregiverProfile>, Task<Void>>() {
            @Override
            public Task<Void> then(Task<List<CaregiverProfile>> task) throws Exception {
                if (task.isCancelled()) {
                    Timber.d("Task is cancelled");
                    handleError(new Exception("Task is cancelled"));
                } else if (task.isFaulted()) {
                    handleError(task.getError());
                } else {
                    onSearchSuccess(task.getResult());
                }

                return Task.cancelled();
            }
        });

    }

    private void onSearchSuccess(final List<CaregiverProfile> result) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.setCaregiverList(result);
            }
        });
    }

    private void handleError(final Exception e) {
        Timber.e("Error: " + Log.getStackTraceString(e));

        if (e instanceof ParseException) {
            ParseException error = (ParseException) e;
            int errorCode = error.getCode();

            Timber.e(errorCode + ": " + e.getMessage());

        } else {
            e.printStackTrace();
        }
    }
}
