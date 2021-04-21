package com.huawei1.huaweimsan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ResultDeRechParNom extends ListActivity {

	private ProgressDialog pDialog;
	JSONParser jParser = new JSONParser();
	ArrayList<HashMap<String, String>> siteslist;
	private static String url_Result_RechParNom = "http://192.168.43.85/ScriptsMSANS/Result_RechParNom.php";
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_SITES = "sites";
	private static final String TAG_IDMSN = "ID";
	private static final String TAG_NAME = "SITE_NAME";
	private String message;
	private int success;
	private String inputnom;
	JSONArray sites = null;
	private String user;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.resul_rech);
		

		Bundle infoNom = this.getIntent().getExtras();
		
		if(infoNom != null){
			inputnom=infoNom.getString("nom");
			user = infoNom.getString("user");
		}
		
		siteslist = new ArrayList<HashMap<String, String>>();
		
		new LoadSrearchedResponsables().execute();
		
		ListView lv = getListView();
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String idmsn = ((TextView) view.findViewById(R.id.idmsn)).getText().toString();
				if(user.equals("1")){
				Intent in = new Intent(getApplicationContext(),ModifierSite.class);
				in.putExtra(TAG_IDMSN,idmsn);
				startActivityForResult(in, 100);
				}
				else{
					Intent in = new Intent(getApplicationContext(),ModifierSiteUt.class);
					in.putExtra(TAG_IDMSN,idmsn);
					startActivityForResult(in, 100);
				}
			}
		});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 100) {
			Intent intent = getIntent();
			finish();
			startActivity(intent);
		}
		if(resultCode==1000){
			Intent i = getIntent();
			setResult(10, i);
			finish();
			
		}
	}

	class LoadSrearchedResponsables extends AsyncTask<String, String, String> {
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ResultDeRechParNom.this);
			pDialog.setMessage("Chargement. Veuillez patienter un instant...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		protected String doInBackground(String... args) {

			MCrypt mcrypt = new MCrypt();
			String ename=null;

			try{
				ename = MCrypt.bytesToHex( mcrypt.encrypt(inputnom));		
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("SITE_NAME",ename));
			JSONObject json = jParser.makeHttpRequest(url_Result_RechParNom, "GET", params);
			Log.d("sites recherchés : ", json.toString());
			try {
				message = json.getString("message");
				success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					
					sites = json.getJSONArray(TAG_SITES);
					for (int i = 0; i < sites.length(); i++) {
						JSONObject c = sites.getJSONObject(i);
						String eID = c.getString(TAG_IDMSN);
						String eName = c.getString(TAG_NAME);
						String ID=null;
						String name=null;
						try{
							ID = new String( mcrypt.decrypt(eID));
							name = new String( mcrypt.decrypt(eName));
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
						HashMap<String, String> map = new HashMap<String, String>();
						map.put(TAG_IDMSN, ID);
						map.put(TAG_NAME, name);
						siteslist.add(map);
					}
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(String file_url) {
			pDialog.dismiss();
			if(success!=1){
				Toast.makeText(ResultDeRechParNom.this,message,Toast.LENGTH_SHORT).show();
				Intent i = getIntent();
				setResult(10, i);
				finish();
			}
			runOnUiThread(new Runnable() {

				public void run() {
					ListAdapter adapter = new SimpleAdapter(
							ResultDeRechParNom.this, siteslist,
							R.layout.list_item, new String[] { TAG_IDMSN,
									TAG_NAME},
									new int[] { R.id.idmsn, R.id.name });
					setListAdapter(adapter);
				}
			});


		}

	}
}
