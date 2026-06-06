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
import com.micro.componentes.Rotulo;
import com.micro.janelas.Lista;
import com.micro.janelas.Painel;
import com.micro.janelas.PainelFatiado;
import com.micro.util.Acao;
import com.micro.util.FabricaUtil;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.InputProcessor;
import com.micro.util.GerenciadorUI;

public class TelaMenu implements Screen, InputProcessor {
    public SpriteBatch pincel;
    public ShapeRenderer pincelFormas;
    public BitmapFont fonte;

    public Texture texturaUi;
    public Texture pixelBranco;

	public GerenciadorUI ui;

    public PainelFatiado visualFatiado;
    public Painel painelPrincipal;
    public CaixaDialogo caixaDialogo;
    public Lista lista;

    @Override
    public void show() {
        pincel = new SpriteBatch();
        pincelFormas = new ShapeRenderer();
        fonte = new BitmapFont();

        texturaUi = new Texture(Gdx.files.internal("texturas/ui.png"));

        pixelBranco = FabricaUtil.criarPixel(Color.WHITE);

        visualFatiado = new PainelFatiado(texturaUi);

        float largPainel = 500;
        float altPainel = 400;
        float painelX = (Gdx.graphics.getWidth() - largPainel) / 2f;
        float painelY = (Gdx.graphics.getHeight() - altPainel) / 2f;

        painelPrincipal = new Painel(visualFatiado, painelX, painelY, largPainel, altPainel, 2.0f);

        Rotulo titulo = new Rotulo("MENU DE TESTES DA UI", fonte, 1.5f);
        titulo.x = (largPainel - 200) / 2f;
        titulo.y = altPainel - 50;
        titulo.largura = 200;
        titulo.altura = 30;
        painelPrincipal.add(titulo);

        CampoTexto campo = new CampoTexto(visualFatiado, fonte, 50, altPainel - 120, largPainel - 100, 40, 1.0f);
        campo.padrao = "Digite seu nome aqui...";
        painelPrincipal.add(campo);

        Painel configVolume = FabricaUtil.criarConfigNum(50, altPainel - 190, largPainel - 100, 40, "Volume do Som:", "7", fonte, 1.0f, visualFatiado, 
            new Acao() {
                @Override public void exec() { Gdx.app.log("UI", "Volume diminuido"); }
            }, 
            new Acao() {
                @Override public void exec() { Gdx.app.log("UI", "Volume aumentado"); }
            }
        );
        painelPrincipal.add(configVolume);

        Botao caixaSelecaoMusica = FabricaUtil.criarSelecao(50, altPainel - 250, largPainel - 100, 40, "Ativar Musicas de Fundo", fonte, 1.0f, pixelBranco, true, 
            new Acao() {
                @Override public void exec() { Gdx.app.log("UI", "Alternou estado da musica"); }
            }
        );
        painelPrincipal.add(caixaSelecaoMusica);

        Botao btAviso = new Botao(50, 40, largPainel - 100, 45, "ABRIR CAIXA DE DIALOGO", fonte, 1.0f, visualFatiado, 
            new Acao() {
                @Override
                public void exec() {
                    caixaDialogo.mostrar("Confirmacao", "A estrutura unificada da sua interface funcionou perfeitamente!", 
                        new CaixaDialogo.Fechar() {
                            @Override
                            public void aoFechar(boolean confirmou) {
                                Gdx.app.log("UI", "Caixa de dialogo fechada. Confirmou? " + confirmou);
                            }
                        }
                    );
                }
            }
        );
        painelPrincipal.add(btAviso);

        // caixa de dialogo configurada com a escala padrão interna 1.0f
        caixaDialogo = new CaixaDialogo(visualFatiado, fonte, 1.0f, pincelFormas);
        caixaDialogo.x = (Gdx.graphics.getWidth() - caixaDialogo.largura) / 2f;
        caixaDialogo.y = (Gdx.graphics.getHeight() - caixaDialogo.altura) / 2f;

        // teste da Lista: painel rolavel com varios itens
        float largLista = 200;
        float altLista = 180;
        lista = new Lista(
            (Gdx.graphics.getWidth() - largPainel) / 2f - largLista - 20,
            (Gdx.graphics.getHeight() - altLista) / 2f,
            largLista, altLista, pixelBranco
        );
        lista.alturaItem = 44f;
        lista.espacoItem = 6f;
        String[] opcoes = { "Novo Jogo", "Continuar", "Opcoes", "Creditos", "Sair" };
        for(int i = 0; i < opcoes.length; i++) {
            final String nome = opcoes[i];
            lista.addItem(new Botao(0, 0, 0, 0, nome, fonte, 1.0f, pixelBranco,
							  new Acao() {
								  @Override public void exec() { Gdx.app.log("Lista", "Clicou: " + nome); }
							  }
						  ));
        }

		ui = new GerenciadorUI();
		ui.add(painelPrincipal);
		ui.add(lista);
		ui.add(caixaDialogo);

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

    @Override
    public void resize(int largura, int altura) {
        painelPrincipal.x = (largura - painelPrincipal.largura) / 2f;
        painelPrincipal.y = (altura - painelPrincipal.altura) / 2f;

        caixaDialogo.x = (largura - caixaDialogo.largura) / 2f;
        caixaDialogo.y = (altura - caixaDialogo.altura) / 2f;
    }

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
