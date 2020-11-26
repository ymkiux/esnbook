package wooyun.esnb.fragment;

import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.text.TextUtils;

import wooyun.esnb.R;

public class SettingFragment extends PreferenceFragment {
    private static final String del_KEY = "del_t", edit_KEY = "home_t";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting);
        try {
            initMistakeTouchPreference();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void initMistakeTouchPreference() {
        SwitchPreference del = (SwitchPreference) findPreference(del_KEY);
        bindPreferenceSummaryToValue(findPreference("edit_pt"));
    }

    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                preference.getContext().getSharedPreferences("CONFIG", Context.MODE_PRIVATE).getString(preference.getKey(), ""));
    }


    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = (Preference preference, Object value) -> {
        String stringValue = value.toString();

        if (preference.getKey().equals("edit_pt")) {
            if (TextUtils.isEmpty(stringValue)) {
                preference.setSummary("输入你喜欢的api链接");
            } else {
                preference.setSummary(stringValue);
            }
        }
        return true;
    };
}


