package me.anuraag.eatifi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.andtinder.model.CardModel;
import com.andtinder.model.Orientations;
import com.andtinder.view.CardContainer;
import com.andtinder.view.SimpleCardStackAdapter;
import com.parse.Parse;
import com.parse.ParseUser;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;


public class HomePage extends Activity {
    private TextView mytext;
    private Button refresh;
    private ParseUser myuser;
    private CardContainer mycards;

    private double lon,lat;
    private String response;
    private boolean infoLoaded = false;
    private boolean imageLoaded = false;
    private ArrayList<String> imageTokens = new ArrayList<String>();
    private String hello,pageToken;
    private ArrayList<JSONObject> placesInformation = new ArrayList<JSONObject>();
    private SimpleCardStackAdapter adapter;
    private LocationManager locationManager;
    private String loc;
    private int placeholder = 0;
    private LocationListener locationListener;
    private Location curLoc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Parse.initialize(this, "n2koKwQHYtQGedP92Uq6jEpHqMw7WByd6F11yMVh", "VObFmhueGhCXuNqeKZlkgeFkCB5Vw01gk1MkQNM9");
        mytext = (TextView)findViewById(R.id.textView);
        mycards = (CardContainer)findViewById(R.id.layoutview);
        mycards.setOrientation(Orientations.Orientation.Ordered);
        adapter = new SimpleCardStackAdapter(this);
//             mycards.setAdapter(adapter);

        doLocation();
        final Intent next = new Intent(this,MyActivity2.class);
        refresh = (Button)findViewById(R.id.button);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLocation();
                RequestTask task = new RequestTask();
                String path ="http://api.yelp.com/v2/search?term=food&radius_filter=40000&ll=" + curLoc.getLatitude() + "," + curLoc.getLongitude();
                String[] hello = new String[1];
                hello[0] = path;
                task.execute(hello);
            }
        });
        try{
            myuser = ParseUser.getCurrentUser();
            Log.i("User", myuser.getEmail());
            mytext.setText("Hello " +myuser.getEmail());

        }catch (NullPointerException e){
            Log.i("Something","is wrong");
        }



    }
    public void doLocation(){
        locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
//                displayLocation(location);

                curLoc = location;
                Log.i("Coordinates",curLoc.toString());
                //doTimeCheck();
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }
    public void displayLocation(Location l){


//         lon = 0;
//         lat = 0;
//        Geocoder gcd = new Geocoder(this, Locale.getDefault());
//        try {
//            List<Address> addresses = gcd.getFromLocation(l.getLatitude(), l.getLongitude(), 1);
//            if (addresses.size() > 0) {
//                loc = addresses.get(0).getPostalCode();
//                lon = addresses.get(0).getLongitude();
//                lat = addresses.get(0).getLatitude();
//            }
//            Log.i("Long",lon + "");
//            Log.i("Lat", lat + "");
//            Log.i("Location",loc);
//        }catch(IOException e){
//            Log.i(e.toString(),e.toString());
//        }catch(NullPointerException e){
//            Log.i(e.toString(),e.toString());
//
//        }


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public class RequestTask extends AsyncTask<String, String, String> {
        private TextView myView;
        private String s;
        @Override
        protected String doInBackground(String... uri) {
            // create a consumer object and configure it with the access
            // token and token secret obtained from the service provider
            OAuthConsumer consumer = new DefaultOAuthConsumer("RzaWTkyPzBYsi3MJx1Ad1Q",
                    "VI7Nqye3YkxBPwbdv9llxX4dReg");
            consumer.setTokenWithSecret("YPgJ2tPzlJx0SEyiI8DscBxIF4CET8v1", "Huq-Asyht_FbMyTGHkoNxM-8chM");

            try {
                 URL url = new URL(uri[0]);

                HttpURLConnection request = (HttpURLConnection) url.openConnection();
                // sign the request
                consumer.sign(request);

                // send the request
                request.connect();

                BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line+"\n");
                }
                br.close();
                response = sb.toString();
                request.disconnect();

                return sb.toString();

            }catch(MalformedURLException m){
                Log.i("URL EXception",m.toString());
            }catch(IOException i){
                Log.i("URL EXception",i.toString());

            }catch(OAuthMessageSignerException o){
                Log.i("URL EXception",o.toString());

            }catch(OAuthExpectationFailedException o){
                Log.i("URL EXception",o.toString());
            }catch(OAuthCommunicationException o){
                Log.i("URL EXception",o.toString());
            }

            return response;

//            HttpClient httpclient = new DefaultHttpClient();
//            HttpResponse response;
//            String responseString = null;
//            try {
//                response = httpclient.execute(new HttpGet(uri[0]));
//                StatusLine statusLine = response.getStatusLine();
//                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
//                    ByteArrayOutputStream out = new ByteArrayOutputStream();
//                    response.getEntity().writeTo(out);
//                    out.close();
//                    responseString = out.toString();
//                } else {
//                    //Closes the connection.
//                    response.getEntity().getContent().close();
//                    throw new IOException(statusLine.getReasonPhrase());
//                }
//            } catch (ClientProtocolException e) {
//                //TODO Handle problems..
//            } catch (IOException e) {
//                //TODO Handle problems..
//            }
//
////                Log.i("", responseString);
//            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            hello = result;
            CardModel card1;
            try {
                JSONObject results = new JSONObject(result);
                JSONArray listOfPlaces = results.getJSONArray("businesses");
                for(int x = 0; x < 20; x++){

                    final String myurl = listOfPlaces.getJSONObject(x).getString("mobile_url");
                    card1 = new CardModel(listOfPlaces.getJSONObject(x).getString("name"), "Description goes here", getResources().getDrawable(R.drawable.ic_launcher));
                    Uri webpage = Uri.parse(myurl);
                    final Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
                    card1.setOnClickListener(new CardModel.OnClickListener() {
                        @Override
                        public void OnClickListener() {
                            startActivity(webIntent);
                        }
                    });
                    adapter.add(card1);
                }
            }catch(JSONException j){
                Log.i("JSONException",j.toString());

            }
             mycards.setAdapter(adapter);

//            Log.i("Result",result);


//            JSONObject results = new JSONObject();
//            try {
//               results = new JSONObject(result);
//            } catch (JSONException j){
//                Log.i("JSON Exception",j.toString());
//
//            }
//
//            try {
//                pageToken = results.getString("next_page_token");
//                Log.i("Page Token", pageToken);
//            }catch(JSONException j){
//                Log.i("JSON Exception",j.toString());
//                pageToken = "none";
//            }
//            CardModel card1;
//            try {
//                JSONArray places = results.getJSONArray("results");
//                for(int x = 0; x < 20; x++) {
//                    placesInformation.add(places.getJSONObject(x));
//                    imageTokens.add(places.getJSONObject(x).getString(""));
////                    card1 = new CardModel(places.getJSONObject(x).getString("name"), "Description goes here", getResources().getDrawable(R.drawable.ic_launcher));
////                    adapter.add(card1);
//                }
//            }catch (JSONException j){
//                Log.i("JSON Exception",j.toString());
//
//            }
////            mycards.setAdapter(adapter);
//            infoLoaded = true;
//            Log.i("Result",result);
        }
    }

}
