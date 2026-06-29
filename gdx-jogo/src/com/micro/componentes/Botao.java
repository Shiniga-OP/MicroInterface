package com.micro.componentes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.micro.janelas.PainelFatiado;

public class Botao extends Componente {
    public Rotulo rotulo;
    public Runnable acaoClique;

    // estados do Botão
    public boolean pressionado = false;
    public boolean alternavel = false;
    public boolean selecionado = false;

    // provedores visuais(opcionais, o botão usa o que for fornecido)
    public PainelFatiado visualFatiado;
    public Texture pixelBranco;
    public float escalaFatiado = 1.0f;

    // customização de cores(para o modo usando pixelBranco)
    public Color corNormal = new Color(0.3f, 0.4f, 0.5f, 1f);
    public Color corPressionado = new Color(0.4f, 0.5f, 0.6f, 1f);
    public Color corSelecionado = new Color(0.3f, 0.5f, 0.3f, 1f);

    // construtor 1: para botões baseados em texturas fatiadas(ex: Janelas de Menu, UI do Jogo)
    public Botao(float x, float y, float largura, float altura, String texto, BitmapFont fonte, float escala, PainelFatiado visual, Runnable acao) {
        super(x, y, largura, altura);
        this.visualFatiado = visual;
        this.escalaFatiado = escala;
        this.acaoClique = acao;
        iniciarRotulo(texto, fonte, escala);
    }

    // construtor 2: para botões solidos/geometricos(substitui o antigo ItemBotao)
    public Botao(float x, float y, float largura, float altura, String texto, BitmapFont fonte, float escala, Texture pixelBranco, Runnable acao) {
        super(x, y, largura, altura);
        this.pixelBranco = pixelBranco;
        this.acaoClique = acao;
        iniciarRotulo(texto, fonte, escala);
    }

    public void iniciarRotulo(String texto, BitmapFont fonte, float escala) {
        this.rotulo = new Rotulo(texto, fonte, escala);
        this.rotulo.largura = this.largura;
        this.rotulo.altura = this.altura;
    }

    public void defTam(float largura, float altura) {
        this.largura = largura;
        this.altura = altura;
        if(rotulo != null) {
            rotulo.largura = largura;
            rotulo.altura = altura;
            rotulo.quebrarTexto();
        }
    }

    // transforma este botão em um seleção/radio
    public Botao tornarAlternavel(boolean estadoInicial) {
        this.alternavel = true;
        this.selecionado = estadoInicial;
        // desloca o texto um pouco para a direita para dar espaço ao indicador redondo
        if(this.rotulo != null) {
            this.rotulo.x = 30;
            this.rotulo.largura = this.largura - 35;
        }
        return this;
    }

    @Override
    public boolean aoTocar(float toqueX, float toqueY, boolean pressionadoAgora) {
        if(contem(toqueX, toqueY)) {
            if(pressionadoAgora) {
                this.pressionado = true;
            } else {
                if(this.pressionado) {
                    if(alternavel) {
                        this.selecionado = !this.selecionado;
                    }
                    if(acaoClique != null) {
                        acaoClique.run();
                    }
                }
                this.pressionado = false;
            }
            return true;
        }
        // se o dedo/mouse saiu arrastado de dentro do botão, cancela o estado de clique
        this.pressionado = false;
        return false;
    }

    @Override
    public void desenhar(SpriteBatch pincel, float delta, float paiX, float paiY) {
        final float desenharX = paiX + x;
        final float desenharY = paiY + y;

        // 1. renderização do Fundo
        if(visualFatiado != null) {
            // se usa PainelFatiado, aplicamos um tom cinza quando pressionado para dar efeito de clique
            if(pressionado) {
                pincel.setColor(0.7f, 0.7f, 0.7f, 1f);
            } else {
                pincel.setColor(Color.WHITE);
            }
            visualFatiado.desenhar(pincel, desenharX, desenharY, largura, altura, escalaFatiado);
            pincel.setColor(Color.WHITE); // restaura o padrão
        } else if(pixelBranco != null) {
            // se usa Pixel Solido, calcula a cor baseada no estado atual
            Color corAtual = selecionado ? corSelecionado : (pressionado ? corPressionado : corNormal);
            pincel.setColor(corAtual);
            pincel.draw(pixelBranco, desenharX, desenharY, largura, altura);

            // desenha bordas simples estilo gordo UI
            pincel.setColor(Color.LIGHT_GRAY);
            pincel.draw(pixelBranco, desenharX, desenharY, largura, 1); // Inferior
            pincel.draw(pixelBranco, desenharX, desenharY + altura - 1, largura, 1); // Superior
            pincel.draw(pixelBranco, desenharX, desenharY, 1, altura); // Esquerda
            pincel.draw(pixelBranco, desenharX + largura - 1, desenharY, 1, altura); // Direita
        }
        // 2. se for um botão de seleção/alternavel, desenha o indicador geometrico
        if(alternavel && pixelBranco != null) {
            final float circX = desenharX + 10;
            final float circY = desenharY + (altura / 2f) - 6;

            // caixa externa do indicador
            pincel.setColor(Color.LIGHT_GRAY);
            pincel.draw(pixelBranco, circX, circY, 12, 12);

            // centro(Preenchido se selecionado, cor de fundo se desmarcado)
            if(selecionado) {
                pincel.setColor(Color.WHITE);
                pincel.draw(pixelBranco, circX + 3, circY + 3, 6, 6);
            } else {
                Color corFundoIndicador = pressionado ? corPressionado : corNormal;
                pincel.setColor(corFundoIndicador);
                pincel.draw(pixelBranco, circX + 2, circY + 2, 8, 8);
            }
        }
        // 3. renderiza o texto centralizado por cima
        pincel.setColor(Color.WHITE);
        if(rotulo != null) {
            rotulo.desenhar(pincel, delta, desenharX, desenharY);
        }
    }
}
