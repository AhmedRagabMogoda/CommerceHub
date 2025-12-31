package com.commercehub.entity;

import java.util.Date;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Setter
@Getter
@EntityListeners({AuditingEntityListener.class})
public abstract class BaseEntity {
	
	private String statusCode;
	
	private boolean isDeleted;
	
	@CreatedBy
	@Column(length=50, updatable=false, nullable=false)
	private String createdBy;
	
	@CreatedDate
	@Column(length=50, updatable=false, nullable=false)
	private Date createdAt;
	
	@LastModifiedBy
	@Column(length=50, nullable=false)
	private String lastModifiedBy;
	
	@LastModifiedDate
	@Column(length=50, nullable=false)
	private Date lastModifiedAt;

}
