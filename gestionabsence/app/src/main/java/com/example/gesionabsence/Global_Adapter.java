package com.example.gesionabsence;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

public class Global_Adapter extends BaseAdapter {
    private Activity _activity;
    private JSONArray _data;
    private int _view_res;
    public Global_Adapter(Activity _activity,int _view_res,JSONArray _data){
        this._activity=_activity;
        this._view_res=_view_res;
        this._data=_data;
    }

    @Override
    public int getCount() {
        return _data.length();
    }

    @Override
    public JSONObject getItem(int i) {
        return _data.optJSONObject(i);
    }

    @Override
    public long getItemId(int i) {
        return _data.optJSONObject(i).optInt("id");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        JSONObject item = getItem(position);

        // Check if the convertView is null. If it is, inflate a new view.
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(_activity);
            convertView = inflater.inflate(_view_res, parent, false);
        }
        if (_view_res==R.layout.elem_etudiant){
            // Get references to the views in the layout.
            TextView txt_code_apogee = convertView.findViewById(R.id.txt_code_apogee);
            TextView txt_nom_complet = convertView.findViewById(R.id.txt_nom_complet);
            //ImageView iconImageView = convertView.findViewById(R.id.icon_image_view); we will ad this later

            // Set the values of the views based on the data for the current item.
            txt_code_apogee.setText(String.valueOf(item.optInt("code_apogee")) );
            txt_nom_complet.setText(item.optString("nom_complet"));
            //txt_code_apogee.setImageResource(currentItem.getIconResourceId());
        }else if (_view_res==R.layout.elem_groupe){
            TextView txt_id = convertView.findViewById(R.id.txt_id);
            TextView txt_groupe = convertView.findViewById(R.id.txt_groupe);
            txt_id.setText(String.valueOf(item.optInt("id")) );
            txt_groupe.setText(item.optString("groupe"));
        }else if (_view_res==R.layout.elem_module){
            TextView txt_module = convertView.findViewById(R.id.txt_module);
            TextView txt_professeur = convertView.findViewById(R.id.txt_professeur);
            TextView txt_seances = convertView.findViewById(R.id.txt_seances);
            String prof=item.optString("professeur",null);
            if (prof==null)
                txt_professeur.setVisibility(View.GONE);
            else
                txt_professeur.setText("Professeur : "+prof);

            txt_seances.setText("Séances : "+String.valueOf(item.optInt("seances")) );
            txt_module.setText("Module : "+item.optString("module"));
        }else if (_view_res==R.layout.elem_absence_date){
            TextView txt_jour = convertView.findViewById(R.id.txt_jour);
            TextView txt_date = convertView.findViewById(R.id.txt_date);
            int def_col=_activity.getResources().getColor(android.R.color.primary_text_light);
            if (item.optBoolean("statut")){
                txt_jour.setTextColor(def_col);
                txt_date.setTextColor(def_col);
            }else{
                txt_jour.setTextColor(Color.RED);
                txt_date.setTextColor(Color.RED);
            }
            txt_jour.setText(item.optString("jour"));
            txt_date.setText(item.optString("date"));
        }else if (_view_res==R.layout.elem_absence_seance){
            TextView txt_heure = convertView.findViewById(R.id.txt_heure);
            TextView txt_module = convertView.findViewById(R.id.txt_module);
            TextView txt_groupe = convertView.findViewById(R.id.txt_groupe);
            int def_col=_activity.getResources().getColor(android.R.color.primary_text_light);
            if (item.optBoolean("statut")){
                JSONObject o=item.optJSONObject("seance");
                txt_heure.setTextColor(def_col);
                txt_heure.setText("Heure : "+o.optString("debut").substring(0,5)+" - "+o.optString("fin").substring(0,5));
                txt_heure.setVisibility(View.VISIBLE);
                txt_module.setText("Module : "+o.optString("module"));
                txt_module.setVisibility(View.VISIBLE);
                txt_groupe.setText("Groupe : "+o.optString("groupe"));
                txt_groupe.setVisibility(View.VISIBLE);
            }else{
                txt_heure.setTextColor(Color.RED);
                txt_heure.setText("Heure : "+item.optString("heure").substring(0,5));
                txt_module.setVisibility(View.GONE);
                txt_groupe.setVisibility(View.GONE);
            }
        }else if (_view_res==R.layout.elem_absence_etudiant){
            TextView txt_nom_complet = convertView.findViewById(R.id.txt_nom_complet);
            TextView txt_code_apogee = convertView.findViewById(R.id.txt_code_apogee);
            Switch switch_statut = convertView.findViewById(R.id.switch_statut);
            txt_nom_complet.setText(item.optString("nom_complet"));
            txt_code_apogee.setText(item.optString("code_apogee"));
            switch_statut.setChecked(item.optString("statut").equals("present"));

        }else if (_view_res==R.layout.elem_absence_rapport){
            TextView txt_date = convertView.findViewById(R.id.txt_date);
            TextView txt_module = convertView.findViewById(R.id.txt_module);
            TextView txt_professeur = convertView.findViewById(R.id.txt_professeur);
            TextView txt_heure = convertView.findViewById(R.id.txt_heure);
            txt_date.setText("Date : "+item.optString("date")+" ("+item.optString("jour")+")");
            txt_module.setText("Module : "+item.optString("module"));
            txt_professeur.setText("Professeur : "+item.optString("professeur"));
            txt_heure.setText("Heure : "+item.optString("debut").substring(0,5)+" - "+item.optString("fin").substring(0,5));
        }

        // Return the view.
        return convertView;
    }
}
