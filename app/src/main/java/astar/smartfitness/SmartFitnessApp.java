package astar.smartfitness;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

import astar.smartfitness.model.CaregiverProfile;
import astar.smartfitness.model.Skill;
import astar.smartfitness.model.User;
import timber.log.Timber;

public class SmartFitnessApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
//        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "aOyWlaIJ3RWHQsScSG2maPda9pLd380xhE3SR5KP", "8VLwTAUhEWdUIAbhyaMPgcB0THae8hEnnbilKs7F");

        ParseObject.registerSubclass(User.class);
        ParseObject.registerSubclass(Skill.class);
        ParseObject.registerSubclass(CaregiverProfile.class);

        Timber.plant(new Timber.DebugTree());
    }
}
