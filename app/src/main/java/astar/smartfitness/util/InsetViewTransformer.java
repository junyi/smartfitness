package astar.smartfitness.util;

import android.view.View;

import com.flipboard.bottomsheet.BaseViewTransformer;
import com.flipboard.bottomsheet.BottomSheetLayout;

public class InsetViewTransformer extends BaseViewTransformer {

    public interface ViewTransformedListener {
        void viewTransformed(float translation, float maxTranslation, float peekedTranslation, BottomSheetLayout parent, View view);
    }

    private ViewTransformedListener mListener = null;

    @Override
    public void transformView(float translation, float maxTranslation, float peekedTranslation, BottomSheetLayout parent, View view) {
        float progress = Math.min(translation / peekedTranslation, 1);
        float scale = (1 - progress) + progress * 0.9f;
        view.setScaleX(scale);
        view.setScaleY(scale);

        if (translation == 0 || translation == parent.getHeight()) {
            parent.setBackgroundColor(0);
            ensureLayer(view, View.LAYER_TYPE_NONE);
        } else {
            parent.setBackgroundColor(0);
            ensureLayer(view, View.LAYER_TYPE_HARDWARE);
        }

        float translationToTop = -(view.getHeight() * (1 - scale)) / 2;
        view.setTranslationY(translationToTop + progress * 20 * view.getContext().getResources().getDisplayMetrics().density);

        if (mListener != null)
            mListener.viewTransformed(translation, maxTranslation, peekedTranslation, parent, view);
    }

    private void ensureLayer(View view, int layerType) {
        if (view.getLayerType() != layerType) {
            view.setLayerType(layerType, null);
        }
    }

    public void setViewTransformedListener(ViewTransformedListener listener) {
        this.mListener = listener;
    }
}