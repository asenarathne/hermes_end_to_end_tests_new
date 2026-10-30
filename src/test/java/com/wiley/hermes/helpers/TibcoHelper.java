package com.wiley.hermes.helpers;

import com.tibco.tibjms.TibjmsConnectionFactory;
import javax.jms.*;

public class TibcoHelper {

    // 1. NEW METHOD: Subscribes FIRST, then sends the event, so we never miss fast replies (Solves the timeout bug)
    public static boolean sendAndWaitForEvent(
            String url, String user, String pass, String queueName, String topicName,
            String payload, String eventType, String eventId, String transactionId,
            String processId, String expectedEvent, int timeoutSeconds) {
        try {
            ConnectionFactory factory = new TibjmsConnectionFactory(url);
            try (Connection connection = factory.createConnection(user, pass)) {
                connection.start();
                try (Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {
                    
                    // Subscribe to the Topic FIRST
                    Topic topic = session.createTopic(topicName);
                    try (MessageConsumer consumer = session.createConsumer(topic)) {
                        
                        // Send the Event to the Queue SECOND
                        Destination queue = session.createQueue(queueName);
                        try (MessageProducer producer = session.createProducer(queue)) {
                            TextMessage message = session.createTextMessage(payload);
                            message.setJMSType(eventType);
                            message.setStringProperty("eventType", eventType);
                            message.setStringProperty("eventId", eventId);
                            message.setStringProperty("transactionId", transactionId);
                            if (processId != null && !processId.isEmpty()) {
                                message.setStringProperty("processId", processId);
                            }
                            producer.send(message);
                            System.out.println("✅ Sent event to " + queueName + " [JMSType=" + eventType + ", txId=" + transactionId + "]");
                        }

                        // Listen on the already-open Topic
                        long endTime = System.currentTimeMillis() + (timeoutSeconds * 1000L);
                        while (System.currentTimeMillis() < endTime) {
                            Message msg = consumer.receive(2000);
                            if (msg instanceof TextMessage) {
                                String text = ((TextMessage) msg).getText();
                                if (text == null) continue;

                                // MATCH ON EITHER TRANSACTION ID OR PROCESS ID
                                boolean matchesTx = transactionId != null && !transactionId.isEmpty() && text.contains(transactionId);
                                boolean matchesProc = processId != null && !processId.isEmpty() && text.contains(processId);

                                if (matchesTx || matchesProc) {
                                    if (text.contains("\"" + expectedEvent + "\"")) {
                                        System.out.println("✅ Found Expected TIBCO Event: " + expectedEvent);
                                        return true;
                                    }
                                    if (text.contains("\"DELIVERY_FAILED\"") || text.contains("\"DELIVERYSET_FAILED\"") || text.contains("\"DELIVERYSET_EMPTY\"")) {
                                        System.err.println("🚨 DPL terminated delivery early!\n" + text);
                                        return false;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("TIBCO sendAndWaitForEvent failed", e);
        }
        return false;
    }

    // 2. YOUR ORIGINAL METHOD: Kept so other existing tests don't break
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
                            Message msg = consumer.receive(2000);
                            if (msg instanceof TextMessage) {
                                String text = ((TextMessage) msg).getText();
                                if (text != null && text.contains(expectedEvent)) {
                                    System.out.println("✅ Found Expected TIBCO Event: " + expectedEvent);
                                    return true;
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

    // 3. YOUR ORIGINAL METHOD: Kept so other existing tests don't break
    public static void sendEventToQueue(String url, String username, String password, String queueName, String payload, String eventType) {
        try {
            ConnectionFactory factory = new TibjmsConnectionFactory(url);
            try (Connection connection = factory.createConnection(username, password);
                 Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {
                Destination destination = session.createQueue(queueName);
                try (MessageProducer producer = session.createProducer(destination)) {
                    TextMessage message = session.createTextMessage(payload);
                    message.setJMSType(eventType);
                    producer.send(message);
                }
            }
        } catch (JMSException e) {
            throw new RuntimeException("Failed to send event to TIBCO EMS", e);
        }
    }
}