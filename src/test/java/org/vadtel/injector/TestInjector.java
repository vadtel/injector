package org.vadtel.injector;

import org.vadtel.injector.exceptions.BindingNotFoundException;
import org.vadtel.injector.exceptions.ConstructorNotFoundException;
import org.vadtel.injector.injector.impl.InjectorImpl;
import org.vadtel.injector.injector.Injector;
import org.junit.jupiter.api.Test;

import org.vadtel.injector.provider.Provider;
import org.vadtel.injector.test.*;

import static org.junit.jupiter.api.Assertions.*;


public class TestInjector {

    @Test
    void testExistingBinding() {

        Injector injector = new InjectorImpl();
        injector.bind(Controller.class, ControllerImpl.class);
        injector.bind(DAO.class, DAOImpl.class);
        injector.bind(Service.class, ServiceImpl.class);
        Provider<Controller> controllerProvider = injector.getProvider(Controller.class);
        assertNotNull(controllerProvider);
        assertNotNull(controllerProvider.getInstance());
        assertSame(ControllerImpl.class, controllerProvider.getInstance().getClass());
    }

    @Test
    void testBindingNotFoundException() {
        Injector injector = new InjectorImpl();
        injector.bind(Controller.class, ControllerImpl.class);

        injector.bind(Service.class, ServiceImpl.class);
        Provider<Controller> controllerProvider = injector.getProvider(Controller.class);
        assertNotNull(controllerProvider);
        assertThrows(BindingNotFoundException.class, () -> controllerProvider.getInstance());

    }

    @Test
    void testConstructorControllerWithoutAnnotationImpl() {
        Injector injector = new InjectorImpl();
        injector.bind(Controller.class, ControllerWithoutAnnotationImpl.class);
        injector.bind(DAO.class, DAOImpl.class);
        injector.bind(Service.class, ServiceImpl.class);
        Provider<Controller> controllerProvider = injector.getProvider(Controller.class);
        assertNotNull(controllerProvider);
        assertThrows(ConstructorNotFoundException.class, () -> controllerProvider.getInstance());

    }

    @Test
    void testControllerWithDefaultConstructorImpl() {
        Injector injector = new InjectorImpl();
        injector.bind(Controller.class, ControllerWithDefaultConstructorImpl.class);
        Provider<Controller> controllerProvider = injector.getProvider(Controller.class);
        assertNotNull(controllerProvider.getInstance());
        assertSame(ControllerWithDefaultConstructorImpl.class, controllerProvider.getInstance().getClass());
    }

    @Test
    void testSingleton() {
        Injector injector = new InjectorImpl();
        injector.bindSingleton(Controller.class, ControllerWithDefaultConstructorImpl.class);
        Provider<Controller> controllerProvider = injector.getProvider(Controller.class);
        Controller instance1 = controllerProvider.getInstance();
        Controller instance2 = controllerProvider.getInstance();
        assertEquals(instance1, instance2);

    }

    @Test
    void testPrototype() {
        Injector injector = new InjectorImpl();
        injector.bind(Controller.class, ControllerWithDefaultConstructorImpl.class);
        Provider<Controller> controllerProvider = injector.getProvider(Controller.class);
        Controller instance1 = controllerProvider.getInstance();
        Controller instance2 = controllerProvider.getInstance();
        assertNotEquals(instance1, instance2);
    }
}
