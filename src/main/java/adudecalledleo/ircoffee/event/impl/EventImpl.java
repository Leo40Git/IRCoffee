package adudecalledleo.ircoffee.event.impl;

import adudecalledleo.ircoffee.event.Event;
import com.google.common.collect.Iterables;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Function;

public final class EventImpl<T> extends Event<T> {
    private final Class<? super T> type;
    private final Function<T[], T> invokerFactory;
    private final ArrayList<T> listeners;
    private final T emptyInvoker;

    public EventImpl(Class<? super T> type, Function<T[], T> invokerFactory) {
        this.type = type;
        this.invokerFactory = invokerFactory;
        listeners = new ArrayList<>();
        //noinspection unchecked
        T[] emptyArray = (T[]) Array.newInstance(type, 0);
        emptyInvoker = invokerFactory.apply(emptyArray);
        update();
    }

    private T[] getListenerArray() {
        //noinspection unchecked
        T[] arr = (T[]) Array.newInstance(type, 0);
        return listeners.toArray(arr);
    }

    private void update() {
        if (listeners.size() <= 1)
            invoker = Iterables.getFirst(listeners, emptyInvoker);
        else
            invoker = invokerFactory.apply(getListenerArray());
    }

    @Override
    public void register(T listener) {
        if (listener == null)
            throw new NullPointerException("listener == null!");
        if (listeners.add(listener))
            update();
    }

    @Override
    public void unregister(T listener) {
        if (listener == null)
            return;
        if (listeners.removeAll(Collections.singleton(listener)))
            update();
    }
}
