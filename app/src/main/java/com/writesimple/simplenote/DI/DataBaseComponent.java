package com.writesimple.simplenote.DI;

import com.writesimple.simplenote.model.DataBase;

import dagger.Component;

@Component(modules = {DataBaseModule.class})
public interface DataBaseComponent {

    DataBase provideDataBase();
}
