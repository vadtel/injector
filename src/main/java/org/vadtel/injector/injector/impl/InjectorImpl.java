package org.vadtel.injector.injector.impl;

import org.vadtel.injector.annotations.Inject;
import org.vadtel.injector.exceptions.BindingNotFoundException;
import org.vadtel.injector.exceptions.ConstructorNotFoundException;
import org.vadtel.injector.exceptions.TooManyConstructorsException;
import org.vadtel.injector.injector.Injector;
import org.vadtel.injector.provider.Provider;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;


public class InjectorImpl implements Injector {
    private Map<Class, Class> classMappings = new HashMap<>();
    private Map<Class, Provider> providerMappings = new HashMap<>();
    private Map<Class, Object> singletonInstances = new HashMap<>();


    public <T> void bind(Class<T> interfaceClass, Class<? extends T> implementationClass) {
        classMappings.put(interfaceClass, implementationClass);
    }

    @Override
    public <T> void bindSingleton(Class<T> interfaceClass, Class<? extends T> implementationClass) {
        bind(interfaceClass, implementationClass);
        singletonInstances.put(interfaceClass, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Provider<T> getProvider(Class<T> interfaceClass) {
        return (Provider<T>) providerMappings.getOrDefault(interfaceClass, (Provider<T>) () -> createOrFindObjectForClass(interfaceClass));
    }

    private boolean classIsSingleton(Class<?> interfaceClass) {
        return singletonInstances.containsKey(interfaceClass);
    }


    @SuppressWarnings("unchecked")
    private <T> T createOrFindObjectForClass(Class<T> intf) {
        if (classIsSingleton(intf)) {
            T singleton = Optional.ofNullable((T) singletonInstances.get(intf))
                    .orElseGet(() -> newObject(intf));
            singletonInstances.put(intf, singleton);
            return singleton;
        } else {
            T object = newObject(intf);
            return object;
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T newObject(Class<T> intf) {
        if (providerMappings.containsKey(intf)) {
            return (T) providerMappings.get(intf).getInstance();
        }

        Class impl;
        if ((impl = classMappings.get(intf)) != null) {
            Constructor<T> constructor = findConstructor(impl);
            Parameter[] parameters = constructor.getParameters();

            final List<Object> arguments = Arrays.stream(parameters)
                    .map(param -> createOrFindObjectForClass(param.getType()))
                    .collect(Collectors.toList());

            try {
                return arguments.size() > 0 ? constructor.newInstance(arguments.toArray()) : constructor.newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new ConstructorNotFoundException();
            }

        } else {
            throw new BindingNotFoundException();
        }
    }

    @SuppressWarnings("unchecked")
    private <T> Constructor<T> findConstructor(Class<T> type) {
        Constructor<?>[] constructors = type.getConstructors();

        if (constructors.length == 0) {
            throw new ConstructorNotFoundException();
        }

        List<Constructor<?>> constructorsWithInject = Arrays
                .stream(constructors)
                .filter(c -> c.isAnnotationPresent(Inject.class))
                .collect(Collectors.toList());

        if (constructorsWithInject.isEmpty()) {
            return (Constructor<T>) Arrays.stream(type.getDeclaredConstructors())
                    .filter(c -> c.getParameterTypes().length == 0)
                    .findFirst()
                    .orElseThrow(ConstructorNotFoundException::new);
        }

        if (constructorsWithInject.size() != 1) {
            throw new TooManyConstructorsException();
        }

        return (Constructor<T>) constructorsWithInject.get(0);

    }
}

