package com.curroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class curroid extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		final Button button = (Button) findViewById(R.id.exit);
		final Button count = (Button) findViewById(R.id.ok);
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final EditText curr = (EditText) findViewById(R.id.entry);
		final TextView result = (TextView) findViewById(R.id.converted);
		final TextView date = (TextView) findViewById(R.id.date);
		final Spinner spinner = (Spinner) findViewById(R.id.spinner);
		final Spinner spinner1 = (Spinner) findViewById(R.id.spinner1);

		// stworzenie parsera

		final CurrencyParser cp = new CurrencyParser(this);

		date.setText("Last updated: " + cp.getPubDate()
				+ ". Data received from ECB.");
		// tablica z symbolami walut
		final String spinner_array[] = new String[cp.currencyCode.size()];

		// przypisanie pobranych symboli do tablicy
		for (int i = 0; i < cp.currencyCode.size(); i++) {
			spinner_array[i] = cp.currencyCode.get(i);
		}

		// ustawienie tablicy z symbolami walut jako listy dla spinnerow oraz
		// wygladu
		ArrayAdapter adapter = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, spinner_array);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner1.setAdapter(adapter);

		// okienko pytajace przy probie wyjscia z programu
		builder.setMessage("Are you sure you want to exit?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								finish();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

		// listnery do przyciskow
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				final AlertDialog alert;
				alert = builder.create();
				alert.show();
			}
		});
		count.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {

					float value = Float.parseFloat(curr.getText().toString());
					value = value
							* cp.getRelativeExchange(spinner.getSelectedItem()
									.toString(), spinner1.getSelectedItem()
									.toString());
					result.setText(String.valueOf(value));
				} catch (Exception ex) {
					result.setText(R.string.error);
				}
			}
		});
	}
}