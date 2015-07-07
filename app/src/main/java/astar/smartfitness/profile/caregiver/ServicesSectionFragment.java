package astar.smartfitness.profile.caregiver;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Checkable;
import android.widget.ListView;

import java.util.ArrayList;

import astar.smartfitness.R;
import butterknife.Bind;
import butterknife.ButterKnife;

public class ServicesSectionFragment extends SectionFragment {
    public static final String ARG_SERVICE_LIST = "service_list";

    @Bind(R.id.list_view)
    ListView listView;

    private ArrayList<Integer> servicesResult = new ArrayList<>();

    private ArrayAdapter<String> listAdapter;

    public ServicesSectionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_caregiver_profile_services, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final String[] serviceList = getResources().getStringArray(R.array.service_list);
        listAdapter = new ArrayAdapter<>(getActivity(), R.layout.item_caregiver_profile_services, serviceList);
        listView.setAdapter(listAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (((Checkable) view).isChecked()) {
                    servicesResult.add((Integer) position);
                } else {
                    servicesResult.remove((Integer) position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        int l = servicesResult.size();
        for (int i = 0; i < l; i++) {
            listView.setItemChecked(servicesResult.get(i), true);
        }
    }

    @Override
    public boolean validateSection() {
        return servicesResult.size() > 0;
    }

    @Override
    public void saveSection(Bundle data) {
        data.putIntegerArrayList(ARG_SERVICE_LIST, servicesResult);
    }

    @Override
    public void restoreSection(Bundle data) {
        servicesResult = data.getIntegerArrayList(ARG_SERVICE_LIST);
    }
}
