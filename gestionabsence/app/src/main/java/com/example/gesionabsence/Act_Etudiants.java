package com.example.gesionabsence;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import org.json.JSONArray;

public class Act_Etudiants extends AppCompatActivity {
    ListView listview;
    private Compte compte=null;
    Global_Adapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_etudiants);
        compte=Compte.extraireCompte(this);
        listview = (ListView) findViewById(R.id.listview);
        int id =getIntent().getIntExtra("groupe_id",-1);
        Ajax.get(this)
                .compte(compte)
                .entry("etudiants")
                .action("get")
                .arg("id",id)
                .start(new Ajax.On() {
                    @Override
                    public void Ok(Object data) {
                        create_list((JSONArray) data);
                    }
                });
    }


    private void create_list(JSONArray data){
        adapter = new Global_Adapter(this,R.layout.elem_etudiant,data);
        listview.setAdapter(adapter);
    }
}