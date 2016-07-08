package com.example.taskmanager.utils;


import com.example.taskmanager.constant.Constant;
import com.example.taskmanager.model.Task;

import java.util.Comparator;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class RealmController {

    private static Realm mRealm;

    public RealmController() {
        mRealm = Realm.getDefaultInstance();
    }

    public static RealmController getInstance() {
        return new RealmController();
    }

    public static Realm getRealm() {
        return mRealm;
    }

    public Task FindTaskByPosition(int position) {

        Task task = getTasks().get(position);
        final String id = task.getID();
        mRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(Task.class).equalTo(Constant.REALM_ID, id)
                        .findFirst();
            }
        });

        return task;
    }

    public RealmResults<Task> getTasks() {
        return mRealm.where(Task.class).findAll();
    }

    public void copyToRealmOrUpdate(Task task) {
        mRealm.beginTransaction();
        mRealm.copyToRealmOrUpdate(task);
        mRealm.commitTransaction();
    }
    public void beginTransaction(){
        mRealm.beginTransaction();
    }

    public void commitTransaction(){
        mRealm.commitTransaction();
    }
    public void delete(final Task task) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                task.deleteFromRealm();
            }
        });
    }

    public void close() {
        mRealm.close();
    }

    public void deleteAllFromRealm(){
        RealmResults<Task> result = mRealm.where(Task.class).findAll();
        mRealm.beginTransaction();
        result.deleteAllFromRealm();
        mRealm.commitTransaction();
    }

    public RealmResults<Task> sortAscending(String category){
        RealmResults<Task> results = mRealm.where(Task.class).findAll();
        RealmResults sorted = results.sort(category, Sort.ASCENDING);
        return sorted;
    }
    public RealmResults<Task> sortDescending(String category){
        RealmResults<Task> results = mRealm.where(Task.class).findAll();
        RealmResults sorted = results.sort(category, Sort.DESCENDING);
        return sorted;

    }
}

