package com.writesimple.simplenote.DI;

import com.writesimple.simplenote.model.ViewModel.FoldNoteViewModel;
import com.writesimple.simplenote.model.ViewModel.FoldViewModel;
import com.writesimple.simplenote.model.ViewModel.NoteViewModel;
import com.writesimple.simplenote.model.ViewModel.UpdateFolderNoteUOnFirebaseViewModel;
import com.writesimple.simplenote.model.ViewModel.UserViewModel;

import dagger.Component;

@Component(modules = {ViewModelModule.class})
public interface ViewModelComponent {

    NoteViewModel provideNoteViewModel();

    FoldNoteViewModel provideFoldNoteViewModel();

    FoldViewModel provideFoldViewModel();

    UserViewModel provideUserViewModel();

    UpdateFolderNoteUOnFirebaseViewModel provideUpdateFolderNoteUOnFirebaseViewModel();
}
