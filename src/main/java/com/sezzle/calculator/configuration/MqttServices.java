package com.sezzle.calculator.configuration;

import com.sezzle.calculator.model.CalculatorActivity;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

import static com.sezzle.calculator.configuration.WebConfig.OBJECT_MAPPER;

@Component
public class MqttServices {

    private IMqttClient client;
    private static final Logger LOG = LoggerFactory.getLogger(MqttServices.class);

    public MqttServices() {
        String publisherId = "1234567";
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(false);
        options.setKeepAliveInterval(10);
        options.setConnectionTimeout(1000);
        options.setUserName("dwwoihtf");
        options.setPassword("gqIZBlNorDJj".toCharArray());
        try {
            client = new MqttClient("tcp://driver.cloudmqtt.com:18986", publisherId);
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    LOG.info("Connection Lost reconnecting....");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {

                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    LOG.info("Message delivered");
                }
            });
            client.connect(options);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(CalculatorActivity activity) throws IOException, MqttException {
        if(!client.isConnected()) {
            LOG.warn("Mqtt client is not connected and is thus not able to send the message");
            return;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OBJECT_MAPPER.writeValue(baos, activity);
        LOG.info("Trying to send the message {} with id {}", baos.toString(), activity.getId());
        MqttMessage message = new MqttMessage(baos.toByteArray());
        message.setId(activity.getId().intValue());
        message.setQos(1);
        message.setRetained(false);
        client.publish("calculatorsezzle", message);
    }
}
