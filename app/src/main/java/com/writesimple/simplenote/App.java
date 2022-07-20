package com.writesimple.simplenote;

import android.app.Application;

import com.writesimple.simplenote.DI.AppComponent;
import com.writesimple.simplenote.DI.AppModule;


public class App extends Application {


   /* private static AppComponent component;

    //получение компонента
    public static AppComponent getComponent()
    {
        return  component;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        component = buildComponent();
    }

    //создание компонента
    protected AppComponent buildComponent() {
        return DaggerAppComponent.builder()
                .appModule(new AppModule())
                .build();
    }*/

}
