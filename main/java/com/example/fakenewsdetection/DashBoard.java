package com.example.fakenewsdetection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;



import de.hdodenhof.circleimageview.CircleImageView;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class DashBoard extends AppCompatActivity {
    private Button check;
    private RelativeLayout fakeLayout,realLayout;
    private EditText editTextNews;
    private CircleImageView tweet;
    private Twitter twitter;
    List<String> wordList;
    private String result;
    private Interpreter interpreter;
    private Map<String, Integer> wordIndex;
    private float[][] wordVectors;
    List<float[]> vectorList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dash_board);
        init();
        try {
            interpreter = new Interpreter(loadModelFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        wordVectors = loadWord2VecModel();


        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newsText = editTextNews.getText().toString();

                // Preprocess the news text
                List<String> preprocessedText = preprocessText(newsText);

                // Perform the prediction using the LSTM model
                float prediction = performPrediction(preprocessedText);

                // Display the prediction result
                String predictionResult = prediction > 0.5 ? "real" : "fake";
                result=predictionResult;
                Toast.makeText(DashBoard.this, "The news is " + predictionResult, Toast.LENGTH_SHORT).show();
            }
        });

        tweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread gfgThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try  {
                            ConfigurationBuilder cb = new ConfigurationBuilder();
                            cb.setDebugEnabled(true)
                                    .setOAuthConsumerKey("ukWpcH9kNDSrtb2mRVDctyrgF")
                                    .setOAuthConsumerSecret("hP7GOyy12IkcKoJ5ALCp9PwKEqClcB1dpyR8Ceo90G2zftjlsZ")
                                    .setOAuthAccessToken("1648363865107996672-Pp5pFvgYugLvibp5kLFDqWeoRZ3Wa6")
                                    .setOAuthAccessTokenSecret("nzEhK2fytZVEjsIazwdkYZNl7gEShg8fJaYUtFdCnnrsC");

                            TwitterFactory tf = new TwitterFactory(cb.build());
                            twitter = tf.getInstance();
                            sendTweet();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                gfgThread.start();




            }
        });
    }
    private ByteBuffer loadModelFile() throws IOException {
        InputStream inputStream = getAssets().open("fake_news_model.tflite");
        int modelSize = inputStream.available();
        byte[] modelBuffer = new byte[modelSize];
        inputStream.read(modelBuffer);
        inputStream.close();
        return ByteBuffer.allocateDirect(modelSize).order(ByteOrder.nativeOrder()).put(modelBuffer);
    }
    private float[][] loadWord2VecModel() {
        try {
            AssetManager assetManager = getAssets();
            FileInputStream fileInputStream = new FileInputStream(assetManager.openFd("word2vec_model.txt").getFileDescriptor());

            wordList = new ArrayList<>();
            vectorList = new ArrayList<>();

            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+");
                String word = parts[0];
                float[] vector = new float[parts.length - 1];
                for (int i = 1; i < parts.length; i++) {
                    vector[i - 1] = Float.parseFloat(parts[i]);
                }
                wordList.add(word);
                vectorList.add(vector);
            }

            float[][] wordVectors = new float[wordList.size()][];
            for (int i = 0; i < wordList.size(); i++) {
                wordVectors[i] = vectorList.get(i);
            }

            return wordVectors;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private List<String> preprocessText(String text) {
        text = text.toLowerCase();


        text = text.replaceAll("[^a-zA-Z0-9\\s]", "");
        text = text.replaceAll("\\s+", " ").trim();


        String[] words = text.split(" ");

        return new ArrayList<>(Arrays.asList(words));
    }

    private float performPrediction(List<String> preprocessedText) {
        float[][] input = new float[1][preprocessedText.size()];
        int index = 0;

        // Convert the preprocessed text to input sequence
        for (String word : preprocessedText) {
            Integer wordIndexValue = vectorList.indexOf(word);
            input[0][index] = (wordIndexValue != null) ? wordIndexValue : 0;
            index++;
        }

        // Perform the prediction
        float[][] output = new float[1][1];
        interpreter.run(input, output);

        return output[0][0];
    }

    private void sendTweet() {
        try {

            twitter.updateStatus(result+"\n"+editTextNews.getText().toString());
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }
    private void init(){
        check= findViewById(R.id.check_button);
        fakeLayout=findViewById(R.id.fake_layout);
        realLayout=findViewById(R.id.real_layout);
        editTextNews=findViewById(R.id.text);
        tweet=findViewById(R.id.tweet);
    }
    private void check(){

    }

}