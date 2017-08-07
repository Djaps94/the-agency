package consumers;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

public class HeartbeatConsumer extends DefaultConsumer {

	private String correlationId;
	private BlockingQueue<String> response;

	public HeartbeatConsumer(Channel channel, String correlationId, BlockingQueue<String> response) {
		super(channel);
		this.correlationId = correlationId;
		this.response = response;

	}

	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) throws IOException {
		if (properties.getCorrelationId().equals(correlationId)) {
			response.offer(new String(body));
		}
	}

}
