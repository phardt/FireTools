package com.tools.conexao;

import com.tools.geral.ConfigGeral;

public class ConfigFirebird {
	
	public static final String nameOrigem	= "base_versao.ECO";
	public static final String nameDestino 	= "BASEEXTRACT.eco";
	public static final String nameEco 		= "ECODADOS.ECO";
	
	
	public static final String caminhoOrigem 	= ConfigGeral.path+"/"+nameOrigem;
	public static final String caminhoDestino 	= ConfigGeral.path+"/"+nameDestino;	
	public static final String caminhoEco 		= ConfigGeral.path+"/"+nameEco;

}
