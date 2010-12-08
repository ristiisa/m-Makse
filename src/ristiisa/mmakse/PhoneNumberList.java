package ristiisa.mmakse;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class PhoneNumberList extends ListActivity {
	private String[] numbers;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		numbers = (String[])getIntent().getExtras().getStringArray("numbers");//getIntent().getExtras().getStringArray("numbers");
	
		this.setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked, numbers));
		
		Intent data = new Intent(this, PhoneNumberList.class);
	    Bundle extras = new Bundle();
	    extras.putString("number", "");
	    data.putExtras(extras);
	    
		setResult(RESULT_OK, data);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		// Get the item that was clicked
		Object o = this.getListAdapter().getItem(position);
		String keyword = o.toString();
		
		Intent data = new Intent(this, PhoneNumberList.class);
	    data.putExtra("number", keyword);   
		setResult(RESULT_OK, data);
		finish();
	}
	
	@Override
	public void onBackPressed() {
	    finish();
	}
}
