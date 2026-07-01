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
import com.micro.componentes.Componente;
import com.micro.janelas.Lista;

public class FabricaUtil {
	public static PainelFatiado visualPadrao;
	public static BitmapFont fontePadrao;
	public static float escalaPadrao = 2.0f;
	
	public static void defPadrao(PainelFatiado visual, BitmapFont fonte, float escala) {
		visualPadrao = visual;
		fontePadrao = fonte;
		escalaPadrao = escala;
	}
	
	public static void add(Painel pai, Componente filho, Propriedade prop) {
		final float largPai = pai.largura - pai.espacoEsquerda - pai.espacoDireita;
		final float altPai = pai.altura - pai.espacoSuperior - pai.espacoInferior;

		final float largFinal = prop.largura == Propriedade.PREENCHER_PAI ? largPai - prop.margemX * 2 : prop.largura;

		float altFinal;
		if(prop.altura == Propriedade.AJUSTAR_CONTEUDO) {
			if(filho instanceof Rotulo) {
				altFinal = ((Rotulo)filho).calcAltura(largFinal);
			} else if(filho instanceof Botao) {
				altFinal = ((Botao)filho).calcAltura(largFinal);
			} else {
				altFinal = filho.altura;
			}
		} else if(prop.altura == Propriedade.PREENCHER_PAI) {
			altFinal = altPai - prop.margemY * 2;
		} else {
			altFinal = prop.altura;
		}
		if(filho instanceof Botao) {
			((Botao)filho).defTam(largFinal, altFinal);
		} else {
			filho.largura = largFinal;
			filho.altura = altFinal;
		}
		filho.x = prop.ancora.calcularX(largPai, largFinal, prop.margemX) + pai.espacoEsquerda;
		filho.y = prop.ancora.calcularY(altPai, altFinal, prop.margemY) + pai.espacoInferior;

		pai.add(filho);
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
	
	public static Rotulo criarConfigNum(Painel painel,
        float x, float y, float largura, float altura,
        String titulo, String valorInicial,
        BitmapFont fonte, float escala,
        Texture pixelBranco, PainelFatiado visual,
        Runnable acaoMenos, Runnable acaoMais
    ) {
		return criarConfigNum(painel, largura, altura, titulo, valorInicial, fonte, escala, visual, acaoMenos, acaoMais);
	}
	
	public static Botao criarSelecao(
        float x, float y, float largura, float altura,
        String texto, boolean estadoInicial,
        BitmapFont fonte, float escala,
        Texture pixelBranco, PainelFatiado visual,
        Runnable acaoAlternar
    ) {
		return criarSelecao(x, y, largura, altura, texto, fonte, escala, pixelBranco, estadoInicial, acaoAlternar);
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
	
	public static CaixaDialogo criarDialogo() {
		final CaixaDialogo dialogo = new CaixaDialogo(visualPadrao, fontePadrao, escalaPadrao);
		return dialogo;
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
	
	public static Lista criarLista(Texture pixelBranco, float x, float y, float largura, float altura) {
		final Lista lista = new Lista(visualPadrao, x, y, largura, altura, escalaPadrao, pixelBranco);
		return lista;
	}
}
