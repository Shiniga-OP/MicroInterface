package com.uniditor.libgdx;

import android.content.Context;
import com.uniditor.nucleo.Util;
import com.uniditor.nucleo.util.Assets;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import com.badlogic.gdx.Gdx;

public class ConfigLibgdx {
	static {
		Util.assets = new Assets() {
			@Override
			public InputStream obter(String caminho) {
				return Gdx.files.internal(caminho).read();
			}
			@Override
			public File obterCache() {
				return new File(Gdx.files.getLocalStoragePath());
			}
		};
	}
}
