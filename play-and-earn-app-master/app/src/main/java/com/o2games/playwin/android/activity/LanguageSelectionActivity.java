package dummydata.android.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import dummydata.android.LanguageManager;
import dummydata.android.R;
import dummydata.android.databinding.ActivityLanguageSelectionBinding;

public class LanguageSelectionActivity extends AppCompat {

    ActivityLanguageSelectionBinding binding;
    private static String LANGUAGE_CODE = "en"; // By default
    private LanguageManager languageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_selection);
        binding = ActivityLanguageSelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        languageManager = new LanguageManager(this);

        binding.languageActEnglishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LANGUAGE_CODE = "en";
                disableContinueBtn(true);
                enableSelectedLanguageBtn(R.string.choose_language_eng, R.string.continue_with_english_lanugage_btn);
            }
        });
        binding.languageActSpanishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LANGUAGE_CODE = "es";
                disableContinueBtn(true);
                enableSelectedLanguageBtn(R.string.choose_language_spanish, R.string.continue_with_spanish_lanugage_btn);
            }
        });
        binding.languageActPortugueseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LANGUAGE_CODE = "pt";
                disableContinueBtn(true);
                enableSelectedLanguageBtn(R.string.choose_language_portuguese, R.string.continue_with_portuguese_lanugage_btn);
            }
        });

        binding.languageActGermanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LANGUAGE_CODE = "de";
                disableContinueBtn(false);
                showCustomToast(getString(R.string.lang_spinner_toast_german_Deutsch_msg));
            }
        });
        binding.languageActFrenchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LANGUAGE_CODE = "fr";
                disableContinueBtn(false);
                showCustomToast(getString(R.string.lang_spinner_toast_french_Français_msg));
            }
        });
        binding.languageActItalianBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LANGUAGE_CODE = "it";
                disableContinueBtn(false);
                showCustomToast(getString(R.string.lang_spinner_toast_italian_Italiano_msg));
            }
        });
        binding.languageActIndonesianBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LANGUAGE_CODE = "in";
                disableContinueBtn(false);
                showCustomToast(getString(R.string.lang_spinner_toast_indonesian_Indonesia_msg));
            }
        });
        binding.languageActJapaneseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LANGUAGE_CODE = "ja";
                disableContinueBtn(false);
                showCustomToast(getString(R.string.lang_spinner_toast_japanese_日本_msg));
            }
        });



        binding.languageActContinueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressDialog();
                languageManager.updateLanguage(LANGUAGE_CODE);
                languageManager.setShowLanguageSelectionFalse();
                finishAffinity();
                startActivity(new Intent(LanguageSelectionActivity.this, SplashScreenActivity.class));
            }
        });


    }

    private void disableContinueBtn(boolean setEnabled) {
        binding.languageActContinueBtn.setEnabled(setEnabled);
        }

    private void enableSelectedLanguageBtn(int chooseLangText, int continueWithLangText) {
        binding.languageActChooseLangTitle.setText(getString(chooseLangText));
        binding.languageActContinueBtnText.setText(getString(continueWithLangText));
        setSelectedLanguageBtn();
    }

    private void setSelectedLanguageBtn() {
        if (LANGUAGE_CODE.equals("en")) {
            binding.languageActEnglishBtn.setBackground(getResources().getDrawable(R.drawable.bg_round_all_white_stroke_fill_darker_blue_color));
            binding.languageActEnglishBtnText.setTextColor(getResources().getColor(R.color.white));
        } else {
            binding.languageActEnglishBtn.setBackground(getResources().getDrawable(R.drawable.bg_round_all_darker_blue_stroke_transparent));
            binding.languageActEnglishBtnText.setTextColor(getResources().getColor(R.color.darker_blue_app_theme));
        }

        /**
         * **/
        if (LANGUAGE_CODE.equals("es")) {
            binding.languageActSpanishBtn.setBackground(getResources().getDrawable(R.drawable.bg_round_all_white_stroke_fill_darker_blue_color));
            binding.languageActSpanishBtnText.setTextColor(getResources().getColor(R.color.white));
        } else {
            binding.languageActSpanishBtn.setBackground(getResources().getDrawable(R.drawable.bg_round_all_darker_blue_stroke_transparent));
            binding.languageActSpanishBtnText.setTextColor(getResources().getColor(R.color.darker_blue_app_theme));
        }

        /**
         * **/
        if (LANGUAGE_CODE.equals("pt")) {
            binding.languageActPortugueseBtn.setBackground(getResources().getDrawable(R.drawable.bg_round_all_white_stroke_fill_darker_blue_color));
            binding.languageActPortugueseBtnText.setTextColor(getResources().getColor(R.color.white));
        } else {
            binding.languageActPortugueseBtn.setBackground(getResources().getDrawable(R.drawable.bg_round_all_darker_blue_stroke_transparent));
            binding.languageActPortugueseBtnText.setTextColor(getResources().getColor(R.color.darker_blue_app_theme));
        }

        /**
         * **/
        if (LANGUAGE_CODE.equals("de")) {
            binding.languageActGermanBtn.setBackground(getResources().getDrawable(R.drawable.bg_round_all_white_stroke_fill_darker_blue_color));
            binding.languageActGermanBtnText.setTextColor(getResources().getColor(R.color.white));
        } else {
            binding.languageActGermanBtn.setBackground(getResources().getDrawable(R.drawable.bg_round_all_darker_blue_stroke_transparent));
            binding.languageActGermanBtnText.setTextColor(getResources().getColor(R.color.darker_blue_app_theme));
        }

        /**
         * **/
        if (LANGUAGE_CODE.equals("fr")) {
            binding.languageActFrenchBtn.setBackground(getResources().getDrawable(R.drawable.bg_round_all_white_stroke_fill_darker_blue_color));
            binding.languageActFrenchBtnText.setTextColor(getResources().getColor(R.color.white));
        } else {
            binding.languageActFrenchBtn.setBackground(getResources().getDrawable(R.drawable.bg_round_all_darker_blue_stroke_transparent));
            binding.languageActFrenchBtnText.setTextColor(getResources().getColor(R.color.darker_blue_app_theme));
        }

        /**
         * **/
        if (LANGUAGE_CODE.equals("it")) {
            binding.languageActItalianBtn.setBackground(getResources().getDrawable(R.drawable.bg_round_all_white_stroke_fill_darker_blue_color));
            binding.languageActItalianBtnText.setTextColor(getResources().getColor(R.color.white));
        } else {
            binding.languageActItalianBtn.setBackground(getResources().getDrawable(R.drawable.bg_round_all_darker_blue_stroke_transparent));
            binding.languageActItalianBtnText.setTextColor(getResources().getColor(R.color.darker_blue_app_theme));
        }

        /**
         * **/
        if (LANGUAGE_CODE.equals("in")) {
            binding.languageActIndonesianBtn.setBackground(getResources().getDrawable(R.drawable.bg_round_all_white_stroke_fill_darker_blue_color));
            binding.languageActIndonesianBtnText.setTextColor(getResources().getColor(R.color.white));
        } else {
            binding.languageActIndonesianBtn.setBackground(getResources().getDrawable(R.drawable.bg_round_all_darker_blue_stroke_transparent));
            binding.languageActIndonesianBtnText.setTextColor(getResources().getColor(R.color.darker_blue_app_theme));
        }

        /**
         * **/
        if (LANGUAGE_CODE.equals("ja")) {
            binding.languageActJapaneseBtn.setBackground(getResources().getDrawable(R.drawable.bg_round_all_white_stroke_fill_darker_blue_color));
            binding.languageActJapaneseBtnText.setTextColor(getResources().getColor(R.color.white));
        } else {
            binding.languageActJapaneseBtn.setBackground(getResources().getDrawable(R.drawable.bg_round_all_darker_blue_stroke_transparent));
            binding.languageActJapaneseBtnText.setTextColor(getResources().getColor(R.color.darker_blue_app_theme));
        }
    }

    private void showCustomToast(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View rootView1 = inflater.inflate(R.layout.layout_toast_custom,
                (ConstraintLayout) findViewById(R.id.custom_toast_constraint));

        TextView toastText = rootView1.findViewById(R.id.custom_toast_text);
        toastText.setText(message);

        Toast toast = new Toast(this);
        toast.setGravity(Gravity.BOTTOM, 0, 100);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(rootView1);

        toast.show();
    }

    private void showProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View rootView1 = LayoutInflater.from(this).inflate(R.layout.layout_dialog_progress_bar_white_short,
                (ConstraintLayout) findViewById(R.id.constraint_dialog_progress));
        builder.setView(rootView1);
        builder.setCancelable(false);

        AlertDialog progressDialog = builder.create();

        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.show();
    }


}