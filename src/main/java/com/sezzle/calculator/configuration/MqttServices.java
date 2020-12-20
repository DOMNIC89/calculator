package com.sezzle.calculator.configuration;

import com.sezzle.calculator.command.CalculatorActivityCO;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static com.sezzle.calculator.configuration.WebConfig.OBJECT_MAPPER;

@Component
public class MqttServices {

    private final IMqttClient client;
    private final MqttConnectOptions options;
    private static final Logger LOG = LoggerFactory.getLogger(MqttServices.class);

    public MqttServices(IMqttClient client, MqttConnectOptions options) {
        this.client = client;
        this.options = options;
    }

    @PostConstruct
    public void connectMqtt() throws MqttException {
        LOG.info("Connecting to mqtt broker");
        this.client.connect(options);
    }

    public void sendMessages(List<CalculatorActivityCO> messages, Long lastMessageId) throws IOException, MqttException {
        if (!client.isConnected()) {
            LOG.warn("Mqtt client is not connected and is thus not able to send the messages");
            return;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OBJECT_MAPPER.writeValue(baos, messages);
        MqttMessage message = generateMqttMessage(baos.toByteArray(), lastMessageId.intValue());
        client.publish("calculatorsezzle", message);
    }

    private MqttMessage generateMqttMessage(byte[] messageContent, int messageId) {
        MqttMessage message = new MqttMessage(messageContent);
        message.setId(1);
        message.setQos(1);
        message.setRetained(false);
        return message;
    }

    @PreDestroy
    public void shutdown() {
        try {
            this.client.disconnect();
        } catch (MqttException e) {
            LOG.warn("Mqtt Exception thrown while disconnecting...");
        }
    }
}
