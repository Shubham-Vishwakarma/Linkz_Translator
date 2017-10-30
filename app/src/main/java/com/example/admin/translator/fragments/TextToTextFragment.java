package com.example.admin.translator.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.translator.IBMUtilies.IBMInitialization;
import com.example.admin.translator.IBMUtilies.TranslationTask;
import com.example.admin.translator.R;
import com.ibm.watson.developer_cloud.language_translator.v2.LanguageTranslator;
import com.ibm.watson.developer_cloud.language_translator.v2.model.Language;

/**
 * A simple {@link Fragment} subclass.
 */
public class TextToTextFragment extends Fragment {

    private static final String TAG = "TextToTextFragment";
    private LanguageTranslator translationService;
    private TextInputEditText textInput;
    private Button translateButton;
    private TextView resultTextView;
    private Language selectedTargetLanguage;
    private Spinner languagesSpinner;

    public TextToTextFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_text_to_text, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.text_to_text);
        translationService = IBMInitialization.initLanguageTranslationService(getContext());

        translateButton = view.findViewById(R.id.translateButton);
        textInput = view.findViewById(R.id.textInput);
        resultTextView = view.findViewById(R.id.resultTextView);
        languagesSpinner = view.findViewById(R.id.languagesSpinner);
        setupSpinner();

        languagesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i)
                {
                    case 0: selectedTargetLanguage = Language.SPANISH;
                        break;
                    case 1: selectedTargetLanguage = Language.GERMAN;
                        break;
                    case 2: selectedTargetLanguage = Language.FRENCH;
                        break;
                    case 3: selectedTargetLanguage = Language.ENGLISH;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedTargetLanguage = Language.SPANISH;
            }
        });

        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!textInput.getText().toString().isEmpty()) {
                    new TranslationTask(getActivity(), translationService, Language.ENGLISH, selectedTargetLanguage, resultTextView)
                            .execute(textInput.getText().toString());
                }
                else
                    Toast.makeText(getContext(),"Please enter some string first",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSpinner(){
        String voices[] = getActivity().getResources().getStringArray(R.array.languages);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),R.layout.spinner_item,voices);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        languagesSpinner.setAdapter(adapter);
        languagesSpinner.setPadding(8,8,8,8);
    }
}
