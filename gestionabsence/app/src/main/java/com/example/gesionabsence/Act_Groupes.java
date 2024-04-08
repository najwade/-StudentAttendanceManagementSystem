package com.example.gesionabsence;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

public class Act_Groupes extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ListView listview;
    Global_Adapter adapter;
    private Compte compte=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_groupes);
        compte=Compte.extraireCompte(this);
        listview = (ListView) findViewById(R.id.listview);
        Ajax.get(this)
                .compte(compte)
                .entry("groupes")
                .action("get")
                .start(new Ajax.On() {
                    @Override
                    public void Ok(Object data) {
                        create_list((JSONArray) data);
                    }
                });
    }

    private void create_list(JSONArray data){
        adapter = new Global_Adapter(this,R.layout.elem_groupe,data);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Intent i = new Intent(this, Act_Etudiants.class);
        Compte.fusionnerCompte(i,compte);
        i.putExtra("groupe_id",adapter.getItem(position).optInt("id"));
        startActivity(i);
    }
}