package com.micro.util;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import com.micro.componentes.Componente;
import com.micro.componentes.CaixaDialogo;
import com.micro.componentes.CampoTexto;
import com.micro.janelas.Painel;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GerenciadorUI implements InputProcessor {
	// sistema de camadas, TreeMap ordena automaticamente por chave(numero da camada)
	public TreeMap<Integer, ArrayList<Componente>> camadas = new TreeMap<Integer, ArrayList<Componente>>();
	public ArrayList<CaixaDialogo> dialogos = new ArrayList<CaixaDialogo>();
	public static CampoTexto campoEmFoco;
	// camera da tela ativa, usada pelo Painel pra projetar coordenadas de mundo pra pixels de tela no glScissor
	public static OrthographicCamera camera;
	public static final Vector3 pontoAuxiliar = new Vector3();
	public static Viewport vista;
	public boolean usarCamera = true;
	public Componente componenteCapturado;
	// camadas padrão
	public static final int CAMADA_FUNDO = 0;
	public static final int CAMADA_PADRAO = 10;
	public static final int CAMADA_UI = 20;
	public static final int CAMADA_TOPO = 30;
	
	public GerenciadorUI() {
		this(true);
	}

	public GerenciadorUI(boolean usarCamera) {
		// tela:
		this.usarCamera = usarCamera;
		camera = new OrthographicCamera();
		vista = new ScreenViewport(camera);
        vista.apply(true);
		// inicia camadas padrão:
		componenteCapturado = null;
		camadas.put(CAMADA_FUNDO, new ArrayList<Componente>());
		camadas.put(CAMADA_PADRAO, new ArrayList<Componente>());
		camadas.put(CAMADA_UI, new ArrayList<Componente>());
		camadas.put(CAMADA_TOPO, new ArrayList<Componente>());
	}

	// adiciona na camada padrão
	public void add(Componente c) {
		if(c instanceof CaixaDialogo) {
			final CaixaDialogo dialogo = (CaixaDialogo)c;
			dialogos.add(dialogo);
			return;
		}
		addCamada(c, CAMADA_PADRAO);
	}

	// adiciona em camada especifica
	public void addCamada(Componente componente, int numeroCamada) {
		// cria a camada se não existir
		if(!camadas.containsKey(numeroCamada)) {
			camadas.put(numeroCamada, new ArrayList<Componente>());
		}
		camadas.get(numeroCamada).add(componente);
	}

	public void rm(Componente c) {
		if(c instanceof CaixaDialogo) {
			final CaixaDialogo dialogo = (CaixaDialogo)c;
			dialogos.remove(dialogo);
			return;
		}
		// remove de todas as camadas
		for(ArrayList<Componente> lista : camadas.values()) {
			lista.remove(c);
		}
	}

	public void limpar() {
		for(ArrayList<Componente> lista : camadas.values()) {
			lista.clear();
		}
		dialogos.clear();
		campoEmFoco = null;
		componenteCapturado = null;
	}

	public boolean processarToque(float x, float y, boolean pressionado) {
		// primeiro verifica dialogos(sempre no topo)
		for(int i = dialogos.size() - 1; i >= 0; i--) {
			final CaixaDialogo d = dialogos.get(i);
			if(d.ativa && d.aoTocar(x, y, pressionado)) return true;
		}
		// se ta soltando o toque(pressionado = false)
		if(!pressionado) {
			// se tem um componente capturado, envia o evento so pra ele
			if(componenteCapturado != null) {
				final boolean resultado = componenteCapturado.aoTocar(x, y, false);
				componenteCapturado = null;
				return resultado;
			}
			// se não tem componente capturado, processa normalmente
		}
		// processamento normal, processa camadas de cima para baixo
		final ArrayList<Integer> numsCamadas = new ArrayList<Integer>(camadas.keySet());
		for(int camadaIdc = numsCamadas.size() - 1; camadaIdc >= 0; camadaIdc--) {
			final int numCamada = numsCamadas.get(camadaIdc);
			final ArrayList<Componente> componentesCamada = camadas.get(numCamada);

			// dentro da camada, processa de tras pra frente
			for(int i = componentesCamada.size() - 1; i >= 0; i--) {
				final Componente c = componentesCamada.get(i);
				if(c.contem(x, y)) {
					if(c.aoTocar(x, y, pressionado)) {
						// so captura se ta pressionando E o componente precisa de arraste
						if(pressionado && c.capturaArraste()) {
							componenteCapturado = c;
						}
						return true;
					}
				}
			}
		}
		return false;
	}

	public void processarArraste(float x, float y) {
		// primeiro processa dialogos
		for(int i = dialogos.size() - 1; i >= 0; i--) {
			final CaixaDialogo dialogo = dialogos.get(i);
			if(dialogo.ativa) {
				dialogo.aoArrastar(x, y);
			}
		}
		// se tem um componente capturado que precisa de arraste, converte global->local e envia
		if(componenteCapturado != null) {
			float localX = x;
			float localY = y;
			if(componenteCapturado instanceof Painel) {
				final Painel p = (Painel)componenteCapturado;
				localX = x - p.ultimoPaiX;
				localY = y - p.ultimoPaiY;
			}
			componenteCapturado.aoTocar(localX, localY, true);
		}
	}

	public boolean processarTecla(int tecla) {
		if(campoEmFoco != null) {
			return campoEmFoco.processarTecla(tecla);
		}
		return false;
	}

	public boolean processarCaractere(char c) {
		if(campoEmFoco != null) {
			return campoEmFoco.processarCaractere(c);
		}
		return false;
	}

	public CaixaDialogo buscarDialogoEm(Componente c) {
		if(c instanceof CaixaDialogo) {
			final CaixaDialogo d = (CaixaDialogo) c;
			return d.ativa ? d : null;
		}
		if(c instanceof Painel) {
			final Painel painel = (Painel) c;
			for(Componente filho : painel.filhos) {
				final CaixaDialogo encontrada = buscarDialogoEm(filho);
				if(encontrada != null) return encontrada;
			}
		}
		return null;
	}

	public void desenhar(SpriteBatch pincel, float delta) {
		if(usarCamera) {
			camera.update();
			pincel.setProjectionMatrix(camera.combined);
		}
		// desenha camadas em ordem crescente(de baixo para cima)
		for(Integer numCamada : camadas.keySet()) {
			final ArrayList<Componente> componentesDaCamada = camadas.get(numCamada);
			for(int i = 0; i < componentesDaCamada.size(); i++) {
				componentesDaCamada.get(i).desenhar(pincel, delta, 0, 0);
			}
		}
		// desenha dialogos sempre por cima
		for(int i = 0; i < dialogos.size(); i++) {
			final CaixaDialogo dialogo = dialogos.get(i);
			if(dialogo.ativa) {
				dialogo.desenhar(pincel, delta, 0, 0);
			}
		}
	}

	public boolean temDialogoAtivo() {
		for(int i = 0; i < dialogos.size(); i++) {
			if(dialogos.get(i).ativa) {
				return true;
			}
		}
		return false;
	}

	public void liberar() {
		for(ArrayList<Componente> lista : camadas.values()) {
			for(Componente c : lista) c.liberar();
		}
		for(CaixaDialogo c : dialogos) c.liberar();
		if(componenteCapturado != null) componenteCapturado.liberar();
		if(campoEmFoco != null) campoEmFoco.liberar();
	}
	
	public void ajustar(int v, int h) {
		vista.update(v, h);
	}

	@Override
	public boolean keyDown(int p) {
		return processarTecla(p);
	}

	@Override
	public boolean keyTyped(char caractere) {
		return processarCaractere(caractere);
	}

	@Override
	public boolean touchDown(int x, int y, int p, int b) {
		camera.unproject(pontoAuxiliar.set(x, y, 0));
		return processarToque(pontoAuxiliar.x, pontoAuxiliar.y, true);
	}

	@Override
	public boolean touchUp(int x, int y, int p, int b) {
		camera.unproject(pontoAuxiliar.set(x, y, 0));
		return processarToque(pontoAuxiliar.x, pontoAuxiliar.y, false);
	}

	@Override
	public boolean touchDragged(int x, int y, int p) {
		camera.unproject(pontoAuxiliar.set(x, y, 0));
		processarArraste(pontoAuxiliar.x, pontoAuxiliar.y);
		return false;
	}
	@Override public boolean keyUp(int p) { return false; }
	@Override public boolean mouseMoved(int telaX, int telaY) { return false; }
	@Override public boolean scrolled(float telaX, float telaY) { return false; }
}
