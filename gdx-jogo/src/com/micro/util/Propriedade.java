package com.micro.util;

public class Propriedade {
	public static final float PREENCHER_PAI = -1f;
	public static final float AJUSTAR_CONTEUDO = -2f;

	public float largura;
	public float altura;
	public Ancora ancora = Ancora.SUPERIOR_ESQUERDO;
	public float margemX = 0;
	public float margemY = 0;
	public float espacoDepois = 0;

	public Propriedade(float largura, float altura) {
		this.largura = largura;
		this.altura = altura;
	}

	public Propriedade ancora(Ancora ancora) {
		this.ancora = ancora;
		return this;
	}

	public Propriedade margem(float x, float y) {
		this.margemX = x;
		this.margemY = y;
		return this;
	}

	public Propriedade espaco(float espaco) {
		this.espacoDepois = espaco;
		return this;
	}
}
