package sjsu.edu.musicapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.content.Intent;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final ImageButton mBtnOpen =(ImageButton) findViewById(R.id.open);
        mBtnOpen.setOnClickListener(new ImageButton.OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                startActivity(new Intent(MainActivity.this,ListSong.class));
                finishActivity(MainActivity.this);

            }

            private void finishActivity(AppCompatActivity MainActivity) {
                // TODO Auto-generated method stub
                finish();
            }

        });
    }
}
