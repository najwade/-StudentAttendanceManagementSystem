package com.example.gesionabsence;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Act_Accueil extends AppCompatActivity implements  View.OnClickListener{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_accueil);
        ((Button)findViewById(R.id.btnProf)).setOnClickListener(this);

        ((Button)findViewById(R.id.btnStudent)).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent i= new Intent(Act_Accueil.this, Act_Login_Registry.class);
        i.putExtra("action_id",0);
        i.putExtra("type",v.getId()==R.id.btnProf?"professeur":"etudiant");
        startActivity(i);
    }

}