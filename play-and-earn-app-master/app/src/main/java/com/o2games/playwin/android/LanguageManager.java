package dummydata.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

//@Keep
public class LanguageManager {

    private Context context;

    private SharedPreferences writeSPref;
    private SharedPreferences.Editor editorSPref;
    private SharedPreferences readSPref;

    public LanguageManager(Context context) {
        this.context = context;
    }

    public void updateLanguage(String langCode) {
        setLanguageSPref(langCode);

        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.locale = locale;
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    public String getLanguageSPref() {
        readSPref = context.getSharedPreferences(Constants.SHARED_PREF_COMMON, Context.MODE_PRIVATE);

        String getLangCode = readSPref.getString(Constants.SP_USER_SELECTED_LANGUAGE, "en");
        return getLangCode;
    }

    public void setLanguageSPref(String code) {
        writeSPref = context.getSharedPreferences(Constants.SHARED_PREF_COMMON, Context.MODE_PRIVATE);
        editorSPref = writeSPref.edit();

        editorSPref.putString(Constants.SP_USER_SELECTED_LANGUAGE, code);
        editorSPref.commit();
    }

    public void setShowLanguageSelectionFalse() {
        writeSPref = context.getSharedPreferences(Constants.SHARED_PREF_COMMON, Context.MODE_PRIVATE);
        editorSPref = writeSPref.edit();

        editorSPref.putBoolean(Constants.SP_SHOW_LANGUAGE_ACTIVITY, false);
        editorSPref.commit();
    }

}
