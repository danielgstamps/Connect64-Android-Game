package edu.uwg.danielstamps.connect64;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class GridActivity extends Activity {

	private final String CURRENT_STAGE_KEY = "currentStageKey";

	private ArrayList<Button> allButtons;
	private ArrayList<Button> buttonHistory;
	private ArrayList<Button> starterButtons;

	private ArrayList<Integer> starterDigits;
	private ArrayAdapter<Integer> spinnerAdapter;

	private int currentDigit;
	private int currentStage;

	private static final int STAGE_REQUEST = 1;
	private TextView currentStageTextView;
	private Spinner enteringSpinner;

	private Button button00, button01, button02, button03, button04, button05,
			button06, button07, button10, button11, button12, button13,
			button14, button15, button16, button17, button20, button21,
			button22, button23, button24, button25, button26, button27,
			button30, button31, button32, button33, button34, button35,
			button36, button37, button40, button41, button42, button43,
			button44, button45, button46, button47, button50, button51,
			button52, button53, button54, button55, button56, button57,
			button60, button61, button62, button63, button64, button65,
			button66, button67, button70, button71, button72, button73,
			button74, button75, button76, button77;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_grid);

		this.allButtons = new ArrayList<Button>(); // store
		this.buttonHistory = new ArrayList<Button>(); // store
		this.starterButtons = new ArrayList<Button>(); // always, pop'd in
														// startStage
		this.starterDigits = new ArrayList<Integer>(); // always, pop'd in
														// startStage

		this.initializeWidgets(); // always
		this.populateButtonList(); // //
		this.setOnClickListeners(); // always

		 this.currentStage = 1;
		 this.startStage(this.currentStage);
	}

	@Override
	protected void onPause() {
		super.onPause();
		SharedPreferences preferences = getPreferences(MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(this.CURRENT_STAGE_KEY, this.currentStage);
		editor.commit();
	}

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences preferences = getPreferences(MODE_PRIVATE);
		this.currentStage = preferences.getInt(this.CURRENT_STAGE_KEY, 1);
//		this.startStage(this.currentStage);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == STAGE_REQUEST) {
			if (resultCode == RESULT_OK) {
				int selectedStage = data.getIntExtra(
						StageListActivity.STAGE_KEY, 1);
				this.currentStage = selectedStage;
				
				SharedPreferences preferences = getPreferences(MODE_PRIVATE);
				SharedPreferences.Editor editor = preferences.edit();
				editor.putInt(this.CURRENT_STAGE_KEY, this.currentStage);
				editor.commit();
				// this.startStage(this.currentStage);
			}
		}
	}

	private void populateEnteringSpinner() {
		this.spinnerAdapter = new ArrayAdapter<Integer>(this,
				R.layout.entering_spinner_item, new ArrayList<Integer>());
		for (int i = 1; i <= 64; i++) {
			spinnerAdapter.add(i);
		}
		for (Integer starter : this.starterDigits) {
			spinnerAdapter.remove(starter);
		}

		this.enteringSpinner.setAdapter(spinnerAdapter);
	}

	private void initializeCurrentDigit() {
		if (!this.starterDigits.contains(1)) {
			this.currentDigit = 1;
		} else {
			this.currentDigit = 2;
		}
	}

	private void incrementCurrentDigit() {
		this.currentDigit++;
		while (this.starterDigits.contains(currentDigit)) {
			currentDigit++;
		}
	}

	private void decrementCurrentDigit() {
		this.currentDigit--;
		while (this.starterDigits.contains(currentDigit)) {
			currentDigit--;
		}
	}

	private void setOnClickListeners() {

		// Main Grid Buttons
		for (Button gridButton : this.allButtons) {
			gridButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					Button button = (Button) view;
					button.setClickable(false);
					button.setText(Integer.toString(currentDigit));
					buttonHistory.add(button);
					spinnerAdapter.remove(currentDigit);
					incrementCurrentDigit();
					if (isBoardFull()) {
						findViewById(R.id.submitButton).setEnabled(true);
					}
				}
			});
		}

		// Undo Button
		findViewById(R.id.undoButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {

				if (currentDigit == 1) {
					return;
				}

				if (starterDigits.contains(1) && currentDigit == 2) {
					return;
				}

				if (buttonHistory.size() == 0) {
					return;
				}

				Button prevButton = buttonHistory.remove(buttonHistory.size() - 1);
				int prevDigit = Integer.parseInt(prevButton.getText()
						.toString());
				spinnerAdapter.add(prevDigit);
				spinnerAdapter.sort(new Comparator<Integer>() {
					@Override
					public int compare(Integer lhs, Integer rhs) {
						return lhs - rhs;
					}
				});
				enteringSpinner.setSelection(spinnerAdapter
						.getPosition(prevDigit));

				decrementCurrentDigit();
				prevButton.setText("");
				prevButton.setClickable(true);
				findViewById(R.id.submitButton).setEnabled(false);
			}
		});

		// Reset Button
		findViewById(R.id.resetButton).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View view) {
						resetButtons();
						startStage(currentStage);
					}
				});

		// Submit button
		findViewById(R.id.submitButton).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View view) {

						if (checkForWin()) {
							// Win text
							if (currentStage == 8) {
								currentStage = 0; // Temp loop fix
							}
							currentStage++;
							startStage(currentStage);
						} else {
							// Lose text
						}
					}
				});

		// Stage Select Button
		findViewById(R.id.stageSelectButton).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(getApplicationContext(),
								StageListActivity.class);
						startActivityForResult(intent, STAGE_REQUEST);
					}
				});

		this.enteringSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						int item = (Integer) parent.getItemAtPosition(position);
						currentDigit = item;
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
						// TODO Auto-generated method stub

					}
				});
	}

	private void resetButtons() {
		for (Button button : allButtons) {
			button.setText("");
			button.setClickable(true);
			button.setBackgroundColor(getResources().getColor(
					R.color.light_blue));
		}

		findViewById(R.id.submitButton).setEnabled(false);
	}

	private boolean isBoardFull() {
		boolean fullBoard = true;
		for (Button button : this.allButtons) {
			if (button.getText() == "") {
				fullBoard = false;
			}
		}
		return fullBoard;
	}

	private boolean checkForWin() {
		boolean win = true;

		for (Button button : this.allButtons) {
			if (!button.getText().equals("64")) {
				if (!this.hasConsecutiveNeighbor(button)) {
					win = false;
					button.setBackgroundColor(Color.RED); // FIX
				}
			}
		}
		return win;
	}

	private boolean hasConsecutiveNeighbor(Button button) {

		String buttonName = button.getResources().getResourceEntryName(
				button.getId());
		String buttonCoords = buttonName.substring(6);
		int row = Character.getNumericValue(buttonCoords.charAt(0));
		int col = Character.getNumericValue(buttonCoords.charAt(1));
		int digit = Integer.parseInt(button.getText().toString());

		int aboveRow = row - 1;
		int belowRow = row + 1;
		int leftCol = col - 1;
		int rightCol = col + 1;

		int aboveDigit = digit; //
		int belowDigit = digit; // Placeholders, since digit != digit+1.
		int leftDigit = digit; //
		int rightDigit = digit; //

		if (aboveRow >= 0) {
			aboveDigit = getNeighborDigit(aboveRow, col);
		}

		if (belowRow <= 7) {
			belowDigit = getNeighborDigit(belowRow, col);
		}

		if (leftCol >= 0) {
			leftDigit = getNeighborDigit(row, leftCol);
		}

		if (rightCol <= 7) {
			rightDigit = getNeighborDigit(row, rightCol);
		}

		if (aboveDigit == digit + 1 || belowDigit == digit + 1
				|| leftDigit == digit + 1 || rightDigit == digit + 1) {
			return true;
		}

		return false;
	}

	int getNeighborDigit(int row, int col) {
		int neighborId = getResources().getIdentifier("button" + row + col,
				"id", this.getPackageName());
		Button neighborButton = (Button) findViewById(neighborId);
		int neighborDigit = Integer.parseInt(neighborButton.getText()
				.toString());
		return neighborDigit;
	}

	private void startStage(int stage) {
		this.buttonHistory.clear();
		this.resetButtons();
		this.starterDigits.clear();
		this.starterButtons.clear();
		String[] stageDescriptions = getResources().getStringArray(
				R.array.stage_descriptions);
		this.currentStageTextView.setText(stageDescriptions[stage - 1]);

		switch (stage) {
		case 1:
			this.startStageOne();
			break;
		case 2:
			this.startStageTwo();
			break;
		case 3:
			this.startStageThree();
			break;
		case 4:
			this.startStageFour();
			break;
		case 5:
			this.startStageFive();
			break;
		case 6:
			this.startStageSix();
			break;
		case 7:
			this.startStageSeven();
			break;
		case 8:
			this.startStageEight();
			break;
		default:
			this.startStageOne();
			break;
		}

		this.populateEnteringSpinner();
		this.initializeCurrentDigit();

	}

	private void setPrefilled(Button button) {
		button.setClickable(false);
		button.setBackgroundColor(getResources().getColor(R.color.dark_blue)); // TODO
																				// change
	}

	private void initializeStarterButtons() {
		int count = 0;
		for (Button button : this.starterButtons) {
			button.setText(Integer.toString(this.starterDigits.get(count)));
			this.setPrefilled(button);
			count++;
		}
	}

	// Digits = 1, 28, 22, 48, 44, 60, 58, 64, 52, 55, 34, 39, 8, 15
	// Buttons = 00, 01, 07, 12, 16, 23, 25, 34, 52, 55, 61, 66, 70, 77
	private void startStageOne() {
		Collections.addAll(this.starterDigits, 1, 28, 22, 48, 44, 60, 58, 64,
				52, 55, 34, 39, 8, 15);
		Collections.addAll(this.starterButtons, button00, button01, button07,
				button12, button16, button23, button25, button34, button52,
				button55, button61, button66, button70, button77);
		this.initializeStarterButtons();
	}

	// Digits: 6, 21, 1, 30, 13, 64, 38, 46, 56
	// Buttons: 02, 14, 23, 26, 30, 37, 44, 71, 73
	private void startStageTwo() {
		Collections
				.addAll(this.starterDigits, 6, 21, 1, 30, 13, 64, 38, 46, 56);
		Collections.addAll(this.starterButtons, button02, button14, button23,
				button26, button30, button37, button44, button71, button73);
		this.initializeStarterButtons();
	}

	// Digits = 1, 12, 51, 64, 14, 49, 21, 48, 22, 31, 34, 39
	// Buttons = 22, 23, 24, 25, 32, 35, 42, 45, 52, 53, 54, 55
	private void startStageThree() {
		Collections.addAll(this.starterDigits, 1, 12, 51, 64, 14, 49, 21, 48,
				22, 31, 34, 39);
		Collections.addAll(this.starterButtons, button22, button23, button24,
				button25, button32, button35, button42, button45, button52,
				button53, button54, button55);
		this.initializeStarterButtons();
	}

	// Digits: 32, 53, 38, 45, 28, 49, 13, 64, 1, 18, 9, 60
	// Buttons:00, 07, 13, 14, 31, 36, 41, 46, 63, 64, 70, 77
	private void startStageFour() {
		Collections.addAll(this.starterDigits, 32, 53, 38, 45, 28, 49, 13, 64,
				1, 18, 9, 60);
		Collections.addAll(this.starterButtons, button00, button07, button13,
				button14, button31, button36, button41, button46, button63,
				button64, button70, button77);
		this.initializeStarterButtons();
	}

	// Digits: 31, 34, 23, 18, 25, 16, 1, 40, 64, 41, 62, 43, 60, 49, 54, 51
	// Buttons: 03, 04, 12, 15, 21, 26, 30, 37, 40, 47, 51, 56, 62, 65, 73, 74
	private void startStageFive() {
		Collections.addAll(this.starterDigits, 31, 34, 23, 18, 25, 16, 1, 40,
				64, 41, 62, 43, 60, 49, 54, 51);
		Collections.addAll(this.starterButtons, button03, button04, button12,
				button15, button21, button26, button30, button37, button40,
				button47, button51, button56, button62, button65, button73,
				button74);
		this.initializeStarterButtons();
	}

	// Digits: 23, 20, 7, 6, 22, 1, 43, 64, 42, 41, 54, 55
	// Buttons: 11, 12, 15, 16, 21, 26, 51, 56, 61, 62, 65, 66
	private void startStageSix() {
		Collections.addAll(this.starterDigits, 23, 20, 7, 6, 22, 1, 43, 64, 42,
				41, 54, 55);
		Collections.addAll(this.starterButtons, button11, button12, button15,
				button16, button21, button26, button51, button56, button61,
				button62, button65, button66);
		this.initializeStarterButtons();
	}

	// Digits: 54, 19, 64, 27, 47, 34, 1, 10
	// Buttons: 00, 07, 33, 34, 43, 44, 70, 77
	private void startStageSeven() {
		Collections.addAll(this.starterDigits, 54, 19, 64, 27, 47, 34, 1, 10);
		Collections.addAll(this.starterButtons, button00, button07, button33,
				button34, button43, button44, button70, button77);
		this.initializeStarterButtons();
	}

	// Digits: 1, 32, 64, 43
	// Buttons: 22, 25, 52, 55
	private void startStageEight() {
		Collections.addAll(this.starterDigits, 1, 32, 64, 43);
		Collections.addAll(this.starterButtons, button22, button25, button52,
				button55);
		this.initializeStarterButtons();
	}

	private void initializeWidgets() {
		this.enteringSpinner = (Spinner) findViewById(R.id.enteringSpinner);
		this.currentStageTextView = (TextView) findViewById(R.id.currentStageTextView);

		this.button00 = (Button) findViewById(R.id.button00);
		this.button01 = (Button) findViewById(R.id.button01);
		this.button02 = (Button) findViewById(R.id.button02);
		this.button03 = (Button) findViewById(R.id.button03);
		this.button04 = (Button) findViewById(R.id.button04);
		this.button05 = (Button) findViewById(R.id.button05);
		this.button06 = (Button) findViewById(R.id.button06);
		this.button07 = (Button) findViewById(R.id.button07);

		this.button10 = (Button) findViewById(R.id.button10);
		this.button11 = (Button) findViewById(R.id.button11);
		this.button12 = (Button) findViewById(R.id.button12);
		this.button13 = (Button) findViewById(R.id.button13);
		this.button14 = (Button) findViewById(R.id.button14);
		this.button15 = (Button) findViewById(R.id.button15);
		this.button16 = (Button) findViewById(R.id.button16);
		this.button17 = (Button) findViewById(R.id.button17);

		this.button20 = (Button) findViewById(R.id.button20);
		this.button21 = (Button) findViewById(R.id.button21);
		this.button22 = (Button) findViewById(R.id.button22);
		this.button23 = (Button) findViewById(R.id.button23);
		this.button24 = (Button) findViewById(R.id.button24);
		this.button25 = (Button) findViewById(R.id.button25);
		this.button26 = (Button) findViewById(R.id.button26);
		this.button27 = (Button) findViewById(R.id.button27);

		this.button30 = (Button) findViewById(R.id.button30);
		this.button31 = (Button) findViewById(R.id.button31);
		this.button32 = (Button) findViewById(R.id.button32);
		this.button33 = (Button) findViewById(R.id.button33);
		this.button34 = (Button) findViewById(R.id.button34);
		this.button35 = (Button) findViewById(R.id.button35);
		this.button36 = (Button) findViewById(R.id.button36);
		this.button37 = (Button) findViewById(R.id.button37);

		this.button40 = (Button) findViewById(R.id.button40);
		this.button41 = (Button) findViewById(R.id.button41);
		this.button42 = (Button) findViewById(R.id.button42);
		this.button43 = (Button) findViewById(R.id.button43);
		this.button44 = (Button) findViewById(R.id.button44);
		this.button45 = (Button) findViewById(R.id.button45);
		this.button46 = (Button) findViewById(R.id.button46);
		this.button47 = (Button) findViewById(R.id.button47);

		this.button50 = (Button) findViewById(R.id.button50);
		this.button51 = (Button) findViewById(R.id.button51);
		this.button52 = (Button) findViewById(R.id.button52);
		this.button53 = (Button) findViewById(R.id.button53);
		this.button54 = (Button) findViewById(R.id.button54);
		this.button55 = (Button) findViewById(R.id.button55);
		this.button56 = (Button) findViewById(R.id.button56);
		this.button57 = (Button) findViewById(R.id.button57);

		this.button60 = (Button) findViewById(R.id.button60);
		this.button61 = (Button) findViewById(R.id.button61);
		this.button62 = (Button) findViewById(R.id.button62);
		this.button63 = (Button) findViewById(R.id.button63);
		this.button64 = (Button) findViewById(R.id.button64);
		this.button65 = (Button) findViewById(R.id.button65);
		this.button66 = (Button) findViewById(R.id.button66);
		this.button67 = (Button) findViewById(R.id.button67);

		this.button70 = (Button) findViewById(R.id.button70);
		this.button71 = (Button) findViewById(R.id.button71);
		this.button72 = (Button) findViewById(R.id.button72);
		this.button73 = (Button) findViewById(R.id.button73);
		this.button74 = (Button) findViewById(R.id.button74);
		this.button75 = (Button) findViewById(R.id.button75);
		this.button76 = (Button) findViewById(R.id.button76);
		this.button77 = (Button) findViewById(R.id.button77);
	}

	private void populateButtonList() {
		Collections.addAll(this.allButtons, button00, button01, button02,
				button03, button04, button05, button06, button07, button10,
				button11, button12, button13, button14, button15, button16,
				button17, button20, button21, button22, button23, button24,
				button25, button26, button27, button30, button31, button32,
				button33, button34, button35, button36, button37, button40,
				button41, button42, button43, button44, button45, button46,
				button47, button50, button51, button52, button53, button54,
				button55, button56, button57, button60, button61, button62,
				button63, button64, button65, button66, button67, button70,
				button71, button72, button73, button74, button75, button76,
				button77);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
