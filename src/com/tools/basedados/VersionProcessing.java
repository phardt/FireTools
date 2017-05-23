package com.tools.basedados;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.tools.conexao.GenericDao;
import com.tools.model.base.ScriptBanco;
import com.tools.model.base.TipoScript;
import com.tools.model.base.Versao;

public class VersionProcessing {
	
	
	public void readStructure(Versao versao){
		
		try {
			
			BufferedReader br = new BufferedReader(new FileReader("C:/baseCorrompida/correcao/meta/meta.sql"));			
			
			StringBuffer linha;			
			StringBuffer blocoCodigo = new StringBuffer();
			
			TipoScript tipoScript = TipoScript.INDEFINIDO;
			
			List<String> lstLinhas = new ArrayList<String>();
			
			StringBuffer tableName = null;
			
			while(br.ready()){							
				lstLinhas.add(br.readLine());
			}
			
			
			
			for (int i = 0; i < lstLinhas.size(); i++) {
				
				linha = new StringBuffer(lstLinhas.get(i));				
					
				if(linha.toString().contains("User defined functions (UDF)")){
					tipoScript = TipoScript.UDF;
					i++;
					i++;
					linha = new StringBuffer(lstLinhas.get(i));
				} else if(linha.toString().contains("/****                              Exceptions                              ****/")){
					tipoScript = TipoScript.EXCEPTIONS;
					i++;
					i++;
					linha = new StringBuffer(lstLinhas.get(i));
					blocoCodigo = new StringBuffer();
				} else if(linha.toString().contains("/****                          Stored procedures                           ****/")){
					tipoScript = TipoScript.PROCEDURES;
					i++;
					i++;
					linha = new StringBuffer(lstLinhas.get(i));
					blocoCodigo = new StringBuffer();
				} else if(linha.toString().contains("/****                                Tables                                ****/")){
					tipoScript = TipoScript.TABLES;
					i++;
					i++;
					linha = new StringBuffer(lstLinhas.get(i));
					blocoCodigo = new StringBuffer();
				} else if(linha.toString().contains("/****                                Views                                 ****/")){
					tipoScript = TipoScript.VIEWS;
					i++;
					i++;
					linha = new StringBuffer(lstLinhas.get(i));
					blocoCodigo = new StringBuffer();
				} else if(linha.toString().contains("/****                          Unique constraints                          ****/")){
					tipoScript = TipoScript.UNIQUE_CONTRAINS;
					i++;
					i++;
					linha = new StringBuffer(lstLinhas.get(i));
					blocoCodigo = new StringBuffer();
				} else if(linha.toString().contains("/****                             Primary keys                             ****/")){
					tipoScript = TipoScript.PRIMARY_KEYS;
					i++;
					i++;
					linha = new StringBuffer(lstLinhas.get(i));
					blocoCodigo = new StringBuffer();
				} else if(linha.toString().contains("/****                             Foreign keys                             ****/")){
					tipoScript = TipoScript.FOREING_KEYS;
					i++;
					i++;
					linha = new StringBuffer(lstLinhas.get(i));
					blocoCodigo = new StringBuffer();
				} else if(linha.toString().contains("/****                               Indices                                ****/")){
					tipoScript = TipoScript.INDICES;
					i++;
					i++;
					linha = new StringBuffer(lstLinhas.get(i));
					blocoCodigo = new StringBuffer();
				} else if(linha.toString().contains("/****                         Triggers for tables                          ****/")){
					tipoScript = TipoScript.TRIGUERS;
					i++;
					i++;
					linha = new StringBuffer(lstLinhas.get(i));
					blocoCodigo = new StringBuffer();
				} else if(linha.toString().contains("/****                          Stored procedures                           ****/")){
					tipoScript = TipoScript.STORED_PROCEDURE;
					i++;
					i++;
					linha = new StringBuffer(lstLinhas.get(i));
					blocoCodigo = new StringBuffer();
				} else if(linha.toString().contains("/****                         Fields descriptions                          ****/")){
					tipoScript = TipoScript.INDEFINIDO;
					i++;
					i++;
					linha = new StringBuffer(lstLinhas.get(i));
					blocoCodigo = new StringBuffer();
				}							
				
				
				if(linha.toString().trim() == ""){
					continue;
				}
				
				
				if( 	   (tipoScript == TipoScript.UDF) 
						|| (tipoScript == TipoScript.EXCEPTIONS) 
						|| (tipoScript == TipoScript.UNIQUE_CONTRAINS)
						|| (tipoScript == TipoScript.PRIMARY_KEYS)
						|| (tipoScript == TipoScript.FOREING_KEYS)
						|| (tipoScript == TipoScript.FIELDS_DESCRIPTIONS)
				  ){
					
					if(linha.toString().contains(";")){
						blocoCodigo.append(" "+linha.toString());
						ScriptBanco sb = new ScriptBanco();
						sb.setTipoScript(tipoScript);
						sb.setScript(blocoCodigo.toString());
						sb.setVersao(versao);
						new GenericDao().save(sb);
						blocoCodigo = new StringBuffer();
						continue;
					}
					
					blocoCodigo.append(linha.toString() + " ");
					blocoCodigo.append(System.lineSeparator());
					
				} else if( 	(tipoScript == TipoScript.PROCEDURES) || 
							(tipoScript == TipoScript.STORED_PROCEDURE	) ||
							(tipoScript == TipoScript.TRIGUERS) 
						 ){
					
					
					if(linha.toString().trim().toUpperCase().equals("END;")){
						blocoCodigo.append(" "+linha.toString());
						ScriptBanco sb = new ScriptBanco();
						sb.setTipoScript(tipoScript);
						sb.setScript(blocoCodigo.toString());
						sb.setVersao(versao);
						new GenericDao().save(sb);
						blocoCodigo = new StringBuffer();
						continue;
					}
					
					blocoCodigo.append(linha.toString() + " ");
					blocoCodigo.append(System.lineSeparator());
					
				} else if(tipoScript == TipoScript.TABLES){
					
					if(linha.toString().contains("CREATE TABLE")){
						tableName = new StringBuffer(linha.toString());
						tableName.delete(0, 13);
						tableName.delete(tableName.indexOf(" "), tableName.length());
					}
					
					if(linha.toString().trim().toUpperCase().equals(");")){
						blocoCodigo.append(" "+linha.toString());
						ScriptBanco sb = new ScriptBanco();
						sb.setTipoScript(tipoScript);
						sb.setScript(blocoCodigo.toString());
						sb.setVersao(versao);
						sb.setTabela(tableName.toString());
						new GenericDao().save(sb);
						blocoCodigo = new StringBuffer();
						continue;
					}
					
					blocoCodigo.append(linha.toString() + " ");
					blocoCodigo.append(System.lineSeparator());
					
				} else if(tipoScript == TipoScript.VIEWS){
					
					
					if(linha.toString().trim().toUpperCase().equals(";")){
						blocoCodigo.append(" "+linha.toString());
						ScriptBanco sb = new ScriptBanco();
						sb.setTipoScript(tipoScript);
						sb.setScript(blocoCodigo.toString());
						sb.setVersao(versao);
						new GenericDao().save(sb);
						blocoCodigo = new StringBuffer();
						continue;
					}
					
					blocoCodigo.append(linha.toString() + " ");
					blocoCodigo.append(System.lineSeparator());
					
				}				
				
			}			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	

}
