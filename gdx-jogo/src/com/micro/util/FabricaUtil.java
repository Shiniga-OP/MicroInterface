package com.micro.util;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.micro.componentes.Botao;
import com.micro.componentes.Rotulo;
import com.micro.janelas.Painel;
import com.micro.janelas.PainelFatiado;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.micro.componentes.CampoTexto;
import com.micro.componentes.CaixaDialogo;

public class FabricaUtil {
	public static PainelFatiado visualPadrao;
	public static BitmapFont fontePadrao;
	public static float escalaPadrao = 2.0f;
	
	public static void defPadrao(PainelFatiado visual, BitmapFont fonte, float escala) {
		visualPadrao = visual;
		fontePadrao = fonte;
		escalaPadrao = escala;
	}
	
    public static Rotulo criarConfigNum(Painel linha, float largura, float altura, String titulo, String valorInicial, BitmapFont fonte, float escala, PainelFatiado visualBotao, Runnable menos, Runnable mais) {
        final Rotulo rotuloTitulo = new Rotulo(titulo, fonte, escala);
        rotuloTitulo.x = 15;
        rotuloTitulo.largura = largura - 200;
        rotuloTitulo.altura = altura;
        linha.add(rotuloTitulo);

        final Rotulo rotuloValor = new Rotulo(valorInicial, fonte, escala);
        rotuloValor.x = largura - 180;
        rotuloValor.largura = 60;
        rotuloValor.altura = altura;
        linha.add(rotuloValor);

        final float tambt = altura - 10;
        final Botao btMenos = new Botao(largura - 110, 5, tambt, tambt, "-", fonte, escala, visualBotao, menos);
        final Botao btMais = new Botao(largura - 55, 5, tambt, tambt, "+", fonte, escala, visualBotao, mais);
        linha.add(btMenos);
        linha.add(btMais);
		return rotuloValor;
    }

    public static Texture criarPixel(Color cor) {
        final Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(cor);
        pixmap.fill();
        final Texture pixel = new Texture(pixmap);
        pixmap.dispose();
        return pixel;
    }

    public static Botao criarSelecao(float x, float y, float largura, float altura, String texto, BitmapFont fonte, float escala, Texture pixelBranco, boolean estadoInicial, Runnable acao) {
        final Botao selecao = new Botao(x, y, largura, altura, texto, fonte, escala, pixelBranco, acao);
        selecao.tornarAlternavel(estadoInicial);
        return selecao;
    }
	
	public static Botao criarBotao(String texto, float x, float y, float largura, float altura, Runnable acao) {
		final Botao botao = new Botao(x, y, largura, altura, texto, fontePadrao, escalaPadrao, visualPadrao, acao);
		return botao;
	}
	
	public static Rotulo criarRotulo(String texto, float x, float y, float largura, float altura) {
		final Rotulo rotulo = new Rotulo(texto, fontePadrao, escalaPadrao);
		rotulo.x = x;
		rotulo.y = y;
		rotulo.largura = largura;
		rotulo.altura = altura;
		return rotulo;
	}
	
	public static CampoTexto criarCampoTexto(String texto, float x, float y, float largura, float altura, Runnable acao) {
		final CampoTexto campo = new CampoTexto(visualPadrao, fontePadrao, x, y, largura, altura, escalaPadrao);
		campo.padrao = texto;
		campo.aoConfirmar = acao;
		return campo;
	}
	
	public static CampoTexto criarCampoTexto(String texto, float x, float y, float largura, float altura) {
		final CampoTexto campo = new CampoTexto(visualPadrao, fontePadrao, x, y, largura, altura, escalaPadrao);
		campo.padrao = texto;
		return campo;
	}
	
	public static Painel criarPainel(float x, float y, float largura, float altura) {
		final Painel campo = new Painel(visualPadrao, x, y, largura, altura, escalaPadrao);
		return campo;
	}
	
	public static CaixaDialogo criarDialogo(float x, float y, float largura, float altura) {
		final CaixaDialogo dialogo = new CaixaDialogo(visualPadrao, fontePadrao, escalaPadrao);
		dialogo.x = x;
		dialogo.y = y;
		dialogo.largura = largura;
		dialogo.altura = altura;
		return dialogo;
	}
	
	public static CaixaDialogo criarDialogo(float x, float y, float largura, float altura, CaixaDialogo.Fechar fechar) {
		final CaixaDialogo dialogo = new CaixaDialogo(visualPadrao, fontePadrao, escalaPadrao);
		dialogo.x = x;
		dialogo.y = y;
		dialogo.largura = largura;
		dialogo.altura = altura;
		dialogo.aoFechar = fechar;
		return dialogo;
	}
}
