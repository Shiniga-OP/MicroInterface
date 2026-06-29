package com.uniditor.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.uniditor.libgdx.graficos.Canvas;
import com.uniditor.nucleo.Editor;
import com.uniditor.nucleo.entradas.Teclado;
import com.micro.componentes.CampoTexto;

public class EditorLibgdxCanvas extends CampoTexto {
    public Editor editor;
    public Canvas canvas;

    // inercia de rolamento
    public float veloRolamento = 0f;
    public boolean arrastando = false;
    public float toqueInicioX, toqueInicioY;
    public float ultimoToqueY;

    public static final float LIMIAR_DRAG = 8f;
    public static final float FATOR_INERCIA = 0.92f;
    public static final float LIMIAR_PARAR = 0.5f;

    public EditorLibgdxCanvas(float x, float y, float largura, float altura, Editor editor, Canvas canvas) {
        super(x, y, largura, altura);
        this.editor = editor;
        this.canvas = canvas;

        editor.render = canvas;
        editor.entrada.teclado = new Teclado() {
            @Override
            public void abrirTeclado() {
                Gdx.input.setOnscreenKeyboardVisible(true);
            }

			@Override
            public void fecharTeclado() {
                Gdx.input.setOnscreenKeyboardVisible(false);
            }
        };
    }

    @Override
    public boolean capturaArraste() {
        return true;
    }

    @Override
    public boolean aoTocar(float toqueX, float toqueY, boolean pressionado) {
		if(contem(toqueX, toqueY) && !pressionado) {
			emFoco = true;
			defFoco(true);
			if(!arrastando) editor.aoTocar(toqueX - x, toqueY - y);
			arrastando = false;
			return true;
		}
        if(pressionado) {
            float dy = Math.abs(toqueY - toqueInicioY);
            float dx = Math.abs(toqueX - toqueInicioX);

            if(!arrastando && (dx > LIMIAR_DRAG || dy > LIMIAR_DRAG)) {
                arrastando = true;
                veloRolamento = 0f;
            }
            if(arrastando) {
                float delta = ultimoToqueY - toqueY;
                aplicarRolamento(delta);
                veloRolamento = veloRolamento * 0.6f + delta * 0.4f;
                ultimoToqueY = toqueY;
            } else {
                toqueInicioX = toqueX;
                toqueInicioY = toqueY;
                ultimoToqueY = toqueY;
            }
        } else {
            if(arrastando) {
                arrastando = false;
                // inercia começa a ser aplicada no att()
            } else {
                editor.aoTocar(toqueX - x, toqueY - y);
				editor.entrada.teclado.abrirTeclado();
            }
        }
        return false;
    }

    public void aplicarRolamento(float delta) {
        editor.rolamentoY += delta;
        float altTotal = editor.render.alturaLinha() * editor.buffer.totalLinhas() + editor.ESPACO_TOPO;
        float maxRolamento = Math.max(0, altTotal - editor.render.altura);
        editor.rolamentoY = Math.max(0, Math.min(editor.rolamentoY, maxRolamento));
    }

	@Override
    public boolean processarTecla(int tecla) {
        switch(tecla) {
            case Input.Keys.UP:
                editor.cursor.mover(-1, 0, editor.buffer);
				return true;
            case Input.Keys.DOWN:
                editor.cursor.mover(1, 0, editor.buffer);
				return true;
            case Input.Keys.LEFT:
                editor.cursor.mover(0, -1, editor.buffer);
				return true;
            case Input.Keys.RIGHT:
                editor.cursor.mover(0, 1, editor.buffer);
				return true;
            case Input.Keys.FORWARD_DEL:
                editor.entrada.rmDepois();
				return true;
			case Input.Keys.BACKSPACE:
				editor.entrada.rmAntes();
				return true;
            default:
                return false;
        }
    }

	@Override
	public boolean processarCaractere(char c) {
		if(!emFoco) return false;
		if(c == '\n' || c == '\r') {
			editor.entrada.aoDigitar("\n");
			return true;
		}
		if(c < 32 || c == 127) return false;
		editor.entrada.aoDigitar(String.valueOf(c));
		return true;
	}

    @Override
    public void desenhar(SpriteBatch pincel, float delta, float paiX, float paiY) {
        // aplica inercia
        if(!arrastando && Math.abs(veloRolamento) >= LIMIAR_PARAR) {
            aplicarRolamento(veloRolamento);
            veloRolamento *= FATOR_INERCIA;
        } else if(!arrastando) {
            veloRolamento = 0f;
        }
        canvas.ajustar((int)largura, (int)altura);
        canvas.origemX = x + paiX;
        canvas.origemY = y + paiY;
        canvas.pincel = pincel;
        canvas.pause = false;
        editor.att();
    }

    @Override
    public void liberar() {
        super.liberar();
        canvas.liberar();
        editor.liberar();
    }
}
