package org.vadtel.injector.test;

public class ControllerWithoutAnnotationImpl implements Controller{
    private final Service service;

    public ControllerWithoutAnnotationImpl(Service service) {
        this.service = service;
    }
}
