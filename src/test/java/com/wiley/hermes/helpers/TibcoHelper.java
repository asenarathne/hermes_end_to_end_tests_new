package com.wiley.hermes.helpers;

import com.tibco.tibjms.TibjmsConnectionFactory;
import javax.jms.*;

public class TibcoHelper {

    // Listens for a specific event on the TIBCO topic
    public static boolean waitForEvent(String url, String user, String pass, String topicName, String expectedEvent, int timeoutSeconds) {
        try {
            ConnectionFactory factory = new TibjmsConnectionFactory(url);
            try (Connection connection = factory.createConnection(user, pass)) {
                connection.start();
                try (Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {
                    Topic topic = session.createTopic(topicName);
                    try (MessageConsumer consumer = session.createConsumer(topic)) {
                        
                        long endTime = System.currentTimeMillis() + (timeoutSeconds * 1000L);
                        while (System.currentTimeMillis() < endTime) {
                            Message msg = consumer.receive(2000); // 2-second wait per check
                            if (msg instanceof TextMessage) {
                                String text = ((TextMessage) msg).getText();
                                
                                if (text != null && text.contains(expectedEvent)) {
                                    System.out.println("✅ Found Expected TIBCO Event: " + expectedEvent);
                                    return true;
                                } else if (text != null && text.contains("DELIVERY")) {
                                    // X-Ray Vision: Print any other delivery events it sees (like DELIVERY_FAILED)
                                    System.out.println("⚠️ [TIBCO Traffic]: " + text);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("TIBCO connection failed", e);
        }
        return false;
    }
}