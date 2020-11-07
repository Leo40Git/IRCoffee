package adudecalledleo.ircoffee.event.impl;

import adudecalledleo.ircoffee.event.Event;
import com.google.common.collect.Iterables;

import java.lang.reflect.Array;
import java.util.LinkedHashSet;
import java.util.function.Function;

public final class EventImpl<T> extends Event<T> {
    private final Class<? super T> type;
    private final Function<T[], T> invokerFactory;
    private final LinkedHashSet<T> handlers;
    private final T emptyInvoker;

    public EventImpl(Class<? super T> type, Function<T[], T> invokerFactory) {
        this.type = type;
        this.invokerFactory = invokerFactory;
        handlers = new LinkedHashSet<>();
        //noinspection unchecked
        T[] emptyArray = (T[]) Array.newInstance(type, 0);
        emptyInvoker = invokerFactory.apply(emptyArray);
        update();
    }

    private T[] getHandlerArray() {
        //noinspection unchecked
        T[] arr = (T[]) Array.newInstance(type, handlers.size());
        return handlers.toArray(arr);
    }

    private void update() {
        if (handlers.size() <= 1)
            invoker = Iterables.getFirst(handlers, emptyInvoker);
        else
            invoker = invokerFactory.apply(getHandlerArray());
    }

    @Override
    public void register(T listener) {
        if (listener == null)
            throw new NullPointerException("listener == null!");
        if (handlers.add(listener))
            update();
    }

    @Override
    public void unregister(T listener) {
        if (listener == null)
            return;
        if (handlers.remove(listener))
            update();
    }
}
