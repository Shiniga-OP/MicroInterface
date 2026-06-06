package com.micro.componentes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import java.util.ArrayList;
import java.util.List;
import com.micro.janelas.PainelFatiado;
import com.micro.janelas.Painel;
import com.micro.util.Acao;
import com.micro.util.Ancora;
import com.micro.util.GerenciadorUI;

public class CaixaDialogo extends Componente {
    public float escala;
    public String titulo;
    public String msg;
    public boolean ativa = false;
    public final ShapeRenderer pincelFormas;
	public final PainelFatiado visual;
    public final BitmapFont fonte;
    public final Painel painelTitulo;
    public final Rotulo rotuloTitulo;
    public final Rotulo rotulomsg;
    public final Painel painelBotoes;
    public final List<Componente> componentes = new ArrayList<Componente>();

    public final Botao botaoFechar;

    public GerenciadorUI gerenciador = null;

    public boolean arrastando = false;
    public float toqueInicialX;
    public float toqueInicialY;

    public interface Fechar {
        void aoFechar(boolean confirmou);
    }
    public Fechar aoFechar;

    public CaixaDialogo(PainelFatiado visual, BitmapFont fonte, float escala, ShapeRenderer pincelFormas) {
        super(0, 0, 400, 250);
        this.visual = visual;
        this.fonte = fonte;
        this.escala = escala;
        this.pincelFormas = pincelFormas;

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
            new Acao() {
                @Override
                public void exec() {
                    ativa = false;
                    if(aoFechar != null) aoFechar.aoFechar(false);
                }
            }
        );
    }

    public void mostrar(String titulo, String msg, Fechar evento) {
        this.titulo = titulo;
        this.msg = msg;
        this.aoFechar = evento;
        this.rotuloTitulo.defTexto(titulo);
        this.rotulomsg.defTexto(msg);
        this.ativa = true;

        // limpa os botões antigos e recria o botão de confirmação padrão
        componentes.clear();
        painelBotoes.filhos.clear();

        final float largBt = 120;
        final float altBt = 40;

        final Botao btOk = new Botao(
            (painelBotoes.largura - largBt) / 2, 
            (painelBotoes.altura - altBt) / 2, 
            largBt, 
            altBt, 
            "OK", 
            fonte, 
            escala, 
            visual, 
            new Acao() {
                @Override
                public void exec() {
                    ativa = false;
                    if(aoFechar != null) aoFechar.aoFechar(true);
                }
            }
        );
        componentes.add(btOk);
        painelBotoes.add(btOk);

        if(gerenciador != null) {
            for(Componente c : componentes) gerenciador.registrarCamposTexto(c);
        }
    }

    @Override
    public boolean aoTocar(float toqueX, float toqueY, boolean pressionado) {
        if(!ativa) return false;

        final float relX = toqueX - x;
        final float relY = toqueY - y;

        if(botaoFechar.aoTocar(relX, relY, pressionado)) return true;
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
        } else {
            arrastando = false;
        }
        return contem(toqueX, toqueY);
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

        // desenha o fundo escurecido atras da caixa
        pincel.end();
        if(pincelFormas != null) {
            pincelFormas.begin(ShapeRenderer.ShapeType.Filled);
            pincelFormas.setColor(0, 0, 0, 0.6f);
            pincelFormas.rect(paiX - 2000, paiY - 2000, 4000, 4000);
            pincelFormas.end();
        }
        pincel.begin();

        // desenha a estrutura da caixa
        visual.desenhar(pincel, desenharX, desenharY, largura, altura, escala);
        painelTitulo.desenhar(pincel, delta, desenharX, desenharY);
        botaoFechar.desenhar(pincel, delta, desenharX, desenharY);
        rotulomsg.desenhar(pincel, delta, desenharX, desenharY);
        painelBotoes.desenhar(pincel, delta, desenharX, desenharY);
    }
}
