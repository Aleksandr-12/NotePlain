package com.writesimple.simplenote.model.ViewModel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.AsyncTask;
import android.os.Build;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.writesimple.simplenote.DI.DaggerDataBaseComponent;
import com.writesimple.simplenote.DI.DataBaseComponent;
import com.writesimple.simplenote.DI.DataBaseModule;
import com.writesimple.simplenote.model.DataBase;
import com.writesimple.simplenote.FireBase.DataFirebase;
import com.writesimple.simplenote.model.Tables.User;

import org.jetbrains.annotations.NotNull;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class UserViewModel extends AndroidViewModel {

    private LiveData<User> user;
    private final DataBase dataBase;
    private final DataFirebase dataFirebase;
    private final MutableLiveData<Long> dateFromFirebaseSubs;
    private final MutableLiveData<Long> dateFromFirebaseAnApp;
    private final MutableLiveData<String> date;
    private final MutableLiveData<Date> dateTrueFalse;
    private Date dateFirebase;
    private final MutableLiveData<String> isNotifyInApp;
    private final MutableLiveData<String> isNotifySubs;
    public MutableLiveData<String> billingSubscribeTrue;


    @RequiresApi(api = Build.VERSION_CODES.O)
    public UserViewModel(@NonNull Application application) {
        super(application);
        DataBaseComponent dataBaseComponent = DaggerDataBaseComponent.builder().dataBaseModule(new DataBaseModule(application)).build();
        dataBase = dataBaseComponent.provideDataBase();
       // dataBase = DataBase.getDatabase(this.getApplication());
        dataFirebase = new DataFirebase();
        //user =  dataBase.UserDao().getAll();
        date = new MutableLiveData<>();
        dateTrueFalse = new MutableLiveData<>();
        dateFromFirebaseSubs = new MutableLiveData<>();
        dateFromFirebaseAnApp = new MutableLiveData<>();
        isNotifyInApp = new MutableLiveData<>();
        isNotifySubs = new MutableLiveData<>();
        billingSubscribeTrue = new MutableLiveData<>();
        operationWithDate();
    }

    public LiveData<String> getDate(){
        return date;
    }

    public MutableLiveData<Long> getDateFirebaseSubs(){
        return dateFromFirebaseSubs;
    }
    public MutableLiveData<Long> getDateFirebaseInApp(){
        return dateFromFirebaseAnApp;
    }

    public MutableLiveData<String> getBillingSubscribeTrue(){
        return billingSubscribeTrue;
    }

    public String getIsSeeSubs(){
        return dataBase.UserDao().getIsSeeSubs();
    }
    public String getIsSeeInApp(){
        return dataBase.UserDao().getIsSeeInApp();
    }

    public LiveData<String> getIsNotifyInApp(){
        return isNotifyInApp;
    }
    public LiveData<String> getIsNotifySubs(){
        return isNotifySubs;
    }

    @SuppressLint("LongLogTag")
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void operationWithDate(){
        List<User> user = dataBase.UserDao().getDate();
        Date currentDate = new Date();
        Date newDate = null;
        Long date = null;
        FirebaseAuth firebaseAuth;
        firebaseAuth = FirebaseAuth.getInstance();

        if(dataBase.UserDao().getIsBuy().size()>0){
            List<User> user1 = dataBase.UserDao().getIsBuy();
            if(user1.size()>0){
                newDate = new Date(user1.get(0).getDate()+ TimeUnit.DAYS.toMillis(56));

                //newDate = new Date(date.getTime()- TimeUnit.DAYS.toMillis(1));
                @SuppressLint("SimpleDateFormat") SimpleDateFormat formatForDateNow = new SimpleDateFormat("dd.MM.yyyy hh:mm");
                String d = formatForDateNow.format(newDate);

                this.date.setValue(d);
                this.dateTrueFalse.setValue(newDate);
                if(newDate.getTime() <= currentDate.getTime()){
                    if(getIsSeeInApp().equals("noSee")){
                        billingSubscribeTrue.setValue("true");
                    }
                }
                if(newDate.getTime() - currentDate.getTime() <=  TimeUnit.DAYS.toMillis(1)){
                    if(user1.get(0) !=null) {
                        if(user1.get(0).getIsNotify().equals("noIsNotify")){
                            isNotifyInApp.setValue("noIsNotify");
                        }
                    }
                }
            }
        }else{
            if(firebaseAuth.getCurrentUser()!=null){
                getDateFromFirebaseInApp();
            }
        }

        if(user.size() > 0){
            date = user.get(0).getDate();
            if(date != null){
                newDate = new Date(date + TimeUnit.DAYS.toMillis(28));

                //newDate = new Date(date.getTime()- TimeUnit.DAYS.toMillis(1));
                @SuppressLint("SimpleDateFormat") SimpleDateFormat formatForDateNow = new SimpleDateFormat("dd.MM.yyyy hh:mm");
                String d = formatForDateNow.format(newDate);

                this.date.setValue(d);
                this.dateTrueFalse.setValue(newDate);
                if(newDate.getTime() <= currentDate.getTime()){
                    if(getIsSeeSubs().equals("noSee")){
                        billingSubscribeTrue.setValue("true");
                    }
                }
                if(newDate.getTime() - currentDate.getTime() <=  TimeUnit.DAYS.toMillis(1)){
                    if(user.get(0) !=null) {
                        if(user.get(0).getIsNotify().equals("noIsNotify")){
                            isNotifySubs.setValue("noIsNotify");
                        }
                    }
                }
            }
        }else{
            if(firebaseAuth.getCurrentUser()!=null){
                getDateFromFirebaseSubs();
            }

        }
    }

    public boolean getBooleanDateSubs(){
        boolean t;
        if(this.dateTrueFalse.getValue()!=null){
            if(this.dateTrueFalse.getValue().getTime()>new Date().getTime()){
                t =  true;
            }else if(this.dateTrueFalse.getValue().getTime()<=new Date().getTime()) {
                t = false;
            }else {
                t = false;
            }
        }else {
            t = false;
        }
        return t;
        //return  true;
    }

    @SuppressLint("LongLogTag")
    private void getDateFromFirebaseSubs() {
        dataFirebase.getMyRefUserSubs().get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    User user =   task.getResult().getValue(User.class);
                    if(user!=null){
                        dateFromFirebaseSubs.setValue(user.getDate());
                    }

                }
            }
        });
    }
    @SuppressLint("LongLogTag")
    private void getDateFromFirebaseInApp() {
        dataFirebase.getMyRefUserInApp().get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    User user =   task.getResult().getValue(User.class);
                    if(user!=null){
                        dateFromFirebaseAnApp.setValue(user.getDate());
                    }

                }
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addDataForSubscribe(User user,Boolean isbuy){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            DataFirebase ref = new DataFirebase();
            if(isbuy){
                ref.setMyRerencesUserInApp(user);
            }else{
                ref.setMyRerencesUserSubs(user);
            }
        }
        new UserViewModel.asyncAddDataFromSubscribe(dataBase,this).execute(user);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addForInApp(User user){
        new UserViewModel.asyncAddDataFromSubscribe(dataBase,this).execute(user);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addForSubs(User user){
        new UserViewModel.asyncAddDataFromSubscribe(dataBase,this).execute(user);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setTimeFromBilling(long purchaseTime,Boolean isbuy) {
        if(isbuy){
            User user = new User(purchaseTime,"noIsNotify","noSee","isBuy");
            addDataForSubscribe(user,true);
        }else{
            User user = new User(purchaseTime,"noIsNotify","noSee");
            addDataForSubscribe(user,false);
        }
    }

    private static class asyncAddDataFromSubscribe extends AsyncTask<User, Void, Void> {

        private final DataBase db;

        @RequiresApi(api = Build.VERSION_CODES.O)
        asyncAddDataFromSubscribe(DataBase dataBase, UserViewModel userViewModel) {
            this.db = dataBase;
            userViewModel.operationWithDate();
        }
        @Override
        protected Void doInBackground(User... users) {
            db.UserDao().insert(users[0]);
            return null;
        }
    }
    public void updateNote(User user,Boolean isbuy){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Date date = new Date();
        if(currentUser!=null){
            DataFirebase ref = new DataFirebase();
            if(isbuy){
                ref.setMyRerencesUserInApp(user);
            }else {
                ref.setMyRerencesUserSubs(user);
            }
        }

        new UserViewModel.updateAsyncDate(dataBase).execute(date);
    }

    private static class updateAsyncDate extends AsyncTask<Date, Void, Void> {

        private DataBase db;

        updateAsyncDate(DataBase dataBase) {
            this.db = dataBase;
        }

        @Override
        protected Void doInBackground(Date... dates) {
            db.UserDao().updateDateUserForSubscribe(dates[0]);
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateIsNotify(String isNotify,Boolean isbuy){
        if(isbuy){
            new updateAsyncNotifyInApp(dataBase).execute(isNotify);
        }else {
            new updateAsyncNotifySubs(dataBase).execute(isNotify);
        }
    }
    private static class updateAsyncNotifyInApp extends AsyncTask<String, Void, Void> {

        private final DataBase db;
        updateAsyncNotifyInApp(DataBase dataBase) {
            this.db = dataBase;
        }
        @Override
        protected Void doInBackground(final String... isNotify) {
            db.UserDao().updateNotifyInApp(isNotify[0]);
            return null;
        }
    }
    private static class updateAsyncNotifySubs extends AsyncTask<String, Void, Void> {

        private final DataBase db;
        updateAsyncNotifySubs(DataBase dataBase) {
            this.db = dataBase;
        }
        @Override
        protected Void doInBackground(final String... isNotify) {
            db.UserDao().updateNotify(isNotify[0]);
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateIsSeeSubs(String isNotify,Boolean isbuy){
        if(isbuy){
            List<User> user1 = dataBase.UserDao().getIsBuy();
            if(user1.size() > 0){
                new updateAsyncIsSeeInApp(dataBase).execute(isNotify);
            }
        }else{
            List<User> user = dataBase.UserDao().getDate();
            if(user.size() > 0){
                new updateAsyncIsSeeSubs(dataBase).execute(isNotify);
            }
        }

    }
    private static class updateAsyncIsSeeSubs extends AsyncTask<String, Void, Void> {

        private final DataBase db;

        updateAsyncIsSeeSubs(DataBase dataBase) {
            this.db = dataBase;
        }

        @Override
        protected Void doInBackground(final String... isNotify) {
            db.UserDao().updateIsSeeSubs(isNotify[0]);
            return null;
        }
    }
    private static class updateAsyncIsSeeInApp extends AsyncTask<String, Void, Void> {

        private final DataBase db;

        updateAsyncIsSeeInApp(DataBase dataBase) {
            this.db = dataBase;
        }

        @Override
        protected Void doInBackground(final String... isNotify) {
            db.UserDao().updateIsSeeInApp(isNotify[0]);
            return null;
        }
    }

}
