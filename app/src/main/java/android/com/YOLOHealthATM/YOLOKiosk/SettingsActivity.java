package android.com.YOLOHealthATM.YOLOKiosk;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static android.com.YOLOHealthATM.YOLOKiosk.MainActivity.myprefs;

public class SettingsActivity extends AppCompatActivity {

    private static final String PASSWORD = "password";

    Button sharedPrefButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPrefButton = findViewById(R.id.sharedPref);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Enter Password");
        alert.setCancelable(false);

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                // Do something with value!
                if (input.getText().toString().equals(PASSWORD)) {
                } else {
                    Intent i = new Intent(SettingsActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
                Intent i = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        alert.show();


        sharedPrefButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(myprefs, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();

                Intent i = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

    }
}
