package edu.uwg.danielstamps.connect64;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class StageListActivity extends ListActivity {

	private String[] stageDescriptions;
	private ArrayAdapter<String> adapter;
	public static final String STAGE_KEY = "STAGE_KEY";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.stageDescriptions = getResources().getStringArray(R.array.stage_descriptions);
		this.createListAdapter();
		this.createListView();
	}

	private void createListAdapter() {
		this.adapter = new ArrayAdapter<String>(this, R.layout.stages, this.stageDescriptions);
		this.setListAdapter(adapter);
	}

	private void createListView() {
		ListView theListView = getListView();
		registerForContextMenu(theListView);
	}

	@Override
    protected void onListItemClick(ListView l, View v, int position, long id){
		String selection = getListView().getItemAtPosition(position).toString();
		int selectedStage = this.getSelectedStage(selection);
    	Intent intent = new Intent();
    	intent.putExtra(STAGE_KEY, selectedStage);
    	setResult(RESULT_OK, intent);
    	finish();
    }

	private int getSelectedStage(String title){
		int selectedStage = 1;
		for(int i = 1; i <= 8; i++){
			if(title.contains(Integer.toString(i))){
				selectedStage = i;
			}
		}
		return selectedStage;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.stage_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
