package com.example.admin.translator.fragments;


import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.translator.IBMUtilies.IBMInitialization;
import com.example.admin.translator.IBMUtilies.MicrophoneRecognizeDelegate;
import com.example.admin.translator.IBMUtilies.SynthesisTask;
import com.example.admin.translator.IBMUtilies.TranslationTask;
import com.example.admin.translator.R;
import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneInputStream;
import com.ibm.watson.developer_cloud.language_translator.v2.LanguageTranslator;
import com.ibm.watson.developer_cloud.language_translator.v2.model.Language;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 */
public class SpeechToSpeechFragment extends Fragment {

    private static final String TAG = "SpeechToSpeechFragment";
    private static final int DEFAULT_TIMEOUT = 5000;
    private LinearLayout speakButton,listenButton;
    private TextView resultTextView,timerTextView;
    private Spinner voicesSpinner;

    private SpeechToText speechService;
    private LanguageTranslator translationService;
    private TextToSpeech textService;
    private MicrophoneInputStream capture;
    private boolean listening = false;
    private Voice selectedVoice;


    public SpeechToSpeechFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_speech_to_speech, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.speech_to_speech);

        speakButton = view.findViewById(R.id.speakButton);
        resultTextView = view.findViewById(R.id.resultTextView);
        timerTextView = view.findViewById(R.id.timerTextView);
        voicesSpinner = view.findViewById(R.id.voicesSpinner);
        listenButton = view.findViewById(R.id.listenButton);

        speechService = IBMInitialization.initSpeechToTextService(getContext());
        translationService = IBMInitialization.initLanguageTranslationService(getContext());
        textService = IBMInitialization.initTextToSpeechService(getContext());
        setupSpinner();

        voicesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0 : selectedVoice = Voice.EN_ALLISON;
                        break;
                    case 1 : selectedVoice = Voice.EN_LISA;
                        break;
                    case 2 : selectedVoice = Voice.EN_MICHAEL;
                        break;
                    case 3 : selectedVoice = Voice.FR_RENEE;
                        break;
                    case 4 : selectedVoice = Voice.GB_KATE;
                        break;
                    case 5 : selectedVoice = Voice.ES_SOFIA;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedVoice = Voice.EN_ALLISON;
            }
        });

        speakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    if(!listening)
                    {
                        timer();
                        capture = new MicrophoneInputStream(true);
                        listening = true;

                        Log.e(TAG,"Speech Recognizing started");
                        speechService.recognizeUsingWebSocket(
                                capture, IBMInitialization.getRecognizeOptions(),
                                new MicrophoneRecognizeDelegate(getActivity(),resultTextView));

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    capture.close();
                                    listening = false;
                                    Log.e(TAG,"Translation started");
                                    new TranslationTask(getActivity(),translationService, Language.ENGLISH,Language.SPANISH,resultTextView)
                                            .execute(resultTextView.getText().toString());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        },DEFAULT_TIMEOUT);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        listenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!resultTextView.getText().toString().isEmpty()) {
                    Log.e(TAG, "Speech started");
                    new SynthesisTask(getActivity(), textService, selectedVoice).execute(resultTextView.getText().toString());
                }
                else
                    Toast.makeText(getContext(),"Please speak something first",Toast.LENGTH_SHORT).show();
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

    private void setupSpinner(){
        String voices[] = getActivity().getResources().getStringArray(R.array.voices);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),R.layout.spinner_item,voices);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        voicesSpinner.setAdapter(adapter);
        voicesSpinner.setPadding(8,8,8,8);
    }
}