package com.microinterface;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.Gdx;
import com.micro.janelas.PainelFatiado;
import com.micro.janelas.Painel;
import com.micro.componentes.Rotulo;
import com.micro.util.GerenciadorUI;
import com.micro.componentes.CampoTexto;
import com.badlogic.gdx.InputProcessor;
import com.micro.util.FabricaUtil;
import com.micro.componentes.Botao;
import com.micro.componentes.CaixaDialogo;

public class TelaExemplo implements Screen {
	public SpriteBatch pincel;
	public BitmapFont fonte;
	public GerenciadorUI ui;
	public Texture texturaUi;
	
	@Override
	public void show() {
		// variaveis gerais
		pincel = new SpriteBatch(); // pra desenhar os componentes na tela
        fonte = new BitmapFont(); // pra textos
		ui = new GerenciadorUI(); // e pra gerenciar
        texturaUi = new Texture(Gdx.files.internal("texturas/ui.png")); // textura geral
		
		PainelFatiado visual = new PainelFatiado(texturaUi); // vai dar o padrão visual pro codigo
		
		FabricaUtil.defPadrao(visual, fonte, 2.0f); // pra evitar reescrever muito codigo
		
		final Painel painel = FabricaUtil.criarPainel(
		350, 1000, // posicao
		530, 530 // tamamho
		);
		
		// pra criar um texto:
		final Rotulo titulo = FabricaUtil.criarRotulo("Texto", 0, 500, 100, 100);
		painel.add(titulo);
		
		// pra criar um campo de texto:
		CampoTexto campo = FabricaUtil.criarCampoTexto("digite aqui", 0, 0, 500, 200,
		new Runnable() {
			@Override
			public void run() {
				// pra quando der enter:
				titulo.defTexto("confirmado");
			}
		});
		painel.add(campo);
		
		// pra criar um botão:
		Botao botao = FabricaUtil.criarBotao("Clique aqui", 0, 250, 100, 100,
			new Runnable() {
				@Override
				public void run() {
					// pra quando for clicado:
					CaixaDialogo dialogo = FabricaUtil.criarDialogo();
					dialogo.add(FabricaUtil.criarCampoTexto("Digite aqui...", 0, 0, 350, 100));
					dialogo.mostrar("Teste de dialogo", "teste");
					painel.add(dialogo);
				}
			});
		painel.add(botao);
		
		ui.add(painel);
		// pra os digitos funcionarem:
		Gdx.input.setInputProcessor(ui);
	}
	
	@Override
	public void render(float delta) {
		pincel.begin();
		ui.desenhar(pincel, delta); // renderiza
		pincel.end();
	}
	
	@Override
	public void dispose() {
		fonte.dispose();
		texturaUi.dispose();
	}
	
	@Override
	public void resize(int v, int h) {}
	@Override
	public void resume() {}
	@Override
	public void hide() {}
	@Override
	public void pause() {}
}
