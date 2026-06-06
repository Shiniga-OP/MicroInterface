package com.micro.componentes;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class Rotulo extends Componente {
    private String texto;
	public float espacoLinha = 1.3f;
	public float alturaLinha;
    public final BitmapFont fonte;
    public final GlyphLayout medidor;
    public final float escala;
    public final Array<String> linhas;

    public Rotulo(String texto, BitmapFont fonte, float escala) {
        super(0, 0, 0, 0);
        this.texto = texto;
        this.fonte = fonte;
        this.escala = escala;
        this.medidor = new GlyphLayout();
        this.linhas = new Array<>();
		quebrarTexto();
    }

	public void defTexto(String texto) {
		this.texto = texto;
		quebrarTexto();
	}

    public void quebrarTexto() {
        linhas.clear();

        if(largura <= 0) {
            linhas.add(texto);
            return;
        }
        fonte.getData().setScale(escala);

        final String[] palavras = texto.split(" ");
        String linhaAtual = "";

        for(int i = 0; i < palavras.length; i++) {
            final String teste = linhaAtual.isEmpty() ? palavras[i] : linhaAtual + " " + palavras[i];
            medidor.setText(fonte, teste);

            if(medidor.width <= largura - 20) {
                linhaAtual = teste;
            } else {
                if(!linhaAtual.isEmpty()) {
                    linhas.add(linhaAtual);
                }
                linhaAtual = palavras[i];
            }
        }
        if(!linhaAtual.isEmpty()) linhas.add(linhaAtual);

        medidor.setText(fonte, "A");
        alturaLinha = medidor.height;

        fonte.getData().setScale(1.0f);
    }

    @Override
    public void desenhar(SpriteBatch pincel, float delta, float paiX, float paiY) {
        if(largura <= 0 || altura <= 0) return; // se não tem tamanho, não desenha pra não bugar

        float escalaOriginalX = fonte.getData().scaleX;
        float escalaOriginalY = fonte.getData().scaleY;

        // 1. reinicia a escala pra medir o tamanho "real/bruto" da fonte no arquivo
        fonte.getData().setScale(1.0f);

        // mede a linha mais larga (largura do bloco)
        float larguraBloco = 0;
        float alturaUmaLinha = 0;
        for(int i = 0; i < linhas.size; i++) {
            medidor.setText(fonte, linhas.get(i));
            if(medidor.width > larguraBloco) larguraBloco = medidor.width;
            if(i == 0) alturaUmaLinha = medidor.height;
        }
        // altura total do bloco: linhas * espaçamento, menos o espaço extra da ultima linha
        float alturaBloco = alturaUmaLinha * espacoLinha * (linhas.size - 1) + alturaUmaLinha;

        // 2. calculo da escala necessaria(regra de 3)
        // texto ocupe no máximo 80% da largura/altura do componente pra não colar nas bordas
        float margem = 0.8f;
        float escalaX = (largura * margem) / larguraBloco;
        float escalaY = (altura * margem) / alturaBloco;

        // usa a menor escala para o texto não ficar deformado(esticado)
        float escalaFinal = Math.min(escalaX, escalaY);

        // se a escala calculada for maior que escalaMaxima, trava na maxima
        if(escalaFinal > escala) escalaFinal = escala;

        // 3. aplica a escala calculada matematicamente e remede com ela
        fonte.getData().setScale(escalaFinal);
        alturaUmaLinha = 0;
        larguraBloco = 0;
        for(int i = 0; i < linhas.size; i++) {
            medidor.setText(fonte, linhas.get(i));
            if(medidor.width > larguraBloco) larguraBloco = medidor.width;
            if(i == 0) alturaUmaLinha = medidor.height;
        }
        alturaBloco = alturaUmaLinha * espacoLinha * (linhas.size - 1) + alturaUmaLinha;

        // 4. posicionamento centralizado do bloco inteiro
        final float blocoTopoY = paiY + y + (altura / 2) + (alturaBloco / 2);

        for(int i = 0; i < linhas.size; i++) {
            medidor.setText(fonte, linhas.get(i));
            float posX = paiX + x + (largura / 2) - (medidor.width / 2);
            float posY = blocoTopoY - (alturaUmaLinha * espacoLinha * i);
            fonte.draw(pincel, linhas.get(i), posX, posY);
        }
        // 5. restaura a escala que estava antes
        fonte.getData().setScale(escalaOriginalX, escalaOriginalY);
    }

	@Override
	public void liberar() {
		super.liberar();
		linhas.clear();
	}
}
