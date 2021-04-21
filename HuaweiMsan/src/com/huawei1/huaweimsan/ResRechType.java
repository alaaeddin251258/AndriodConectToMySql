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

public class ResRechType extends ListActivity {

	private ProgressDialog pDialog;
	JSONParser jParser = new JSONParser();
	ArrayList<HashMap<String, String>> siteslist;
	private static String url_Result_RechParDirection = "http://192.168.43.85/ScriptsMSANS/Result_RechParType.php";
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_SITES = "sites";
	private static final String TAG_IDMSN = "ID";
	private static final String TAG_NAME = "SITE_NAME";
	private String inputType;
	JSONArray sites = null;
	private String message;
	private int success;
	private String user;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.resul_rech);
		
		Bundle infoType = this.getIntent().getExtras();
		if(infoType != null){
			inputType=infoType.getString("type");
			user = infoType.getString("user");
		}

		siteslist = new ArrayList<HashMap<String, String>>();

		new LoadSrearchedResponsables().execute();

		ListView lv = getListView();

		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String idmsan = ((TextView) view.findViewById(R.id.idmsn)).getText().toString();
				if(user.equals("1")){
					Intent in = new Intent(getApplicationContext(),ModifierSite.class);
					in.putExtra(TAG_IDMSN,idmsan);
					startActivityForResult(in, 100);
					}
					else{
						Intent in = new Intent(getApplicationContext(),ModifierSiteUt.class);
						in.putExtra(TAG_IDMSN,idmsan);
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
			pDialog = new ProgressDialog(ResRechType.this);
			pDialog.setMessage("Loading... Please wait a moment!");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		protected String doInBackground(String... args) {

			MCrypt mcrypt = new MCrypt();
			String eType=null;

			try{
				eType = MCrypt.bytesToHex(mcrypt.encrypt(inputType));		
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("type",eType));
			JSONObject json = jParser.makeHttpRequest(url_Result_RechParDirection, "GET", params);
			Log.d("Sites recherch�s : ", json.toString());
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
							ID = new String( mcrypt.decrypt( eID) );
							name = new String( mcrypt.decrypt(eName) );
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
			runOnUiThread(new Runnable() {
				public void run() {
					ListAdapter adapter = new SimpleAdapter(
							ResRechType.this, siteslist,
							R.layout.list_item, new String[] { TAG_IDMSN,
									TAG_NAME},
									new int[] { R.id.idmsn, R.id.name });
					setListAdapter(adapter);
				}
			});
			if(success!=1){
				Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
				Intent i = getIntent();
				setResult(10, i);
				finish();
			}
		}
	}
}
