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
    public Painel painelTeste;
    public boolean painelTesteVisivel = false;
    public BarraProgresso barraProgresso;
	public Rotulo rotuloVolume;
	
	public int volume = 5;

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

        final float larg = largPainel - 50 * 2;

        Rotulo titulo = new Rotulo("TESTES DA MICRO-"+Versao.formatar(Versao.atual()), fonte, 2.0f);
        titulo.largura = 200;
        titulo.altura = 50;

        CampoTexto campo = new CampoTexto(visualFatiado, fonte, 0, 0, larg, 40, 2.0f);
        campo.padrao = "Digite seu nome aqui...";
		
		Painel painelVolume = new Painel(0, 0, larg, 40);
        rotuloVolume = FabricaUtil.criarConfigNum(painelVolume, larg, 40, "Volume do Som:", "7", fonte, 2.0f, visualFatiado,
            new Runnable() {
                @Override public void run() {
					volume--;
					rotuloVolume.defTexto(volume);
				}
            },
            new Runnable() {
                @Override public void run() {
					volume++;
					rotuloVolume.defTexto(volume);
				}
            }
        );
        Botao caixaSelecaoMusica = FabricaUtil.criarSelecao(0, 0, 0, 40, "Ativar Musicas de Fundo", fonte, 2.0f, pixelBranco, true,
            new Runnable() {
                @Override public void run() { Gdx.app.log("UI", "Alternou estado da musica"); }
            }
        );
        Rotulo rotuloBarra = new Rotulo("Progresso:", fonte, 2.0f);
        rotuloBarra.largura = 0;
        rotuloBarra.altura = 20;

        barraProgresso = new BarraProgresso(0, 0, 0, 24, pixelBranco);

        Botao btProgresso = new Botao(0, 0, 0, 40, "+ 10% PROGRESSO", fonte, 2.0f, pixelBranco,
            new Runnable() {
                @Override
                public void run() {
                    barraProgresso.progresso = Math.min(1f, barraProgresso.progresso + 0.1f);
                    Gdx.app.log("UI", "Progresso: " + (int)(barraProgresso.progresso * 100) + "%");
                }
            }
        );
        Botao btTeste = new Botao(0, 0, 0, 45, "TESTE: PAINEL ANINHADO", fonte, 2.0f, visualFatiado,
            new Runnable() {
                @Override
                public void run() {
                    painelTesteVisivel = !painelTesteVisivel;
                    if(painelTesteVisivel) ui.addCamada(painelTeste, GerenciadorUI.CAMADA_TOPO);
                    else ui.rm(painelTeste);
                }
            }
        );
        Botao btAviso = new Botao(0, 0, 0, 45, "ABRIR CAIXA DE DIALOGO", fonte, 2.0f, visualFatiado,
            new Runnable() {
                @Override
                public void run() {
					final Runnable aoFechar = new Runnable() {
						@Override
						public void run() {
							Gdx.app.log("UI", "Caixa de dialogo fechada.");
						}
					};
					caixaDialogo.acaoOk = aoFechar;
					caixaDialogo.acaoX = aoFechar;
                    caixaDialogo.mostrar("Confirmação", "A estrutura unificada da sua interface funcionou perfeitamente!");
                }
            }
        );
        MontadorPainel montador = new MontadorPainel(painelPrincipal, 50, 10);
        montador.addFixo(titulo)
            .add(campo)
            .add(painelVolume)
            .add(caixaSelecaoMusica)
            .addFixo(rotuloBarra)
            .add(barraProgresso)
            .add(btProgresso)
            .add(btTeste)
            .add(btAviso);

        // caixa de dialogo configurada com a escala padrão interna 1.0f
        caixaDialogo = new CaixaDialogo(visualFatiado, fonte, 2.0f, pincelFormas);
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
            lista.addItem(new Botao(0, 0, 0, 0, nome, fonte, 2.0f, pixelBranco,
							  new Runnable() {
								  @Override public void run() { Gdx.app.log("Lista", "Clicou: " + nome); }
							  }
						  ));
        }
        // painel de teste: painel externo contendo um painel rolavel aninhado
        // serve para validar a correcao de coordenadas no arraste
        float largTeste = 320;
        float altTeste = 300;
        painelTeste = new Painel(visualFatiado,
								 (Gdx.graphics.getWidth() - largTeste) / 2f,
								 (Gdx.graphics.getHeight() - altTeste) / 2f,
								 largTeste, altTeste, 2.0f);

        Rotulo rotuloTeste = new Rotulo("Painel aninhado rolavel:", fonte, 2.0f);
        rotuloTeste.x = 10;
        rotuloTeste.y = altTeste - 40;
        rotuloTeste.largura = largTeste - 20;
        rotuloTeste.altura = 30;
        painelTeste.add(rotuloTeste);

        // lista rolavel aninhada, deslocada para nao estar na origem (0,0)
        float largInterno = largTeste - 40;
        float altInterno = 160;
        Lista listaInterna = new Lista(20, 30, largInterno, altInterno, pixelBranco);
        listaInterna.alturaItem = 38f;
        listaInterna.espacoItem = 4f;

        String[] itens = { "Item A", "Item B", "Item C", "Item D", "Item E", "Item F", "Item G" };
        for(int i = 0; i < itens.length; i++) {
            final String nome = itens[i];
            listaInterna.addItem(new Botao(0, 0, 0, 0, nome, fonte, 2.0f, pixelBranco,
									 new Runnable() {
										 @Override public void run() { Gdx.app.log("Teste", "Clicou: " + nome); }
									 }
								 ));
        }
        painelTeste.add(listaInterna);

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
