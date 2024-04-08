package com.example.gesionabsence;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Act_Dashboard extends AppCompatActivity implements View.OnClickListener {
    private Compte compte=null;


    CardView card_btn_modules;
    CardView card_btn_emploie;
    CardView card_btn_absence;
    CardView card_btn_groupes;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_dashboard);
        compte=Compte.extraireCompte(this);
        card_btn_modules=((CardView)findViewById(R.id.card_btn_modules));
        card_btn_emploie=((CardView)findViewById(R.id.card_btn_emploie));
        card_btn_absence=((CardView)findViewById(R.id.card_btn_absence));
        card_btn_groupes=((CardView)findViewById(R.id.card_btn_groupes));

        if (compte.is_prof())
            card_btn_groupes.setOnClickListener(this);
        else
            card_btn_groupes.setVisibility(View.GONE);

        card_btn_modules.setOnClickListener(this);
        card_btn_emploie.setOnClickListener(this);
        card_btn_absence.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.card_btn_emploie){
            Intent i =new Intent(this,Act_Emploie.class);
            Compte.fusionnerCompte(i,compte);
            startActivity(i);
            return ;
        }
        if (v.getId()==R.id.card_btn_groupes){
            Intent i =new Intent(this,Act_Groupes.class);
            Compte.fusionnerCompte(i,compte);
            startActivity(i);
            return ;
        }
        if (v.getId()==R.id.card_btn_modules){
            Intent i =new Intent(this,Act_Modules.class);
            Compte.fusionnerCompte(i,compte);
            startActivity(i);
            return ;
        }
        if (v.getId()==R.id.card_btn_absence){
            Intent i =new Intent(this,Act_Absence.class);
            Compte.fusionnerCompte(i,compte);
            i.putExtra("action_id",compte.is_prof()?0:3 );
            startActivity(i);
            return ;
        }
    }
}