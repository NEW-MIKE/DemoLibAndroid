package com.kaya.lbsdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.kaya.lbsdemo.databinding.ActivityMainBinding;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    private String mLocationProvider;
    private LocationManager mLocationManager;
    public static int LOCATION_CODE = 301;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        requestPermission();
    }


    private void requestPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (PermissionUtil.checkPermission(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,},
                    LOCATION_CODE
            )
            ) {
                updateLocation();
            }
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getLocation();
        }
    }

    private void getLocation(){
        //1.获取位置管理器
        mLocationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        if (PermissionUtil.checkPermission(
                this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                },
                LOCATION_CODE
        )
        ) {
            updateLocation();
        }
    }

    private void updateLocation(){
        if (PermissionUtil.checkSinglePermissionGrant(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION
        })){
            try {
                //1.获取位置管理器
                mLocationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

                List<String> providers = mLocationManager.getProviders(true);
                Location bestLocation = null;
                for (String provider : providers) {
                    Location l = mLocationManager.getLastKnownLocation(provider);
                    if (l == null) {
                        continue;
                    }
                    if (provider.equals("network") || provider.equals("gps")) {
                        if (provider.equals("network")){
                            Location location = mLocationManager.getLastKnownLocation(provider);
                        }else{
                            Location location = mLocationManager.getLastKnownLocation(provider);
                        }
                        if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                            mLocationProvider = provider;
                            bestLocation = l;
                        }
                    }
                }
                if (providers.contains(LocationManager.GPS_PROVIDER) && (mLocationProvider == null)) {
                    //如果是GPS
                    mLocationProvider = LocationManager.GPS_PROVIDER;
                }else if (mLocationProvider == null){
                    return;
                }
                //3.获取上次的位置，一般第一次运行，此值为null
                Location location = mLocationManager.getLastKnownLocation(mLocationProvider);
                locationEvent(location);

                // 监视地理位置变化，第二个和第三个参数分别为更新的最短时间minTime和最短距离minDistace
                mLocationManager.requestLocationUpdates(
                        mLocationProvider,
                        0,
                        0f,
                        locationListener
                );

            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }else {
        }
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            locationEvent(location);
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {

        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {
        }
    };


    private void locationEvent(Location location){
        SyncUtil.ui(() ->{
            if (location != null) {
                binding.showLocationTv.setText("经度：" + location.getLongitude()+" 纬度是："+location.getLatitude());
            }else {
                binding.showLocationTv.setText("经度：" + null+" 纬度是："+null);
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_CODE) {
            updateLocation();
        }
    }
}