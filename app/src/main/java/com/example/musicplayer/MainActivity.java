package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener {

    ImageButton playPauseBtn, stop, open;
    MediaPlayer mediaPlayer = null;
    SeekBar seekbar;
    TextView num;
    int duration = 0;
    boolean finish = false;
    boolean pauseFinish = false;
    int current = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playPauseBtn = findViewById(R.id.playPauseBtn);
        stop = findViewById(R.id.stop);
        open = findViewById(R.id.open);
        seekbar = findViewById(R.id.seekbar);
        num = findViewById(R.id.num);

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                current = seekBar.getProgress();
                current = current * 1000;
                mediaPlayer.seekTo(current);
                num.setText(""+current+"/"+duration);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.playPauseBtn:
                if(mediaPlayer.isPlaying()) {
                    pauseMusic();
                }
                else {
                    playMusic();
                }
                break;
            case R.id.open:
                openAudio();
                break;
            case R.id.stop:
                stopAudio();
                break;
            default:
                break;
        }
    }

    public void openAudio(){

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);  //  USED TO FETCH ANY TYPE OF CONTENT FILE FROM CLIENTS DEVICE
        intent.setType("audio/*");  //  intent.setType("video/*");  // intent.setType("images/*");
        startActivityForResult(Intent.createChooser(intent,"SELECT YOUR SONG"), 151);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==151 && resultCode==RESULT_OK){
            Uri uri = data.getData();   // URI is used to store location for given content

            mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
            mediaPlayer.start();

            duration = mediaPlayer.getDuration();
            duration = duration/1000;   // Convert millisec to sec
            seekbar.setMax(duration);

            num.setText("0/"+duration);

            finish = false;
            pauseFinish = false;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!finish){
                        try { Thread.sleep(1000); }
                        catch (Exception e){}

                        if(!pauseFinish) {

                            current = mediaPlayer.getCurrentPosition();
                            current = current / 1000;
                            seekbar.setProgress(current);

                            num.post(new Runnable() {
                                @Override
                                public void run() {
                                    num.setText(""+current+"/"+duration);
                                }
                            });

                            if (current >= duration) {
                                pauseFinish = true;
                                finish = true;
                                duration = 0;
                                current = 0;
                                seekbar.setProgress(0);
                                mediaPlayer = null;

                                num.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        num.setText("0/0");
                                    }
                                });
                            }
                        }

                    }
                }
            }).start();
        }
    }

    public void pauseMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            pauseFinish=true;
            playPauseBtn.setBackgroundResource(R.drawable.ic_play);
        } else {
            openAudio();
        }
    }

    public void playMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            playPauseBtn.setBackgroundResource(R.drawable.ic_pause);
        } else {
            openAudio();
        }
    }

    public void stopAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            finish = true;
            pauseFinish = true;
            mediaPlayer = null;
            seekbar.setProgress(0);
            duration = 0;
            current = 0;
            num.setText("0/0");
        } else {
            openAudio();
        }
    }
}