package com.huawei1.huaweimsan;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class ModifierSite extends Activity {
	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
	private EditText txtName;
	private EditText txtid;
	private EditText txttype;
	private EditText txtDirection;
	private EditText txtLongitude;
	private EditText txtLatitiude;
	private EditText txtAdressIP;
	private EditText txtVDBM;
	private EditText txtASPB;
	private Spinner spStatus;
	
	private Button btnEnrg;
	private Button btnSupp;
	private Button btnModifeir;
	
	private String s1;
	private String s2;
	private String idmsn;
	private String eidmsn;
	private static final String url_detials = "http://192.168.43.85/ScriptsMSANS/AficherLesDetailsDeSite.php";
	private static final String url_modifier = "http://192.168.43.85/ScriptsMSANS/ModifierSite.php";
	private static final String url_supprimer = "http://192.168.43.85/ScriptsMSANS/SupprimerSite.php";
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_SITE = "site";
	private static final String TAG_IDMSAN = "ID";
	private static final String TAG_NAME = "SITE_NAME";
	private static final String TAG_TYPE = "TYPE";
	private static final String TAG_SITEID = "SITE_ID";
	private static final String TAG_VDPM = "VDPM_BOARD";
	private static final String TAG_ASPB = "ASPB_BOARD";
	private static final String TAG_DIRECTION = "DIRECTION";
	private static final String TAG_LONGITUDE = "LONGITUDE";
	private static final String TAG_LATITUDE = "LATITUDE";
	private static final String TAG_ADDRESS_IP = "ADDRESS_IP";
	private static final String TAG_STATUS = "STATUS";
	
	

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modifier_respo);
		
		  if (android.os.Build.VERSION.SDK_INT > 9) {
		      StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		      StrictMode.setThreadPolicy(policy);
		    }
	
		btnSupp = (Button) findViewById(R.id.btnDeletMo);
		btnModifeir = (Button) findViewById(R.id.btnEditMo);
		btnEnrg = (Button) findViewById(R.id.btnsaveMo);

		btnEnrg.setEnabled(false);

		Intent i = getIntent();
		idmsn = i.getStringExtra(TAG_IDMSAN);
		MCrypt mcrypt = new MCrypt();
		
		try{
			eidmsn = MCrypt.bytesToHex( mcrypt.encrypt(idmsn));	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		new GetSiteDetails().execute();
	
	
	
		
		


		btnModifeir.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				txtName.setEnabled(true);
				txtDirection.setEnabled(true);
				txttype.setEnabled(true);
				txtLatitiude.setEnabled(true);
				txtLongitude.setEnabled(true);
				txtAdressIP.setEnabled(true);
				txtid.setEnabled(true);
				spStatus.setEnabled(true);
				btnEnrg.setEnabled(true);
			}
		});
		btnEnrg.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				showEnregButtonDialog();
			}
		});

		btnSupp.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				showSuppgButtonDialog();

			}
		});

	}

	private void showEnregButtonDialog() {

		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle("Saving modifications!");
		dialogBuilder.setMessage("Do you want to save the modifications ?");
		dialogBuilder.setIcon(R.drawable.save);
		dialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {


			public void onClick(DialogInterface arg0, int arg1) {
				new SaveResponsableDetails().execute();		
			}
		});
		dialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {


			public void onClick(DialogInterface arg0, int arg1) {
				new GetSiteDetails().execute();				
			}
		});

		AlertDialog alertDialog = dialogBuilder.create();
		alertDialog.show();

	}
	private void showSuppgButtonDialog() {

		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle("Attention!");
		dialogBuilder.setMessage("Do you want delete this site ? ");
		dialogBuilder.setIcon(R.drawable.delete);
		dialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {


			public void onClick(DialogInterface arg0, int arg1) {
				new DeleteResponsable().execute();
			}
		});
		dialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {


			public void onClick(DialogInterface arg0, int arg1) {
				new GetSiteDetails().execute();				
			}
		});

		AlertDialog alertDialog = dialogBuilder.create();
		alertDialog.show();

	}
	
	class GetSiteDetails extends AsyncTask<String, String, String> {

		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ModifierSite.this);
			pDialog.setMessage("Loading... Please wait a moment!!");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... params) {
			runOnUiThread(new Runnable() {
				public void run() {

					int success;
					try {
						MCrypt mcrypt = new MCrypt();

						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("ID", eidmsn));
						JSONObject json = jsonParser.makeHttpRequest(
								url_detials, "GET", params);
						Log.d("Details du site", json.toString());
						success = json.getInt(TAG_SUCCESS);
						if (success == 1) {
							JSONArray siteObj = json.getJSONArray(TAG_SITE); 
							JSONObject site = siteObj.getJSONObject(0);

							txtName = (EditText) findViewById(R.id.edtNomModif);
							txtid = (EditText) findViewById(R.id.edtSiteIDSMo);
							txttype = (EditText) findViewById(R.id.edtTypeMo);
							txtDirection  =  (EditText) findViewById(R.id.edtDirectionMo);
							spStatus = (Spinner) findViewById(R.id.spStatusMo);
							txtLongitude  =  (EditText) findViewById(R.id.edtLongitudeMo);
							txtLatitiude  =  (EditText) findViewById(R.id.edtLatitideMo);
							txtAdressIP  =  (EditText) findViewById(R.id.edtAddressMo);
							txtVDBM = (EditText) findViewById(R.id.edtVDPMsMo);
							txtASPB = (EditText) findViewById(R.id.edtASPBMo);
							
							
							try{
								
								txtName.setText(new String(mcrypt.decrypt(site.getString(TAG_NAME))));
								txtid.setText(new String( mcrypt.decrypt(site.getString(TAG_SITEID))));
								txtDirection.setText(new String( mcrypt.decrypt(site.getString(TAG_DIRECTION))));
								txttype.setText(new String(mcrypt.decrypt(site.getString(TAG_TYPE))));
								txtLongitude.setText(new String(mcrypt.decrypt(site.getString(TAG_LONGITUDE))));
								txtLatitiude.setText(new String(mcrypt.decrypt(site.getString(TAG_LATITUDE))));
								txtAdressIP.setText(new String(mcrypt.decrypt(site.getString(TAG_ADDRESS_IP))));
								txtASPB.setText(new String(mcrypt.decrypt(site.getString(TAG_ASPB))));
								txtVDBM.setText(new String(mcrypt.decrypt(site.getString(TAG_VDPM))));
								
								
								String sptxt = new String(mcrypt.decrypt(site.getString(TAG_STATUS)));							
								ArrayAdapter<String> myAdap = (ArrayAdapter) spStatus.getAdapter(); 
								int spinnerPosition = myAdap.getPosition(sptxt);
								spStatus.setSelection(spinnerPosition);
								
			
							}
							catch(Exception e)
							{
								e.printStackTrace();
							}
							
							txtName.setEnabled(false);
							txtDirection.setEnabled(false);
							txttype.setEnabled(false);
							txtLatitiude.setEnabled(false);
							txtLongitude.setEnabled(false);
							txtAdressIP.setEnabled(false);
							txtid.setEnabled(false);
							spStatus.setEnabled(false);
							

							btnEnrg.setEnabled(false);


						}else{
						}
					} catch (JSONException e) {
						e.printStackTrace();
						
					}
				}
			});

			return null;
		}

		protected void onPostExecute(String file_url) {
			pDialog.dismiss();
		}
	}
		

	
	class SaveResponsableDetails extends AsyncTask<String, String, String> {

		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ModifierSite.this);
			pDialog.setMessage("Saving ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		protected String doInBackground(String... args) {
			MCrypt mcrypt = new MCrypt();

			
			String name = txtName.getText().toString();
			String idd = txtid.getText().toString();
			String type= txttype.getText().toString();
			String direction = txtDirection.getText().toString();
			String address = txtDirection.getText().toString();
			String vdpm = txtVDBM.getText().toString();
			String aspb = txtASPB.getText().toString();
			String latitude = txtLatitiude.getText().toString();
			String longitude = txtLongitude.getText().toString();
			String status = spStatus.getSelectedItem().toString();
			
			String ename = null;
			String eidd = null;
			String etype= null;
			String edirection = null;
			String eaddress = null;
			String evdpm = null;
			String easpb = null;
			String elatitude = null;
			String elongitude = null;
			String estatus = null;
			

		

			try{
				ename = MCrypt.bytesToHex( mcrypt .encrypt(name));
				eidd = MCrypt.bytesToHex( mcrypt .encrypt(idd));
				etype = MCrypt.bytesToHex( mcrypt .encrypt(type));
				edirection = MCrypt.bytesToHex( mcrypt .encrypt(direction));
				eaddress = MCrypt.bytesToHex( mcrypt .encrypt(address));
				evdpm = MCrypt.bytesToHex( mcrypt .encrypt(vdpm));
				easpb = MCrypt.bytesToHex( mcrypt .encrypt(aspb));
				elatitude = MCrypt.bytesToHex( mcrypt .encrypt(latitude));
				elongitude = MCrypt.bytesToHex( mcrypt .encrypt(longitude));
				estatus = MCrypt.bytesToHex( mcrypt .encrypt(status));
		
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(TAG_IDMSAN,eidmsn));
			params.add(new BasicNameValuePair(TAG_NAME, ename));
			params.add(new BasicNameValuePair(TAG_SITEID,eidd));
			params.add(new BasicNameValuePair(TAG_TYPE, etype));
			params.add(new BasicNameValuePair(TAG_DIRECTION, edirection));
			params.add(new BasicNameValuePair(TAG_ADDRESS_IP, eaddress));
			params.add(new BasicNameValuePair(TAG_VDPM,evdpm));
			params.add(new BasicNameValuePair(TAG_ASPB, easpb));
			params.add(new BasicNameValuePair(TAG_LATITUDE, elatitude));
			params.add(new BasicNameValuePair(TAG_LONGITUDE, elongitude));
			params.add(new BasicNameValuePair(TAG_STATUS, estatus));
			JSONObject json = jsonParser.makeHttpRequest(url_modifier,
					"POST", params);
			try {
				
				int success = json.getInt(TAG_SUCCESS);
				s1 = json.getString("message");
				if (success == 1) {
					Intent i = getIntent();
					setResult(100, i);
					finish();	
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}
		protected void onPostExecute(String file_url) {
			pDialog.dismiss();
			Toast.makeText(getApplicationContext(),s1,Toast.LENGTH_SHORT).show();
		}
	}
	
	class DeleteResponsable extends AsyncTask<String, String, String> {
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ModifierSite.this);
			pDialog.setMessage("Deleting...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		protected String doInBackground(String... args) {
			int success;
			try {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("ID", eidmsn));
				JSONObject json = jsonParser.makeHttpRequest(
						url_supprimer, "POST", params);
				Log.d("Suppression", json.toString());
				success = json.getInt(TAG_SUCCESS);
				s2 = json.getString("message");
				if (success == 1) {
					Intent i = getIntent();
					setResult(100, i);
					finish();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(String file_url) {
			pDialog.dismiss();
			Toast.makeText(getApplicationContext(),s2,Toast.LENGTH_SHORT).show();

		}
	}




}


