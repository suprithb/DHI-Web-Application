package com.brillio.dhi.dao.entity;

// Generated Mar 11, 2017 11:20:18 PM by Hibernate Tools 4.0.0

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * UserProfile generated by hbm2java
 */
@Entity
@Table(name = "Font")
public class FontEntity implements java.io.Serializable {

	private int id;
	private String family;
	private String category;;

	public FontEntity() {
	}
	
	

	public FontEntity(String family, String category) {
		super();
		this.family = family;
		this.category = category;
	}



	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", unique = true, nullable = false, length=20)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "family",  nullable = false)
	public String getFamily() {
		return family;
	}

	public void setFamily(String family) {
		this.family = family;
	}

	@Column(name = "category",  nullable = false)
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	
	
	
}
