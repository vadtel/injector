package org.vadtel.injector.test;

import org.vadtel.injector.annotations.Inject;

public class ServiceImpl implements Service{
    private final DAO dao;

    @Inject
    public ServiceImpl(DAO dao) {
        this.dao = dao;
    }
}
