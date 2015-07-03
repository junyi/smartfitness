package astar.smartfitness.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import astar.smartfitness.R;
import astar.smartfitness.animation.AnimUtils;
import butterknife.Bind;
import butterknife.ButterKnife;

public class ChipView extends RelativeLayout implements View.OnClickListener {
    @Bind(R.id.text)
    TextView textView;

    @Bind(R.id.circle)
    ImageView doneView;

    private String mText;
    private boolean mSelected = false;
    private Drawable doneDrawable;

    private Animation popInAnim;
    private Animation popOutAnim;

    public interface OnSelectedListener {
        public void onSelected(ChipView chipView, boolean isSelected);
    }

    private OnSelectedListener mOnSelectedListener = null;

    public ChipView(Context context) {
        super(context);
        initView(context);
    }

    public ChipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        initAttrs(context, attrs);
    }

    public ChipView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        initAttrs(context, attrs);
    }

    private void initView(Context context) {
        View.inflate(context, R.layout.chip_view, this);

        setOnClickListener(this);

        doneDrawable = getResources().getDrawable(R.mipmap.ic_done_black_24dp);
        DrawableCompat.setTint(doneDrawable, getResources().getColor(R.color.login_blue));

        popInAnim = AnimationUtils.loadAnimation(getContext(), R.anim.pop_in);
        popOutAnim = AnimationUtils.loadAnimation(getContext(), R.anim.pop_out);

        popInAnim.setInterpolator(AnimUtils.getEaseOutExpoInterpolator());
        popOutAnim.setInterpolator(AnimUtils.getEaseOutExpoInterpolator());

        popInAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                doneView.setImageDrawable(doneDrawable);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        popOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                doneView.setImageDrawable(null);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ChipView,
                0, 0);

        try {
            mText = a.getString(R.styleable.ChipView_android_text);
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.bind(this);

        if (mText != null)
            setText(mText);
    }

    public void setText(CharSequence s) {
        textView.setText(s);
    }

    @Override
    public void onClick(View v) {
        changeState();

    }

    public void changeState() {
        mSelected = !mSelected;
        setSelected(mSelected);

        if (mOnSelectedListener != null)
            mOnSelectedListener.onSelected(this, mSelected);

        updateState();
    }

    public boolean isSelected() {
        return mSelected;
    }

    private void updateState() {
        if (mSelected) {
            doneView.startAnimation(popInAnim);

        } else {
            doneView.startAnimation(popOutAnim);
        }
    }


}
