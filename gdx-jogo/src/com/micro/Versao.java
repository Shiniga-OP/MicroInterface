package com.micro;

public class Versao {
    public static final int INICIO = 0x01;
	public static final int LISTAS = 0x02;
	public static final int OTIMI1 = 0x03;
    
    public static int atual() {
        return OTIMI1;
    }
	
	public static String formatar(int versao) {
		switch(versao) {
			case INICIO: return "0.0.1";
			case LISTAS: return "0.0.2";
			case OTIMI1: return "0.0.3";
			default: return "Desconhecida";
		}
	}
}
