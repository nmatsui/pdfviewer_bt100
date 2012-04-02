package jp.co.tis.tc.command.master;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class StartActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
        
        Button button = (Button)findViewById(R.id.connect_btn);
        button.requestFocus();
        button.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				Intent master = new Intent(StartActivity.this, MasterActivity.class);
				master.putExtra("address", ((EditText)findViewById(R.id.address)).getText().toString());
				master.putExtra("port", Integer.parseInt(((EditText)findViewById(R.id.port)).getText().toString()));
				startActivity(master);
			}
		});
    }
}