package com.uniditor.libgdx.graficos;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.HdpiUtils;

import com.uniditor.nucleo.graficos.Renderizador;
import com.uniditor.nucleo.sintaxe.Token;

import java.io.File;
import java.util.ArrayDeque;

public class Canvas implements Renderizador {
    public SpriteBatch pincel;
    public BitmapFont fonte;
    public Texture pixel;
    public GlyphLayout tela;

    // translação acumulada(rolamento)
    public float transX = 0f, transY = 0f;
    public float origemX = 0f, origemY = 0f;

    public ArrayDeque<Rectangle> pilhaRecorte = new ArrayDeque<Rectangle>();

    public int TAM_TAB = 4;
    public int corTextoAtual;

    public Canvas(SpriteBatch pincel, BitmapFont fonte) {
        this.pincel = pincel;
        this.fonte = fonte;
        this.tela = new GlyphLayout();
        this.pause = true;

        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(Color.WHITE);
        pm.fill();
        pixel = new Texture(pm);
        pm.dispose();
    }

    @Override
    public void defAPI(Object renderizacao) {
        if(renderizacao instanceof SpriteBatch) {
            pincel = (SpriteBatch) renderizacao;
            pause = false;
        }
    }

    @Override
    public void ajustar(int h, int v) {
        this.largura = h;
        this.altura = v;
    }

    @Override
    public void liberar() {
        pixel.dispose();
        pilhaRecorte.clear();
    }

    @Override
    public void iniciarQuadro() {
        transX = origemX;
        transY = origemY;
        pilhaRecorte.clear();
    }

    @Override
    public void fimQuadro() {

    }

    @Override
    public void limpar(int cor) {
        renderRetangulo(0, 0, largura, altura, cor);
    }

    @Override
    public void defFonte(File arquivo) {

    }

    @Override
    public void defTextoTam(float tam) {
        // BitmapFont tem tamanho fixo; usa setScale se necessario
        float escala = tam / fonte.getCapHeight();
        fonte.getData().setScale(escala);
    }

    @Override
    public void defTabTam(int espacos) {
        TAM_TAB = espacos;
    }

    @Override
    public void defCorTexto(int cor) {
        corTextoAtual = cor;
        float r = ((cor >> 16) & 0xFF) / 255f;
        float g = ((cor >> 8)  & 0xFF) / 255f;
        float b = (cor & 0xFF) / 255f;
        fonte.setColor(r, g, b, 1f);
    }

    @Override
    public void renderTexto(String texto, float x, float y) {
        if(texto.indexOf('\t') < 0) {
            // libgdx y=linhaBase; converte de topo pra linhaBase
            fonte.draw(pincel, texto, transX + x, transY + (altura - y) + ascente());
            return;
        }
        float largTab = larguraEspaco() * TAM_TAB;
        float ox = x;
        int inicio = 0;
        for(int i = 0; i <= texto.length(); i++) {
            if(i == texto.length() || texto.charAt(i) == '\t') {
                if(i > inicio) {
                    String parte = texto.substring(inicio, i);
                    fonte.draw(pincel, parte, transX + ox, transY + (altura - y) + ascente());
                    ox += larguraTexto(parte);
                }
                if(i < texto.length()) ox += largTab;
                inicio = i + 1;
            }
        }
    }

    @Override
    public void renderTextoCor(String texto, Token[] cores, float x, float y) {
        if(cores == null || cores.length == 0) {
            renderTexto(texto, x, y);
            return;
        }
        int corOriginal = corTextoAtual;
        int pos = 0;
        for(Token token : cores) {
            if(token.inicio < token.fim && token.fim <= texto.length()) {
                defCorTexto(token.cor);
                float ox = x + larguraTexto(texto.substring(0, token.inicio));
                renderTexto(texto.substring(token.inicio, token.fim), ox, y);
                pos = token.fim;
            }
        }
        if(pos < texto.length()) {
            defCorTexto(corOriginal);
            float ox = x + larguraTexto(texto.substring(0, pos));
            renderTexto(texto.substring(pos), ox, y);
        }
        defCorTexto(corOriginal);
    }

    @Override
    public void renderRetangulo(float x, float y, float larguraR, float alturaR, int cor) {
        float r = ((cor >> 16) & 0xFF) / 255f;
        float g = ((cor >> 8)  & 0xFF) / 255f;
        float b = (cor & 0xFF) / 255f;
        float a = ((cor >> 24) & 0xFF) / 255f;
        if(a == 0f) a = 1f; // cor sem alpha explicito = opaco
        pincel.setColor(r, g, b, a);
        // LibGDX y=base; converte topo pra base
        pincel.draw(pixel, transX + x, transY + (altura - y - alturaR), larguraR, alturaR);
        pincel.setColor(Color.WHITE);
    }

    @Override
    public void renderRetanguloContorno(float x, float y, float larguraR, float alturaR, int cor, float espessura) {
        // 4 bordas como retangulos finos
        renderRetangulo(x, y, larguraR, espessura, cor);
        renderRetangulo(x, y + alturaR - espessura, larguraR, espessura, cor);
        renderRetangulo(x, y, espessura, alturaR, cor);
        renderRetangulo(x + larguraR - espessura, y, espessura, alturaR, cor);
    }

    @Override
    public float larguraTexto(String texto) {
        if(texto.isEmpty()) return 0f;
        if(texto.indexOf('\t') < 0) {
            tela.setText(fonte, texto);
            return tela.width;
        }
        float total = 0f;
        float largTab = larguraEspaco() * TAM_TAB;
        for(int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            if(c == '\t') total += largTab;
            else total += larguraCaractere(c);
        }
        return total;
    }

    @Override
    public float larguraCaractere(char c) {
        if(c == '\t') return larguraEspaco() * TAM_TAB;
        tela.setText(fonte, String.valueOf(c));
        return tela.width;
    }

    @Override
    public float alturaLinha() {
        return fonte.getLineHeight();
    }

    @Override
    public float ascente() {
        return fonte.getCapHeight();
    }

    @Override
    public float descente() {
        return fonte.getDescent();
    }

    @Override
    public void addRecorte(float x, float y, float larguraR, float alturaR) {
        Rectangle r = new Rectangle(transX + x, transY + (altura - y - alturaR), larguraR, alturaR);
        pilhaRecorte.push(r);
        HdpiUtils.glScissor((int)r.x, (int)r.y, (int)r.width, (int)r.height);
        Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);
    }

    @Override
    public void subRecorte() {
        if(!pilhaRecorte.isEmpty()) pilhaRecorte.pop();
        if(pilhaRecorte.isEmpty()) {
            Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);
        } else {
            Rectangle r = pilhaRecorte.peek();
            HdpiUtils.glScissor((int)r.x, (int)r.y, (int)r.width, (int)r.height);
        }
    }

    @Override
    public void defPos(float dx, float dy) {
        transX += dx;
        transY += dy;
    }

    @Override
    public int larguraTela() {
        return largura;
    }

    @Override
    public int alturaTela() {
        return altura;
    }

    public float larguraEspaco() {
        tela.setText(fonte, " ");
        return tela.width;
    }
}
