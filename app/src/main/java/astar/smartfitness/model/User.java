package astar.smartfitness.model;

import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class User extends ParseUser {
    public final static String ROLE_PATIENT = "Patient";
    public final static String ROLE_CAREGIVER = "Caregiver";

    public final static String KEY_FIRST_NAME = "firstName";
    public final static String KEY_LAST_NAME = "lastName";
    public final static String KEY_POSTAL_CODE = "postalCode";
    public final static String KEY_PHONE = "phone";
    public final static String KEY_ROLES = "roles";

    public void setFirstName(String firstName) {
        put(KEY_FIRST_NAME, firstName);
    }

    public void setLastName(String lastName) {
        put(KEY_LAST_NAME, lastName);
    }

    public void setPostalCode(String postalCode) {
        put(KEY_POSTAL_CODE, postalCode);
    }

    public void setPhone(String phone) {
        put(KEY_PHONE, phone);
    }

    public void addRole(String role) {
        List<String> roles = new ArrayList<>();
        roles.add(role);
        put(KEY_ROLES, roles);
    }

    public List<String> getRoles() {
        JSONArray array = getJSONArray(KEY_ROLES);
        int l = array.length();

        List<String> roles = new ArrayList<>();
        try {
            for (int i = 0; i < l; i++) {
                roles.add(array.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return roles;
    }
}
