package com.google.cloud.android.speech.data;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.cloud.android.speech.entities.User;
import com.google.cloud.android.speech.utils.RequestService;

/**
 * Created by Abhi on 20 Jan 2018 020.
 */

public class SessionHandler {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_USER_ID="userid";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_EMAIL="email";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_EMPTY = "";
    private Context mContext;
    private SharedPreferences.Editor mEditor;
    private SharedPreferences mPreferences;
    private RequestService request;
    public SessionHandler(Context mContext){
        this.mContext = mContext;
        mPreferences = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.mEditor = mPreferences.edit();
        request = new RequestService(mContext);
    }

    /**
     * Logs in the user by saving user details and setting session
     *
     * @param user
     *
     */
    public void loginUser(User user) {
        /*
        System.out.println("[--LOGIN START--]");
        JSONObject response = new JSONObject();

        try
        {
            System.out.println("[-- PUT VALUES TO JSON OBJ --]");

            JSONObject data = new JSONObject();
            data.put("email",email);
            data.put("password",password);
            System.out.println("[-- START POST REQUEST --]");
            HashMap headers = new HashMap();
            headers.put("Content-Type","application/json");
            response=request.POST(constants.SERVICE_AUTHENTICATION,data,headers);
            if(!response.has("message")){
                System.out.println("logqueado");
                System.out.println("[ PRUEBA/-- ]: "+response);
            }else {
                System.out.println(" mensaje de error: "+  response.getString("message"));
            }

            if(response.getBoolean("log"))
            {
                mEditor.putString(KEY_TOKEN,response.getString("token"));
                mEditor.putString(KEY_EMAIL,response.getString("email"));
                mEditor.putString(KEY_FULL_NAME,response.getString("name"));
            }

        }catch (JSONException e){
            e.printStackTrace();
        }
*/
        //return  response;
        mEditor.putString(KEY_TOKEN,user.getToken());
        mEditor.putString(KEY_EMAIL,user.getEmail());
        mEditor.putString(KEY_FULL_NAME,user.getFullName());
        mEditor.putInt(KEY_USER_ID,user.getId());
        mEditor.commit();
    }

    /**
     * Checks whether user is logged in
     *
     * @return
     */
    public boolean isLoggedIn() {
        String auth = mPreferences.getString(KEY_TOKEN, KEY_EMPTY);
        return !auth.isEmpty();
    }

    /**
     * Fetches and returns user details
     *
     * @return user details
     */
    public User getUserDetails() {
        //Check if user is logged in first
        if (!isLoggedIn())
        {
            return null;
        }
        User user = new User();
        user.setFullName(mPreferences.getString(KEY_FULL_NAME, KEY_EMPTY));
        user.setEmail(mPreferences.getString(KEY_EMAIL,KEY_EMPTY));
        user.setToken(mPreferences.getString(KEY_TOKEN,KEY_EMPTY));
        user.setId(mPreferences.getInt(KEY_USER_ID,0));
        return user;
    }

    /**
     * Logs out user by clearing the session
     */

    public void logoutUser(){
        mEditor.clear();
        mEditor.commit();
    }

}
