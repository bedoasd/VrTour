package com.example.vrtour;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.vrtour.myinsta.MainActivity;
import com.example.vrtour.myinsta.RegisterActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.util.List;






public class MapActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback
        , TextToSpeech.OnInitListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private FusedLocationProviderClient fusedLocationClient;

    private Toolbar toolbar;
    private GoogleMap Mmap;
    double latitude,longitude;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastlocation;
    private Marker currentLocationmMarker;
    int PROXIMITY_RADIUS = 10000;

    FusedLocationProviderClient mFusedLocationClient;


    //search
    SearchView searchView;
    SupportMapFragment mapFragment;
    public static final int REQUEST_LOCATION_CODE = 99;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkLocationPermission();

        }

        mapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        //side menu
        DrawerLayout drawerLayout=findViewById(R.id.drawerlayout);

        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(
                this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close

        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView=findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    public void onLocationChanged(Location location) {

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        lastlocation = location;
        if(currentLocationmMarker != null)
        {
            currentLocationmMarker.remove();
        }
        Log.d("lat = ",""+latitude);
        LatLng latLng = new LatLng(location.getLatitude() , location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        currentLocationmMarker = Mmap.addMarker(markerOptions);
        Mmap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        Mmap.animateCamera(CameraUpdateFactory.zoomBy(20));

        if(client != null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(client, (LocationListener) this);
        }
    }

    protected synchronized void bulidGoogleApiClient() {
        client = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        client.connect();

    }

    public boolean checkLocationPermission()
    {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)  != PackageManager.PERMISSION_GRANTED )
        {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            return false;

        }
        else
            return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout=findViewById(R.id.drawerlayout);
        if (drawerLayout.isDrawerOpen(GravityCompat.END))
        {
            drawerLayout.closeDrawer(GravityCompat.END);
        }else{
            super.onBackPressed();

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_map_side, menu);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Mmap=map;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            bulidGoogleApiClient();
            Mmap.setMyLocationEnabled(true);
        }


        LatLng Egypt = new LatLng(26.540476, 29.790305);
        Mmap.moveCamera(CameraUpdateFactory.newLatLngZoom(Egypt,6));
        Mmap.addMarker(new MarkerOptions().position(new LatLng(29.979448, 31.134234)).title("The Great Pyramid of Giza"));
        Mmap.addMarker(new MarkerOptions().position(new LatLng(30.028776, 31.225150)).title("Salah El Din Al Ayouby"));
        Mmap.addMarker(new MarkerOptions().position(new LatLng(26.142129, 32.670147)).title("Dendera Temple Complex"));
        Mmap.addMarker(new MarkerOptions().position(new LatLng(29.849686, 31.254888)).title("Sphinx of Memphis"));
        Mmap.addMarker(new MarkerOptions().position(new LatLng(25.718951, 32.657217)).title("El Karnak"));
        Mmap.addMarker(new MarkerOptions().position(new LatLng(25.293440, 32.556270)).title("Temple of Khnum"));
        Mmap.addMarker(new MarkerOptions().position(new LatLng(24.978094, 32.873328)).title("The Temple of Horus"));
        Mmap.addMarker(new MarkerOptions().position(new LatLng(30.045989, 31.224247)).title("Cairo Tower"));
        Mmap.addMarker(new MarkerOptions().position(new LatLng(29.147591, 34.590199)).title("Sinai Canyons"));
        Mmap.addMarker(new MarkerOptions().position(new LatLng(30.008335, 30.895731)).title("Emerald City"));
        Mmap.addMarker(new MarkerOptions().position(new LatLng(25.719622, 32.601168)).title("Habu Temple Medinet Habu"));
        Mmap.addMarker(new MarkerOptions().position(new LatLng(26.142177, 32.670062)).title("Dendera Temple complex"));
        Mmap.addMarker(new MarkerOptions().position(new LatLng(24.452485, 32.928389)).title("Kom Ombo Temple"));
        Mmap.addMarker(new MarkerOptions().position(new LatLng(25.738457, 32.606494)).title("Mortuary Temple of Hatshepsu"));
        Mmap.addMarker(new MarkerOptions().position(new LatLng(31.214304, 29.885553)).title("Qaitbay Citadel"));
        Mmap.addMarker(new MarkerOptions().position(new LatLng(30.010419, 31.233180)).title("Egypt Mosques omar ibn alas"));
        Mmap.addMarker(new MarkerOptions().position(new LatLng(22.337549, 31.625756)).title("Abu Simbel temples"));
        Mmap.addMarker(new MarkerOptions().position(new LatLng(29.267990, 30.022512)).title("Wadi Al-Hitan"));
        Mmap.addMarker(new MarkerOptions().position(new LatLng(29.194860, 30.400266)).title("Wadi El Rayan"));
        Mmap.addMarker(new MarkerOptions().position(new LatLng(25.416977, 30.566524)).title("Qesm Al Wahat Al Khargah"));
        Mmap.addMarker(new MarkerOptions().position(new LatLng(24.978317, 32.873242)).title("Edfu Temple The Temple of Horus"));
        Mmap.addMarker(new MarkerOptions().position(new LatLng(28.623567, 31.286148)).title("Sannur Cave"));

        Mmap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String marketTitle=marker.getTitle();
                switch (marketTitle)
                {
                    case "The Great Pyramid of Giza":
                        Intent a = new Intent(MapActivity.this, PyramidsActivity.class);
                        a.putExtra("title",marketTitle);
                        startActivity(a);
                        break;
                    case "Salah El Din Al Ayouby":
                        Intent b = new Intent(MapActivity.this, AlQalahActivity.class);
                        b.putExtra("title",marketTitle);
                        startActivity(b);
                        break;
                    case "Dendera Temple Complex":
                        Intent c = new Intent(MapActivity.this, DeatilsVrActivity.class);
                        c.putExtra("title",marketTitle);
                        startActivity(c);
                        break;
                    case "Sphinx of Memphis":
                        Intent d = new Intent(MapActivity.this, SRamsesVrActivity.class);
                        d.putExtra("title",marketTitle);
                        startActivity(d);
                        break;
                    case "Cairo Tower":
                        Intent e = new Intent(MapActivity.this, CairoAboveCairoTowerActivity.class);
                        e.putExtra("title",marketTitle);
                        startActivity(e);
                        break;
                    case "El Karnak":
                        Intent f = new Intent(MapActivity.this, ElkarnakVrActivity.class);
                        f.putExtra("title",marketTitle);
                        startActivity(f);
                        break;
                    case "Temple of Khnum":
                        Intent g = new Intent(MapActivity.this, TempleOfKhnumVrActivity.class);
                        g.putExtra("title",marketTitle);
                        startActivity(g);
                        break;
                    case "The Temple of Horus":
                        Intent h = new Intent(MapActivity.this, TempleOfHorusVrActivity.class);
                        h.putExtra("title",marketTitle);
                        startActivity(h);
                        break;
                    case "Sinai Canyons":
                        Intent i = new Intent(MapActivity.this, Sinai_CanyonsActivity.class);
                        i.putExtra("title",marketTitle);
                        startActivity(i);
                        break;
                    case "Emerald City":
                        Intent j = new Intent(MapActivity.this, EmeraldCityActivity.class);
                        j.putExtra("title",marketTitle);
                        startActivity(j);
                        break;
                    case "Habu Temple Medinet Habu":
                        Intent k = new Intent(MapActivity.this, HabuTempleMedinetHabuActivity.class);
                        k.putExtra("title",marketTitle);
                        startActivity(k);
                        break;
                    case "Dendera Temple complex":
                        Intent l = new Intent(MapActivity.this, DenderaTempleComplexActivity.class);
                        l.putExtra("title",marketTitle);
                        startActivity(l);
                        break;
                    case "Kom Ombo Temple":
                        Intent m = new Intent(MapActivity.this, KomOmboTempleActivity.class);
                        m.putExtra("title",marketTitle);
                        startActivity(m);
                        break;
                    case "Mortuary Temple of Hatshepsu":
                        Intent n = new Intent(MapActivity.this, MortuaryTempleofHatshepsutActivity.class);
                        n.putExtra("title",marketTitle);
                        startActivity(n);
                        break;
                    case "Qaitbay Citadel":
                        Intent o = new Intent(MapActivity.this, QaitbayCitadelActivity.class);
                        o.putExtra("title",marketTitle);
                        startActivity(o);
                        break;
                    case "Egypt Mosques omar ibn alas":
                        Intent p = new Intent(MapActivity.this, EgyptMosquesomaribnalasActivity.class);
                        p.putExtra("title",marketTitle);
                        startActivity(p);
                        break;
                    case "Abu Simbel temples":
                        Intent q = new Intent(MapActivity.this, AbuSimbeltemplesActivity.class);
                        q.putExtra("title",marketTitle);
                        startActivity(q);
                        break;
                    case "Wadi Al-Hitan":
                        Intent r = new Intent(MapActivity.this, ValleyoftheWhalesWadiAlHitanActivity.class);
                        r.putExtra("title",marketTitle);
                        startActivity(r);
                        break;
                    case "Wadi El Rayan":
                        Intent s = new Intent(MapActivity.this, ElMedawaraMountWadiElRayanActivity.class);
                        s.putExtra("title",marketTitle);
                        startActivity(s);
                        break;
                    case "Sannur Cave":
                        Intent t  = new Intent(MapActivity.this, SannurCaveActivity.class);
                        t.putExtra("title",marketTitle);
                        startActivity(t);
                        break;
                    case "Qesm Al Wahat Al Khargah":
                        Intent v = new Intent(MapActivity.this, QesmAlWahatAlKhargahNewValleyGovernorateEgyptActivity.class);
                        v.putExtra("title",marketTitle);
                        startActivity(v);
                        break;
                    case "Edfu Temple The Temple of Horus":
                        Intent x = new Intent(MapActivity.this, EdfuTempleTheTempleofHorusActivity.class);
                        x.putExtra("title",marketTitle);
                        startActivity(x);
                        break;

                }
                return false;
            }
        });


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        sidemenulistner(item.getItemId());
        return false;

    }

    public  void sidemenulistner(int itemid){
        switch (itemid)
        {
            case R.id.weather:
                startActivity(new Intent(getApplicationContext(),WeatherActivity.class));
                break;
            case R.id.currency:
                startActivity(new Intent(getApplicationContext(), CurrencyActivity.class));
                break;
            case R.id.histogram:
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                break;
            case R.id.ListOfPlaces:
                startActivity(new Intent(getApplicationContext(),CatigorizationOfPlaces.class));
                break;

            case R.id.signoutsidemenu:
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                break;

        }

    }

    @Override
    public void onInit(int status)
    {

    }


    public void onClick(View view)
    {
        Object dataTransfer[] = new Object[2];
        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();

        switch(view.getId())
        {
            case R.id.B_search:
                EditText tf_location =  findViewById(R.id.TF_location);
                String location = tf_location.getText().toString();
                List<Address> addressList;


                if(!location.equals(""))
                {
                    Geocoder geocoder = new Geocoder(this);

                    try {
                        addressList = geocoder.getFromLocationName(location, 5);

                        if(addressList != null)
                        {
                            for(int i = 0;i<addressList.size();i++)
                            {
                                LatLng latLng = new LatLng(addressList.get(i).getLatitude() , addressList.get(i).getLongitude());
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(latLng);
                                markerOptions.title(location);
                                Mmap.addMarker(markerOptions);
                                Mmap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                Mmap.animateCamera(CameraUpdateFactory.zoomTo(20));
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

        }
    }


    private String getUrl(double latitude , double longitude , String nearbyPlace)
    {

        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location="+latitude+","+longitude);
        googlePlaceUrl.append("&radius="+PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type="+nearbyPlace);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key="+"AIzaSyATWpl2vkLFXtmsuTjSQq8Wnb_FN-1Lp1I");

        Log.d("MapActivity", "url = "+googlePlaceUrl.toString());

        return googlePlaceUrl.toString();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED)
        {

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {

                            if (location != null) {

                            }
                        }
                    });
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

