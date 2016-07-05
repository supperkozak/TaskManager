package com.example.taskmanager.utils;


import com.example.taskmanager.model.Task;

import io.realm.Realm;
import io.realm.RealmResults;

public class RealmController {

        private static Realm realm;

        public RealmController() {
            realm = Realm.getDefaultInstance();
        }

        public static RealmController getInstance() {
            return new RealmController();
        }

        public static Realm getRealm() {
            return realm;
        }

       /* public static RealmResults<Task> getTasks() {
            return realm.where(Task.class).findAll();
        }

        public static void copyToRealmOrUpdate(Task task) {
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(task);
            realm.commitTransaction();
        }

        public static void delete(Task task) {
            RealmResults<Task> tasks = realm.where(Task.class).equalTo("id", task.getId()).findAll();
            realm.beginTransaction();

            for(Task t : tasks){
                t.removeFromRealm();
            }

            realm.commitTransaction();
        }*/

    }
