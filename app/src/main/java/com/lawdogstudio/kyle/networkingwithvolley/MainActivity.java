package com.lawdogstudio.kyle.networkingwithvolley;
/**
 * Networking using Volley from Google and grabbing JSON object from Best Buy API as an example
 * Make sure to include compile 'com.android.volley:volley:1.0.0'
 * to build.gradle(Module: app) dependency and
 * <uses-permission android:name="android.permission.INTERNET"/> in manifest above <application
 * Along with <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/> to check
 * network connection.
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //Setup variables and widgets
    final String bbyAPIKey = "8uhwa4p7xe34pqz6u3eqj3yq";
    String jsonResponse = "";

    //Setup url to be used to retrieve data
    String url = "https://api.bestbuy.com/v1/stores(area(94510,100))?format=json&show=storeId,storeType,name&apiKey=" + bbyAPIKey;

    ArrayList<String> storeArray = new ArrayList<>();

    ListView mListView;

    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setup an array adapter to be used to update the listView data and determine its layout
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, storeArray);

        //Assign the listView variable to its layout ID
        mListView = (ListView) findViewById(android.R.id.list);

        //Attach the adapter to the listView so the adapter knows which view to update
        mListView.setAdapter(arrayAdapter);

        //Instantiate a network status manager
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        //Instantiate a network info object and assign it the network info
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        //If network status connection return true, call the Best Buy API
        // If false, create a toast saying so
        if (isConnected) {
            //Call the network method
            JSONAction();
        } else {
            Toast.makeText(this, "No Data Connection", Toast.LENGTH_LONG).show();
        }

    }

    public void JSONAction() {
        //JSON Object request method call from Volley Gradle import
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    //Best Buy uses an array that holds their store information in a JSON object
                    //within the array so we need to pull the array, pull the stores object from
                    //the array, and store the individual store data in strings for display

                    //Variable setup
                    String storeType = "";
                    String storeName = "";
                    JSONObject storeObject;

                    //Calls the high level JSON Array that Best Buy sends
                    JSONArray stores = response.getJSONArray("stores");

                    //Iterates through the array pulling each individual store's information,
                    //which is put together as an object and assigns each key-value pair to a variable
                    for (int i = 0; i < stores.length(); i++) {
                        storeObject = stores.getJSONObject(i);
                        storeType = storeObject.getString("storeType");
                        storeName = storeObject.getString("name");

                        //Places the variables into a sentence variable
                        jsonResponse = "Store Type: " + storeType + "\r\n Store Name: " + storeName;

                        //Adds the sentence to the store array
                        storeArray.add(jsonResponse);
                    }

                    //After iteration is complete, the array adapter is notified that the store array
                    //data has been changed and updates the view accordingly
                    arrayAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }

        });

        //JSON Array which starts the set with [ instead of {
        /*JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                try {
                    // Parsing json array response
                    // loop through each json object
                    jsonResponse = "";
                    for (int i = 0; i < response.length(); i++) {

                        JSONObject store = (JSONObject) response
                                .get(i);

                        String storeType = store.getString("storeType");
                        String storeName = store.getString("name");

                        jsonResponse += "Store Type:" + storeType +"\r\n";
                        jsonResponse += "Store Name: " + storeName + "\n\n";

                    }

                    storeArray.add(jsonResponse);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){

            }
        });
        */

        //Actually calls the JSON request from the internet
        Volley.newRequestQueue(this).add(jsonRequest);

    }

}
