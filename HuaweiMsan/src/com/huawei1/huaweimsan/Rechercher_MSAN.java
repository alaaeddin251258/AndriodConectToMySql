package com.huawei1.huaweimsan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.os.Bundle;

public class Rechercher_MSAN extends Activity {
	
	private Spinner spinner1;
	private Button btnValiderRech;
	private EditText inputNom;
    private String user;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rech_msan);
        
		Intent i = getIntent();
	    user = i.getStringExtra("user");
	    
		inputNom =(EditText) findViewById(R.id.edtNomRech);		
		spinner1 = (Spinner) findViewById(R.id.spinner1);
		
		btnValiderRech = (Button) findViewById(R.id.btnValiderRech);

		inputNom.setEnabled(false);	
		spinner1.setEnabled(false);
		
	}

	public void onRadioButtonClicked(View view) {
		boolean checked = ((RadioButton) view).isChecked();
		switch(view.getId()) {
		
		case R.id.rdbNom:
			
			if (checked)
			{

				inputNom.setEnabled(true);
				spinner1.setEnabled(false);
				btnValiderRech.setOnClickListener(new OnClickListener() {

					public void onClick(View arg0) {

						Intent inet = new Intent(Rechercher_MSAN.this,ResultDeRechParNom.class);
						inet.putExtra("nom",inputNom.getText().toString());
						inet.putExtra("user",user);
						if(inputNom.getText().toString().equals("")){
							
							Toast.makeText(Rechercher_MSAN.this,"Please enter the site name...",Toast.LENGTH_SHORT).show();	
							
						}else{		
							startActivityForResult(inet, 10);
							
						}

					}
				});
			}
			break;
		case R.id.rdbType:
			if (checked)
			{
				spinner1.setEnabled(true);
				spinner1.setOnItemSelectedListener(new MyOnItemSelectedListener());
				inputNom.setEnabled(false);

				
				btnValiderRech.setOnClickListener(new OnClickListener() {

					public void onClick(View arg0) {
						Intent inet = new Intent(Rechercher_MSAN.this,ResRechType.class);
						String s1 = String.valueOf(spinner1.getSelectedItem());
						inet.putExtra("type",s1);
						inet.putExtra("user",user);
						startActivityForResult(inet, 10);
						
					}
				});
			}
			break;

		}
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 10) {
			Intent intent = getIntent();
			finish();
			startActivity(intent);
		}
	}
}
