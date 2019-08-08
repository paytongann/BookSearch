package com.example.networkcalltest;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements CustomListener{

    private static final String TAG = MainActivity.class.getSimpleName();
    TextInputLayout til_search;
    Button btn_search;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        til_search = findViewById(R.id.til_search_title);
        btn_search = findViewById(R.id.btn_search_book);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CustomAsyncTask(
                        buildUri(
                                til_search.getEditText()
                                        .getText()
                                        .toString()),
                        MainActivity.this).execute();
            }
        });


    }

    public Uri buildUri(String title) {
        String baseURL = "https://www.googleapis.com/books/v1/volumes?";
        String paramQ = "q";
        String paramMaxResults = "maxResults";
        String paramPrintType = "printType";

        Uri uri = Uri.parse(baseURL).buildUpon()
                .appendQueryParameter(paramQ, title)
                .appendQueryParameter(paramMaxResults, "5")
                .appendQueryParameter(paramPrintType, "books")
                .build();
        return uri;
    }

    public BookPojo initNetworkCall(Uri uri) throws Exception {
        URL url = new URL(uri.toString());
        InputStream inputStream;
        HttpURLConnection httpURLConnection;
        httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setReadTimeout(10000);
        httpURLConnection.setConnectTimeout(15000);
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setDoInput(true);

        httpURLConnection.connect();
        int responseCode = httpURLConnection.getResponseCode();
        inputStream = httpURLConnection.getInputStream();

        if (responseCode > 300) sendErrorToast();

        String parseResponse = convertIsToString(inputStream);

        Log.d(TAG, "initNetworkCall: " + parseResponse);

        //todo Parse from String to BookPojo using JSONObject
        BookPojo bookPojo = parseFromString(parseResponse);
        return bookPojo;
    }

    public BookPojo parseFromString(String response) throws Exception{
        JSONObject jsonObject = new JSONObject(response);

        if(jsonObject.getString("totalItems").equals("0")) return null;
        BookPojo book = new BookPojo();
        List<BookItem> listItems = new ArrayList<>();

        JSONArray arrayItems = jsonObject.getJSONArray("items");
        for(int i=0; i<arrayItems.length(); i++){
            BookItem items = new BookItem();
            VolumeInfo volumeInfo = new VolumeInfo();
            ImageLinks imageLinks = new ImageLinks();
            JSONObject item = arrayItems.getJSONObject(i)
                    .getJSONObject("volumeInfo");
            JSONObject itemImageLinks = item.getJSONObject("imageLinks");
            imageLinks.smallThumbnail =
                    itemImageLinks.getString("smallThumbnail");
            volumeInfo.title = item.getString("title");

            try{
                volumeInfo.subtitle = item.getString("subtitle");
            }catch(JSONException exception){
                exception.printStackTrace();
                volumeInfo.subtitle = "N/A";
            }

            volumeInfo.imageLinks = imageLinks;
            items.volumeInfo = volumeInfo;
            listItems.add(items);
        }
        book.items = listItems;
        return book;
    }


    private String convertIsToString(InputStream inputStream)
            throws IOException {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(inputStream));
        String line;

        while ((line = reader.readLine()) != null) {
            builder.append(line + "\n");
        }
        if (builder.length() == 0) return null;

        return builder.toString();
    }

    private void sendErrorToast() {
        Toast.makeText(this,
                "Error",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void itemClicked(BookItem item) {
        Toast.makeText(this,
                item.toString(),
                Toast.LENGTH_SHORT).show();
    }

    class CustomAsyncTask extends AsyncTask<Void, Void, BookPojo> {

        private CustomListener listener;

        public CustomAsyncTask(Uri uri, CustomListener listener) {
            this.uri = uri;
            this.listener = listener;
        }

        private Uri uri;

        @Override
        protected BookPojo doInBackground(Void... voids) {
            BookPojo bookPojo;
            try {
                bookPojo = initNetworkCall(uri);
            } catch (Exception e) {
                e.printStackTrace();
                bookPojo = null;
            }

            return bookPojo;
        }

        @Override
        protected void onPostExecute(BookPojo bookPojo) {
            super.onPostExecute(bookPojo);
            CustomAdapter adapter = new CustomAdapter();
            adapter.setListener(listener);
            adapter.setDataSet(bookPojo);
            recyclerView.setAdapter(adapter);
        }
    }
}








