package com.allcom.security.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
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
 * 受保护的资源.
 * 
 * @author dw
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "SS_RESOURCE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Resource implements java.io.Serializable {
	// resourceType常量 //
	public static final String URL_TYPE = "url";
	public static final String MENU_TYPE = "menu";
	public static final String BUTTON_TYPE = "button";

	private Long id;
	private Resource parentResource;
	private String resourceType;
	private String name;
	private String url;
	private String postUrl;
	private String properties;
	private String nodeProperties;
	private int nodePosition;
	private String memo;

	@Transient
	private boolean checked = false;
	private List<Authority> authorityList = new ArrayList<Authority>();

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "Resource_Gen")
	@TableGenerator(name = "Resource_Gen", table = "SS_PK", pkColumnName = "name", valueColumnName = "value", pkColumnValue = "Resource_Key", allocationSize = 1)
	@Column(name = "RESOURCE_ID", unique = true, nullable = false, precision = 22, scale = 0)
	public Long getId() {
		return this.id;
	}

	public void setId(Long resourceId) {
		this.id = resourceId;
	}

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST })
	@JoinColumn(name = "PARENT_RESOURCE_ID")
	public Resource getParentResource() {
		return this.parentResource;
	}

	public void setParentResource(Resource parentResource) {
		this.parentResource = parentResource;
	}

	@Column(name = "RESOURCE_TYPE", nullable = false, length = 64)
	public String getResourceType() {
		return this.resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	@Column(name = "NAME", nullable = false, length = 64)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "URL", length = 256)
	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Column(name = "POST_URL", length = 256)
	public String getPostUrl() {
		return this.postUrl;
	}

	public void setPostUrl(String postUrl) {
		this.postUrl = postUrl;
	}

	@Column(name = "PROPERTIES", length = 256)
	public String getProperties() {
		return this.properties;
	}

	public void setProperties(String properties) {
		this.properties = properties;
	}

	@Column(name = "NODE_PROPERTIES", length = 256)
	public String getNodeProperties() {
		return this.nodeProperties;
	}

	public void setNodeProperties(String nodeProperties) {
		this.nodeProperties = nodeProperties;
	}

	@Column(name = "MEMO", length = 256)
	public String getMemo() {
		return this.memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	@Column(name = "NODE_POSITION")
	public int getNodePosition() {
		return nodePosition;
	}

	public void setNodePosition(int nodePosition) {
		this.nodePosition = nodePosition;
	}

	/**
	 * 可访问该资源的授权集合.
	 */
	@ManyToMany
	@JoinTable(name = "SS_RESOURCE_AUTHORITY", joinColumns = { @JoinColumn(name = "RESOURCE_ID") }, inverseJoinColumns = { @JoinColumn(name = "AUTHORITY_ID") })
	@Fetch(FetchMode.JOIN)
	@OrderBy("id")
	@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	public List<Authority> getAuthorityList() {
		return authorityList;
	}

	public void setAuthorityList(List<Authority> authorityList) {
		this.authorityList = authorityList;
	}

	/**
	 * 可访问该资源的授权名称字符串, 多个授权用','分隔.
	 */
	@Transient
	public String getAuthNames() {
		return ReflectionUtils.fetchElementPropertyToString(authorityList, "name", ",");
	}

	@Transient
	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	@Transient
	public boolean isChecked() {
		return checked;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
