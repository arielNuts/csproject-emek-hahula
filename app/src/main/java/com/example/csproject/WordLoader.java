package com.example.csproject;

import android.content.Context;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class WordLoader {

    public static String[] loadWords(Context context) {
        ArrayList<String> wordList = new ArrayList<>();
        InputStream inputStream = context.getResources().openRawResource(R.raw.words);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                // Convert each word to uppercase
                wordList.add(line.trim().toUpperCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return wordList.toArray(new String[0]);
    }
}

