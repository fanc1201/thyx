package com.allcom.security.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springside.modules.utils.ReflectionUtils;

/**
 * 权限.
 * 
 * @author calvin
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "SS_AUTHORITY")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Authority implements java.io.Serializable {
	private Long id;
	private String name;
	private String displayName;
	private String memo;

	private List<Resource> resourceList = new ArrayList<Resource>(); // 有序的关联对象集合.

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "Authority_Gen")
	@TableGenerator(name = "Authority_Gen", table = "SS_PK", pkColumnName = "name", valueColumnName = "value", pkColumnValue = "Authority_Key", allocationSize = 1)
	@Column(name = "AUTHORITY_ID", unique = true, nullable = false, precision = 22, scale = 0)
	public Long getId() {
		return this.id;
	}

	public void setId(Long authorityId) {
		this.id = authorityId;
	}

	@Column(name = "NAME", nullable = false, length = 64)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "DISPLAY_NAME", length = 256)
	public String getDisplayName() {
		return this.displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Column(name = "MEMO", length = 256)
	public String getMemo() {
		return this.memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	// 多对多定义
	@ManyToMany
	// 中间表定义,表名采用默认命名规则
	@JoinTable(name = "SS_RESOURCE_AUTHORITY", joinColumns = { @JoinColumn(name = "AUTHORITY_ID") }, inverseJoinColumns = { @JoinColumn(name = "RESOURCE_ID") })
	// Fecth策略定义
	@Fetch(FetchMode.SUBSELECT)
	// 集合按id排序.
	@OrderBy("id")
	// 集合中对象id的缓存.
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	public List<Resource> getResourceList() {
		return resourceList;
	}

	public void setResourceList(List<Resource> resourceList) {
		this.resourceList = resourceList;
	}

	// 非持久化属性.
	@Transient
	public String getResourceNames() {
		return ReflectionUtils.fetchElementPropertyToString(resourceList, "name", ",");
	}

	// 非持久化属性.
	@Transient
	@SuppressWarnings("unchecked")
	public List<Long> getResourceIds() {
		return ReflectionUtils.fetchElementPropertyToList(resourceList, "id");
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
