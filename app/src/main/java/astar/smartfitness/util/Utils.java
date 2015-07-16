package astar.smartfitness.util;

import android.app.Activity;
import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.parse.ParseUser;

import java.util.ArrayList;

import astar.smartfitness.R;
import astar.smartfitness.model.User;

public class Utils {

    public static void hideKeyboard(Activity activity) {
        // Check if no view has focus:
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void hideKeyboard(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyboard(Activity activity) {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    public static boolean isValidNric(String nric) {
        if (nric == null || nric.length() != 9)
            return false;

        nric = nric.toUpperCase();

        char first = nric.charAt(0);
        char last = nric.charAt(8);

        int[] weights = {2, 7, 6, 5, 4, 3, 2};
        int[] icArray = new int[7];
        for (int i = 0; i < 7; i++) {
            icArray[i] = Integer.parseInt(Character.toString(nric.charAt(i + 1))) * weights[i];
        }

        int weight = 0;
        for (int i = 0; i < 7; i++) {
            weight += icArray[i];
        }

        int offset = (first == 'T' || first == 'G') ? 4 : 0;
        int temp = (offset + weight) % 11;

        char[] st = {'J', 'Z', 'I', 'H', 'G', 'F', 'E', 'D', 'C', 'B', 'A'};
        char[] fg = {'X', 'W', 'U', 'T', 'R', 'Q', 'P', 'N', 'M', 'L', 'K'};

        char theAlpha = st[temp];
        if (first == 'S' || first == 'T') {
            theAlpha = st[temp];
        } else if (first == 'F' || first == 'G') {
            theAlpha = fg[temp];
        }

        return (last == theAlpha);
    }

    public static User getCurrentUser() {
        return (User) ParseUser.getCurrentUser();
    }

    public static int dpToPx(Context context, float sizeInDp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, sizeInDp, context.getResources().getDisplayMetrics());
    }

    /**
     * Map a value within a given range to another range.
     *
     * @param value    the value to map
     * @param fromLow  the low end of the range the value is within
     * @param fromHigh the high end of the range the value is within
     * @param toLow    the low end of the range to map to
     * @param toHigh   the high end of the range to map to
     * @return the mapped value
     */
    public static double mapValueFromRangeToRange(
            double value,
            double fromLow,
            double fromHigh,
            double toLow,
            double toHigh) {
        double fromRangeSize = fromHigh - fromLow;
        double toRangeSize = toHigh - toLow;
        double valueScale = (value - fromLow) / fromRangeSize;
        return toLow + (valueScale * toRangeSize);
    }

    /**
     * Clamp a value to be within the provided range.
     *
     * @param value the value to clamp
     * @param low   the low end of the range
     * @param high  the high end of the range
     * @return the clamped value
     */
    public static double clamp(double value, double low, double high) {
        return Math.min(Math.max(value, low), high);
    }

    public static String getFormattedServiceText(Context context, ArrayList<Integer> servicesResult) {
        return getFormattedServiceText(context, servicesResult, "No Preference");
    }

    public static String getFormattedServiceText(Context context, ArrayList<Integer> servicesResult, String otherText) {
        if (servicesResult.size() > 0) {
            if (servicesResult.size() == 1) {
                int selectedIndex = servicesResult.get(0);

                String[] services = context.getResources().getStringArray(R.array.service_list);
                return services[selectedIndex];
            } else {
                return String.format("%d services selected", servicesResult.size());
            }
        } else {
            return otherText;
        }
    }

    public static float getActionBarHeight(Context context) {
        float actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
        }

        return actionBarHeight;
    }
}
