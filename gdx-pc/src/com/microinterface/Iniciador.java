package com.microinterface;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Iniciador {
    public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "MicroInterface";
        config.addIcon("micro.png", com.badlogic.gdx.Files.FileType.Internal);
        config.width = 1280;
        config.height = 720;

        try {
            new LwjglApplication(new Inicio(), config);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
