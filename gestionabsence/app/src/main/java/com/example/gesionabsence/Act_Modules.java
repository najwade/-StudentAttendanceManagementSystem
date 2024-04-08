package com.example.gesionabsence;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;

public class Act_Modules extends AppCompatActivity  {
    ListView listview;
    Global_Adapter adapter;
    private Compte compte=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_modules);
        compte=Compte.extraireCompte(this);
        listview = (ListView) findViewById(R.id.listview);
        Ajax.get(this)
                .compte(compte)
                .entry("modules")
                .action("get")
                .start(new Ajax.On() {
                    @Override
                    public void Ok(Object data) {
                        create_list((JSONArray) data);
                    }
                });
    }

    private void create_list(JSONArray data){
        adapter = new Global_Adapter(this,R.layout.elem_module,data);
        listview.setAdapter(adapter);

    }



}