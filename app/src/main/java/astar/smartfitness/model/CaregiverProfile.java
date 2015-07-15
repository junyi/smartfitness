package astar.smartfitness.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

@ParseClassName("CaregiverProfile")
public class CaregiverProfile extends ParseObject {
    public final static String KEY_USER_ID = "userId";
    public final static String KEY_YEAR_OF_EXP = "yearOfExperience";
    public final static String KEY_WAGE_RANGE_MIN = "wageRangeMin";
    public final static String KEY_WAGE_RANGE_MAX = "wageRangeMax";
    public final static String KEY_LANGUAGES = "languages";
    public final static String KEY_SERVICES = "services";
    public final static String KEY_PROFILE_IMAGE = "profileImage";
    public final static String KEY_RATING = "rating";

    public void setUserId(User user) {
        put(KEY_USER_ID, user);
    }

    public void setYearOfExp(int yearOfExp) {
        put(KEY_YEAR_OF_EXP, yearOfExp);
    }

    public void setWageRangeMin(int wageRangeMin) {
        put(KEY_WAGE_RANGE_MIN, wageRangeMin);
    }

    public void setWageRangeMax(int wageRangeMax) {
        put(KEY_WAGE_RANGE_MAX, wageRangeMax);
    }

    public void setLanguages(ArrayList<Integer> languages) {
        put(KEY_LANGUAGES, languages);
    }

    public void setServices(ArrayList<Integer> services) {
        put(KEY_SERVICES, services);
    }

    public void setProfileImage(String image) {
        put(KEY_PROFILE_IMAGE, image);
    }

    public User getUser() {
        return (User) getParseObject(KEY_USER_ID);
    }

    public int getYearOfExp() {
        return getInt(KEY_YEAR_OF_EXP);
    }

    public int getWageRangeMin() {
        return getInt(KEY_WAGE_RANGE_MIN);
    }

    public int getWageRangeMax() {
        return getInt(KEY_WAGE_RANGE_MAX);
    }

    public ArrayList<Integer> getLanguages() {
        ArrayList<Integer> result = new ArrayList<>();
        JSONArray jArray = getJSONArray(KEY_LANGUAGES);

        try {
            if (jArray != null) {
                for (int i = 0; i < jArray.length(); i++) {
                    result.add((Integer) jArray.get(i));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public ArrayList<Integer> getServices() {
        ArrayList<Integer> result = new ArrayList<>();
        JSONArray jArray = getJSONArray(KEY_SERVICES);

        try {
            if (jArray != null) {
                for (int i = 0; i < jArray.length(); i++) {
                    result.add((Integer) jArray.get(i));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public String getProfileImage() {
        return getString(KEY_PROFILE_IMAGE);
    }

    public float getRating() {
        return getNumber(KEY_RATING).floatValue();
    }

}
