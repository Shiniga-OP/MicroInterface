package com.microinterface;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class Inicio extends Game {
	public static Game tela;
	@Override
	public void create() {
		tela = this;
		setScreen(new TelaMenu());
	}
}
