package astar.smartfitness.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Skill")
public class Skill extends ParseObject {
    public final static String KEY_TITLE = "title";
    public final static String KEY_DESCRIPTION = "description";

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
}
