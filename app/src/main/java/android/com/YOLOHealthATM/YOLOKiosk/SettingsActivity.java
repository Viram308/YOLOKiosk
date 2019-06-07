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
import android.widget.TextView;

import static android.com.YOLOHealthATM.YOLOKiosk.MainActivity.myprefs;

public class SettingsActivity extends AppCompatActivity {

    private static final String PASSWORD = "password";
    String s,p;
    TextView ssi,pas;
    Button sharedPrefButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ssi=findViewById(R.id.groupssid);
        pas=findViewById(R.id.grouppass);
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
//                    finish();
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
                Intent i = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(i);
//                finish();
            }
        });
        alert.show();
        final SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(myprefs, Context.MODE_PRIVATE);
        s = sharedPreferences.getString("ssid", "0");
        p = sharedPreferences.getString("pass", "0");
        if (s.equals("0")) {
            ssi.setText("");
            pas.setText("");
        } else {
            ssi.setText(s);
            pas.setText(p);
        }
        sharedPrefButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("hostMac", "");
                editor.putString("ip","");
                editor.commit();

                Intent i = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

    }
}
