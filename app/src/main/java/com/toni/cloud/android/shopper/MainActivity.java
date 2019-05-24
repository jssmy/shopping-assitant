/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.toni.cloud.android.shopper;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.cloud.android.shopper.R;
import com.toni.cloud.android.shopper.utils.MessageDialogFragment;

import com.toni.cloud.android.shopper.utils.SpeechService;
import com.toni.cloud.android.shopper.utils.VoiceRecorder;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIDataService;
import ai.api.android.AIService;
import ai.api.android.GsonFactory;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Metadata;
import ai.api.model.Result;
import ai.api.model.Status;

/*
* text to speech
* */

import org.ispeech.SpeechSynthesis;
import org.ispeech.SpeechSynthesisEvent;
import org.ispeech.error.InvalidApiKeyException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;



/* android stt */
import android.speech.tts.TextToSpeech;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements MessageDialogFragment.Listener {

    private static final String FRAGMENT_MESSAGE_DIALOG = "message_dialog";

    private static final String STATE_RESULTS = "results";

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1;

    private Gson gson = GsonFactory.getGson();

    private SpeechService mSpeechService;
    public static final String TAG = MainActivity.class.getName();
    private VoiceRecorder mVoiceRecorder;
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

    // Resource caches
    private int mColorHearing;
    private int mColorNotHearing;

    // View references
    private TextView mStatus;
    private TextView mText;
    private ResultAdapter mAdapter;
    private RecyclerView mRecyclerView;


    //AI
    private  AIDataService dataService;
    private AIService aiService;

    /* tts*/
    SpeechSynthesis synthesis;


    /* tts android */
    TextToSpeech speecher;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            mSpeechService = SpeechService.from(binder);
            mSpeechService.addListener(mSpeechServiceListener);
            mStatus.setVisibility(View.VISIBLE);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mSpeechService = null;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Resources resources = getResources();
        final Resources.Theme theme = getTheme();
        mColorHearing = ResourcesCompat.getColor(resources, R.color.status_hearing, theme);
        mColorNotHearing = ResourcesCompat.getColor(resources, R.color.status_not_hearing, theme);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        mStatus = (TextView) findViewById(R.id.status);
        mText = (TextView) findViewById(R.id.text);
        /* resultss view */
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_voice);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final ArrayList<String> results = savedInstanceState == null ? null :
                savedInstanceState.getStringArrayList(STATE_RESULTS);
        mAdapter = new ResultAdapter(results);
        mRecyclerView.setAdapter(mAdapter);
        iniService();
        /* tts */
        //prepareTTSEngine();
        //synthesis.setStreamType(AudioManager.STREAM_MUSIC);

        /*tts  android*/
        speecher = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    speecher.setLanguage(Locale.ROOT);
                    speecher.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    stopVoiceRecorder();
                                }
                            });
                        }

                        @Override
                        public void onDone(String utteranceId) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    startVoiceRecorder();
                                }
                            });
                        }

                        @Override
                        public void onError(String utteranceId) {

                        }
                    });

                }
            }

            @Override
            protected void finalize() throws Throwable {
                super.finalize();

            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();

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
    }

    @Override
    protected void onStop() {
        // Stop listening to voice
        stopVoiceRecorder();
        // Stop Cloud Speech API
        mSpeechService.removeListener(mSpeechServiceListener);
        unbindService(mServiceConnection);
        mSpeechService = null;
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mAdapter != null) {
            outState.putStringArrayList(STATE_RESULTS, mAdapter.getResults());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (permissions.length == 1 && grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startVoiceRecorder();
            } else {
                showPermissionMessageDialog();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_file:
                mSpeechService.recognizeInputStream(getResources().openRawResource(R.raw.audio));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startVoiceRecorder() {
        if (mVoiceRecorder != null) {
            mVoiceRecorder.stop();
        }
        mVoiceRecorder = new VoiceRecorder(mVoiceCallback);
        mVoiceRecorder.start();
    }

    private void stopVoiceRecorder() {
        if (mVoiceRecorder != null) {
            mVoiceRecorder.stop();
            mVoiceRecorder = null;
        }
    }

    private void showPermissionMessageDialog() {
        MessageDialogFragment
                .newInstance(getString(R.string.permission_message))
                .show(getSupportFragmentManager(), FRAGMENT_MESSAGE_DIALOG);
    }

    private void showStatus(final boolean hearingVoice) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mStatus.setTextColor(hearingVoice ? mColorHearing : mColorNotHearing);

            }
        });
    }

    @Override
    public void onMessageDialogDismissed() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                REQUEST_RECORD_AUDIO_PERMISSION);
    }

    private final SpeechService.Listener mSpeechServiceListener =
            new SpeechService.Listener() {
                @Override
                public void onSpeechRecognized(final String text, final boolean isFinal) {
                    if (isFinal) {
                        mVoiceRecorder.dismiss();
                    }
                    if (mText != null && !TextUtils.isEmpty(text)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isFinal) {
                                    mText.setText(null);
                                    sendRequest(text);

                                    //mAdapter.addResult(text);
                                    //mRecyclerView.smoothScrollToPosition(0);
                                } else {
                                    mText.setText(text);
                                    /*
                                    try {
                                        //synthesis.speak("hola");
                                    }catch (BusyException e){
                                        e.printStackTrace();
                                    }catch (NoNetworkException n){
                                    }catch (NoNetworkException n){
                                        n.printStackTrace();
                                    }*/
                                }
                            }
                        });
                    }
                }
            };

    private static class ViewHolder extends RecyclerView.ViewHolder {

        TextView text;

        ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_result, parent, false));
            text = (TextView) itemView.findViewById(R.id.text);
        }

    }

    private static class ResultAdapter extends RecyclerView.Adapter<ViewHolder> {

        private final ArrayList<String> mResults = new ArrayList<>();

        ResultAdapter(ArrayList<String> results) {
            if (results != null) {
                mResults.addAll(results);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.text.setText(mResults.get(position));
        }

        @Override
        public int getItemCount() {
            return mResults.size();
        }

        void addResult(String result) {
            mResults.add(0, result);
            notifyItemInserted(0);
        }

        public ArrayList<String> getResults() {
            return mResults;
        }



    }

    private void iniService(){
        final AIConfiguration  config = new AIConfiguration("c6f424e220314859b7d7024c9c7daa6f",AIConfiguration.SupportedLanguages.Spanish,AIConfiguration.RecognitionEngine.System);
        dataService = new AIDataService(getApplicationContext(),config);



    }


    private void sendRequest(String queryString) {



        final AsyncTask<String, Void, AIResponse> task = new AsyncTask<String, Void, AIResponse>() {

            private AIError aiError;

            @Override
            protected AIResponse doInBackground(String... params) {
                final AIRequest request = new AIRequest();
                String query = params[0];

                request.setQuery(query);
                try {
                    final AIResponse response = dataService.request(request);
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

        //task.execute(queryString);
    }

    private void onResult(final AIResponse response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "onResult");
                //resultTextView.setText(gson.toJson(response));
                System.out.println(gson.toJson(response));
                Log.i(TAG, "Received success response");
                // this is example how to get different parts of result object
                final Status status = response.getStatus();
                Log.i(TAG, "Status code: " + status.getCode());
                Log.i(TAG, "Status type: " + status.getErrorType());

                final Result result = response.getResult();
                Log.i(TAG, "Resolved query: " + result.getResolvedQuery());

                Log.i(TAG, "Action: " + result.getAction());

                final String speech = result.getFulfillment().getSpeech();
                speak(speech);
                mAdapter.addResult(speech);
                mRecyclerView.smoothScrollToPosition(0);
                /*
                try {
                    synthesis.speak(speech);
                }catch (BusyException e){
                    e.printStackTrace();
                }catch (NoNetworkException n){
                    n.printStackTrace();
                }*/

                Log.i(TAG, "Speech: " + speech);
                //TTS.speak(speech);

                final Metadata metadata = result.getMetadata();
                if (metadata != null) {
                    Log.i(TAG, "Intent id: " + metadata.getIntentId());
                    Log.i(TAG, "Intent name: " + metadata.getIntentName());
                }

                final HashMap<String, JsonElement> params = result.getParameters();
                if (params != null && !params.isEmpty()) {
                    Log.i(TAG, "Parameters: ");
                    for (final Map.Entry<String, JsonElement> entry : params.entrySet()) {
                        Log.i(TAG, String.format("%s: %s", entry.getKey(), entry.getValue().toString()));
                    }
                }


            }

        });
    }

    /*
    * tts
    * */

    private void prepareTTSEngine() {
        try {
                synthesis = SpeechSynthesis.getInstance(this);
            synthesis.setSpeechSynthesisEvent(new SpeechSynthesisEvent() {

                public void onPlaySuccessful() {
                    Log.i(TAG, "onPlaySuccessful");
                }

                public void onPlayStopped() {
                    Log.i(TAG, "onPlayStopped");
                }

                public void onPlayFailed(Exception e) {
                    Log.e(TAG, "onPlayFailed");


                    AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                    builder.setMessage("Error[TTSActivity]: " + e.toString())
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }

                public void onPlayStart() {
                    Log.i(TAG, "onPlayStart");
                }

                @Override
                public void onPlayCanceled() {
                    Log.i(TAG, "onPlayCanceled");
                }
            });


        } catch (InvalidApiKeyException e) {
            Log.e(TAG, "Invalid API key\n" + e.getStackTrace());
            Toast.makeText(getApplicationContext(), "ERROR: Invalid API key", Toast.LENGTH_LONG).show();
        }
    }
    protected void onPause() {
        this.synthesis.stop();
        super.onPause();
    }


    private void speak(String text){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            speecher.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }else{
            speecher.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }



}
