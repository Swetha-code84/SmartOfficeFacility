package com.notification.decorators;
import com.notification.core.Notifier;
public abstract class NotifierDecorator implements Notifier {
    protected Notifier wrappedNotifier;
    public NotifierDecorator(Notifier wrappedNotifier) {
        this.wrappedNotifier = wrappedNotifier;
    }
    @Override
    public void send(String message) {
        wrappedNotifier.send(message);
    }
}
