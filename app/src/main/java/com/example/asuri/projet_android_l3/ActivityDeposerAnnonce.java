package com.example.asuri.projet_android_l3;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ActivityDeposerAnnonce extends AppCompatActivity {

    String prefPseudo;
    String prefMail;
    String prefTel;

    TextView pseudoAnnonce;
    TextView mailAnnonce;
    TextView telAnnonce;

    TextView titreAnnonce;
    TextView prixAnnonce;
    TextView villeAnnonce;
    TextView CPAnnonce;
    TextView descriptionAnnonce;

    Button deposerAnnonce;

    Annonce annonce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposer_annonce);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        this.prefPseudo = prefs.getString("pseudo", "");
        this.prefMail = prefs.getString("email", "");
        this.prefTel = prefs.getString("phone", "");

        if(Objects.equals(this.prefPseudo, "") || Objects.equals(this.prefMail, "") || Objects.equals(this.prefTel, "")){
            finish();
        }

        this.pseudoAnnonce = findViewById(R.id.pseudo);
        this.mailAnnonce = findViewById(R.id.mail);
        this.telAnnonce = findViewById(R.id.tel);
        pseudoAnnonce.setText(prefPseudo);
        mailAnnonce.setText(prefMail);
        telAnnonce.setText(prefTel);

        this.titreAnnonce = findViewById(R.id.titre);
        this.descriptionAnnonce = findViewById(R.id.description);
        this.prixAnnonce = findViewById(R.id.prix);
        this.villeAnnonce = findViewById(R.id.ville);
        this.CPAnnonce = findViewById(R.id.cp);

        this.deposerAnnonce = findViewById(R.id.buttonDeposer);

        this.deposerAnnonce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestData("https://ensweb.users.info.unicaen.fr/android-api/");
            }
        });
    }

    public void requestData(String uri) {

        StringRequest request = new StringRequest(Request.Method.POST, uri,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.getBoolean("success")){
                                annonce = AnnonceJSONParser.parseAnnonce(response);
                                Intent intent = new Intent(getApplicationContext(),ActivityVoirAnnonce.class);
                                intent.putExtra("annonce", annonce);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(), "ERREUR: "+jsonObject.getString("response"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ActivityDeposerAnnonce.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Override
                    protected Map<String,String> getParams(){
                        Map<String,String> params = new HashMap<String, String>();
                        params.put("apikey","21404260");
                        params.put("method","save");

                        params.put("titre", titreAnnonce.getText().toString());
                        params.put("description", descriptionAnnonce.getText().toString());
                        params.put("prix", prixAnnonce.getText().toString());
                        params.put("pseudo", pseudoAnnonce.getText().toString());
                        params.put("emailContact", mailAnnonce.getText().toString());
                        params.put("telContact", telAnnonce.getText().toString());
                        params.put("ville", villeAnnonce.getText().toString());
                        params.put("cp", CPAnnonce.getText().toString());

                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String,String> params = new HashMap<String, String>();
                        params.put("Content-Type","application/x-www-form-urlencoded");
                        return params;
                    }
                };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}
