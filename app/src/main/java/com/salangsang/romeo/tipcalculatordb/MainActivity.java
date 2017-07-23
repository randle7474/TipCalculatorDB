package com.salangsang.romeo.tipcalculatordb;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import java.text.NumberFormat;
import java.util.List;


public class MainActivity extends Activity

        implements OnEditorActionListener, OnClickListener {

    // define variables for the widgets

    private EditText billAmountEditText;
    private TextView percentTextView;
    private Button   percentUpButton;
    private Button   percentDownButton;
    private TextView tipTextView;
    private TextView totalTextView;
    private Button saveButton;

    // define instance variables that should be saved

    private String billAmountString = "";
    private float tipPercent = .15f;

    // set up preferences
    private SharedPreferences prefs;

    private static final String TAG = "TipCalculatorActivity";

    EditText inputEditText;

    DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get references to the widgets

        billAmountEditText = (EditText) findViewById(R.id.billAmountEditText);
        percentTextView = (TextView) findViewById(R.id.percentTextView);
        percentUpButton = (Button) findViewById(R.id.percentUpButton);
        percentDownButton = (Button) findViewById(R.id.percentDownButton);
        tipTextView = (TextView) findViewById(R.id.tipTextView);
        totalTextView = (TextView) findViewById(R.id.totalTextView);
        saveButton = (Button) findViewById(R.id.saveButton);


        // set the listeners
        billAmountEditText.setOnEditorActionListener(this);
        percentUpButton.setOnClickListener(this);
        percentDownButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);

        // get default SharedPreferences object
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        dbHandler = new DBHandler(this, null,null,1);

    }

    @Override

    public void onPause() {
        // save the instance variables

        Editor editor = prefs.edit();
        editor.putString("billAmountString", billAmountString);
        editor.putFloat("tipPercent", tipPercent);
        editor.commit();

        super.onPause();

    }


    @Override
    public void onResume() {
        super.onResume();

        // get the instance variables
        billAmountString = prefs.getString("billAmountString", "");
        tipPercent = prefs.getFloat("tipPercent", 0.15f);
        // set the bill amount on its widget
        billAmountEditText.setText(billAmountString);

        // calculate and display
        calculateAndDisplay();

        List<Tip> dbString = dbHandler.getTips(1);
        String log="";
        for(Tip c  : dbString){
            log += " ID: " + c.getId() +
                    " Date:" + c.getDateMillis() +
                    " Bill Amount" + c.getBillAmount() +
                    " Tip Percent" + c.getTipPercent() +
                    "\n";

        }
        Log.i(TAG, log);

        Tip dateLastSaved = dbHandler.getLastSaved();
        Log.d(TAG, "Date and time of last saved Tip:"
                + dateLastSaved.getDateStringFormatted());
        Tip averageTip = dbHandler.getAverageTip();
        Log.d(TAG, "Average Tip Percent: " + averageTip.getTipPercent());
        calculateAndDisplay();

    }

    public void calculateAndDisplay() {

        // get the bill amount
        billAmountString = billAmountEditText.getText().toString();
        float billAmount;
        if (billAmountString.equals("")) {
            billAmount = 0;
        }
        else {
            billAmount = Float.parseFloat(billAmountString);
        }

        // calculate tip and total
        float tipAmount = billAmount * tipPercent;
        float totalAmount = billAmount + tipAmount;
        // display the other results with formatting
        NumberFormat currency = NumberFormat.getCurrencyInstance();
        tipTextView.setText(currency.format(tipAmount));
        totalTextView.setText(currency.format(totalAmount));

        NumberFormat percent = NumberFormat.getPercentInstance();
        percentTextView.setText(percent.format(tipPercent));

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE ||
                actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
            calculateAndDisplay();

        }
        return false;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.percentDownButton:
                tipPercent = tipPercent - .01f;
                calculateAndDisplay();

                break;
            case R.id.percentUpButton:
                tipPercent = tipPercent + .01f;
                calculateAndDisplay();
                break;
            case R.id.saveButton:
                Tip tip = new Tip();

                tip.setBillAmount((Float.parseFloat(billAmountEditText.getText().toString())));
                tip.setTipPercent(tipPercent);
                dbHandler.savedTipCalc(tip);
                billAmountEditText.setText("");
        }

    }
    }

