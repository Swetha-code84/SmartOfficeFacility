package com.notification.decorators;

import com.notification.core.Notifier;

// 3. Abstract Decorator: Holds a reference to the wrapped object and delegates the call
public abstract class NotifierDecorator implements Notifier {

    // Reference to the Component (or another Decorator)
    protected Notifier wrappedNotifier;

    public NotifierDecorator(Notifier wrappedNotifier) {
        this.wrappedNotifier = wrappedNotifier;
    }

    // Delegates the core functionality
    @Override
    public void send(String message) {
        wrappedNotifier.send(message);
    }
}