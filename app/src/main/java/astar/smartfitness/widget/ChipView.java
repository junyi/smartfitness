package astar.smartfitness.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import astar.smartfitness.R;
import butterknife.Bind;
import butterknife.ButterKnife;

public class ChipView extends RelativeLayout implements View.OnClickListener {
    @Bind(R.id.text)
    TextView textView;

    @Bind(R.id.left)
    ImageView doneView;

    private String mText;
    private boolean mSelected = false;
    private Drawable doneDrawable;

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
        updateState();
    }

    public boolean isSelected() {
        return mSelected;
    }

    private void updateState() {
        if (mSelected) {
            doneView.setImageDrawable(doneDrawable);
        } else {
            doneView.setImageDrawable(null);
        }
        invalidate();
    }


}
