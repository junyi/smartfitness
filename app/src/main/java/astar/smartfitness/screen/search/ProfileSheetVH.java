package astar.smartfitness.screen.search;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.math.RoundingMode;

import astar.smartfitness.R;
import astar.smartfitness.model.CaregiverProfile;
import astar.smartfitness.util.Utils;
import butterknife.Bind;
import butterknife.ButterKnife;

public class ProfileSheetVH {
    @Bind(R.id.avatar)
    ImageView avatar;

    @Bind(R.id.name)
    TextView nameTextView;

    @Bind(R.id.rating)
    TextView ratingTextView;

    @Bind(R.id.price)
    TextView priceTextView;

    @Bind(R.id.years_of_exp)
    TextView yearsOfExpTextView;

    public void showProfileSheet(Context context, BottomSheetLayout bottomSheet, CaregiverProfile caregiverProfile) {
        View view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_profile, bottomSheet, false);

//        InsetViewTransformer transformer = new InsetViewTransformer();
//        bottomSheet.setDefaultViewTransformer(transformer);
        bottomSheet.showWithSheetView(view);

        ButterKnife.bind(this, view);

        setupProfile(context, caregiverProfile);
    }

    private void setupProfile(Context context, CaregiverProfile caregiver) {
        float sizeInDp = context.getResources().getDimension(R.dimen.avatar_size);
        int avatarSize = Utils.dpToPx(context, sizeInDp);
        Drawable placeholder = context.getResources().getDrawable(R.drawable.avatar_placeholder);
        Picasso.with(context)
                .load(caregiver.getProfileImage())
                .resize(avatarSize, avatarSize)
                .placeholder(placeholder)
                .into(this.avatar);

        this.nameTextView.setText(caregiver.getUser().getFirstName() + " " + caregiver.getUser().getLastName());

        float rating = caregiver.getRating();
        String ratingStr = new BigDecimal(rating).setScale(1, RoundingMode.HALF_EVEN).stripTrailingZeros().toString();
        ratingStr = String.format("%s stars", ratingStr);
        this.ratingTextView.setText(ratingStr);

        String priceStr = String.format("Charges at $%d/hr onwards", caregiver.getWageRangeMin());
        this.priceTextView.setText(priceStr);

        int yearOfExp = caregiver.getYearOfExp();
        String yearsOfExpStr = context.getResources().getQuantityString(R.plurals.year_of_exp_plural, yearOfExp, yearOfExp);
        this.yearsOfExpTextView.setText(yearsOfExpStr);

    }
}
