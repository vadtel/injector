package org.vadtel.injector.injector;

import org.vadtel.injector.provider.Provider;

public interface Injector {
    <T> Provider<T> getProvider(Class<T> type);
    <T> void bind(Class<T> intf, Class<? extends T> impl);
    <T> void bindSingleton(Class<T> intf, Class<? extends T> impl);


}
