package com.micro.janelas;

import com.badlogic.gdx.graphics.Texture;
import com.micro.componentes.Botao;
import com.micro.componentes.Componente;

public class Lista extends Painel {
    public float alturaItem = 48f;
    public float espacoItem = 4f;
    public boolean alturaVariavel = false;

    // construtor com fundo texturizado
    public Lista(PainelFatiado visual, float x, float y, float largura, float altura, float escala, Texture pixelBranco) {
        super(visual, x, y, largura, altura, escala);
        tornarRolavel(pixelBranco);
    }

    // construtor transparente
    public Lista(float x, float y, float largura, float altura, Texture pixelBranco) {
        super(x, y, largura, altura);
        tornarRolavel(pixelBranco);
    }
    
    @Override
    public void add(Componente c) {
        addItem(c);
    }

    public void addItem(Componente item) {
        final float novaAltura = alturaVariavel ? item.altura : alturaItem;
		final float novaLargura = largura - larguraBarra - margemBarra;
		
        if(!alturaVariavel) item.defTam(novaLargura, novaAltura);
        
        item.x = 0;
        // cada item novo vai abaixo dos anteriores: y decresce
        // recalcula todos os y para manter ordem topo->base
        filhos.add(item);
        reorganizar();
        calcularAlturaConteudo();
        // scroll começa mostrando o topo
        deslocamentoY = alturaConteudo > altura ? alturaConteudo - altura : 0;
    }

    public void removerItem(Componente item) {
        final int idc = filhos.indexOf(item);
        if(idc < 0) return;
        filhos.remove(idc);
        reorganizar();
        calcularAlturaConteudo();
        if(deslocamentoY > alturaConteudo - altura) {
            deslocamentoY = Math.max(0, alturaConteudo - altura);
        }
    }

    public void limparItens() {
        filhos.clear();
        deslocamentoY = 0;
        alturaConteudo = 0;
    }

    // distribui os itens de cima pra baixo(y decresce a cada item)
    // o primeiro item fica no topo, com y = alturaTotal - alturaItem
    public void reorganizar() {
        // calcula altura total necessaria
        float total = 0;
        for(Componente filho : filhos) {
            total += filho.altura + espacoItem;
        }
        if(total > 0) total -= espacoItem;

        float y = total > altura ? total : altura;
        y -= filhos.get(0).altura;

        for(int i = 0; i < filhos.size(); i++) {
            final Componente filho = filhos.get(i);
            filho.y = y;
            y -= filho.altura + espacoItem;
        }
    }

    @Override
    public void calcularAlturaConteudo() {
        if(filhos.isEmpty()) {
            alturaConteudo = 0;
            return;
        }
        float maxY = 0;
        for(Componente filho : filhos) {
            final float topo = filho.y + filho.altura;
            if(topo > maxY) maxY = topo;
        }
        alturaConteudo = maxY;
    }
}
