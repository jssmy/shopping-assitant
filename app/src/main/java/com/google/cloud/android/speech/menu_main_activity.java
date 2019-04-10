package com.google.cloud.android.speech;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.cloud.android.speech.adapter.ProductApater;
import com.google.cloud.android.speech.data.SessionHandler;
import com.google.cloud.android.speech.models.Product;
import com.google.cloud.android.speech.models.User;
import com.google.cloud.android.speech.utils.MySingleton;
import com.google.cloud.android.speech.utils.SpeechService;
import com.google.cloud.android.speech.utils.VoiceRecorder;
import com.google.cloud.android.speech.utils.MessageDialogFragment;
import com.google.cloud.android.speech.utils.constants;
import com.google.protobuf.Method;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class menu_main_activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
        , MessageDialogFragment.Listener{

        private static final String FRAGMENT_MESSAGE_DIALOG = "message_dialog";

        private static final String STATE_RESULTS = "results";

        private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1;

        private SpeechService mSpeechService;

        private VoiceRecorder mVoiceRecorder;


        // Resource caches
        private int mColorHearing;
        private int mColorNotHearing;
        private ColorStateList mColorMicHearing;
        private ColorStateList mColorMicNotHearing;
        private FloatingActionButton microphone;
        private  SessionHandler session;
        /// listening
        boolean listening = false;

        // View references
        //private TextView mStatus;
        private  TextView assitant_status;
        //private TextView mText;
        private TextView mUserName;
        private TextView mUserEmail;

        private View mHeaderView;
        private User user;


        private ProgressBar progressBar;

        //product view

        List<Product> products;
        private RecyclerView recyclerViewProduct;
        private JsonArrayRequest requestProduct;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.launcher_menu);
            Toolbar toolbar = (Toolbar) findViewById(R.id.launcher_toolbar);
            setSupportActionBar(toolbar);

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.launcer_drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
            NavigationView navigationView = (NavigationView) findViewById(R.id.launcher_nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            progressBar = new ProgressBar(this);

            /* micropjone */
            microphone =  (FloatingActionButton)findViewById(R.id.fab);
            progressBar = (ProgressBar) findViewById(R.id.progress_bar);


            /*  speecher */
            final Resources resources = getResources();
            final Resources.Theme theme = getTheme();
            mColorHearing = ResourcesCompat.getColor(resources, R.color.status_hearing, theme);
            mColorNotHearing = ResourcesCompat.getColor(resources, R.color.status_not_hearing, theme);
            mColorMicHearing = getResources().getColorStateList(R.color.status_hearing);
            mColorMicNotHearing = getResources().getColorStateList(R.color.status_not_hearing);

            //mStatus = (TextView) findViewById(R.id.status);
            //mText = (TextView) findViewById(R.id.text);
            assitant_status = (TextView)findViewById(R.id.assitant_status);
            mUserName = (TextView)findViewById(R.id.launcher_header_username);
            mUserEmail= (TextView)findViewById(R.id.launcher_header_email);

            user = new User("User","email","token");
            session = new SessionHandler(this);
            if(!session.isLoggedIn()){
                Intent login = new Intent(this,login_activity.class);
                startActivity(login);
                finish();
            }else{
                user = session.getUserDetails();
            }

            mHeaderView = navigationView.getHeaderView(0);
            mUserName = (TextView) mHeaderView.findViewById(R.id.launcher_header_username);
            mUserEmail = (TextView)mHeaderView.findViewById(R.id.launcher_header_email);

            /* product view */
            products = new ArrayList<>();
            recyclerViewProduct = (RecyclerView)findViewById(R.id.product_list);
            requestProducts();
        }


        @Override
        protected void onStart() {
            super.onStart();
            mUserEmail.setText(user.getEmail());
            mUserName.setText(user.getFullName());
        }


    /* button fab */

        public void speaking(View view)
        {



            listening=!listening;

            if(listening)
            {
                assitant_status.setText("Escuchando...");
                // Prepare Cloud Speech API
                bindService(new Intent(this, SpeechService.class), mServiceConnection, BIND_AUTO_CREATE);

                // Start listening to voices
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                        == PackageManager.PERMISSION_GRANTED) {
                    startVoiceRecorder();
                } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.RECORD_AUDIO)) {
                    showPermissionMessageDialog();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                            REQUEST_RECORD_AUDIO_PERMISSION);
                }

            }else {
                stopVoiceRecorder();
                microphone.setImageResource(R.drawable.ic_mic_none_black_24dp);
                microphone.setBackgroundTintList(mColorMicNotHearing);
                assitant_status.setText("Asistente desconectado");
                //mText.setText(null);
                //mStatus.setText(null);
            }
        }


        @Override
        public void onBackPressed() {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.launcer_drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_bar, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_settings) {
                return true;
            }

            return super.onOptionsItemSelected(item);
        }

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            // Handle navigation view item clicks here.
            int id = item.getItemId();

            if (id == R.id.nav_popular) {
                // Handle the camera action
            } else if (id == R.id.nav_cart) {

            } else if (id == R.id.nav_shopping_historial) {

            } else if (id == R.id.nav_setting_accounts) {

            } else if (id == R.id.nav_exit) {
                Intent login = new Intent(this,login_activity.class);
                session.logoutUser();
                startActivity(login);
                finish();
            }

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.launcer_drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }

        @Override
        public void onPointerCaptureChanged(boolean hasCapture) {

        }


        @Override
        public void onMessageDialogDismissed() {

        }


        /* ************** speacker ***************/

        private final SpeechService.Listener mSpeechServiceListener =
                new SpeechService.Listener() {
                    @Override
                    public void onSpeechRecognized(final String text, final boolean isFinal) {
                        if (isFinal) {
                            mVoiceRecorder.dismiss();
                        }
                        if (!TextUtils.isEmpty(text)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (isFinal) {
                                        //mText.setText(null);
                                        //mAdapter.addResult(text);
                                        //mRecyclerView.smoothScrollToPosition(0);
                                    } else {
                                        System.out.println("[ USER SAY]: "+text);
                                        //mText.setText(text);
                                    }
                                }
                            });
                        }
                    }
                };

        private void showPermissionMessageDialog() {
            MessageDialogFragment
                    .newInstance(getString(R.string.permission_message))
                    .show(getSupportFragmentManager(), FRAGMENT_MESSAGE_DIALOG);
        }

        private final ServiceConnection mServiceConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName componentName, IBinder binder) {
                mSpeechService = SpeechService.from(binder);
                mSpeechService.addListener(mSpeechServiceListener);
                //mStatus.setVisibility(View.VISIBLE);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                mSpeechService = null;
            }

        };

        private void showStatus(final boolean hearingVoice) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    //mStatus.setTextColor(hearingVoice ? mColorHearing : mColorNotHearing);
                    //microphone.setBackgroundTintList(hearingVoice?getResources().getColorStateList(R.color.accent):getResources().getColorStateList(R.color.primary_dark));
                    microphone.setImageResource(hearingVoice?R.drawable.ic_mic_black_24dp:R.drawable.ic_mic_none_black_24dp);
                    microphone.setBackgroundTintList(hearingVoice?mColorMicHearing:mColorMicNotHearing);


                    //microphone.setBackgroundColor(hearingVoice ? Color.parseColor("#0972cc") : Color.parseColor("#7B1FA2"));
                }
            });
        }

        private final VoiceRecorder.Callback mVoiceCallback = new VoiceRecorder.Callback() {

            @Override
            public void onVoiceStart() {
                showStatus(true);
                if (mSpeechService != null) {
                    mSpeechService.startRecognizing(mVoiceRecorder.getSampleRate());
                }
            }

            @Override
            public void onVoice(byte[] data, int size) {
                if (mSpeechService != null) {
                    mSpeechService.recognize(data, size);
                }
            }

            @Override
            public void onVoiceEnd() {
                showStatus(false);
                if (mSpeechService != null) {
                    mSpeechService.finishRecognizing();
                }
            }

        };

        private void startVoiceRecorder() {

            if (mVoiceRecorder != null) {
                mVoiceRecorder.stop();
            }
            mVoiceRecorder = new VoiceRecorder(mVoiceCallback);
            mVoiceRecorder.start();
        }

        private void stopVoiceRecorder()
        {
            mVoiceRecorder.stop();
        }


        /*
        * recycler view
        * */
        public void requestProducts(){
            System.out.println("--[ PRODUCT REQUEST ]--");
            requestProduct = new JsonArrayRequest(
                    constants.SERVICE_PRODUCTS,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            System.out.println("[ PRODUCT RESPONSE ]");
                            JSONObject obj;
                            try{
                                for (int i=0; i<response.length(); i++){

                                    obj = response.getJSONObject(i);
                                    Product product = new Product(obj.getInt("id"),obj.getString("name"),Float.parseFloat(obj.getString("price")),obj.getInt("ranking"),obj.getString("description"),obj.getString("url_img"));
                                    products.add(product);
                                }

                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                            progressBar.setVisibility(View.INVISIBLE);

                            productAdapterSettings(products);
                            products=null;
                }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        System.out.println("[ PRODUCT REQUEST ERROR ]");

                        volleyError.printStackTrace();
                    }
                }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return super.getHeaders();
                }
            };

            MySingleton.getInstance(getApplicationContext()).addToRequestQueue(requestProduct);
            System.out.println("[ PRODUCT REQUEST END ]");


        }

        private void productAdapterSettings(List<Product> products){

            ProductApater productApater = new ProductApater(getApplicationContext(),products);
            recyclerViewProduct.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            recyclerViewProduct.setAdapter(productApater);

        }


}
