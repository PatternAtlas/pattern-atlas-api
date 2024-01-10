package io.github.patternatlas.api.aggregation;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;

public class ContentHelper {
    static String getQueueProducerTemplate() {
        return """
            import javax.jms.*;
            import javax.naming.*;
                        
            public class QueueProducer {
                        
                private QueueSession session;
                private QueueSender sender;
                        
                public static void main(String[] args) {
                    QueueProducer producer = new QueueProducer();
                    producer.send("Hello world!");
                }
                        
                public QueueProducer() {
                    initialize();
                }
                        
                private void initialize() {
                    try {
                        Context jndi = new InitialContext();
                        QueueConnectionFactory connectionFactory = (QueueConnectionFactory) jndi.lookup("ConnectionFactory");
                        QueueConnection connection = connectionFactory.createQueueConnection();
                        QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
                        Queue queue = (Queue) jndi.lookup("$channelEndpointUri$");
                        QueueSender sender = session.createSender(queue);
                        
                        connection.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                        
                private void send(String message) {
                    try {
                        TextMessage textMessage = session.createTextMessage();
                        textMessage.setText(message);
                        sender.send(textMessage);
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            }
            """;
    }

    static Map<String, Object> getQueueProducerProperties() {
        return Map.of("configHook", "channelEndpointUri");
    }

    static String getQueueConsumerTemplate() {
        return """
            import javax.jms.*;
            import javax.naming.*;
                        
            public class QueueConsumer {
                        
                public static void main(String[] args) {
                    new QueueConsumer();
                }
                        
                public QueueConsumer() {
                    initialize();
                }
                        
                private void initialize() {
                    try {
                        Context jndi = new InitialContext();
                        QueueConnectionFactory connectionFactory = (QueueConnectionFactory) jndi.lookup("ConnectionFactory");
                        QueueConnection connection = connectionFactory.createQueueConnection();
                        QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
                        Queue queue = (Queue) jndi.lookup("$channelEndpointUri$");
                        QueueReceiver receiver = session.createReceiver(queue);
                        
                        connection.start();
                        
                        while (true) {
                            process(receiver.receive(1000));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                        
                private void process(Message message) {
                    // TODO: implement message processing
                }
            """;
    }

    static Map<String, Object> getQueueConsumerProperties() {
        return Map.of("configHook", "channelEndpointUri");
    }

    static String getMessageFilterTemplate() {
        return """
            <route>
                <description>Message Filter</description>
                <from uri="activemq:$input-channel$" />
                <filter>
                    <!-- TODO: add filter rule, e.g.
                    <xpath>/sensor/value > -50</xpath>
                    -->
                    <to uri="activemq:$output-channel$" />
                </filter>
            </route>
            """;
    }

    static Map<String, Object> getMessageFilterProperties() {
        return Map.of("inputChannelConfigHook", "input-channel", "outputChannelConfigHook", "output-channel");
    }

    static String getPointToPointChannelTemplate() {
        return """
            <route>
                <description>Point to Point Channel</description>
                <from uri="activemq:$producer$" />
                <to uri="activemq:$consumer$" />
            </route>
            """;
    }

    static Map<String, Object> getP2PChannelProperties() {
        return Map.of("producerConfigHook", "producer", "consumerConfigHook", "consumer");
    }

    static String getMoMTemplate() {
        return """
            <beans
               xmlns="http://www.springframework.org/schema/beans"
               xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
               xsi:schemaLocation="
                 http://camel.apache.org/schema/spring http://camel.apache.org/schema/spring/camel-spring.xsd
                 http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
                        
                <camelContext id="camel" xmlns="http://camel.apache.org/schema/spring">
                        
                    <!-- The following Spring XML configuration is proudly presented by Pattern Atlas -->
                    $camelContext$
                </camelContext>
                        
            </beans>
            """;
    }

    static Map<String, Object> getMessageOrientedMiddlewareProperties() {
        return Map.of("configHook", "camelContext");
    }

}
