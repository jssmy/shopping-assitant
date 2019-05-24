package com.toni.cloud.android.shopper;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
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
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.cloud.android.shopper.R;
import com.toni.cloud.android.shopper.adapter.ProductApater;
import com.toni.cloud.android.shopper.data.SessionHandler;
import com.toni.cloud.android.shopper.entities.Product;
import com.toni.cloud.android.shopper.entities.User;
import com.toni.cloud.android.shopper.utils.MySingleton;
import com.toni.cloud.android.shopper.utils.SpeechService;
import com.toni.cloud.android.shopper.utils.VoiceRecorder;
import com.toni.cloud.android.shopper.utils.MessageDialogFragment;
import com.toni.cloud.android.shopper.utils.constants;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIDataService;
import ai.api.android.GsonFactory;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;

/* text to speechg */



public class menu_main_activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
        , MessageDialogFragment.Listener {

        private static final String FRAGMENT_MESSAGE_DIALOG = "message_dialog";
        private static final String STATE_RESULTS = "results";
        private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1;
        private SpeechService mSpeechService;
        private VoiceRecorder mVoiceRecorder;
        /* structures */
        boolean listening = false;
        private User user;
        List<Product> products;
        private RecyclerView recyclerViewProduct;
        private JsonArrayRequest requestProduct;
        private AIDataService aiDataService;
        public static final String TAG = menu_main_activity.class.getName();
        private Gson gson = GsonFactory.getGson();

        /* resources */
        private ProgressBar progressBar;
        private NavigationView navigationView;
        private TextView mUserName;
        private TextView mUserEmail;
        private View mHeaderView;
        private  TextView assitant_status;
        private ColorStateList mColorMicHearing;
        private ColorStateList mColorMicNotHearing;
        private FloatingActionButton microphone;
        private SessionHandler session;
        private int mColorHearing;
        private int mColorNotHearing;
        private ViewGroup.LayoutParams params;

        private TextToSpeech toSpeech;
        @Override
        protected void onCreate(Bundle savedInstanceState) {


            super.onCreate(savedInstanceState);
            setContentView(R.layout.launcher_menu);
            this.setupLauncher();

            /*  speecher */
            this.setupSpeecher();

            this.checkAuth();

            this.setupResources();

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

    @Override
    protected void onResume() {
        super.onResume();

    }

    private  void setupResources(){
            assitant_status = (TextView)findViewById(R.id.assitant_status);
            mUserName = (TextView)findViewById(R.id.launcher_header_username);
            mUserEmail= (TextView)findViewById(R.id.launcher_header_email);
            progressBar = new ProgressBar(this);

            /* micropjone */
            microphone =  (FloatingActionButton)findViewById(R.id.fab);
            progressBar = (ProgressBar) findViewById(R.id.progress_bar);
            mHeaderView = navigationView.getHeaderView(0);
            mUserName = (TextView) mHeaderView.findViewById(R.id.launcher_header_username);
            mUserEmail = (TextView)mHeaderView.findViewById(R.id.launcher_header_email);
            params = microphone.getLayoutParams();

        }
        private void setupLauncher(){
            Toolbar toolbar = (Toolbar) findViewById(R.id.launcher_toolbar);
            setSupportActionBar(toolbar);
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.launcer_drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
            navigationView = (NavigationView) findViewById(R.id.launcher_nav_view);
            navigationView.setNavigationItemSelectedListener(this);


        }
        private void setupSpeecher(){
            final Resources resources = getResources();
            final Resources.Theme theme = getTheme();
            mColorHearing = ResourcesCompat.getColor(resources, R.color.status_hearing, theme);
            mColorNotHearing = ResourcesCompat.getColor(resources, R.color.status_not_hearing, theme);
            mColorMicHearing = getResources().getColorStateList(R.color.status_hearing);
            mColorMicNotHearing = getResources().getColorStateList(R.color.status_not_hearing);
            final AIConfiguration config = new AIConfiguration("a013eae742554cd9bcd609a6552bab1d",AIConfiguration.SupportedLanguages.Spanish,AIConfiguration.RecognitionEngine.System);
            aiDataService = new AIDataService(getApplicationContext(),config);

            toSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    toSpeech.setLanguage(Locale.ROOT);
                }
            });

        }
        private void checkAuth(){
            user = new User("User","email","token");
            session = new SessionHandler(this);
            if(!session.isLoggedIn()){
                Intent login = new Intent(this,login_activity.class);
                startActivity(login);
                finish();
            }else{
                user = session.getUserDetails();
            }
        }

        /* button list cart */
        public  void showCart(MenuItem item){
            Intent i = new Intent(this, list_cart_activity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(i);
        }


    /* button fab */

        public void speaking(View view)
        {
            listening=!listening;

            if(listening)
            {

                // Prepare Cloud Speech API
                bindService(new Intent(this, SpeechService.class), mServiceConnection, BIND_AUTO_CREATE);

                // Start listening to voices
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                        == PackageManager.PERMISSION_GRANTED) {
                    listen();
                } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.RECORD_AUDIO)) {
                    showPermissionMessageDialog();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                            REQUEST_RECORD_AUDIO_PERMISSION);
                }

            }else {
                stopVoiceRecorder();
                assitant_status.setText("Asistente desconectado");
                //mText.setText(null);
                //mStatus.setText(null);
            }
        }

        private  void  listen()
        {
            startVoiceRecorder();
            assitant_status.setText("Escuchando...");
        }

        private void speak(String text){

            stopVoiceRecorder();
            assitant_status.setText("Hablando...");
            toSpeech.speak(text,TextToSpeech.QUEUE_FLUSH,null,null);
            long time = 99 * text.length();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    listen();
                }
            },time);
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
            if(id==R.id.speech){
                Intent speech = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(speech);
                finish();
                return true;
            }
            //noinspection SimplifiableIfStatement


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
                return  true;
            }else if(id==R.id.speech){
                Intent speech = new Intent(getApplicationContext(),dialog_flow_activity.class);
                startActivity(speech);
                finish();
                return true;
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
        /*
        * speecher listenin
        * */

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
                                        /* enviar a dialog flow */
                                        assitant_status.setText("Espere...");
                                        sendRequest(text);

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
                    speechListerResource(hearingVoice);
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
            if(mVoiceRecorder!=null) mVoiceRecorder.stop();
            listening=false;
            speechListerResource(false);
        }

        /*
        * recycler view
        * */
        public void requestProducts(){
            requestProduct = new JsonArrayRequest(
                    constants.SERVICE_PRODUCTS,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {

                            JSONObject obj;
                            try{
                                for (int i=0; i<response.length(); i++){

                                    obj = response.getJSONObject(i);
                                    Product product = new Product(
                                            obj.getInt("id"),
                                            obj.getString("name"),
                                            Float.parseFloat(obj.getString("price")),
                                            obj.getInt("ranking"),
                                            obj.getString("description"),
                                            obj.getString("url_img"),
                                            "",
                                            obj.getInt("discount"),
                                            obj.getInt("stock"));
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
                        volleyError.printStackTrace();
                    }
                }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return super.getHeaders();
                }
            };

            MySingleton.getInstance(getApplicationContext()).addToRequestQueue(requestProduct);
        }
        private void productAdapterSettings(List<Product> products){
            ProductApater productApater = new ProductApater(getApplicationContext(),products);
            recyclerViewProduct.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            recyclerViewProduct.setAdapter(productApater);

        }

        private void speechListerResource(boolean hearingVoice){

            microphone.setImageResource(hearingVoice?R.drawable.ic_mic_black_24dp:R.drawable.ic_mic_none_black_24dp);
            microphone.setBackgroundTintList(hearingVoice?mColorMicHearing:mColorMicNotHearing);
        }

        /*
        * dialog flow
        * */
        private void sendRequest(String queryString) {
            System.out.println(" VOICE REQUEST :" + queryString);
            final AsyncTask<String, Void, AIResponse> task = new AsyncTask<String, Void, AIResponse>() {

                @Override
                protected AIResponse doInBackground(String... params) {
                    final AIRequest request = new AIRequest();
                    String query = params[0];
                    request.setQuery(query);

                    try {
                        final AIResponse response = aiDataService.request(request);
                        return response;
                    } catch (AIServiceException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(final AIResponse response) {

                    if (response != null) {
                        onResult(response);
                    } else {
                        //onError(aiError);
                    }
                }


            }.execute(queryString);
        }

        private void onResult(final AIResponse response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {


                System.out.println(gson.toJson(response));
                final Result result = response.getResult();
                final String speech = result.getFulfillment().getSpeech();
                speak(speech);
                //toSpeech.speak(speech  ,TextToSpeech.QUEUE_FLUSH,null,null);
                System.out.println("[  HABLAR ]: "+speech);
                //stopVoiceRecorder();
                /*
                * action
                * */

                switch (result.getAction())
                {
                        case  "buscar":
                            case "busqueda_sugerida_genero_color_talla":
                            System.out.println("dentro de la acción buscar");
                            System.out.println("data: "+result.getFulfillment().getData());
                                System.out.println("data: "+result.getParameters());
                                if(!result.getMetadata().isWebhookUsed()) break;

                                try {
                                    JsonElement  data = result.getFulfillment().getData().get("items");
                                    //System.out.println("DATA: "+data);
                                    System.out.println("DATA"+ data.toString());
                                    JSONObject items = new JSONObject(data.toString());
                                    JSONArray values =items.getJSONArray("data");
                                    if(values.length()==0) break;
                                    JSONObject obj;
                                    products = new ArrayList<>();
                                    for(int i=0; i<values.length(); i++)
                                    {
                                        obj = values.getJSONObject(i);
                                        Product product = new Product();
                                        product.setID(obj.getInt("id"));
                                        product.setName(obj.getString("name"));
                                        product.setUrl_img(obj.getString("url_img"));
                                        product.setPrice(Float.parseFloat(obj.getString("price")));
                                        product.setDescription(obj.getString("description"));
                                        products.add(product);
                                    }
                                    productAdapterSettings(products);

                                }catch (JSONException e){
                                    e.printStackTrace();
                                }

                        break;
                }

                /*
                long time = 99 * speech.length();
                assitant_status.setText("Hablando...");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        startVoiceRecorder();
                        assitant_status.setText("Escuchando...");
                    }
                },time);
                */

            }

        });
    }
}
