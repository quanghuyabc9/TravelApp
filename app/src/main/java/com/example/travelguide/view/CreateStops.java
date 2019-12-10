package com.example.travelguide.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.travelguide.R;

import java.util.Calendar;
import java.util.Objects;

public class CreateStops extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_stops);

//        Spinner spinner=findViewById(R.id.SP_province);
//        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this,R.array.province,android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);
//        spinner.setOnItemSelectedListener( this);
//
//        Spinner spinner1=findViewById(R.id.SP_serviceType);
//        ArrayAdapter<CharSequence> adapter1=ArrayAdapter.createFromResource(this,R.array.serviceName,android.R.layout.simple_spinner_item);
//        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner1.setAdapter(adapter1);
//        spinner1.setOnItemSelectedListener( this);
//
//        final TextView date= findViewById(R.id.edt_date);
//
//        date.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Calendar cal = Calendar.getInstance();
//                int year = cal.get(Calendar.YEAR);
//                int month = cal.get(Calendar.MONTH);
//                int day = cal.get(Calendar.DAY_OF_MONTH);
//
//                DatePickerDialog dialog = new DatePickerDialog(CreateStops.this,
//                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
//                        mDateSetListener,
//                        year,month,day);
//                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                dialog.show();
//            }
//        });
//        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                month = month +1;
//                String date1 = dayOfMonth + "/" + month + "/" + year;
//                date.setText(date1);
//            }
//        };
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
