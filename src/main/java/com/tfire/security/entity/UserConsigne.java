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
 * UserConsigne 实体对象.
 */
@SuppressWarnings("serial")
@Entity
@Table(name="SS_USER_CONSIGNE")
public class UserConsigne  implements java.io.Serializable {
 	private Long id;
 	private String consigner;
 	private String consignee;
 	
    public UserConsigne() {
    }

  
    public UserConsigne(Long consigneId, String consigner, String consignee) {
        this.setId(consigneId);
        this.consigner = consigner;
        this.consignee = consignee;
    }
   
    @Id 
	@GeneratedValue(strategy = GenerationType.TABLE,generator = "UserConsigne_Gen")
	@TableGenerator(name = "UserConsigne_Gen", table = "SS_PK", pkColumnName = "name", valueColumnName = "value", pkColumnValue = "UserConsigne_Key", allocationSize = 1)	

    
    @Column(name="CONSIGNE_ID", unique=true, nullable=false, precision=22, scale=0)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long consigneId) {
        this.id = consigneId;
    }    

    
    @Column(name="CONSIGNER", nullable=false, length=32)
    public String getConsigner() {
        return this.consigner;
    }
    
    public void setConsigner(String consigner) {
        this.consigner = consigner;
    }

    
    @Column(name="CONSIGNEE", nullable=false, length=32)
    public String getConsignee() {
        return this.consignee;
    }
    
    public void setConsignee(String consignee) {
        this.consignee = consignee;
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


