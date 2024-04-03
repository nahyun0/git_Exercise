package com.example.termproject;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewStructure;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    private static final int LOCATION_TEXT_ID = R.id.locationText;
    private static final int LOCATION_BUTTON_ID = R.id.locationButton;
    private RequestPermissiomnsUtil requestPermissionsUtil;

    @Override
    protected void onStart() {
        super.onStart();
        requestPermissionsUtil = new RequestPermissiomnsUtil(this);
        requestPermissionsUtil.requestLocation(); // 위치 권한 요청
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView locationText = findViewById(LOCATION_TEXT_ID);
        Button locationButton = findViewById(LOCATION_BUTTON_ID);
        locationButton.setOnClickListener(view -> getLocation(locationText, this));
    }

    @SuppressLint("MissingPermission")
    private void getLocation(final TextView textView, Context context) {
        FusedLocationProviderClient fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(context);

        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            textView.setText(location.getLatitude() + ", " + location.getLongitude());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        textView.setText(e.getLocalizedMessage());
                    }
                });
    }

    @SuppressLint("MissingPermission")
    private void getLocation(final TextView textView, AppCompatActivity activity) {
        FusedLocationProviderClient fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(activity);

        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            List<Address> addressList = getAddress(location.getLatitude(), location.getLongitude(), activity);
                            if (addressList != null && !addressList.isEmpty()) {
                                Address address = addressList.get(0);
                                StringBuilder addressString = new StringBuilder();
                                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                                    addressString.append(address.getAddressLine(i)).append("\n");
                                }
                                textView.setText(addressString.toString());

                                // 텍스트 뷰에서 텍스트를 가져와 공백을 기준으로 단어를 나누기
                                // 결과가 대한민국 경기도 성남시 수정구 복정동 554-1
                                // logcat 통해서 words[2] = 성남시 확인 완료
                                String[] words = textView.getText().toString().split("\\s+");
                                Log.d("MainActivity", "words[2] 값: " + words[2]);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                         textView.setText(e.getLocalizedMessage());

                    }
                });
    }

    private List<Address> getAddress(double lat, double lng, Context context) {
        List<Address> address = null;

        try {
            Geocoder geocoder = new Geocoder(context, Locale.KOREA);
            address = geocoder.getFromLocation(lat, lng, 1);
        } catch (IOException e) {
            Toast.makeText(context, "주소를 가져 올 수 없습니다", Toast.LENGTH_SHORT).show();
        }

        return address;
    }



    }




