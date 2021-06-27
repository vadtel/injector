package org.vadtel.injector.test;

import org.vadtel.injector.annotations.Inject;

public class ControllerImpl implements Controller {

    private final Service service;

    @Inject
    public ControllerImpl(Service service) {
        this.service = service;
    }
}
