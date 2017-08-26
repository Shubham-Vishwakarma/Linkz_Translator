package com.example.admin.translator.IBMUtilies;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.ibm.watson.developer_cloud.language_translator.v2.LanguageTranslator;
import com.ibm.watson.developer_cloud.language_translator.v2.model.Language;

/**
 * Created by Admin on 04/08/2017.
 */

public class TranslationTask extends AsyncTask<String,Void,String> {

    private static final String TAG = "TranslationTask";
    private Activity activity;
    private LanguageTranslator translationService;
    private Language sourceLanguage, targetLanguage;
    private TextView resultTextView;

    public TranslationTask(Activity activity, LanguageTranslator translationService, Language sourceLanguage, Language targetLanguage, TextView resultTextView)
    {
        this.activity = activity;
        this.translationService = translationService;
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
        this.resultTextView = resultTextView;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String translatedText = translationService.translate(strings[0], sourceLanguage, targetLanguage).execute().getFirstTranslation();
            Log.e(TAG, "Translated Text = " + translatedText);
            showTranslation(translatedText);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return "Did Translate";
    }

    private void showTranslation(final String translatedString)
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                resultTextView.setText(translatedString);
            }
        });
    }
}
