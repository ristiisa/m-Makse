package ristiisa.mmakse;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Contacts.People;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Main extends Activity {
    private static final int PICK_CONTACT = 1;
    private static final int PICK_NUMBER = 2;
    private Button btnContacts; 
    private Button btnCall; 
    private EditText editSum; 
    private Main instance;
    private String selectedNumber = "";
	private SharedPreferences preferences;
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        btnContacts = (Button) findViewById(R.id.Button01); 
        btnCall = (Button) findViewById(R.id.Button02); 
        editSum = (EditText) findViewById(R.id.EditText01);
        
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editSum.setText(preferences.getString("defaultsum", "25.00"));
        
        instance = this;
        
        btnContacts.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_PICK);
		        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
		        startActivityForResult(intent, PICK_CONTACT);			
			}
		});
        
        btnCall.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				if(editSum.getText().length() == 0) {
					Toast.makeText(instance, "Palun sisesta summa makse sooritamiseks", Toast.LENGTH_LONG).show();
					return;
				}
				
				String[] n = editSum.getText().toString().replace(',', '.').split("\\.");
				int a = 0, b = 0;
				try {
					a = Integer.parseInt(n[0]);
					if(n.length>1) b = Integer.parseInt(n[1]);
				} catch (NumberFormatException e) {
					Toast.makeText(instance, "Palun sisesta korrektne summa", Toast.LENGTH_LONG).show();
				}
				
				if(selectedNumber.length() == 0) {
					Toast.makeText(instance, "Palun vali kontakt makse sooritamiseks", Toast.LENGTH_LONG).show();
					return;
				}
				
				String sum = String.format("%02d*%02d", a, b);
				
				try {
					startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+preferences.getString("phonenumber", "1214")+"*"+selectedNumber+"*"+sum)));
			    } catch (Exception e) {
			    	e.printStackTrace();
			    }
			}
		});
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.mainmenu, menu);
    	return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.item01:
				Intent i = new Intent(Main.this, Preferences.class);
				startActivity(i);
				Toast.makeText(Main.this,
						"Siin saad muuta vaikimisi sätteid.",
						Toast.LENGTH_LONG).show();
				break;
			case R.id.item02:
				finish();
				break;

		}
		return true;
	}
    
    @Override  
    public void onActivityResult(int reqCode, int resultCode, Intent data) {  
        super.onActivityResult(reqCode, resultCode, data);  
        switch (reqCode) {  
            case PICK_CONTACT:  
                if (resultCode == Activity.RESULT_OK) {  
                    Uri contactData = data.getData();  
                    Cursor c = managedQuery(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                    	int hasPhone = c.getInt(c.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                    	String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                    	if(hasPhone != 0) {
                    		Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId, null, null);
                    		ArrayList<String> pns = new ArrayList<String>();
                    		while (phones.moveToNext()) { 
                    			String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    			if(pns.indexOf(phoneNumber) == -1)
                    				pns.add(phoneNumber);
                    		}
                    		if(pns.size() > 1) {
	                    		Intent pnl = new Intent(this, PhoneNumberList.class);
	                			pnl.putExtra("numbers", pns.toArray(new String[pns.size()]));
	                			startActivityForResult(pnl, PICK_NUMBER);
                    		} else selectedNumber = pns.get(0);
                			
                    		String name = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
	                        btnContacts.setText(name);  
                    	} else {
                    		Toast.makeText(instance, "Sellel kontaktil pole ühtegi telefoni numbrit", Toast.LENGTH_LONG).show();
                    	}
                    }  
                }  
                break;  
            case PICK_NUMBER:
            	selectedNumber = data.getExtras().getString("number");
            	break;
        }  
    } 
}