package com.writesimple.simplenote.model;
import android.content.Context;
import com.writesimple.simplenote.model.Dao.FolderDao;
import com.writesimple.simplenote.model.Dao.NoteDao;
import com.writesimple.simplenote.model.Dao.UpdateFolderNoteUploadOnFirebaseDao;
import com.writesimple.simplenote.model.Dao.UserDao;
import com.writesimple.simplenote.model.Dao.dataForFirebaseDateDeleteDao;
import com.writesimple.simplenote.model.Dao.dataForFirebaseDateUpdateDao;
import com.writesimple.simplenote.model.Tables.FolderBase;
import com.writesimple.simplenote.model.Tables.NoteBase;
import com.writesimple.simplenote.model.Tables.UpdateFolderNoteUploadOnFirebase;
import com.writesimple.simplenote.model.Tables.User;
import com.writesimple.simplenote.model.Tables.dataForFirebaseDateDelete;
import com.writesimple.simplenote.model.Tables.dataForFirebaseDateUpdate;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {NoteBase.class, dataForFirebaseDateUpdate.class, dataForFirebaseDateDelete.class,
        FolderBase.class, UpdateFolderNoteUploadOnFirebase.class, User.class},
        version = 19,exportSchema = false)
@TypeConverters(NoteBase.DateStringConverter.class)
public abstract class DataBase extends RoomDatabase {
    private static DataBase INSTANCE;

    public static DataBase getDatabase(Context context){
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), DataBase.class, "DataBase")
                    //.addMigrations(DataBase.MIGRATION_1_2)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
    public abstract NoteDao noteDao();
    public abstract FolderDao FolderDao();
    public abstract UserDao UserDao();
    public abstract dataForFirebaseDateUpdateDao dataForFirebaseDateUpdateDao();
    public abstract UpdateFolderNoteUploadOnFirebaseDao updateFolderNoteUploadOnFirebaseDao();
    //public abstract dataForFirebaseDateAddDao dataForFirebaseDateAddDao();
    public abstract dataForFirebaseDateDeleteDao dataForFirebaseDateDeleteDao();

  /* public static final Migration MIGRATION_1_2 = new Migration(25, 26) {
        @Override
        public void migrate(final SupportSQLiteDatabase database) {
            //database.execSQL("ALTER TABLE 'любая таблица' ADD COLUMN 'любое поле' INTEGER DEFAULT 0 NOT NULL");
        }
    };*/
}
