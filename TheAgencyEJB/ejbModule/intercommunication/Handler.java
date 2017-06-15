package intercommunication;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;


@MessageDriven(
		activationConfig = { @ActivationConfigProperty(
				propertyName = "destinationType", propertyValue = "javax.jms.Queue")
		})
public class Handler implements MessageListener {


    public Handler() {
        // TODO Auto-generated constructor stub
    }
	

    public void onMessage(Message message) {
        // TODO Auto-generated method stub
        
    }

}
