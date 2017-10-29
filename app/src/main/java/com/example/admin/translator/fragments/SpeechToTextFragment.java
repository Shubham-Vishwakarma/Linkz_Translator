package com.example.admin.translator.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.admin.translator.IBMUtilies.IBMInitialization;
import com.example.admin.translator.IBMUtilies.MicrophoneRecognizeDelegate;
import com.example.admin.translator.R;
import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneInputStream;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class SpeechToTextFragment extends Fragment {

    private static final String TAG = "SpeechToTextFragment";
    private static final int DEFAULT_TIMEOUT = 5000;
    private LinearLayout speakButton;
    private TextView resultTextView,timerTextView;

    private SpeechToText speechService;
    private MicrophoneInputStream capture;
    private boolean listening = false;

    public SpeechToTextFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_speech_to_text, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.speech_to_text);

        speechService = IBMInitialization.initSpeechToTextService(getContext());
        speakButton = view.findViewById(R.id.speakButton);
        resultTextView = view.findViewById(R.id.resultTextView);
        timerTextView = view.findViewById(R.id.timerTextView);

        speakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something");

                startActivityForResult(i,1010);

                Log.e(TAG,"Listening = " + listening);
                if(!listening){
                    timer();
                    capture = new MicrophoneInputStream(true);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                speechService.recognizeUsingWebSocket(
                                                capture,IBMInitialization.getRecognizeOptions(),
                                                new MicrophoneRecognizeDelegate(getActivity(),resultTextView));
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    listening = true;
                }
                else {
                    try {
                        capture.close();
                        listening = false;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void timer(){
        new CountDownTimer(DEFAULT_TIMEOUT,100){
            @Override
            public void onTick(long l) {
                Log.e(TAG,"l = " + l);
                String leftTime = "00:" + "0" + ((l/1000)%60) + ":" + ((l/100)%60);
                timerTextView.setText(leftTime);
            }

            @Override
            public void onFinish() {
                timerTextView.setText(R.string._00_00_00);
                cancel();
            }
        }.start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        // retrieves data from the VoiceRecognizer
        if (requestCode == 1010 && resultCode == RESULT_OK) {

            Log.e(TAG,"RequestCode = " + requestCode);
            Log.e(TAG,"ResultCode = " + resultCode);
            Log.e(TAG,"Data = " + data.getExtras());
            Log.e(TAG,"RecongnizerIntent = " + data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS));
            Log.e(TAG,"Voice = " + data.getStringArrayListExtra(RecognizerIntent.EXTRA_MAX_RESULTS));
            Log.e(TAG,"Confidence = " + data.getStringExtra(RecognizerIntent.EXTRA_CONFIDENCE_SCORES));

            //results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            //options.setAdapter(new ArrayAdapter<String>(this,
                    //android.R.layout.simple_list_item_1, results));
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
