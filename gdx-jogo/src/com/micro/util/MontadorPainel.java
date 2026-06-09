package com.micro.util;

import com.micro.componentes.Botao;
import com.micro.componentes.Componente;
import com.micro.janelas.Painel;

public class MontadorPainel {
    public Painel painel;
    public float margem;
    public float espaco;
    public float larguraDisponivel;
    public float cursorY;

    public MontadorPainel(Painel painel, float margem, float espaco) {
        this.painel = painel;
        this.margem = margem;
        this.espaco = espaco;
        this.larguraDisponivel = painel.largura - margem * 2;
        this.cursorY = painel.altura - margem;
    }

    public MontadorPainel add(Componente c) {
        cursorY -= c.altura;
        c.x = margem;
        c.y = cursorY;
        if(c instanceof Botao) ((Botao)c).defTam(larguraDisponivel, c.altura);
        else c.largura = larguraDisponivel;
			
        painel.add(c);
        cursorY -= espaco;
        return this;
    }

    public MontadorPainel addFixo(Componente c) {
        cursorY -= c.altura;
        c.x = margem;
        c.y = cursorY;
        painel.add(c);
        cursorY -= espaco;
        return this;
    }

    public MontadorPainel pular(float altura) {
        cursorY -= altura;
        return this;
    }
}
