package com.writesimple.simplenote.DI;

import com.writesimple.simplenote.Repository.Repository;

import dagger.Component;

@Component(modules = {RepositoryModule.class})
public interface RepositoryComponent {
    Repository provideRepository();
}
