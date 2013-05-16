/**
 * Copyright (C) 2007 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.newproyectjmb;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

import com.newproyectjmb.R;

public class Opciones extends PreferenceActivity implements OnSharedPreferenceChangeListener{


    SharedPreferences prefs;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.layout.preferences_opciones);
        
        prefs = getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        prefs.registerOnSharedPreferenceChangeListener(this);

        //getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }
    public void onSharedPreferenceChanged(SharedPreferences 
    		sharedPreferences, String key) { 
    		                Log.v("changed", key); 
    		        } 

}
