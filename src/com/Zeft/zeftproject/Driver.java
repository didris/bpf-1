package com.Zeft.zeftproject;
import java.util.LinkedList;
import java.util.List;

import maps.GoogleMapv2;

import com.Zeft.zeftproject.R;
import com.Zeft.zeftproject.ListView.SearchCategoryProducts;
import com.Zeft.zeftproject.ListView.SearchProductAdapter;
import com.Zeft.zeftproject.ListView.SearchProducts;
import com.example.bdf.SQLite.SQLiteHelper;
import com.example.bdf.data.Category;
import com.example.bdf.data.Product;
import com.example.bdf.data.UserProfile;
import com.example.bdf.data.Vendor;
import com.example.bdf.data.VendorHasProduct;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;

public class Driver extends Activity implements OnClickListener{
	private Button btn_log;
	private Button btn_bar;
	private Typeface typeface;
	private TextView txt_welcome;
	private Spinner spinner;
	private Typeface typeface2;
	private SQLiteHelper db;
	private EditText loginUsername; 
	private EditText loginPwd;
	private Dialog loginDialog;
	private Button btn_signup;
	private Vendor vendor;
	private Button btn_search_for_products;
	private Button btn_search_cat;
	private GoogleMap map;
	private String stringLatitude;
	private String stringLongitude;
	private int returninig;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_driver);
		////////////////////////////////////////////
		/////////////////////////////////////////////
		///ZINITALIZATION
		db=SQLiteHelper.getInstance(this);
		vendor = new Vendor();
		typeface = Typeface.createFromAsset(getAssets(), "abc.TTF");
		typeface2 = Typeface.createFromAsset(getAssets(), "abc2.ttf");
		btn_log = (Button) findViewById(R.id.btn_login);
		btn_bar = (Button) findViewById(R.id.btn_search_by_barcode);
		btn_signup = (Button) findViewById(R.id.btn_signup);
		btn_search_for_products = (Button) findViewById(R.id.btn_search_products);
		txt_welcome = (TextView)  findViewById(R.id.txt_welcome);
		btn_search_cat = (Button) findViewById(R.id.btn_search_cat);
		spinner = (Spinner) findViewById(R.id.cat_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.planets_array, R.layout.spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
		/////////////////////////////////////////  
		///TYPEFACE
		btn_bar.setTypeface(typeface);
		txt_welcome.setTypeface(typeface2);
		////////////////////////////////////////
		///LISTENERS
		btn_bar.setOnClickListener(this);
		btn_search_for_products.setOnClickListener(this);
		btn_log.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				loginDialog = new Dialog(Driver.this);

				loginDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				loginDialog.setContentView(R.layout.login_layout);
				loginUsername = (EditText) loginDialog.findViewById(R.id.etxt_username);
				loginPwd = (EditText) loginDialog.findViewById(R.id.etxt_pwd);
				TextView txt_header = (TextView) loginDialog.findViewById(R.id.txt_Credintals);
				Button btn_login = (Button) loginDialog.findViewById(R.id.btn_login_in);
				btn_login.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Vendor vendor=db.getVendor(loginUsername.getText().toString());
						if(vendor.getId()!=0 &&vendor.getPassword().equals(loginPwd.getText().toString()))

						{
							UserProfile.login(vendor.getId(), getApplicationContext());
							Intent i= new Intent(getApplicationContext(),Vendor_info.class);
							Bundle b = new Bundle();
							b.putInt("UserID", vendor.getId());
							i.putExtras(b);
							startActivity(i);
							finish();
							loginDialog.cancel();
						}
						else
						{
							Toast.makeText(getApplicationContext(), "Wrong username or password",Toast.LENGTH_SHORT).show();

						}

					} 
				});
				loginUsername.setTypeface(typeface2);
				loginPwd.setTypeface(typeface2);
				txt_header.setTypeface(typeface2);
				loginDialog.show();

			}
		});

		btn_signup.setOnClickListener(this);
		btn_search_cat.setOnClickListener(this);


	}

	@Override
	public void onClick(View v)
	{
		if(v.getId() == R.id.btn_search_cat)
		{
			if(spinner.getSelectedItem().toString().equals("") 
					|| spinner.getSelectedItem().toString().equalsIgnoreCase("Search By Category"))
			{
				Toast.makeText(getApplicationContext(), "Chosse A Category", Toast.LENGTH_SHORT).show();
			}
			else
			{
				Intent i = new Intent(getApplicationContext() , SearchCategoryProducts.class);
				Bundle b = new Bundle();
				b.putString("category", (String)spinner.getSelectedItem());
				i.putExtras(b);
				startActivity(i);			


			}
		}
		if(v.getId() == R.id.btn_search_by_barcode)
		{
			IntentIntegrator scanIntegrator = new IntentIntegrator(this);
			scanIntegrator.initiateScan();
		}
		if(v.getId() == R.id.btn_search_products)
		{
			Intent i = new Intent(getApplicationContext(),Search_for_product.class);
			startActivity(i);

		}
		if(v.getId() == R.id.btn_signup)
		{
			Dialog d = new Dialog(Driver.this);
			//////////////////////
			//ADDING VIEW
			d.requestWindowFeature(Window.FEATURE_NO_TITLE);
			d.setContentView(R.layout.sign_up);
			////////////////////////////
			///INTALIZATION
			TextView txt_header = (TextView) d.findViewById(R.id.txt_Credintals);
			final EditText etxt_name = (EditText) d.findViewById(R.id.etxt_name);
			final EditText etxt_pwd = (EditText) d.findViewById(R.id.etxt_pwd);
			final EditText etxt_pwd_conf = (EditText) d.findViewById(R.id.etxt_pwd_conf);
			final EditText etxt_email = (EditText) d.findViewById(R.id.etxt_pwd_email);
			final EditText etxt_phone = (EditText) d.findViewById(R.id.etxt_pwd_phone);
			final EditText etxt_lat = (EditText) d.findViewById(R.id.etxt_lat);
			final EditText etxt_long = (EditText) d.findViewById(R.id.etxt_longitude);
			Button btn_sign = (Button) d.findViewById(R.id.btn_login_in);
			// check if GPS enabled
			GPSTracker gpsTracker = new GPSTracker(this);
			if (gpsTracker.getIsGPSTrackingEnabled())
			{
				stringLatitude = String.valueOf(gpsTracker.latitude);
				stringLongitude = String.valueOf(gpsTracker.longitude);
				etxt_lat.setText(stringLatitude);
				etxt_long.setText(stringLongitude);

			}
			else
			{
				Toast.makeText(getApplicationContext(), "Problem Of Finding Your Location , Add it Manually" , Toast.LENGTH_SHORT).show();
			}
			///////////////////////////////
			txt_header.setTypeface(typeface2);
			etxt_name.setTypeface(typeface2);
			etxt_pwd.setTypeface(typeface2); 
			etxt_pwd_conf.setTypeface(typeface2);
			etxt_email.setTypeface(typeface2);
			etxt_phone.setTypeface(typeface2);
			etxt_lat.setTypeface(typeface2);
			etxt_long.setTypeface(typeface2);
			///////////////////
			//LISTENER
			btn_sign.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {


					if(!(etxt_name.getText()+"").equals("") && !(etxt_phone.getText()+"").equals("") 
							&&!(etxt_pwd.getText()+"").equals("")&&!(etxt_pwd_conf.getText()+"").equals("") 
							&&!(etxt_email.getText()+"").equals("") &&!(etxt_lat.getText()+"").equals("")
							&&!(etxt_long.getText()+"").equals(""))
					{
						if(etxt_pwd.getText().toString().equals(etxt_pwd_conf.getText().toString()))
						{
							//// 0 --> lang //// 1 ---> long ////// 2 ---> title ////////// 3 ----> info  ///// 4 ---> phone
							Double Locations[] = {Double.parseDouble(etxt_lat.getText().toString()) , Double.parseDouble(etxt_long.getText().toString())}; 
							String title = etxt_name.getText().toString();
							String info = etxt_email.getText().toString();
							String phone = etxt_phone.getText().toString();
							//adding vendor info to the DataBase
							vendor.setName(title);
							vendor.setPassword(etxt_pwd.getText().toString());
							vendor.setLatitude(Locations[0]);
							vendor.setLongitude(Locations[1]);
							vendor.setEmail(info);
							vendor.setPhone(phone);
							Vendor addedVendor = db.addVendor(vendor);
							UserProfile.login(addedVendor.getId(), getApplicationContext());
							Intent i= new Intent(getApplicationContext(),Vendor_info.class);
							Bundle b = new Bundle();
							b.putInt("UserID", vendor.getId());
							//Toast.makeText(getApplicationContext(), ""+vendor.getId(), Toast.LENGTH_SHORT).show();
							i.putExtras(b);
							startActivity(i);
							finish();
						}
						else
						{
							Toast.makeText(getApplicationContext(), "PassWord Dos'nt Match!", Toast.LENGTH_SHORT).show();
						}
					}
					else{
						Toast.makeText(getApplicationContext(), "Some Feilds Are Empty", Toast.LENGTH_SHORT).show();
					}

				}
			});
			//////////////////
			///STARTING VIEW
			d.show();
		}

	}
	public void onActivityResult(int requestCode, int resultCode, Intent intent) 
	{
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (scanningResult != null)
		{
			String scanContent = scanningResult.getContents();
			String scanFormat = scanningResult.getFormatName();
			if(!(scanContent==null||scanContent.equals("")))
			{
				if((scanContent.subSequence(0, 3)+"").equalsIgnoreCase("729"))
				{
					Dialog d = new Dialog(Driver.this);
					d.requestWindowFeature(Window.FEATURE_NO_TITLE);
					d.setContentView(R.layout.alerting_il);
					d.show();
				}
				else{
					Intent i = new Intent(getApplicationContext() , SearchProducts.class);
					Bundle b = new Bundle();
					b.putString("barcode", scanContent );
					i.putExtras(b);
					startActivity(i);			
				}
			}
		
		}
		else
		{
			Toast toast = Toast.makeText(getApplicationContext(), 
					"No scan data received!", Toast.LENGTH_SHORT);
			toast.show();
		}
	}
	public class MyOnItemSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent,
				View view, int pos, long id) {
			Toast.makeText(spinner.getContext(), "The planet is " +
					spinner.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();
		}

		public void onNothingSelected(AdapterView parent) {
			// Do nothing.
		}
	}
}
