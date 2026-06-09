package com.micro;

public class Versao {
    public static final int INICIO = 0x01;
	public static final int LISTAS = 0x02;
	public static final int OTIMI1 = 0x03;
	public static final int BPeO = 0x04;
    
    public static int atual() {
        return BPeO;
    }
	
	public static String formatar(int versao) {
		switch(versao) {
			case INICIO: return "0.0.1";
			case LISTAS: return "0.0.2";
			case OTIMI1: return "0.0.3";
			case BPeO: return "0.0.4";
			default: return "Desconhecida";
		}
	}
}
