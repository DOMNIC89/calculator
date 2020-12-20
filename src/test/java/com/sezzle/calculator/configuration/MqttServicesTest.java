package com.sezzle.calculator.configuration;

import com.sezzle.calculator.command.CalculatorActivityCO;
import com.sezzle.calculator.model.CalculatorActivity;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class MqttServicesTest {

    @Mock
    private MqttClient client;

    @Mock
    private MqttConnectOptions options;

    private MqttServices mqttServices;

    @BeforeEach
    public void setup() {
        mqttServices = new MqttServices(client, options);
    }

    @Test
    public void testMqttConnect() throws MqttException {
        mqttServices.connectMqtt();
        Mockito.verify(client).connect(options);
    }

    @Test
    public void testSendMessagesWithClientConnected() throws IOException, MqttException {
        Mockito.when(client.isConnected()).thenReturn(true);

        CalculatorActivityCO activity1 = createDummyCalculatorActivityCO(1, 0L);
        List<CalculatorActivityCO> messages = Collections.singletonList(activity1);


        mqttServices.sendMessages(messages, 1L);
        Mockito.verify(client, Mockito.times(1)).publish(eq("calculatorsezzle"),
                Mockito.any(MqttMessage.class));
    }

    @Test
    public void testSendMessagesWithClientNotConnected() throws IOException, MqttException {
        Mockito.when(client.isConnected()).thenReturn(false);

        CalculatorActivityCO activity1 = createDummyCalculatorActivityCO(1, 0L);
        List<CalculatorActivityCO> messages = Collections.singletonList(activity1);
        mqttServices.sendMessages(messages, 1L);
        Mockito.verify(client, Mockito.times(0)).publish(eq("calculatorsezzle"),
                Mockito.any(MqttMessage.class));
    }

    @Test
    public void testShutdown() throws MqttException {
        mqttServices.shutdown();
        Mockito.verify(client, Mockito.times(1)).disconnect();
    }

    private CalculatorActivityCO createDummyCalculatorActivityCO(int id, long timeInDifference) {
        return new CalculatorActivityCO("user-"+id, "2+2", "4", LocalDateTime.now().minusMinutes(timeInDifference));
    }
}