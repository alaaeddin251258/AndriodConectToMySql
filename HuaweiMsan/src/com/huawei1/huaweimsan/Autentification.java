package com.huawei1.huaweimsan;


import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Autentification extends Activity implements OnClickListener {

	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();	
	private EditText edtLogin;
	private EditText edtMotDePasse;
	private Button btnValider;
	private static String url_verification_authentification = "http://192.168.43.85/ScriptsMSANS/Authentification.php";
	private String message;
	private String user;
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.authentification);
		edtLogin = (EditText) findViewById(R.id.edtLogin);
		edtMotDePasse = (EditText) findViewById(R.id.edtMotdePasse);
		btnValider = (Button) findViewById(R.id.btnValiderAuth);
		btnValider.setOnClickListener(this);
	}

	public void onClick(View arg0) {
		if(edtLogin.getText().toString().equals("") || edtMotDePasse.getText().toString().equals("")){
			Toast.makeText(getApplicationContext(),"You forgot to specify login or password.",Toast.LENGTH_SHORT).show();
		}
		else
		{
			new Verification().execute();
		}

	}
	class Verification extends AsyncTask<String, String, String> {

		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Autentification.this);
			pDialog.setMessage("Verification");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {
			String login = edtLogin.getText().toString();
			String motdepass = edtMotDePasse.getText().toString();
			MCrypt mcrypt = new MCrypt();
			String eLogin=null;
			String eMotdepass=null;
			try{
				eLogin = MCrypt.bytesToHex( mcrypt.encrypt(login));
				eMotdepass = MCrypt.bytesToHex( mcrypt .encrypt(motdepass));
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("pseudo",eLogin));
			params.add(new BasicNameValuePair("motdepass",eMotdepass));

			JSONObject json = jsonParser.makeHttpRequest(url_verification_authentification,
					"POST", params);
			Log.d("Verification", json.toString());
			
			try {
				message = json.getString("message");
				int success = json.getInt("success");
				
				if(success == 1 ){
                    user = "1";
					Intent inet = new Intent(getApplicationContext(),PageDAcceuil.class);
					inet.putExtra("user",user);
					startActivity(inet);
					finish();
				}
				else if(success == 2)
				{
				    user="2";				
					Intent inet = new Intent(getApplicationContext(),Rechercher_MSAN.class);
					inet.putExtra("user",user);
					startActivity(inet); 
					finish();
				}
				else {}
				
			} catch (JSONException e) {		
				e.printStackTrace();
			}

			return null;
		}
		
		protected void onPostExecute(String file_url) {
			pDialog.dismiss();
			Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
			edtLogin.setText("");
			edtMotDePasse.setText("");

		}
	}
}
