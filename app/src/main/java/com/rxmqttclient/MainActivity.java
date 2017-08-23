package com.rxmqttclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AndroidException;
import android.util.Log;
import android.util.Xml;
import com.rxmqtt.enums.ClientType;
import com.rxmqtt.exceptions.RxMqttException;
import com.rxmqtt.implementation.RxMqttAsyncClient;
import com.rxmqtt.implementation.RxMqttClientFactory;
import com.rxmqtt.implementation.RxMqttMessage;
import com.rxmqtt.interfaces.IRxMqttClient;
import java.util.Enumeration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttPersistable;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.json.JSONObject;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {


  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

  @Override protected void onStart() {
    super.onStart();
  }
}
