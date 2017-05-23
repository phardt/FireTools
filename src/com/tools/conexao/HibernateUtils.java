package com.tools.conexao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtils {
	
	private static final SessionFactory sessionFactory = buildSessionFactory();
	
	private static SessionFactory buildSessionFactory(){
		
		try {
			
			ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();
			
			Metadata metadata = new MetadataSources(serviceRegistry).getMetadataBuilder().build();
			
			return metadata.getSessionFactoryBuilder().build();
			
		} catch (Exception e) {
			System.out.println(e.toString());
			throw new ExceptionInInitializerError(e);
		}
		
		
	}
	
	public static SessionFactory getSessionFactory(){
		return sessionFactory;
	}
	
	public static Session getSession(){
		return sessionFactory.openSession();
	}
	
	
	public static void shutdown(){
		getSessionFactory().close();
	}

}
