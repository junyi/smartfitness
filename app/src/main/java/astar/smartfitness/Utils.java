package astar.smartfitness;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

public class Utils {

    public static void hideKeyboard(Activity activity) {
        // Check if no view has focus:
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void showKeyboard(Activity activity) {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    public static boolean isValidNric(String nric) {
        if (nric.length() != 9)
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
        for (int i = 1; i < 8; i++) {
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
}
