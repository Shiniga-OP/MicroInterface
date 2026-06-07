package com.micro;

public class Versao {
    public static final int INICIO = 0x01,
	LISTAS = 0x02;
    
    public static int atual() {
        return LISTAS;
    }
	
	public static String formatar(int versao) {
		switch(versao) {
			case 0x01: return "0.0.1";
			case 0x02: return "0.0.2";
			default: return "Desconhecida";
		}
	}
}
