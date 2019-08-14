package com.homie.takeanote.Settings;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.homie.roompersistence.R;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private ImageView mBackBtn,mBackground_theme,mToolBackground;
    private Spinner mThemeSpinner;
    private List<String> mThemesList;
    private ArrayAdapter<String> spinnerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        changeStatusBarColor(R.color.colorPrimaryDark);

        mBackBtn=findViewById(R.id.back_iv);
        mBackground_theme=findViewById(R.id.background_theme);
        mToolBackground=findViewById(R.id.settings_background_toolbar);
        mThemeSpinner=findViewById(R.id.settings_themes_spinner);
        mThemesList=new ArrayList<>();

        mThemesList.add("Default Theme");
        mThemesList.add("Dark Theme");
        mThemesList.add("Bright Theme");

        spinnerAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,mThemesList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mThemeSpinner.setAdapter(spinnerAdapter);

        mThemeSpinner.setOnItemSelectedListener(SpinnerItemSelectedListener);
        mBackBtn.setOnClickListener(BackClickListener);
    }

    AdapterView.OnItemSelectedListener SpinnerItemSelectedListener=new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            switch(position){

                case 1:
                    //Default Theme
                    mBackground_theme.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    mToolBackground.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

                case 2:
                    //Dark Theme
                    mBackground_theme.setBackgroundColor(getResources().getColor(R.color.block));
                    mToolBackground.setBackgroundColor(getResources().getColor(R.color.block));

                case 3:
                    //Bright Theme
                    mBackground_theme.setBackgroundColor(getResources().getColor(R.color.white));
                    mToolBackground.setBackgroundColor(getResources().getColor(R.color.white));

            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    View.OnClickListener BackClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            finish();
        }
    };

    private void changeStatusBarColor(int color){
        Window window = SettingsActivity.this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(SettingsActivity.this,color));

    }
}
