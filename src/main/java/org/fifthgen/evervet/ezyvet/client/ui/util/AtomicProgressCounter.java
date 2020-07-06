package org.fifthgen.evervet.ezyvet.client.ui.util;

import lombok.Getter;
import lombok.Setter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.concurrent.atomic.AtomicInteger;

public class AtomicProgressCounter {

    private final PropertyChangeSupport propertyChangeSupport;
    private final AtomicInteger counter;

    @Getter
    @Setter
    private String errorMsg;

    public AtomicProgressCounter() {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        this.counter = new AtomicInteger(0);
        this.errorMsg = "";
    }

    public void set(int value) {
        int oldValue = this.counter.get();
        this.counter.set(value);
        this.propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this, "progress", oldValue, value));
    }

    public int get() {
        return this.counter.get();
    }

    public void setPropertyChangeListener(PropertyChangeListener listener) {
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }
}
