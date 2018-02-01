package com.taotao.mq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.Test;

public class TestActiveMQ {

	@Test
	public void testQueueProducer() throws Exception {
		// 1、创建一个连接工程ConnectionFactory对象。指定服务的ip及端口号
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.131:61616");
		// 2、使用ConnectionFactory对象创建一个Connection对象。
		Connection connection = connectionFactory.createConnection();
		// 3、开启连接，调用start方法
		connection.start();
		// 4、使用Connection对象创建一个Session对象。
		// 第一个参数：是否开启事务，一般不开启。当参数为false时，第二个参数才有意义。
		// 第二个参数：消息的应答模式。手动应答和自动应答。一般使用自动应答
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		// 5、使用Session对象创建一个Destination，目的地有两种queue、topic
		Queue queue = session.createQueue("test-queue");
		// 6、使用Session对象创建一个Producer对象。
		MessageProducer producer = session.createProducer(queue);
		// 7、使用producer发送消息。
		TextMessage textMessage = new ActiveMQTextMessage();
		textMessage.setText("使用activemq 发送的队列消息66666");
		// TextMessage textMessage2 = session.createTextMessage("使用activemq
		// 发送的队列消息");
		producer.send(textMessage);
		// 8、关闭资源。
		producer.close();
		session.close();
		connection.close();
	}

	@Test
	public void testQueueConsumer() throws Exception {
		// 创建一连接工厂对象
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.131:61616");
		// 使用工厂对象创建一个连接
		Connection connection = connectionFactory.createConnection();
		// 开启连接
		connection.start();
		// 使用连接对象创建一个session对象
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		// 创建一个destination对象，使用queue
		Queue queue = session.createQueue("test-queue");
		// 使用session对象创建一个消费者
		MessageConsumer consumer = session.createConsumer(queue);
		// 使用消费者对象接收消息。
		consumer.setMessageListener(new MessageListener() {

			@Override
			public void onMessage(Message message) {
				// 打印消息
				TextMessage textMessage = (TextMessage) message;
				String text = "";
				try {
					text = textMessage.getText();
					System.out.println(text);
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
		});
		
		 /*
	     * 程序等待接收用户结束操作，等待键盘输入
	     * 程序自己并不知道什么时候有消息，也不知道什么时候不再发送消息了，这就需要手动干预了，
	     * 当我们想停止接收消息时，可以在控制台输入任意键，然后回车即可结束接收操作（也可以直接按回车）。  
	     */
		System.in.read();

		/*
		 * while(true) { Message message = consumer.receive(3000); if (message
		 * == null) { break; } TextMessage textMessage = (TextMessage) message;
		 * String text = ""; try { text = textMessage.getText();
		 * System.out.println(text); } catch (JMSException e) {
		 * e.printStackTrace(); } }
		 */
		// 关闭资源
		consumer.close();
		session.close();
		connection.close();
	}

	@Test
	public void testTopicProducer() throws Exception {
		// 创建一个连接工厂对象
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.131:61616");
		// 使用连接工厂对象创建一个连接
		Connection connection = connectionFactory.createConnection();
		// 开启连接
		connection.start();
		// 使用连接对象创建一个session对象
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		// 使用session对象创建一个topic
		Topic topic = session.createTopic("test-topic");
		// 使用session创建一个producer，指定目的地时候topic。
		MessageProducer producer = session.createProducer(topic);
		// 创建一个TextMessage对象
		TextMessage message = session.createTextMessage("使用topic发送的消息111");
		// 使用producer对象发送消息。
		producer.send(message);
		// 关闭资源
		producer.close();
		session.close();
		connection.close();

	}

	@Test
	public void testTopicConsumer() throws Exception {
		// 创建一连接工厂对象
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.131:61616");
		// 使用工厂对象创建一个连接
		Connection connection = connectionFactory.createConnection();
		// 开启连接
		connection.start();
		// 使用连接对象创建一个session对象
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		// 创建一个destination对象，使用topic
		Topic topic = session.createTopic("test-topic");
		// 使用session对象创建一个消费者
		MessageConsumer consumer = session.createConsumer(topic);
		System.out.println("topic消费者3.。。。。");
		// 使用消费者对象接收消息。
		consumer.setMessageListener(new MessageListener() {

			@Override
			public void onMessage(Message message) {
				// 打印消息
				TextMessage textMessage = (TextMessage) message;
				String text = "";
				try {
					text = textMessage.getText();
					System.out.println(text);
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
		});
		//程序等待
		System.in.read();

		// 关闭资源
		consumer.close();
		session.close();
		connection.close();
	}
}
