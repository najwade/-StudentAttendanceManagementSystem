package com.example.gesionabsence;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

public class Act_Emploie extends AppCompatActivity {
    private Compte compte=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_emploie);
        compte=Compte.extraireCompte(this);
        Ajax.get(this)
                .compte(compte)
                .entry("emploie")
                .action("get")
                .wait_message("Traitement...")
                .start(new Ajax.On() {
                    @Override
                    public void Ok(Object data) {
                        create_emploie((JSONArray)data);
                    }
                });


    }

    private void cell_click(JSONObject object){
        Intent i = new Intent(this,Act_Emploie_Cell.class);
        i.putExtra("data_str",object.toString());
        Compte.fusionnerCompte(i,compte);
        startActivity(i);
    }


    int p_col=-1;
    String[] p_cols=new String[]{
            "#342196F3","#3B673AB7","#2F3F51B5"
    };
    int n_col=-1;
    String[] n_cols=new String[]{
            "#32F44336","#31FF5722","#31C50303"
    };
    int generate_col(boolean positive){
        if (positive){
            if (++p_col>2) p_col=0;
            return Color.parseColor(p_cols[p_col]);
        }else{
            if (++n_col>2) n_col=0;
            return Color.parseColor(n_cols[n_col]);
        }
    }
    private void make_cell_view(LinearLayout parent,int weight,String str,final JSONObject object){
        RelativeLayout rl =new RelativeLayout(this);
        LinearLayout.LayoutParams rl_p=new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,weight);
        parent.addView(rl,rl_p);
        if (str!=null){
            rl.setBackgroundColor(generate_col(true));
            TextView tv = new TextView(this);
            tv.setText(str);
            tv.setTextSize(Utils.px(12,this));
            tv.setTypeface(null, Typeface.BOLD);
            tv.setTextColor(Color.parseColor("#090909"));
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
            RelativeLayout.LayoutParams tv_p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tv_p.addRule(RelativeLayout.CENTER_IN_PARENT);
            rl.addView(tv,tv_p);

            rl.setClickable(true);
            rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cell_click(object);
                }
            });

        }else
            rl.setBackgroundColor(generate_col(false));//Color.parseColor("#39880E03"));

    }
    private String[] days=new String[]{
            "lundi", // -> 0
            "mardi",
            "mercredi",
            "jeudi",
            "vendredi", //
            "samedi" // -> 5
    };
    private int day_to_int(String day){
        for (int i=0;i<days.length;i++)
            if (days[i].equals(day))
                return i;
        return -1;
    }




    private void create_cell(int when_cat,int when_day,int weight,Object data){
        LinearLayout when_cat_ln=null;

        if (when_cat==0)
            when_cat_ln=((LinearLayout)findViewById(R.id.ln_emploie_1));
        else
            when_cat_ln=((LinearLayout)findViewById(R.id.ln_emploie_2));
        LinearLayout when_day_ln= (LinearLayout) when_cat_ln.getChildAt(when_day+1);

        String str=null;
        if (data!=null) {
            str = ((JSONObject)data).optString("module");
            str+= "\n";
            if (compte.is_prof())
                str+= "groupe "+((JSONObject)data).optString("groupe");
            else
                str+= "Mr "+((JSONObject)data).optString("professeur");
        }
        make_cell_view(when_day_ln,weight*2,str,data==null?null:((JSONObject)data));
    }

    private int time_to_int(String t){
        t=t.replace(":","").substring(0,4);
        return Integer.parseInt(t);
    }

    private void create_emploie(JSONArray data){
        for (int when_cat=0;when_cat<2;when_cat++){
            for (int when_day=0;when_day<6;when_day++){
                int start=when_cat==0?800:1400;
                int end=when_cat==0?1200:1800;
                // we should create first title cell
                for (int when_time=start;when_time<end;when_time+=100){
                    boolean found=false;
                    for (int i=0;i<data.length();i++){
                        JSONObject o=data.optJSONObject(i);
                        int jour=day_to_int(o.optString("jour"));
                        int debut=time_to_int(o.optString("debut"));
                        if (jour==when_day && debut==when_time){
                            int fin=time_to_int(o.optString("fin"));
                            int weight=(fin-debut)/100;
                            create_cell(when_cat,when_day,weight,o);
                            if (weight>1){
                                when_time+=(weight-1)*100;
                            }
                            found=true;
                            break;
                        }
                    }
                    if (!found){
                        //create empty cell
                        create_cell(when_cat,when_day,1,null);
                    }
                }
            }

        }


    }





}