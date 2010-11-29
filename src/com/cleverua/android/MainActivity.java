package com.cleverua.android;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.cleverua.android.post.MultipartPost;
import com.cleverua.android.post.PostParameter;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
    
	private static final File FILE = new File(Environment.getExternalStorageDirectory() + "/icon.png");
	private static final String TAG = "MainActivity";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button button = (Button) findViewById(R.id.send_post_button);
        button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Log.d(TAG, "send_post_button clicked, sending request");
				sendPostRequest();
			}
		});
    }
    
    private void sendPostRequest() {
    	Log.d(TAG, "sendPostRequest");
    	List<PostParameter> params = new ArrayList<PostParameter>();
    	params.add(new PostParameter<String>("fullname", "John Doe"));
    	params.add(new PostParameter<File>("headshot", FILE));
    	
    	try {
    		MultipartPost post = new MultipartPost(params);
    		post.send("http://www.yourhost.com");
    		Toast.makeText(this, "POST has sent", Toast.LENGTH_SHORT).show();
    	} catch (Exception e) {
    		Log.e(TAG, "sendPostRequest", e);
    		Log.d(TAG, "==================================================");
    		Toast.makeText(this, "Failed to send POST request, see log for details!", Toast.LENGTH_SHORT).show();
		}
    }
}