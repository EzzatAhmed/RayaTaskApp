package com.example.rayatask;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.app.VoiceInteractor;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.rayatask.model.Locations;
import com.example.rayatask.model.Pins;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.rayatask.databinding.ActivityMapsBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    public Button start, stop;
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient fusedLocationProviderClient;

    Pins pins = new Pins();
    Location location ;
    String URL = "https://192.168.1.7:44381/GetAllLocations";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
        buttonAction();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;




        //current try

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationClickListener(new GoogleMap.OnMyLocationClickListener() {
            @Override
            public void onMyLocationClick(Location location) {
                CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(20.0f);
                mMap.clear();

                MarkerOptions mp = new MarkerOptions();

                mp.position(new LatLng(location.getLatitude(), location.getLongitude()));

                mp.title("my position");

                mMap.addMarker(mp);
                mMap.moveCamera(center);
                mMap.animateCamera(zoom);

            }
        });
    }

    public void buttonAction(){
        start = findViewById(R.id.start);
        stop = findViewById(R.id.stop);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                try {
//                    sendGet("https://localhost:44381/GetAllLocations");
//                    getNearestBranch();
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                }

                try {
                    connectionAPI();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                LatLng nasrCity = new LatLng(30.016893, 31.377033);
                mMap.addMarker(new MarkerOptions().position(nasrCity).title("Marker in Nasr City"));
                LatLng elTahrir = new LatLng(30.044258,31.235819);
                mMap.addMarker(new MarkerOptions().position(elTahrir).title("Marker in El Tahrir"));
                LatLng helioplies = new LatLng(30.088850,31.316091);
                mMap.addMarker(new MarkerOptions().position(helioplies).title("Marker in Helioplies"));
                LatLng fifthsettle= new LatLng(30.001922,31.430067);
                mMap.addMarker(new MarkerOptions().position(fifthsettle).title("Marker in fifth Settlement"));
                LatLng october = new LatLng(29.952654, 30.921919);
                mMap.addMarker(new MarkerOptions().position(october).title("Marker in October"));

            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
            }
        });
    }

//    public List<Pins> getNearestBranch() throws MalformedURLException {
//        String urlString = "https://localhost:44381/GetAllLocations";
//        System.out.println(urlString);
//        URL url = new URL(urlString);
//        try
//        {
//            URLConnection conn = url.openConnection();
//            InputStream is = conn.getInputStream();
//
//           return  null;
//        }catch(Exception e)
//        {
//
//        }
//
//        return null;
//    }
//    public static String sendGet(final String url) {
//        StringBuilder result = new StringBuilder();
//        HttpURLConnection urlConnection = null;
//        try {
//            String apiUrl = "https://localhost:44381/GetAllLocations"; // concatenate uri with base url eg: localhost:8080/ + uri
//            URL requestUrl = new URL(apiUrl);
//            urlConnection = (HttpURLConnection) requestUrl.openConnection();
//            urlConnection.connect(); // no connection is made
//            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
//            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//            String line;
//            while ((line = reader.readLine()) != null) {
//                result.append(line);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            urlConnection.disconnect();
//        }
//        return result.toString();
//    }

    public void connectionAPI() throws IOException {

        handleSSLHandshake();

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


                Log.e("Rest Response success",response.toString());



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Rest Response error",error.toString());
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        JSONObject obj = new JSONObject(res);
                    } catch (UnsupportedEncodingException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    } catch (JSONException e2) {
                        // returned data is not JSONObject?
                        e2.printStackTrace();
                    }
                }
            }

        });
        objectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 30000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 30000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        requestQueue.add(objectRequest);
    }
    @SuppressLint("TrulyRandom")
    public static void handleSSLHandshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        } catch (Exception ignored) {
        }
    }

}