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
  compile 'com.github.ministryofprogramming:RxMQTTClient:1.0.2'
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

### Licence

Copyright 2017 Ministry of Programming

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
