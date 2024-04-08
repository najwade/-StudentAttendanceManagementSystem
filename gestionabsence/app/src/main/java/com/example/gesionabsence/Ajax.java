package com.example.gesionabsence;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ajax extends AsyncTask<Void, Void, Void> {

    public interface On{
        void Ok(Object data);
    }

    private final static String _root="http://192.168.42.220:80/api/api";



    private String _method="get";

    private Compte _compte=null;
    private String _entry=null;

    private String _action=null;



    private ArrayList<String> _body=null;
    private ArrayList<String> _args=new ArrayList<>();

    private On _on=null;

    boolean _status=false;
    private Object _data=null;
    private Activity _activity;


    private AlertDialog _dialog=null;
    private AlertDialog.Builder _dialogb=null;
    private View _dialog_view=null;

    private String _wait_message=null;
    private String _ok_message=null;


    public Ajax(Activity _activity){
        this._activity=_activity;
    }

    public Ajax compte(Compte _compte){this._compte=_compte;return this;}

    public Ajax entry(String _entry){this._entry=_entry;return this;}
    public Ajax action(String _action){this._action=_action;return this;}

    public Ajax method(String _method){
        this._method=_method.toLowerCase();
        if (this._method.equals("post") || this._method.equals("put"))
            _body=new ArrayList<>();
        return this;
    }

    public Ajax ok_message(String _ok_message){this._ok_message=_ok_message;return this;}
    public Ajax wait_message(String _wait_message){this._wait_message=_wait_message;return this;}
    private Ajax _put(String name,String value){
        try {
            this._body.add(name+"="+URLEncoder.encode(value,"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return this;
    }
    Ajax put(String name,Object value) {
        return _put(name,String.valueOf(value));
    }
    Ajax put(String name,String value) {
        return _put(name,value);
    }

    private Ajax _arg(String name,String value){
        try {
            _args.add(name+"="+URLEncoder.encode(value,"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return this;
    }
    Ajax arg(String name,Object value) {
        return _arg(name,String.valueOf(value));
    }
    Ajax arg(String name,String value) {
        return _arg(name,value);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        _dialog_message(MSG_WAIT,_wait_message);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        _dialog.dismiss();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            String url_str=Ajax._root+"?entry="+this._entry+"&action="+this._action;
            if (_compte!=null)
                url_str += "&who=" + _compte.type();

            if (_args.size()>0)
                url_str += "&" + String.join("&",_args);


            System.out.println(url_str);
            URL url=new URL(url_str);
            HttpURLConnection con=(HttpURLConnection)url.openConnection();
            con.setRequestMethod(this._method.toUpperCase());
            if (_compte!=null)
                con.setRequestProperty("Cookie","PHPSESSID="+_compte.session());

            if (this._body!=null && this._body.size()>0){
                    con.setDoOutput(true);
                    String postData = String.join("&",this._body);
                    OutputStream outputStream = con.getOutputStream();
                    outputStream.write(postData.getBytes("UTF-8"));
                    outputStream.flush();
                    outputStream.close();
             }
            // Get response body
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            con.disconnect();
            JSONObject res_json=new JSONObject(response.toString());
            if (!res_json.has("status")) {
                _data = new String("wrong response");
                return null;
            }
            Boolean res_j_status=res_json.getBoolean("status");
            if (res_j_status == null){
                _data = new String("wrong response");
                return null;
            }
            _status=res_j_status.booleanValue();
            _data=res_json.get("data");

        } catch (MalformedURLException e) {
           _data=e.getMessage();
        } catch (IOException e) {
            _data=e.getMessage();
        } catch (JSONException e) {
            _data=e.getMessage();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
        if (!_status) {
            _dialog_message(MSG_ERR, (String) _data);
            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    _dialog.dismiss();
                }
            }, 1000);
        } else {
            if(_ok_message!=null) {
                _dialog_message(MSG_OK, _ok_message);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        _dialog.dismiss();
                        _on.Ok(_data);
                    }
                },1000);
            }else {
                _dialog.dismiss();
                System.out.println(_data.toString());
                _on.Ok(_data);
            }
        }
    }

    void start(On _on){
        this._on=_on;
        _dialog_init();
        this.execute();
    }

    private void _dialog_init(){
        LayoutInflater inflater = _activity.getLayoutInflater();
        _dialog_view = inflater.inflate(R.layout.layout_ajax, null);
        _dialogb=new AlertDialog.Builder(_activity);
        _dialogb.setView(_dialog_view);
        _dialogb.setCancelable(false);
        _dialog_gone_all();
        _dialog=_dialogb.show();
    }
    private void _dialog_gone_all(){
        ((ImageView)_dialog_view.findViewById(R.id.img_error)).setVisibility(View.GONE);
        ((ImageView)_dialog_view.findViewById(R.id.img_ok)).setVisibility(View.GONE);
        ((ProgressBar)_dialog_view.findViewById(R.id.progress_wait)).setVisibility(View.GONE);
        ((TextView)_dialog_view.findViewById(R.id.txt_message)).setText("");
    }
    private final static int MSG_OK=0;
    private final static int MSG_ERR=1;
    private final static int MSG_WAIT=2;

    private void _dialog_message(int msg_type,String message){
        _dialog_gone_all();
        if (message==null) {
            if (msg_type==MSG_WAIT)
                message = "Traitement...";
            else if(msg_type==MSG_OK)
                message = "Fait";
            else
                message = "Erreur inconnue";
        }
        if (msg_type==MSG_WAIT)
            ((ProgressBar)_dialog_view.findViewById(R.id.progress_wait)).setVisibility(View.VISIBLE);
        else if (msg_type==MSG_OK)
            ((ImageView)_dialog_view.findViewById(R.id.img_ok)).setVisibility(View.VISIBLE);
        else
            ((ImageView)_dialog_view.findViewById(R.id.img_error)).setVisibility(View.VISIBLE);
        ((TextView)_dialog_view.findViewById(R.id.txt_message)).setText(message);
        ((TextView)_dialog_view.findViewById(R.id.txt_message)).setTextColor(msg_type==MSG_ERR?Color.RED:Color.BLACK);
    }


    public static Ajax get(Activity _activity){
        return new Ajax(_activity).method("get");
    }
    public static Ajax post(Activity _activity){
        return new Ajax(_activity).method("post");
    }
}
