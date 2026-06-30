package com.micro.componentes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.ArrayList;
import java.util.List;
import com.micro.janelas.PainelFatiado;
import com.micro.janelas.Painel;
import com.micro.util.Ancora;
import com.micro.util.GerenciadorUI;

public class CaixaDialogo extends Componente {
    public float escala;
    public String titulo;
    public String msg;
    public boolean ativa = false;
	public final PainelFatiado visual;
    public final BitmapFont fonte;
    public final Painel painelTitulo;
    public final Rotulo rotuloTitulo;
    public final Rotulo rotulomsg;
    public final Painel painelBotoes;
    public final List<Componente> componentes = new ArrayList<Componente>();

    public Botao botaoFechar, botaoOk;
    public GerenciadorUI gerenciador;

    // a propria CaixaDialogo controla quem ta em foco, sem depender de GerenciadorUI
    public CampoTexto campoEmFoco;

    public boolean arrastando = false;
    public float toqueInicialX;
    public float toqueInicialY;

    public Fechar aoFechar;

	public static interface Fechar {
		public void confirmou(boolean acao);
	}

    public CaixaDialogo(PainelFatiado visual, BitmapFont fonte, float escala) {
        super(0, 0, 400, 250);
        this.visual = visual;
        this.fonte = fonte;
        this.escala = escala;

        // painel para o titulo(transparente)
        this.painelTitulo = new Painel(0, altura - 50, largura, 50);
        this.rotuloTitulo = new Rotulo("Aviso", fonte, escala);
        this.painelTitulo.addAncorado(rotuloTitulo, Ancora.CENTRO, 0, 0);

        // texto da mensagem
        this.rotulomsg = new Rotulo("Mensagem padrão do sistema ui.", fonte, escala);
        this.rotulomsg.x = 20;
        this.rotulomsg.y = 80;
        this.rotulomsg.largura = largura - 40;
        this.rotulomsg.altura = altura - 140;

        // painel para organizar os botões na parte inferior
        this.painelBotoes = new Painel(20, 15, largura - 40, 50);

        // botão Fechar(X) no topo direito usando o novo construtor do Botao
        final float tamFechar = 35f;
        this.botaoFechar = new Botao(
            largura - tamFechar - 10, 
            altura - tamFechar - 8, 
            tamFechar, 
            tamFechar, 
            "X", 
            fonte, 
            escala, 
            visual, 
            new Runnable() {
                @Override
                public void run() {
                    fechar(false);
                }
            }
        );
    }

    public void mostrar(String titulo, String msg) {
        this.titulo = titulo;
        this.msg = msg;
        this.rotuloTitulo.defTexto(titulo);
        this.rotulomsg.defTexto(msg);
        this.ativa = true;

		if(botaoOk == null) {
			final float largBt = 120;
			final float altBt = 40;

			botaoOk = new Botao(
				(painelBotoes.largura - largBt) / 2, 
				(painelBotoes.altura - altBt) / 2, 
				largBt, 
				altBt, 
				"OK", 
				fonte, 
				escala, 
				visual, 
				new Runnable() {
					@Override
					public void run() {
						fechar(true);
					}
				}
			);
			componentes.add(botaoOk);
			painelBotoes.add(botaoOk);
		}
    }

	@Override
	public void add(Componente c) {
		super.add(c);
		componentes.add(c);
		painelBotoes.add(c);
	}

    @Override
    public boolean aoTocar(float toqueX, float toqueY, boolean pressionado) {
        if(!ativa) return false;

        final float relX = toqueX - x;
        final float relY = toqueY - y;

        if(botaoFechar.aoTocar(relX, relY, pressionado)) return true;

        // antes de repassar o toque, descobre se algum CampoTexto vai ganhar foco com ele,
        // assim a CaixaDialogo sempre sabe quem é o campo focado, sem precisar de GerenciadorUI
        if(!pressionado) {
            for(Componente comp : componentes) {
                if(comp instanceof CampoTexto) {
                    CampoTexto campo = (CampoTexto) comp;
                    if(campo.contem(relX, relY)) {
                        defFocoInterno(campo);
                    }
                }
            }
        }
        if(painelBotoes.aoTocar(relX, relY, pressionado)) return true;
        for(Componente comp : componentes) {
            if(comp.aoTocar(relX, relY, pressionado)) return true;
        }
        if(pressionado) {
            if(painelTitulo.contem(relX, relY)) {
                arrastando = true;
                toqueInicialX = toqueX - x;
                toqueInicialY = toqueY - y;
                return true;
            }
            // tocou em qualquer outro lugar da caixa que não é um campo de texto: perde o foco
            defFocoInterno(null);
        } else {
            arrastando = false;
        }
        return contem(toqueX, toqueY);
    }

    // troca o campo em foco internamente, sem depender de GerenciadorUI
    public void defFocoInterno(CampoTexto novoFoco) {
        if(campoEmFoco != null && campoEmFoco != novoFoco) {
            campoEmFoco.defFoco(false);
        }
        campoEmFoco = novoFoco;
        if(novoFoco != null) {
            novoFoco.defFoco(true);
        }
    }

    // chamado pelo GerenciadorUI(ou por qualquer InputProcessor) quando uma tecla é pressionada.l
    // retorna true se o dialogo consumiu o evento
    public boolean processarTecla(int tecla) {
        if(!ativa || campoEmFoco == null) return false;
        return campoEmFoco.processarTecla(tecla);
    }

    // chamado pelo GerenciadorUI quando um caractere é digitado
    public boolean processarCaractere(char caractere) {
        if(!ativa || campoEmFoco == null) return false;
        return campoEmFoco.processarCaractere(caractere);
    }

    public void aoArrastar(float toqueX, float toqueY) {
        if(ativa && arrastando) {
            this.x = toqueX - toqueInicialX;
            this.y = toqueY - toqueInicialY;
        }
    }

    @Override
    public void desenhar(SpriteBatch pincel, float delta, float paiX, float paiY) {
        if(!ativa) return;

        final float desenharX = paiX + x;
        final float desenharY = paiY + y;

        // desenha a estrutura da caixa
        visual.desenhar(pincel, desenharX, desenharY, largura, altura, escala);
        painelTitulo.desenhar(pincel, delta, desenharX, desenharY);
        botaoFechar.desenhar(pincel, delta, desenharX, desenharY);
        rotulomsg.desenhar(pincel, delta, desenharX, desenharY);
        painelBotoes.desenhar(pincel, delta, desenharX, desenharY);
    }

	// retrocompatibilidade:
	// botão posicionado por ancora(uso geral)
    public Botao addBotao(String texto, PainelFatiado visualBotao, Ancora ancoragem, float margemX, Runnable acao) {
        final Botao botao = new Botao(texto, visualBotao, fonte, 0, 0, 120, 40, escala, acao);
		componentes.add(botao);
        painelBotoes.addAncorado(botao, ancoragem, margemX, 0);
		return botao;
    }

    // botão posicionado manualmente dentro do painelBotoes(x/y explicitos)
    public Botao addBotaoManual(String texto, PainelFatiado visualBotao, float x, float y, float larg, float alt, Runnable acao) {
        final Botao botao = new Botao(texto, visualBotao, fonte, x, y, larg, alt, escala, acao);
		componentes.add(botao);
        painelBotoes.add(botao);
		return botao;
    }

	public void addOk(PainelFatiado visualBotao) {
        botaoOk = new Botao("OK", visualBotao, fonte, 0, 0, 120, 40, escala, new Runnable() {
				public void run() {
					fechar(true);
				}
			});
		componentes.add(botaoOk);
        painelBotoes.addAncorado(botaoOk, Ancora.CENTRO_DIREITO, -10, 0);
    }

    public void addCancelar(PainelFatiado visualBotao) {
        final Botao botaoCancelar = new Botao("Cancelar", visualBotao, fonte, 0, 0, 120, 40, escala, new Runnable() {
				public void run() {
					fechar(false);
				}
			});
		componentes.add(botaoCancelar);
        painelBotoes.addAncorado(botaoCancelar, Ancora.CENTRO_ESQUERDO, 10, 0);
    }

	public void mostrar(String titulo, String msg, Fechar fechar) {
		mostrar(titulo, msg);
		this.aoFechar = fechar;
	}

	public void fechar(boolean confirmou) {
        this.ativa = false;
        campoEmFoco = null;
        if(aoFechar != null) {
            aoFechar.confirmou(confirmou);
        }
		// limpa os botões antigos e recria o botão de confirmação padrão
        componentes.clear();
        painelBotoes.filhos.clear();
        botaoOk = null;
    }

	public void defTam(float larg, float alt) {
        this.largura = larg;
        this.altura = alt;

        painelTitulo.largura = larg;
        painelTitulo.y = alt + 10; // flutua 10px acima do painel

        rotuloTitulo.largura = larg - 50;
        rotuloTitulo.altura = 50;

        botaoFechar.x = larg - 46;
        botaoFechar.y = 4;

        painelBotoes.largura = larg;

        rotulomsg.largura = larg - 40;
    }

    public void centralizar(float larguraTela, float alturaTela) {
        this.x = (larguraTela - this.largura) / 2;
        this.y = (alturaTela - this.altura) / 2;
    }
}
