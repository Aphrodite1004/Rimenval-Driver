package driver.transporterimenval.com.motorizado;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;

import driver.transporterimenval.com.R;

import static driver.transporterimenval.com.motorizado.DeliveryStatus.changeStatsBarColor;


public class Termcondition extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_termcondition);
		changeStatsBarColor(Termcondition.this);
		ImageButton ib_back = findViewById(R.id.ib_back);
		ib_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		setTermsCondition();
	}
	private void setTermsCondition() {

		WebView web = findViewById(R.id.web);
		web.loadUrl("file:///android_asset/"+getString(R.string.terms_condition_filename));
	}


}
