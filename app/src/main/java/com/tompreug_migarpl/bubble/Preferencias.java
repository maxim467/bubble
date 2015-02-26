package com.tompreug_migarpl.bubble;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

/**
 * Created by Tomy on 24/02/2015.
 */
    /*public class Preferencias extends PreferenceActivity {
    @Override
        public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferencias); //de esta manera este metodo esta obsoleto
        }
}*/
    public class Preferencias extends PreferenceActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
        }// el fragment manager es para sustituir el contenido de la pantalla (android.R.id.content) por el de nuestro fragment de preferencias recién definido

        public static class MyPreferenceFragment extends PreferenceFragment {
            @Override
            public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                addPreferencesFromResource(R.xml.preferencias);
            }
        }
    }