package com.example.lostandfound;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateAdvertActivity extends AppCompatActivity {

    private AdvertDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_advert);

        dbHelper = new AdvertDbHelper(this);

        final CheckBox isLostCheckBox = findViewById(R.id.isLostCheckBox);
        final CheckBox isFoundCheckBox = findViewById(R.id.isFoundCheckBox);
        final EditText nameEditText = findViewById(R.id.nameEditText);
        final EditText phoneEditText = findViewById(R.id.phoneEditText);
        final EditText descriptionEditText = findViewById(R.id.descriptionEditText);
        final EditText locationEditText = findViewById(R.id.locationEditText);
        final Button saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(AdvertDbHelper.COLUMN_IS_LOST, isLostCheckBox.isChecked() ? 1 : 0);
                values.put(AdvertDbHelper.COLUMN_IS_FOUND, isFoundCheckBox.isChecked() ? 1 : 0);
                values.put(AdvertDbHelper.COLUMN_NAME, nameEditText.getText().toString());
                values.put(AdvertDbHelper.COLUMN_PHONE, phoneEditText.getText().toString());
                values.put(AdvertDbHelper.COLUMN_DESCRIPTION, descriptionEditText.getText().toString());
                values.put(AdvertDbHelper.COLUMN_LOCATION, locationEditText.getText().toString());

                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                String Date = sdf.format(new Date());
                values.put(AdvertDbHelper.COLUMN_DATE, Date);

                db.insert(AdvertDbHelper.TABLE_NAME, null, values);

                Toast.makeText(CreateAdvertActivity.this, "Advert created successfully", Toast.LENGTH_SHORT).show();

                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
