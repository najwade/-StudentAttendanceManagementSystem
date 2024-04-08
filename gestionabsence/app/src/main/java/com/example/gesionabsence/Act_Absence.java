package com.example.gesionabsence;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Act_Absence extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ListView listview;
    Global_Adapter adapter;
    private Compte compte=null;

    private int action_id; // 0 'liste_dates',1 'liste_seances',2 'liste_absence'
    String[] actions=new String[]{
            "liste_dates","liste_seances","liste_absence","get"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_absence);
        compte=Compte.extraireCompte(this);
        listview = (ListView) findViewById(R.id.listview);
        action_id=getIntent().getIntExtra("action_id",-1);
        Ajax ajx=Ajax.get(this).compte(compte)
                .entry("absence")
                .action(actions[action_id]);
        if (action_id==1 || action_id==2)
            ajx.arg("date",getIntent().getStringExtra("date"));
        if (action_id==2)
            ajx.arg("id",getIntent().getIntExtra("emploie_id",-1));
        ajx.start(new Ajax.On() {
            @Override
            public void Ok(Object data) {
                create_list((JSONArray) data);
            }
        });


    }



    private void create_list(JSONArray data){
        int res;
        if (action_id==0)
            res=R.layout.elem_absence_date;
        else if (action_id==1)
            res=R.layout.elem_absence_seance;
        else if (action_id==2)
            res=R.layout.elem_absence_etudiant;
        else
            res=R.layout.elem_absence_rapport;

        adapter = new Global_Adapter(this,res,data);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(this);
    }



    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if(action_id==3)
            return ;
        final JSONObject o=adapter.getItem(position);
        if (action_id==2){

            Ajax.post(this)
                    .arg("statut",o.optString("statut").equals("present")?"absent":"present")
                    .arg("id",o.optInt("id"))
                    .entry("absence")
                    .action("cocher_statut")
                    .compte(compte)
                    .start(new Ajax.On() {
                        @Override
                        public void Ok(Object data) {
                            try {
                                o.put("statut",o.optString("statut").equals("present")?"absent":"present");
                                adapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
        }else{
            if (!o.optBoolean("statut"))
                return;
            Intent i = new Intent(this, Act_Absence.class);
            Compte.fusionnerCompte(i,compte);
            i.putExtra("action_id",(action_id+1) );
            if (action_id==0 || action_id==1)
                i.putExtra("date",o.optString("date"));
            if (action_id==1)
                i.putExtra("emploie_id",o.optJSONObject("seance").optInt("id"));
            startActivity(i);
        }


    }
}