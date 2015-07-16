package astar.smartfitness.screen.search;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import astar.smartfitness.R;
import astar.smartfitness.model.CaregiverProfile;
import astar.smartfitness.util.Utils;
import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchResultsRecyclerViewAdapter extends RecyclerView.Adapter<SearchResultsRecyclerViewAdapter.ViewHolder> {
    private static final int EMPTY_VIEW = 99;

    private ArrayList<CaregiverProfile> caregiverList;
    private Context context;
    private RecyclerView recyclerView;
    private SearchResultsFragment.SortType sortType;

    public SearchResultsRecyclerViewAdapter(Context context, SearchResultsFragment.SortType sortType) {
        this.context = context;
        this.caregiverList = new ArrayList<>();
        this.sortType = sortType;
    }

    public SearchResultsRecyclerViewAdapter(Context context, ArrayList<CaregiverProfile> caregiverList) {
        this.context = context;
        this.caregiverList = caregiverList;
    }

    public void setSortType(SearchResultsFragment.SortType sortType) {
        this.sortType = sortType;
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

        float sizeInDp = context.getResources().getDimension(R.dimen.avatar_size);
        int avatarSize = Utils.dpToPx(context, sizeInDp);
        Drawable placeholder = context.getResources().getDrawable(R.drawable.avatar_placeholder);
        Picasso.with(context)
                .load(caregiver.getProfileImage())
                .resize(avatarSize, avatarSize)
                .placeholder(placeholder)
                .into(holder.avatar);
        holder.titleTextView.setText(caregiver.getUser().getFirstName() + " " + caregiver.getUser().getLastName());

        String infoString;
        switch (sortType) {
            default:
            case RATING:
                float rating = caregiver.getRating();
                String ratingStr = new BigDecimal(rating).setScale(1, RoundingMode.HALF_EVEN).stripTrailingZeros().toString();
                infoString = String.format("%s &#x2605;", ratingStr);
                break;
            case PRICE:
                int price = caregiver.getWageRangeMin();
                infoString = String.format("$%d+", price);
                break;
            case YEARS_OF_EXP:
                int years = caregiver.getYearOfExp();
                infoString = String.format("%d yrs", years);
                break;
        }

        holder.infoTextView.setText(Html.fromHtml(infoString));
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
        TextView titleTextView;

        @Bind(R.id.info)
        TextView infoTextView;

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

    public CaregiverProfile getCaregiverProfile(int position) {
        return caregiverList.get(position);
    }

}
