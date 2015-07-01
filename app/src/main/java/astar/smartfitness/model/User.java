package astar.smartfitness.model;

import com.parse.ParseUser;

public class User extends ParseUser {
    public final static String KEY_FIRST_NAME = "firstName";
    public final static String KEY_LAST_NAME = "lastName";
    public final static String KEY_POSTAL_CODE = "postalCode";
    public final static String KEY_PHONE = "phone";

    public void setFirstName(String firstName){
        put(KEY_FIRST_NAME, firstName);
    }

    public void setLastName(String lastName){
        put(KEY_LAST_NAME, lastName);
    }

    public void setPostalCode(String postalCode){
        put(KEY_POSTAL_CODE, postalCode);
    }

    public void setPhone(String phone){
        put(KEY_PHONE, phone);
    }
}
