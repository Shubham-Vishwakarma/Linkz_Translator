package com.example.admin.translator.IBMUtilies;

import android.content.Context;
import android.util.Log;

import com.example.admin.translator.R;
import com.ibm.watson.developer_cloud.android.library.audio.utils.ContentType;
import com.ibm.watson.developer_cloud.language_translator.v2.LanguageTranslator;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;

import static android.content.ContentValues.TAG;

/**
 * Created by Admin on 04/08/2017.
 */

public class IBMInitialization {

    public static SpeechToText initSpeechToTextService(Context context){
        SpeechToText service = new SpeechToText();
        String username = context.getString(R.string.speech_text_username);
        String password = context.getString(R.string.speech_text_password);
        service.setUsernameAndPassword(username,password);
        Log.e(TAG,"Models = " + service.getModels());
        service.setEndPoint(context.getString(R.string.speechToTextEndPoint));
        return service;
    }

    public static TextToSpeech initTextToSpeechService(Context context){
        TextToSpeech service = new TextToSpeech();
        String username = context.getString(R.string.text_speech_username);
        String password = context.getString(R.string.text_speech_password);
        service.setUsernameAndPassword(username,password);
        Log.e(TAG,"Voices = " + service.getVoices());
        Log.e(TAG,"Here");
        return service;
    }

    public static LanguageTranslator initLanguageTranslationService(Context context){
        LanguageTranslator translator = new LanguageTranslator();
        String username = context.getString(R.string.language_translation_username);
        String password = context.getString(R.string.language_translation_password);
        translator.setUsernameAndPassword(username,password);
        return translator;
    }

    public static RecognizeOptions getRecognizeOptions(){
        return new RecognizeOptions.Builder()
                .continuous(true)
                .contentType(ContentType.OPUS.toString())
                .model("en-US_BroadbandModel")
                .interimResults(true)
                .wordConfidence(true)
                .inactivityTimeout(2000)
                .build();
    }
}
