package com.huawei1.huaweimsan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.os.Bundle;

public class PageDAcceuil extends Activity {

	
	private Button btnRechPage;
	private Button btnAjoutPage;
	private String user;

	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_acceuil);
		
		Intent i = getIntent();
	    user = i.getStringExtra("user");	
		
		btnAjoutPage = (Button) findViewById(R.id.btnAjoutPage);
		
		btnAjoutPage.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				
				
				Intent inet1 = new Intent(PageDAcceuil.this,AjoutSite.class);
				startActivity(inet1);
				
			}
		});
		
		btnRechPage = (Button) findViewById(R.id.btnRechPage);
		btnRechPage.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent inet2 = new Intent(PageDAcceuil.this,Rechercher_MSAN.class);
				inet2.putExtra("user",user);
				startActivity(inet2);
			}
		});
	}
}