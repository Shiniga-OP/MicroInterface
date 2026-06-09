package com.micro.componentes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.micro.janelas.PainelFatiado;

public class BarraProgresso extends Componente {
    // valor entre 0.0 e 1.0
    public float progresso = 0f;

    public Color corFundo = new Color(0.15f, 0.15f, 0.2f, 1f);
    public Color corPreenchimento = new Color(0.2f, 0.7f, 0.3f, 1f);
    public Color corBorda = Color.LIGHT_GRAY;

    // provedores visuais
    public PainelFatiado visualFatiado;
    public Texture pixelBranco;
    public float escalaFatiado = 1.0f;

    // construtor 1: fundo com 9-pedaços
    public BarraProgresso(float x, float y, float largura, float altura, PainelFatiado visual, float escala) {
        super(x, y, largura, altura);
        this.visualFatiado = visual;
        this.escalaFatiado = escala;
    }

    // construtor 2: fundo solido com pixelBranco
    public BarraProgresso(float x, float y, float largura, float altura, Texture pixelBranco) {
        super(x, y, largura, altura);
        this.pixelBranco = pixelBranco;
    }

    @Override
    public void desenhar(SpriteBatch pincel, float delta, float paiX, float paiY) {
        final float dx = paiX + x;
        final float dy = paiY + y;
        final float p = Math.max(0f, Math.min(1f, progresso));

        if(visualFatiado != null) {
            // fundo com 9-pedaços
            pincel.setColor(Color.WHITE);
            visualFatiado.desenhar(pincel, dx, dy, largura, altura, escalaFatiado);

            // preenchimento interno(com margem pra não cobrir a borda fatiada)
            final float margem = 4f * escalaFatiado;
            final float largInterna = largura - margem * 2;
            final float altInterna = altura - margem * 2;
            pincel.setColor(corPreenchimento);
            pincel.draw(pixelBranco != null ? pixelBranco : null, dx + margem, dy + margem, largInterna * p, altInterna);
            pincel.setColor(Color.WHITE);
        } else if(pixelBranco != null) {
            // fundo
            pincel.setColor(corFundo);
            pincel.draw(pixelBranco, dx, dy, largura, altura);

            // preenchimento
            if(p > 0f) {
                pincel.setColor(corPreenchimento);
                pincel.draw(pixelBranco, dx + 1, dy + 1, (largura - 2) * p, altura - 2);
            }
            // borda
            pincel.setColor(corBorda);
            pincel.draw(pixelBranco, dx, dy, largura, 1);
            pincel.draw(pixelBranco, dx, dy + altura - 1, largura, 1);
            pincel.draw(pixelBranco, dx, dy, 1, altura);
            pincel.draw(pixelBranco, dx + largura - 1, dy, 1, altura);

            pincel.setColor(Color.WHITE);
        }
        desenharFilhos(pincel, delta, dx, dy);
    }
}
