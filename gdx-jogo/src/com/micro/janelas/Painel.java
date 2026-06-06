package com.micro.janelas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.micro.componentes.Componente;
import com.micro.util.Ancora;

public class Painel extends Componente {
    // propriedades visuais
    public PainelFatiado visual;
    public float escala = 1.0f;
    public boolean temFundo;
    public Color corFundo = Color.WHITE;

    // propriedades de espaço
    public float espacoEsquerda = 0, espacoDireita = 0, espacoSuperior = 0, espacoInferior = 0;
    protected Componente filhoCapturado = null;

    // propriedades de rolagem
    public boolean rolavel = false;
    public float deslocamentoY = 0;
    public float alturaConteudo = 0;
    public float veloRolagem = 20f;

    public boolean mostrarBarra = true;
    public float larguraBarra = 24f;
    public float margemBarra = 4f;
    public Color corBarra = new Color(0.6f, 0.6f, 0.6f, 0.9f);
    public Color corBarraArrastando = new Color(0.8f, 0.8f, 0.8f, 1f);
    public Color corFundoBarra = new Color(0.2f, 0.2f, 0.2f, 0.5f);

    public boolean arrastandoBarra = false;
    public float toqueInicialY = 0;
    public float deslocamentoInicialY = 0;
    public Texture pixelBranco; // passado se o painel for rolavel para desenhar a barra

    // construtor 1: painel classico/janela(Com bordas texturizadas)
    public Painel(PainelFatiado visual, float x, float y, float largura, float altura, float escala) {
        super(x, y, largura, altura);
        this.visual = visual;
        this.escala = escala;
        this.temFundo = true;
    }

    // construtor 2: painel Transparente
    public Painel(float x, float y, float largura, float altura) {
        super(x, y, largura, altura);
        this.temFundo = false;
    }

    // ativador de comportamento de rolagem(transforma o Painel em "PainelRolavel")
    public Painel tornarRolavel(Texture pixelBranco) {
        this.rolavel = true;
        this.pixelBranco = pixelBranco;
        return this;
    }

    public void defEspaco(float todos) {
        this.espacoEsquerda = todos;
		this.espacoDireita = todos;
        this.espacoSuperior = todos;
		this.espacoInferior = todos;
    }

    public void addAncorado(Componente filho, Ancora ancoragem, float margemX, float margemY) {
        final float larguraDisponivel = largura - espacoEsquerda - espacoDireita;
        final float alturaDisponivel = altura - espacoSuperior - espacoInferior;

        filho.x = ancoragem.calcularX(larguraDisponivel, filho.largura, margemX) + espacoEsquerda;
        filho.y = ancoragem.calcularY(alturaDisponivel, filho.altura, margemY) + espacoInferior;
        filhos.add(filho);

        if(rolavel) calcularAlturaConteudo();
    }

    public void calcularAlturaConteudo() {
        float maxY = 0;
        for(Componente filho : filhos) {
            final float topoFilho = filho.y + filho.altura;
            if(topoFilho > maxY) maxY = topoFilho;
        }
        this.alturaConteudo = maxY + espacoSuperior + espacoInferior;
    }

    public boolean precisaRolagem() {
        return rolavel && (alturaConteudo > altura);
    }

    @Override
    public boolean aoTocar(float toqueX, float toqueY, boolean pressionado) {
        if(!pressionado) {
            filhoCapturado = null;
            arrastandoBarra = false;
        }
        final float relX = toqueX - x;
        final float relY = toqueY - y;

        // logica de interação com a barra de rolagem
        if(precisaRolagem() && mostrarBarra) {
            final float xBarra = largura - larguraBarra - margemBarra;
            if(relX >= xBarra && relX <= xBarra + larguraBarra && relY >= 0 && relY <= altura) {
                if(pressionado) {
                    arrastandoBarra = true;
                    toqueInicialY = toqueY;
                    deslocamentoInicialY = deslocamentoY;
                }
                return true;
            }
        }
        // se estiver arrastando a barra, calcula o rolamento
        if(arrastandoBarra && pressionado) {
            final float deltaToqueY = toqueY - toqueInicialY;
            final float alturaAreaBarra = altura - margemBarra * 2;
            final float alturaArrasto = (altura / alturaConteudo) * alturaAreaBarra;
            final float espacoDisponivelBarra = alturaAreaBarra - alturaArrasto;

            if(espacoDisponivelBarra > 0) {
                final float porcentagem = deltaToqueY / espacoDisponivelBarra;
                final float maxDeslocamento = alturaConteudo - altura;
                deslocamentoY = deslocamentoInicialY + (porcentagem * maxDeslocamento);
                if(deslocamentoY < 0) deslocamentoY = 0;
                if(deslocamentoY > maxDeslocamento) deslocamentoY = maxDeslocamento;
            }
            return true;
        }
        // repassa o toque para os filhos(aplicando o pos de rolagem se necessario)
        final float ajusteY = rolavel ? deslocamentoY : 0;
        for(int i = filhos.size() - 1; i >= 0; i--) {
            final Componente filho = filhos.get(i);
            if(filho.aoTocar(relX, relY + ajusteY, pressionado)) {
                if(pressionado) filhoCapturado = filho;
                return true;
            }
        }
        return contem(toqueX, toqueY);
    }

    @Override
    public boolean capturaArraste() {
        return arrastandoBarra || (filhoCapturado != null && filhoCapturado.capturaArraste());
    }

    @Override
    public void desenhar(SpriteBatch pincel, float delta, float paiX, float paiY) {
        final float desenharX = paiX + x;
        final float desenharY = paiY + y;

        // 1. desenha o fundo/janela se houver
        if(temFundo && visual != null) {
            final Color corOriginal = pincel.getColor();
            pincel.setColor(corFundo);
            visual.desenhar(pincel, desenharX, desenharY, largura, altura, escala);
            pincel.setColor(corOriginal);
        }
        // 2. renderiza filhos(com ou sem recorte de rolagem)
        if(precisaRolagem()) {
            pincel.flush();
            // seu calculo matematico customizado nativo do glScissor:
            final float sX = desenharX;
            final float sY = desenharY;
            final float sLargura = largura;
            final float sAltura = altura;

            // transforma coordenadas para a tela
            Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);
            Gdx.gl.glScissor((int)sX, (int)sY, (int)sLargura, (int)sAltura);

            for(Componente filho : filhos) {
                filho.desenhar(pincel, delta, desenharX, desenharY - deslocamentoY);
            }
            pincel.flush();
            Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);

            // 3. desenha a barra de rolagem por cima
            if(mostrarBarra && pixelBranco != null) {
                final float xBarra = desenharX + largura - larguraBarra - margemBarra;
                final float yBarra = desenharY + margemBarra;
                final float alturaAreaBarra = altura - margemBarra * 2;

                pincel.setColor(corFundoBarra);
                pincel.draw(pixelBranco, xBarra, yBarra, larguraBarra, alturaAreaBarra);

                final float alturaArrasto = (altura / alturaConteudo) * alturaAreaBarra;
                final float maxDeslocamento = alturaConteudo - altura;
                final float barraY = yBarra + (alturaAreaBarra - alturaArrasto) * (deslocamentoY / maxDeslocamento);

                pincel.setColor(arrastandoBarra ? corBarraArrastando : corBarra);
                pincel.draw(pixelBranco, xBarra, barraY, larguraBarra, alturaArrasto);
            }
        } else {
            // desenho normal sem rolagem ativa
            desenharFilhos(pincel, delta, desenharX, desenharY);
        }
    }
}
