package com.allcom.security.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * UserRole 实体对象.
 */
@SuppressWarnings("serial")
@Entity
@Table(name="SS_USER_ROLE")
public class UserRole  implements java.io.Serializable {
 	private UserRoleId id;
 	private Role role;
 	private User user;

    public UserRole() {
    }

	
    public UserRole(UserRoleId id) {
        this.setId(id);
    }
  
    public UserRole(UserRoleId id, Role role, User user) {
        this.setId(id);
        this.role = role;
        this.user = user;
    }
   
    @EmbeddedId
	@GeneratedValue(strategy = GenerationType.SEQUENCE)

    
    @AttributeOverrides( {
        @AttributeOverride(name="roleId", column=@Column(name="ROLE_ID", precision=22, scale=0) ), 
        @AttributeOverride(name="userId", column=@Column(name="USER_ID", precision=22, scale=0) ) } )
    public UserRoleId getId() {
        return this.id;
    }
    
    public void setId(UserRoleId id) {
        this.id = id;
    }    

	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="ROLE_ID", insertable=false, updatable=false)
    public Role getRole() {
        return this.role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }

	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="USER_ID", insertable=false, updatable=false)
    public User getUser() {
        return this.user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * toString().
     * @return String
     */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}


