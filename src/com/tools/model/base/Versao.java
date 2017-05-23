package com.tools.model.base;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Versao {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long idVersao;
	
	@Column(nullable=false)
	private String numeroVersao;

	public Long getId() {
		return idVersao;
	}

	public void setId(Long id) {
		this.idVersao = id;
	}

	public String getVersao() {
		return numeroVersao;
	}

	public void setVersao(String versao) {
		this.numeroVersao = versao;
	}
	

}
