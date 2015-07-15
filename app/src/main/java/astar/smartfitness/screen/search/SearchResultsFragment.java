package astar.smartfitness.screen.search;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appyvet.rangebar.RangeBar;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import astar.smartfitness.R;
import astar.smartfitness.model.CaregiverProfile;
import astar.smartfitness.model.User;
import astar.smartfitness.util.MarginDecoration;
import astar.smartfitness.widget.EmptyRecyclerView;
import astar.smartfitness.widget.MultiOptionView;
import astar.smartfitness.widget.SingleOptionView;
import bolts.Continuation;
import bolts.Task;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class SearchResultsFragment extends BaseSearchFragment {
    public static final String ARG_SEARCH_DATA = "search_data";
    private Bundle searchBundle = null;

    @Bind(R.id.bottomsheet)
    BottomSheetLayout bottomSheet;

    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @Bind(R.id.recycler_view)
    EmptyRecyclerView recyclerView;

    @Bind(R.id.empty_view)
    View emptyView;


    static class BottomSheetViewHolder {
        @Bind(R.id.rangebar)
        RangeBar rangeBar;

        @Bind(R.id.location_multi_view)
        MultiOptionView locationMultiOptionView;

        @Bind(R.id.language_multi_view)
        MultiOptionView languageMultiOptionView;

        @Bind(R.id.sort_view)
        SingleOptionView sortView;

        public void setupSortView() {
            sortView.setCurrentSelectedPosition(0);
        }

        @OnClick(R.id.language_clear_filter_button)
        public void clearFilterForLanguage() {
            languageMultiOptionView.clearSelection();
        }

        @OnClick(R.id.location_clear_filter_button)
        public void clearFilterForLocation() {
            locationMultiOptionView.clearSelection();
        }

    }

    final BottomSheetViewHolder bottomSheetViewHolder = new BottomSheetViewHolder();

    private SearchResultsRecyclerViewAdapter adapter;

    public static SearchResultsFragment newInstance(Bundle data) {
        SearchResultsFragment fragment = new SearchResultsFragment();
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_results, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (searchBundle != null) {
            executeSearch(searchBundle);
        } else if (getArguments() != null) {
            searchBundle = getArguments();
            executeSearch(getArguments());
        }

        adapter = new SearchResultsRecyclerViewAdapter(getActivity());
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new MarginDecoration(getActivity()));
        recyclerView.setEmptyView(emptyView);
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setColorSchemeResources(R.color.login_blue);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (searchBundle != null) {
                    executeSearch(searchBundle);
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

    }

    public void setSearchBundle(Bundle searchBundle) {
        this.searchBundle = searchBundle;
    }

    @Override
    public void saveSection(Bundle data) {
    }

    @Override
    public void restoreSection(Bundle data) {
        if (data != null) {
            searchBundle = data;
        }
    }

    private void executeSearch(Bundle data) {
        ArrayList<Integer> servicesResult = data.getIntegerArrayList(FilterFragment.ARG_SERVICES);
        int[] budgetResult = data.getIntArray(FilterFragment.ARG_BUDGET);

        ParseQuery<CaregiverProfile> query = ParseQuery.getQuery(CaregiverProfile.class);
        query.include(CaregiverProfile.KEY_USER_ID);

        ParseQuery<User> innerQuery = ParseQuery.getQuery(User.class);
        innerQuery.whereEqualTo(User.KEY_ROLES, User.ROLE_CAREGIVER);

        query.addDescendingOrder(CaregiverProfile.KEY_RATING);

//        if (genderResult != null) {
//            ParseQuery<User> genderQuery = ParseQuery.getQuery(User.class);
//            genderQuery.whereEqualTo(User.KEY_GENDER, genderResult);
//            query.whereMatchesQuery(CaregiverProfile.KEY_USER_ID, genderQuery);
//        }
//
        if (budgetResult != null) {
            query.whereGreaterThanOrEqualTo(CaregiverProfile.KEY_WAGE_RANGE_MIN, budgetResult[0]);
            query.whereLessThanOrEqualTo(CaregiverProfile.KEY_WAGE_RANGE_MAX, budgetResult[1]);
        }
//
//        if (yearOfExpResult != -1) {
//            query.whereGreaterThanOrEqualTo(CaregiverProfile.KEY_YEAR_OF_EXP, yearOfExpResult);
//        }
//
        if (servicesResult != null && servicesResult.size() > 0) {
            query.whereContainedIn(CaregiverProfile.KEY_SERVICES, servicesResult);
        }
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
                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);

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

    @OnClick(R.id.filter_button)
    public void onFilterButtonClicked() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.bottom_sheet_search_filter, bottomSheet, false);
        bottomSheet.showWithSheetView(view);

        ButterKnife.bind(bottomSheetViewHolder, view);

        bottomSheetViewHolder.setupSortView();
    }
}
