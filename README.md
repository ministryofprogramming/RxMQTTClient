# RxMQTTClient

[![](https://jitpack.io/v/ministryofprogramming/RxMQTTClient.svg)](https://jitpack.io/#ministryofprogramming/RxMQTTClient)

RxMQTTClient is small library which simplify usage of [Paho](http://www.eclipse.org/paho/) MQTT trough RxJava.
Inspired by [rxMqtt](https://github.com/xudshen/rxMqtt) library

### Features

* Easy connect, publish and subscribe
* Disconnect and Force Disconnect
* Status report about connection 

### Download

Add to your project gradle

```
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```

Download via Gradle:
```gradle
dependencies {
compile 'com.github.nihad92:RxMQTTClient:1.0.1'
}
```

### Example

Create IRxMqttClient

```
rxMqttClient = new RxMqttClientFactory().create({HOST}, {PORT},
          {CLIENT_ID}, false, ClientType.Async);
```

Subscribe for Status report 

```
rxMqttClient.statusReport().subscribe(new StateSubscriber());
```

You will receive states and time when state changed (Connecting, Connected, etc.)

Connect to MQTT 

```
rxMqttClient.connect()
        .subscribe(new ConectionSubscriber())
```

And you are ready to subscribe or publish to some topics

``` 
rxMqttClient.subscribeTopic("chat/topic", 1/*QoS*/)
        .subscribe(new TopicSubscriber());

rxMqttClient.publish("chat/topic", payload)
```

Do not forget to disconnect at the end

```
rxMqttClient.disconnect().subscribe(new DisconnectSubscriber())
```

