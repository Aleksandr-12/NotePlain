package com.writesimple.simplenote.DI;


import com.writesimple.simplenote.model.DataBase;
import com.writesimple.simplenote.model.ViewModel.NoteViewModel;

import dagger.Component;

@Component(modules = {AppModule.class})
public interface AppComponent {

  // DataBase getDatabase();

   void inject(NoteViewModel noteViewModel);
}
