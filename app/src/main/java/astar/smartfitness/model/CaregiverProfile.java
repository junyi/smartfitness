package astar.smartfitness.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.ArrayList;

@ParseClassName("CaregiverProfile")
public class CaregiverProfile extends ParseObject {
    public final static String KEY_USER_ID = "userId";
    public final static String KEY_YEAR_OF_EXP = "yearOfExperience";
    public final static String KEY_WAGE_RANGE_MIN = "wageRangeMin";
    public final static String KEY_WAGE_RANGE_MAX = "wageRangeMax";
    public final static String KEY_LANGUAGES = "languages";
    public final static String KEY_SERVICES = "services";

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


}
