package com.writesimple.simplenote.DI;

import android.content.Context;

import com.writesimple.simplenote.Repository.Repository;
import androidx.annotation.NonNull;
import dagger.Module;
import dagger.Provides;
@Module
public class RepositoryModule {
    private final Context context;

    public RepositoryModule(@NonNull Context context) {
        this.context = context;
    }
    @Provides
    Repository provideRepository(){
        return new Repository(context);
    }
}
