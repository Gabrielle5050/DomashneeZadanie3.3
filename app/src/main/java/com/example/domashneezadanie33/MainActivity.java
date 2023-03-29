package com.example.domashneezadanie33;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private final String DATA_STREAM = "http://ep128.hostingradio.ru:8030/ep128";
    private final String DATA_SD = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_MUSIC) + "/music.mp3";
    private String nameAudio;
    private String nameSource;
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private Toast toast;
    TextView textOut, textSource;
    private Switch switchLoop;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textOut = findViewById(R.id.textOut);
        textSource = findViewById(R.id.textSource);
        switchLoop = findViewById(R.id.SwitchLoop);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        switchLoop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mediaPlayer != null) {
                    mediaPlayer.setLooping(isChecked);
                }
            }
        });
    }

    public void onClickSource(View v) {
        releaseMediaPlayer();
        try {
            switch (v.getId()){
                case R.id.btnStream:
                    toast = Toast.makeText(MainActivity.this, "Трансляция радио",
                            Toast.LENGTH_SHORT);
                    toast.show();
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource("http://ep128.hostingradio.ru:8030/ep128");
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.start();
                            toast = Toast.makeText(MainActivity.this, "Запуск медиа-плейера",
                                    Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });
                    mediaPlayer.prepareAsync();
                    nameAudio = "Трансляция радио EuropaPlus";
                    nameSource = "Трансляция интернет-радио";
                    break;
                case R.id.btnRAW:
                    toast = Toast.makeText(MainActivity.this, "Воспроизведение аудио-файла с телефона",
                            Toast.LENGTH_SHORT);
                    toast.show();
                    mediaPlayer = MediaPlayer.create(this,R.raw.oldtownroad);
                    mediaPlayer.start();
                    nameAudio = "Исполитель: Lil Nas X\nТрек: Old Town Road";
                    nameSource = "Воспроизведение музыки с телефона";
                    break;
                case R.id.btnSD:
                    toast = Toast.makeText(MainActivity.this, "Воспроизведение аудио-файла с SD-карты",
                            Toast.LENGTH_SHORT);
                    toast.show();
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(DATA_SD);
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            toast = Toast.makeText(MainActivity.this, "Источник информации не найден",
                    Toast.LENGTH_SHORT);
            toast.show();
        }
        if (mediaPlayer==null){
            mediaPlayer.setLooping(switchLoop.isChecked());
            mediaPlayer.setOnCompletionListener((MediaPlayer.OnCompletionListener) this);
        }
    }
    public void onClick(View v){
        if (mediaPlayer==null) return;

        switch (v.getId()){
            case R.id.btnResume:
                if (!mediaPlayer.isPlaying()) mediaPlayer.start();
                break;
            case R.id.btnPause:
                if (mediaPlayer.isPlaying()) mediaPlayer.pause();
                break;
            case R.id.btnStop:
                mediaPlayer.stop();
                break;
            case R.id.btnForward:
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+5000);
                break;
            case R.id.btnBack:
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-5000);
                break;
        }
        textOut.setText(nameAudio + "\n(проигрывание " + mediaPlayer.isPlaying() + ", время "+
                (mediaPlayer.getCurrentPosition()/1000) + " сек." +
                ", громкость: " + audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        textSource.setText("Источник: " + nameSource);
    }
    public void onPrepared(MediaPlayer mediaPlayer){
        mediaPlayer.start();
        toast = Toast.makeText(MainActivity.this, "Запуск медиа-плейера",
                Toast.LENGTH_SHORT);
        toast.show();
    }
    public void onCompletion(MediaPlayer mediaPlayer){
        toast = Toast.makeText(MainActivity.this, "Отлючение медиа-плейера",
                Toast.LENGTH_SHORT);
        toast.show();
    }
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }
    private void releaseMediaPlayer(){
        if (mediaPlayer != null){
            try {
                mediaPlayer.release();
                mediaPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}