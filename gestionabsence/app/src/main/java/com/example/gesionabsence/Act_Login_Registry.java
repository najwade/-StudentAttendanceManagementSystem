package com.example.gesionabsence;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

public class Act_Login_Registry extends AppCompatActivity implements View.OnClickListener {


    int action_id;// 0 1
    String  type; // professeur ou etudiant

    String[] text=new String[]{
            "Vous n'avez pas de compte? S'inscrire",
            "Vous avez déja un compte?"
    };
    String[] titre=new String[]{
            "Login",
            "Enregistrer"
    };

    String[] btn_type=new String[]{
            "login",
            "registry"
    };

    Button btn_ok;
    TextView txt_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login_registry);
        action_id=getIntent().getIntExtra("action_id",-1);
        if (action_id==0)
            ((EditText)findViewById(R.id.et_password2)).setVisibility(View.GONE);
        else
            ((EditText)findViewById(R.id.et_password2)).setText("");
        ((EditText)findViewById(R.id.et_password)).setText("");
        type=getIntent().getStringExtra("type");
        btn_ok=((Button)findViewById(R.id.btn_ok));
        btn_ok.setText(titre[action_id]);
        btn_ok.setOnClickListener(this);
        txt_text=((TextView)findViewById(R.id.txt_text));
        txt_text.setText(text[action_id]);
        txt_text.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if (view.getId()==R.id.txt_text){
            Intent i = new Intent(this,Act_Login_Registry.class);
            i.putExtra("action_id",action_id==0?1:0);
            i.putExtra("type",type);
            startActivity(i);
        }else{
            final String email = ((EditText)findViewById(R.id.et_email)).getText().toString().trim();
            String passe = ((EditText)findViewById(R.id.et_password)).getText().toString().trim();
            Ajax ajx=Ajax.post(this)
                    .arg("who",type)
                    .entry("compte")
                    .action(btn_type[action_id])
                    .put("email",email)
                    .put("mot_de_passe",passe);
            if (action_id==0){
                ajx
                        .wait_message("Connexion...")
                        .ok_message("Connecté avec succès");
            }else{
                String passe2 = ((EditText)findViewById(R.id.et_password2)).getText().toString().trim();
                ajx.put("mot_de_passe2",passe2)
                        .wait_message("Enregistrement...")
                        .ok_message("Enregistré avec succès");
            }
            ajx.start(new Ajax.On() {
                        @Override
                        public void Ok(Object data) {
                            if (action_id==1){
                                // registry success
                                Intent i = new Intent(Act_Login_Registry.this, Act_Accueil.class);
                                startActivity(i);
                                finish();
                                return ;
                            }
                            JSONObject j = (JSONObject)data;
                            Compte compte=new Compte(type,j.optInt("id"),j.optInt("compte_id"),j.optString("nom_complet"),j.optString("session"));
                            Intent i = new Intent(Act_Login_Registry.this, Act_Dashboard.class);
                            Compte.fusionnerCompte(i,compte);
                            startActivity(i);
                        }

                    });
        }


    }
}