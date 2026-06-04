package com.microinterface;

import android.os.Bundle;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class InicioActivity extends AndroidApplication {
    @Override
    public void onCreate(Bundle s) {
        super.onCreate(s);
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();    
        initialize(new Inicio(), cfg);
    }
}
