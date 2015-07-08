package astar.smartfitness.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("CaregiverSkills")
public class Skill extends ParseObject implements Parcelable {
    public final static String KEY_USER_ID = "userId";
    public final static String KEY_TITLE = "title";
    public final static String KEY_DESCRIPTION = "description";

    public void setUserId(User user) {
        put(KEY_USER_ID, user);
    }

    public void setTitle(String title) {
        put(KEY_TITLE, title);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public String getTitle() {
        return getString(KEY_TITLE);
    }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public Skill() {

    }

    public Skill(Parcel in) {
        String[] data = new String[2];

        in.readStringArray(data);
        setTitle(data[0]);
        setDescription(data[1]);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        String[] data = new String[]{getTitle(), getDescription()};

        dest.writeStringArray(data);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Skill createFromParcel(Parcel in) {
            return new Skill(in);
        }

        public Skill[] newArray(int size) {
            return new Skill[size];
        }
    };
}
