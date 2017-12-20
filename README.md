# Listeners&lt;T&gt;

[![listeners](https://img.shields.io/maven-central/v/ru.noties/listeners.svg?label=listeners)](http://search.maven.org/#search|ga|1|g%3A%22ru.noties%22%20AND%20a%3A%22listeners%22)


Simple data structure for listeners and observers that allows adding/removing elements whilst iterating without copying underlying collection. Aimed for use in one thread (for example some UI events listeners)


```java
interface MyListener {
    void apply(@NonNull MyListenersStore store);
}
```

```java
class MyListenersStore {

    private final Listeners<MyListener> listeners = Listeners.create();

    void register(@NonNull MyListener listener) {
        listeners.add(listener);
    }

    void unregister(@NonNull MyListener listener) {
        listeners.remove(listener);
    }

    void notifyListeners() {
        for (MyListener listener : listeners.begin()) {
            listener.apply(this);
        }
    }
}
```

Then, `MyListener` can be implemented like this:
```java
class MyListenerImpl implements MyListener {

    @Override
    public void apply(@NonNull MyListenersStore store) {

        // do something here
        doSomething();

        // and unregister
        store.unregister(this);
    }
}
```


### Limitations

Only one iteration can happen at a time

```java
for (MyListener listener1 : listeners.begin()) {
    for (MyListener listener2 : listeners.begin()) { // will throw

    }
}
```

The same if listener itself triggers notification:

```java
class MyListenerImpl implements MyListener {

    @Override
    public void apply(@NonNull MyListenersStore store) {
        store.notifyListeners(); // will throw
    }
}
```

If you plan to iterate on part of collection (for example with early break or some condition), explicit `end()` must be called.

```java
for (MyListener listener : listeners.begin()) {
    if (!isValid()) {
        break;
    }
}
listeners.end();
```

## License

```
  Copyright 2017 Dimitry Ivanov (mail@dimitryivanov.ru)

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
```