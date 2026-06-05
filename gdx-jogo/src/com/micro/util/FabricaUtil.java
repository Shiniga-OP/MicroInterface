package com.micro.util;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.micro.componentes.Botao;
import com.micro.componentes.Rotulo;
import com.micro.janelas.Painel;
import com.micro.janelas.PainelFatiado;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;

public class FabricaUtil {
    public static Painel criarConfigNum(float x, float y, float largura, float altura, String titulo, String valorInicial, BitmapFont fonte, float escala, PainelFatiado visualBotao, Acao menos, Acao mais) {
        Painel linha = new Painel(x, y, largura, altura);

        Rotulo rotuloTitulo = new Rotulo(titulo, fonte, escala);
        rotuloTitulo.x = 15;
        rotuloTitulo.largura = largura - 200;
        rotuloTitulo.altura = altura;
        linha.add(rotuloTitulo);

        Rotulo rotuloValor = new Rotulo(valorInicial, fonte, escala);
        rotuloValor.x = largura - 180;
        rotuloValor.largura = 60;
        rotuloValor.altura = altura;
        linha.add(rotuloValor);

        float tamBtn = altura - 10;
        Botao btnMenos = new Botao(largura - 110, 5, tamBtn, tamBtn, "-", fonte, escala, visualBotao, menos);
        Botao btnMais = new Botao(largura - 55, 5, tamBtn, tamBtn, "+", fonte, escala, visualBotao, mais);

        linha.add(btnMenos);
        linha.add(btnMais);

        return linha;
    }
	
	public static Texture criarPixel(Color cor, int altura, int largura) {
		final Pixmap pixmap = new Pixmap(altura, largura, Pixmap.Format.RGBA8888);
        pixmap.setColor(cor);
        pixmap.fill();
        final Texture pixel = new Texture(pixmap);
        pixmap.dispose();
		return pixel;
	}
	
	public static Texture criarPixel(Color cor) {
		return criarPixel(cor, 1, 1);
	}

    public static Botao criarSelecao(float x, float y, float largura, float altura, String texto, BitmapFont fonte, float escala, Texture pixelBranco, boolean estadoInicial, Acao acao) {
        Botao selecao = new Botao(x, y, largura, altura, texto, fonte, escala, pixelBranco, acao);
        selecao.tornarAlternavel(estadoInicial);
        return selecao;
    }
}
