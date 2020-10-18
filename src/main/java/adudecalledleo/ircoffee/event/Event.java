package adudecalledleo.ircoffee.event;

import java.util.function.Function;

public abstract class Event<T> {
    public static <T> Event<T> create(Class<? super T> type, Function<T[], T> invokerFactory) {
        return new EventImpl<>(type, invokerFactory);
    }

    protected T invoker;

    public final T invoker() {
        return invoker;
    }

    public abstract void register(T listener);
    public abstract void unregister(T listener);
}
