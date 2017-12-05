package com.allcom.security.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * UserAssigne 实体对象.
 */
@SuppressWarnings("serial")
@Entity
@Table(name="SS_USER_ASSIGNE")
public class UserAssigne  implements java.io.Serializable {
 	private Long id;
 	private String assigner;
 	private String assignee;
 	
    public UserAssigne() {
    }

  
    public UserAssigne(Long assigneId, String assigner, String assignee) {
        this.setId(assigneId);
        this.assigner = assigner;
        this.assignee = assignee;
    }
   
    @Id 
	@GeneratedValue(strategy = GenerationType.TABLE,generator = "UserAssigne_Gen")
	@TableGenerator(name = "UserAssigne_Gen", table = "SS_PK", pkColumnName = "name", valueColumnName = "value", pkColumnValue = "UserAssigne_Key", allocationSize = 1)	

    
    @Column(name="ASSIGNE_ID", unique=true, nullable=false, precision=22, scale=0)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long assigneId) {
        this.id = assigneId;
    }    

    
    @Column(name="ASSIGNER", nullable=false, length=32)
    public String getAssigner() {
        return this.assigner;
    }
    
    public void setAssigner(String assigner) {
        this.assigner = assigner;
    }

    
    @Column(name="ASSIGNEE", nullable=false, length=32)
    public String getAssignee() {
        return this.assignee;
    }
    
    public void setAssignee(String assignee) {
        this.assignee = assignee;
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


