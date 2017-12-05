package com.allcom.security.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * UserRoleId 实体对象.
 */
@Embeddable
public class UserRoleId  implements java.io.Serializable {
 	private Long roleId;
 	private Long userId;

    public UserRoleId() {
    }

  
    public UserRoleId(Long roleId, Long userId) {
        this.roleId = roleId;
        this.userId = userId;
    }
   


    @Column(name="ROLE_ID", precision=22, scale=0)
    public Long getRoleId() {
        return this.roleId;
    }
    
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }


    @Column(name="USER_ID", precision=22, scale=0)
    public Long getUserId() {
        return this.userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * toString().
     * @return String
     */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
   public boolean equals(Object other) {
         if ( (this == other ) ) return true;
		 if ( (other == null ) ) return false;
		 if ( !(other instanceof UserRoleId) ) return false;
		 UserRoleId castOther = ( UserRoleId ) other; 
         
		 return ( (this.getRoleId()==castOther.getRoleId()) || ( this.getRoleId()!=null && castOther.getRoleId()!=null && this.getRoleId().equals(castOther.getRoleId()) ) )
 && ( (this.getUserId()==castOther.getUserId()) || ( this.getUserId()!=null && castOther.getUserId()!=null && this.getUserId().equals(castOther.getUserId()) ) );
   }
   
   public int hashCode() {
         int result = 17;
         
         result = 37 * result + ( getRoleId() == null ? 0 : this.getRoleId().hashCode() );
         result = 37 * result + ( getUserId() == null ? 0 : this.getUserId().hashCode() );
         return result;
   }   

}


