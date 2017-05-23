package com.tools.main;

import java.util.Iterator;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tools.basedados.BaseProcessing;
import com.tools.basedados.VersionProcessing;
import com.tools.conexao.GenericDao;
import com.tools.conexao.HibernateUtils;
import com.tools.model.base.Versao;

public class Main {

	public static void main(String[] args) {
		
		
		Versao v = new Versao();
		v.setVersao("14350000");
//		new GenericDao().save(v);
//		new ProcessaVersao().readStructure(v);		
		
		BaseProcessing pb = new BaseProcessing();
		
		if(!pb.makeTabelas(v)){
			System.out.println("ERRO AO CRIAR ESTRUTURA");
			System.exit(0);
		}		
		
		if(!pb.copyData(v)){
			System.out.println("ERRO AO COPIAR DADOS");
			System.exit(0);
		}
		
		if(!pb.copyBaseToBackup()){
			System.out.println("ERRO AO COPIAR BASEEXTRACT.ECO PARA BACKUP");
			System.exit(0);
		}
		
		if(!pb.getAndSetGenerators()){
			System.out.println("ERRO AO CRIAR GENERATORS");
			System.exit(0);
		}
		
		if(!pb.makeMetaData(v)){
			System.out.println("ERRO AO CRIAR METADADOS");
			System.exit(0);
		}
		
		System.exit(0);
//		

		
	}

}
