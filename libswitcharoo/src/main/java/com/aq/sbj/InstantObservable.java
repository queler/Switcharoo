package com.aq.sbj;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by amq102 on 7/7/2015.
 */
public class InstantObservable extends Observable {
    @Override
    public synchronized void addObserver(Observer observer) {
        super.addObserver(observer);
        setChanged();
        notifyObservers();
    }
}
