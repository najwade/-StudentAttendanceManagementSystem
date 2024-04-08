package com.example.gesionabsence;

import android.app.Activity;
import android.content.Intent;

public class Compte {
    private String _type;
    private int _id;
    private int _compte_id;
    private String _nom_complet;
    private String _session;
    public Compte(String _type,int _id,int _compte_id,String _nom_complet,String _session){
        this._type=_type;
        this._id=_id;
        this._compte_id=_compte_id;
        this._nom_complet=_nom_complet;
        this._session=_session;
    }
    String type(){return _type;}
    int id(){return _id;}
    int compte_id(){return _compte_id;}
    String nom_complet(){return _nom_complet;}
    String session(){return _session;}

    boolean is_prof(){return _type.equals("professeur");}

    public static Compte extraireCompte(Activity activity){
        String type=activity.getIntent().getStringExtra("type");
        int id=activity.getIntent().getIntExtra("id",-1);
        int compte_id=activity.getIntent().getIntExtra("compte_id",-1);
        String nom_complet=activity.getIntent().getStringExtra("nom_complet");
        String session=activity.getIntent().getStringExtra("session");
        return new Compte(type,id,compte_id,nom_complet,session);
    }

    public static void fusionnerCompte(Intent intent,Compte compte){
        intent.putExtra("type",compte.type());
        intent.putExtra("id",compte.id());
        intent.putExtra("compte_id",compte.compte_id());
        intent.putExtra("nom_complet",compte.nom_complet());
        intent.putExtra("session",compte.session());
    }
}
