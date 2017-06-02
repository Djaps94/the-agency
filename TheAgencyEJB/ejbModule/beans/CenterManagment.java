package beans;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Local;
import javax.ejb.Singleton;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Local(CenterManagmentLocal.class)
public class CenterManagment implements CenterManagmentLocal{
	

}
