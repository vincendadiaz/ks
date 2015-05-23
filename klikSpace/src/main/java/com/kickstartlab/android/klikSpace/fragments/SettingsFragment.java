package com.kickstartlab.android.klikSpace.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.github.machinarius.preferencefragment.PreferenceFragment;
import com.jenzz.materialpreference.Preference;
import com.kickstartlab.android.klikSpace.R;
import com.kickstartlab.android.klikSpace.events.VenueEvent;
import com.kickstartlab.android.klikSpace.events.DeviceTypeEvent;
import com.kickstartlab.android.klikSpace.events.ImageEvent;
import com.kickstartlab.android.klikSpace.events.LogEvent;

import de.greenrobot.event.EventBus;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends PreferenceFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        addPreferencesFromResource(R.xml.pref_settings);
        Preference sync = (Preference) findPreference("sync_parameter");
        Preference sync_images = (Preference) findPreference("sync_images");
        Preference upsync_images = (Preference) findPreference("upsync_images");
        Preference upsync_assets = (Preference) findPreference("upsync_assets");
        Preference sync_log = (Preference) findPreference("sync_log");
        Preference purge_log = (Preference) findPreference("purge_log");



        sync.setOnPreferenceClickListener(new android.preference.Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(android.preference.Preference preference) {
                EventBus.getDefault().post(new DeviceTypeEvent("sync"));
                return false;
            }
        });

        sync_images.setOnPreferenceClickListener(new android.preference.Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(android.preference.Preference preference) {
                EventBus.getDefault().post(new ImageEvent("sync","all", "all"));
                return false;
            }
        });

        upsync_images.setOnPreferenceClickListener(new android.preference.Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(android.preference.Preference preference) {
                EventBus.getDefault().post(new ImageEvent("upsync","all", "all"));
                return false;
            }
        });

        upsync_assets.setOnPreferenceClickListener(new android.preference.Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(android.preference.Preference preference) {
                EventBus.getDefault().post(new VenueEvent("upsyncBatch"));
                return false;
            }
        });
        sync_log.setOnPreferenceClickListener(new android.preference.Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(android.preference.Preference preference) {
                EventBus.getDefault().post(new LogEvent("syncLog"));
                return false;
            }
        });
        purge_log.setOnPreferenceClickListener(new android.preference.Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(android.preference.Preference preference) {
                EventBus.getDefault().post(new LogEvent("purgeLog"));
                return false;
            }
        });
    }

    /*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }
    */


}
