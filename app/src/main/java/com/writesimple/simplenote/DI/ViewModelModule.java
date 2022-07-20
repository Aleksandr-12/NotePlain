package com.writesimple.simplenote.DI;

import android.content.Context;

import com.writesimple.simplenote.model.ViewModel.FoldNoteViewModel;
import com.writesimple.simplenote.model.ViewModel.FoldViewModel;
import com.writesimple.simplenote.model.ViewModel.NoteViewModel;
import com.writesimple.simplenote.model.ViewModel.UpdateFolderNoteUOnFirebaseViewModel;
import com.writesimple.simplenote.model.ViewModel.UserViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import dagger.Module;
import dagger.Provides;

@Module
public class ViewModelModule {
    private final Context context;

    public ViewModelModule(@NonNull Context context) {
        this.context = context;
    }
    @Provides
    NoteViewModel provideNoteViewModel(){
        return new ViewModelProvider((ViewModelStoreOwner) context).get(NoteViewModel.class);
    }
    @Provides
    FoldNoteViewModel provideFoldNoteViewModel(){
        return new ViewModelProvider((ViewModelStoreOwner) context).get(FoldNoteViewModel.class);
    }
    @Provides
    FoldViewModel provideFoldViewModel(){
        return new ViewModelProvider((ViewModelStoreOwner) context).get(FoldViewModel.class);
    }

    @Provides
    UserViewModel provideUserViewModel(){
        return new ViewModelProvider((ViewModelStoreOwner) context).get(UserViewModel.class);
    }
    @Provides
    UpdateFolderNoteUOnFirebaseViewModel provideUpdateFolderNoteUOnFirebaseViewModel(){
        return new ViewModelProvider((ViewModelStoreOwner) context).get(UpdateFolderNoteUOnFirebaseViewModel.class);
    }
}
