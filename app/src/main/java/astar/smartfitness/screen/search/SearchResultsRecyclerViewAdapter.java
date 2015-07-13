package astar.smartfitness.screen.search;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import astar.smartfitness.R;
import astar.smartfitness.model.CaregiverProfile;
import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchResultsRecyclerViewAdapter extends RecyclerView.Adapter<SearchResultsRecyclerViewAdapter.ViewHolder> {
    private static final int EMPTY_VIEW = 99;

    private ArrayList<CaregiverProfile> caregiverList;
    private Context context;
    private RecyclerView recyclerView;

    public SearchResultsRecyclerViewAdapter(Context context) {
        this.context = context;
        this.caregiverList = new ArrayList<>();
    }

    public SearchResultsRecyclerViewAdapter(Context context, ArrayList<CaregiverProfile> caregiverList) {
        this.context = context;
        this.caregiverList = caregiverList;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public SearchResultsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_search_results, parent, false);
        // Return a new holder instance
        return new SearchResultsRecyclerViewAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CaregiverProfile caregiver = caregiverList.get(position);

        Picasso.with(context)
                .load(caregiver.getProfileImage())
                .into(holder.avatar);
        holder.titleTextView.setText(caregiver.getUser().getFirstName() + " " + caregiver.getUser().getLastName());
    }

    @Override
    public int getItemCount() {
        return caregiverList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        @Bind(R.id.avatar)
        ImageView avatar;

        @Bind(R.id.title)
        public TextView titleTextView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setCaregiverList(List<CaregiverProfile> caregiverList) {
        this.caregiverList.clear();
        this.caregiverList.addAll(caregiverList);
        notifyItemRangeChanged(0, caregiverList.size());
        notifyDataSetChanged();
    }

}
