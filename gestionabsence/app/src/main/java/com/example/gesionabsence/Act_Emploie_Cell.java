package com.example.gesionabsence;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;



public class Act_Emploie_Cell extends AppCompatActivity implements View.OnClickListener {
    JSONObject object;
    private Compte compte=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_emploie_cell);
        compte=Compte.extraireCompte(this);
        TextView txt_heure=(TextView) findViewById(R.id.txt_heure);
        TextView txt_module=(TextView) findViewById(R.id.txt_module);
        TextView txt_groupe_or_prof=(TextView) findViewById(R.id.txt_groupe_or_prof);
        String data_str  = getIntent().getStringExtra("data_str");
        try {
            object  = new JSONObject(data_str);
            txt_heure.setText(object.optString("debut").substring(0,5)+" - "+object.optString("fin").substring(0,5));// 08:00:00
            txt_module.setText(object.optString("module"));

            if (compte.is_prof()){
                ((TextView) findViewById(R.id.txt_groupe_or_prof_titre)).setText("Groupe");
                txt_groupe_or_prof.setText(object.optString("groupe"));
                RelativeLayout rl_btn_groupe=(RelativeLayout)findViewById(R.id.rl_btn);
                rl_btn_groupe.setOnClickListener(this);
            }else {
                ((TextView) findViewById(R.id.txt_groupe_or_prof_titre)).setText("Professeur");
                txt_groupe_or_prof.setText(object.optString("professeur"));
            }


        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onClick(View view) {
        Intent i = new Intent(this, Act_Etudiants.class);
        Compte.fusionnerCompte(i,compte);
        i.putExtra("groupe_id",object.optInt("groupe_id"));
        startActivity(i);
    }
}