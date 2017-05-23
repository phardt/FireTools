package com.tools.conexao;

import org.hibernate.Session;

import org.hibernate.Transaction;

public class GenericDao {
	
	
	private Session session;
	
	public Object save(Object obj){
		this.getSession();
		Transaction trans = this.session.beginTransaction();		
		this.session.save(obj);		
		trans.commit();		
		return obj;
	}
	
	
	private void getSession(){
		
		if(this.session == null){
			this.session = HibernateUtils.getSession();
		}
	}

}
