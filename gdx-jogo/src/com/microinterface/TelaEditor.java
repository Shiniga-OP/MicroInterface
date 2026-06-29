package com.microinterface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.micro.componentes.Botao;
import com.micro.componentes.CaixaDialogo;
import com.micro.componentes.CampoTexto;
import com.micro.componentes.BarraProgresso;
import com.micro.janelas.Lista;
import com.micro.janelas.Painel;
import com.micro.janelas.PainelFatiado;
import com.micro.util.FabricaUtil;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.InputProcessor;
import com.micro.util.GerenciadorUI;
import com.micro.util.MontadorPainel;
import com.micro.Versao;
import com.micro.componentes.Rotulo;
import com.uniditor.libgdx.EditorLibgdxCanvas;
import com.uniditor.nucleo.sintaxe.cpp.TokenizadorCpp;
import com.uniditor.nucleo.editores.VisaoEditor;
import com.uniditor.libgdx.graficos.Canvas;
import com.uniditor.libgdx.ConfigLibgdx;
import com.uniditor.nucleo.Util;

public class TelaEditor implements Screen, InputProcessor {
    public SpriteBatch pincel;
    public ShapeRenderer pincelFormas;
    public BitmapFont fonte;

    public Texture texturaUi;
    public Texture pixelBranco;

	public GerenciadorUI ui;

    public PainelFatiado visualFatiado;
    public Painel painelPrincipal;
    
    @Override
    public void show() {
        pincel = new SpriteBatch();
        pincelFormas = new ShapeRenderer();
        fonte = new BitmapFont();

        texturaUi = new Texture(Gdx.files.internal("texturas/ui.png"));

        pixelBranco = FabricaUtil.criarPixel(Color.WHITE);

        visualFatiado = new PainelFatiado(texturaUi);

        float largPainel = 500;
        float altPainel = 530;
        float painelX = (Gdx.graphics.getWidth() - largPainel) / 2f;
        float painelY = (Gdx.graphics.getHeight() - altPainel) / 2f;

        painelPrincipal = new Painel(visualFatiado, painelX, painelY, largPainel, altPainel, 2.0f);

        Rotulo titulo = new Rotulo("TESTES DA MICRO-"+Versao.formatar(Versao.atual()), fonte, 2.0f);
        titulo.largura = 200;
        titulo.altura = 50;
		
		new ConfigLibgdx();
		
		Canvas canvas = new Canvas(
			pincel,
			fonte
		); // renderizador

		VisaoEditor visaoEditor = new VisaoEditor(
			canvas, new TokenizadorCpp() // regras de sintaxe
		);
		visaoEditor.render.defFonte(
			Util.arquivo.copiarAssets("firacode-regular.ttf")
		);
		EditorLibgdxCanvas editor = new EditorLibgdxCanvas(0, 0, largPainel, altPainel, visaoEditor, canvas);

        MontadorPainel montador = new MontadorPainel(painelPrincipal, 50, 10);
        montador.addFixo(titulo)
            .add(editor);
			
		ui = new GerenciadorUI();
		ui.add(painelPrincipal);
		
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.15f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        pincel.begin();
		ui.desenhar(pincel, delta);
        pincel.end();
    }

    @Override public void resize(int largura, int altura) {}

    @Override
    public void dispose() {
        pincel.dispose();
        pincelFormas.dispose();
        fonte.dispose();
        texturaUi.dispose();
        pixelBranco.dispose();
		ui.liberar();
    }

	@Override
	public boolean keyDown(int p) {
		return ui.processarTecla(p);
	}

	@Override
	public boolean keyTyped(char caractere) {
		return ui.processarCaractere(caractere);
	}

	@Override
	public boolean touchDown(int telaX, int telaY, int p, int b) {
		float uiY = Gdx.graphics.getHeight() - telaY;
		return ui.processarToque(telaX, uiY, true);
	}

	@Override
	public boolean touchUp(int telaX, int telaY, int p, int b) {
		float uiY = Gdx.graphics.getHeight() - telaY;
		return ui.processarToque(telaX, uiY, false);
	}

	@Override
	public boolean touchDragged(int telaX, int telaY, int p) {
		float uiY = Gdx.graphics.getHeight() - telaY;
		ui.processarArraste(telaX, uiY);
		return false;
	}
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
	@Override public boolean keyUp(int p) { return false; }
	@Override public boolean mouseMoved(int telaX, int telaY) { return false; }
	@Override public boolean scrolled(float telaX, float telaY) { return false; }
}
