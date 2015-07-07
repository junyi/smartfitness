package astar.smartfitness.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
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
import timber.log.Timber;

public class ChipView extends RelativeLayout implements View.OnClickListener {
    @Bind(R.id.text)
    TextView textView;

    @Bind(R.id.circle)
    ImageView doneView;

    private String mText;
    private Drawable doneDrawable;

    private Animation popInAnim;
    private Animation popOutAnim;

    public interface OnSelectedListener {
        void onSelected(ChipView chipView, boolean isSelected);
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

    public ChipView(Context context, AttributeSet attrs, int defStyleAttr, String text) {
        this(context, attrs, defStyleAttr);
        mText = text;
        if (mText != null) {
            drawText(mText);
        }
    }

    private void initView(Context context) {
        View.inflate(context, R.layout.chip_view, this);

        ButterKnife.bind(this);

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

        drawSelected(isSelected());
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        if (attrs == null)
            return;

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

    public void setText(String s) {
        mText = s;
    }

    private void drawText(CharSequence s) {
        textView.setText(mText);
    }

    private void drawSelected(boolean isSelected) {
        if (isSelected) {
            if (doneDrawable == null) {
                doneDrawable = getResources().getDrawable(R.mipmap.ic_done_black_24dp);
                DrawableCompat.setTint(doneDrawable, getResources().getColor(R.color.login_blue));
            }
            doneView.setImageDrawable(doneDrawable);
        } else {
            doneView.setImageDrawable(null);
        }
    }

    @Override
    public void onClick(View v) {
        changeState();
    }

    public void changeState() {
        setSelected(!isSelected());

        if (mOnSelectedListener != null)
            mOnSelectedListener.onSelected(this, isSelected());

        updateState();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Timber.d("onAttachedToWindow");

        drawSelected(isSelected());
    }

    private void updateState() {
        if (isSelected()) {
            doneView.startAnimation(popInAnim);

        } else {
            doneView.startAnimation(popOutAnim);
        }
    }

    public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
        this.mOnSelectedListener = onSelectedListener;
    }

    public String getText() {
        return mText;
    }

}
