package com.imeja.carpooling.model;


import io.realm.Realm;

public class RealmUtils {


    public static void updateAccount(Account account) {
        Realm realm = Realm.getDefaultInstance();
        Account ac = realm.where(Account.class).findFirst();
        if (ac == null) {
            ac = new Account();
        }
        realm.beginTransaction();
        ac.phone = account.phone;
        ac.firstname = account.firstname;
        ac.lastname = account.lastname;
        ac.email = account.email;
        ac.profile = account.profile;
        ac.licence = account.licence;
        ac.registration = account.registration;
        ac.home = account.home;
        ac.office = account.office;
        realm.copyToRealmOrUpdate(ac);
        realm.commitTransaction();
    }

    public static String getLicence() {
        Realm realm = Realm.getDefaultInstance();
        Account ac = realm.where(Account.class).findFirst();
        if (ac == null) {
            ac = new Account();
        }
        return ac.driverlicence;
    }

    public static void setPhone(String jeff) {
        Realm realm = Realm.getDefaultInstance();
        Account ac = realm.where(Account.class).findFirst();
        if (ac == null) {
            ac = new Account();
        }
        realm.beginTransaction();
        ac.uniquePhone = jeff;
        realm.copyToRealmOrUpdate(ac);
        realm.commitTransaction();
    }

    public static String getPhoneNumber() {
        Realm realm = Realm.getDefaultInstance();
        Account ac = realm.where(Account.class).findFirst();
        if (ac == null) {
            ac = new Account();
        }
        return ac.uniquePhone;
    }

    public static void setIdBackURL(String jeff) {
        Realm realm = Realm.getDefaultInstance();
        Account ac = realm.where(Account.class).findFirst();
        if (ac == null) {
            ac = new Account();
        }
        realm.beginTransaction();
        ac.useridback = jeff;
        realm.copyToRealmOrUpdate(ac);
        realm.commitTransaction();
    }

    public static void setIdFrontURL(String jeff) {
        Realm realm = Realm.getDefaultInstance();
        Account ac = realm.where(Account.class).findFirst();
        if (ac == null) {
            ac = new Account();
        }
        realm.beginTransaction();
        ac.useridfront = jeff;
        realm.copyToRealmOrUpdate(ac);
        realm.commitTransaction();
    }

    public static void setProfileURL(String jeff) {
        Realm realm = Realm.getDefaultInstance();
        Account ac = realm.where(Account.class).findFirst();
        if (ac == null) {
            ac = new Account();
        }
        realm.beginTransaction();
        ac.profileImage = jeff;
        realm.copyToRealmOrUpdate(ac);
        realm.commitTransaction();
    }

    public static void setVehicleUrl(String jeff) {
        Realm realm = Realm.getDefaultInstance();
        Account ac = realm.where(Account.class).findFirst();
        if (ac == null) {
            ac = new Account();
        }
        realm.beginTransaction();
        ac.vehicleregistration = jeff;
        realm.copyToRealmOrUpdate(ac);
        realm.commitTransaction();
    }

    public static void setLicenceUrl(String jeff) {
        Realm realm = Realm.getDefaultInstance();
        Account ac = realm.where(Account.class).findFirst();
        if (ac == null) {
            ac = new Account();
        }
        realm.beginTransaction();
        ac.driverlicence = jeff;
        realm.copyToRealmOrUpdate(ac);
        realm.commitTransaction();
    }

    public static String getRegistration() {
        Realm realm = Realm.getDefaultInstance();
        Account ac = realm.where(Account.class).findFirst();
        if (ac == null) {
            ac = new Account();
        }
        return ac.vehicleregistration;
    }

    public static String getProfile() {
        Realm realm = Realm.getDefaultInstance();
        Account ac = realm.where(Account.class).findFirst();
        if (ac == null) {
            ac = new Account();
        }
        return ac.profileImage;
    }

    public static void setLogged(boolean b) {
        Realm realm = Realm.getDefaultInstance();
        Account ac = realm.where(Account.class).findFirst();
        if (ac == null) {
            ac = new Account();
        }
        realm.beginTransaction();
        ac.loggedIn = b;
        realm.copyToRealmOrUpdate(ac);
        realm.commitTransaction();

    }

    public static boolean isLoggedIn() {
        Realm realm = Realm.getDefaultInstance();
        Account ac = realm.where(Account.class).findFirst();
        if (ac == null) {
            ac = new Account();
        }
        return ac.loggedIn;
    }

    public static Account getAccount() {

        Realm realm = Realm.getDefaultInstance();
        return realm.where(Account.class).findFirst();
    }
}
