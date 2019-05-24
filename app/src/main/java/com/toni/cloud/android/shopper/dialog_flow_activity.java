package com.toni.cloud.android.shopper;

import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.cloud.android.shopper.R;

import java.util.Locale;

import ai.api.AIListener;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;

public class dialog_flow_activity extends AppCompatActivity implements  AIListener{

    /* define  */
    private AIService aiService;
    private TextToSpeech toSpeech;
    private FloatingActionButton listening_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_flow_activity);
        this.initService();
        listening_btn = (FloatingActionButton) findViewById(R.id.start_listening);
        listening_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aiService.startListening();
                System.out.println("--[START LISTENING]--");
            }
        });


    }

    private void initService(){
        final AIConfiguration  config = new AIConfiguration("a013eae742554cd9bcd609a6552bab1d",AIConfiguration.SupportedLanguages.Spanish,AIConfiguration.RecognitionEngine.System);
        aiService = AIService.getService(getApplicationContext(),config);
        aiService.setListener(this);
        toSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                toSpeech.setLanguage(Locale.ROOT);
                toSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {
                        Toast.makeText(getApplicationContext(),"empieza",Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        Toast.makeText(getApplicationContext(),"termina",Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onError(String utteranceId) {

                    }
                });
            }
        });
    }

    /* dialog flow listener  */

    @Override
    public void onResult(AIResponse aiResponse) {
        Result result = aiResponse.getResult();
        String speechText = result.getFulfillment().getSpeech();
        Toast.makeText(getApplicationContext(),speechText,Toast.LENGTH_LONG).show();
        toSpeech.speak(speechText,TextToSpeech.QUEUE_FLUSH,null,null);
        long time = 99 * speechText.length();


        System.out.println("[ START SPEAKING]: " + result.getFulfillment().getSpeech());
        System.out.println("[ TEXT LENGTH]: "+ speechText.length());
        System.out.println("[ TIME]: "+ String.valueOf(time));

        synchronized (aiService){
            try {
                aiService.wait(time);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        aiService.startListening();
        /*
         new Handler().postDelayed(new Runnable() {
             @Override
             public void run() {
                 aiService.startListening();
             }
         },time);*/
    }

    @Override
    public void onError(AIError aiError) {

    }

    @Override
    public void onAudioLevel(float v) {

    }

    @Override
    public void onListeningStarted() {

    }

    @Override
    public void onListeningCanceled() {

    }

    @Override
    public void onListeningFinished() {

    }

    /**/
}
