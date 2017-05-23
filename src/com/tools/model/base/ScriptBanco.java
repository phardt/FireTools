package com.tools.model.base;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class ScriptBanco {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long idTabela;
	
	private String tabela;
	
	@Column(columnDefinition="TEXT")
	private String script;
	
	
	private TipoScript tipoScript;
	
	
	@ManyToOne
	@JoinColumn(name="idVersao")
	private Versao versao;
	

	public TipoScript getTipoScript() {
		return tipoScript;
	}


	public void setTipoScript(TipoScript tipoScript) {
		this.tipoScript = tipoScript;
	}


	public Long getIdTabela() {
		return idTabela;
	}


	public void setIdTabela(Long idTabela) {
		this.idTabela = idTabela;
	}


	public String getTabela() {
		return tabela;
	}


	public void setTabela(String tabela) {
		this.tabela = tabela;
	}


	public String getScript() {
		return script;
	}


	public void setScript(String script) {
		this.script = script;
	}


	public Versao getVersao() {
		return versao;
	}


	public void setVersao(Versao versao) {
		this.versao = versao;
	}
	
	
	

}
