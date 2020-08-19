package com.spect.pantaucovid19;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.ContentValues.TAG;

public class HomeFragment extends Fragment {
    private TextView tv_sembuh, tv_positif, tv_meninggal, tv_sembuh_prov, tv_meninggal_prov, tv_positif_prov;
    private ViewFlipper v_flipper;
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    private AutoCompleteTextView select_prov;
    private ImageView img_dropdown, imgsearch;
    private ProgressBar prog_sembuh, prog_meni, prog_posi;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Context mContext;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);;
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        int images[] = {R.drawable.slider1,
                R.drawable.slider2,
                R.drawable.slider3,
                R.drawable.slider4,
                R.drawable.slider5,
                R.drawable.slider6};

        tv_sembuh = v.findViewById(R.id.tv_sembuh);
        tv_meninggal = v.findViewById(R.id.tv_meninggal);
        tv_positif = v.findViewById(R.id.tv_positif);
        v_flipper = v.findViewById(R.id.v_flipper);
        select_prov = v.findViewById(R.id.select_prov);
        tv_sembuh_prov = v.findViewById(R.id.tv_sembuh_prov);
        tv_meninggal_prov = v.findViewById(R.id.tv_mening_prov);
        tv_positif_prov = v.findViewById(R.id.tv_positif_prov);
        img_dropdown = v.findViewById(R.id.imgdroopdown);
        imgsearch = v.findViewById(R.id.imgsearch);
        prog_meni = v.findViewById(R.id.prog_mening);
        prog_posi = v.findViewById(R.id.prog_positif);
        prog_sembuh = v.findViewById(R.id.prog_sembuh);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        getLocation();

        img_dropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select_prov.showDropDown();
            }
        });

        for (int image : images) {
            flipperImages(image);
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.kawalcorona.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        getPosts();

        getProvinsi();

        return v;
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(mContext
                , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null) {
                        try {
                            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                            select_prov.setText(addresses.get(0).getAdminArea());
                            Log.d(TAG, "Location : " + addresses.get(0).getAdminArea());
                            setData();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                }
            });
        } else {
            ActivityCompat.requestPermissions(HomeFragment.super.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    private void getProvinsi() {
        Call<List<Provinsi>> call = jsonPlaceHolderApi.getProv();

        call.enqueue(new Callback<List<Provinsi>>() {
            @Override
            public void onResponse(Call<List<Provinsi>> call, Response<List<Provinsi>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Code : " + response.code(), Toast.LENGTH_SHORT).show();
                }
//                Log.d(TAG, "onResponse :    " + response.body().toString());
                final List<Provinsi> prov = response.body();
                final String[] namaprov = new String[prov.size()];
                for (int i = 0; i < prov.size(); i++) {
                    Log.d(TAG, "onResponseSuccess : \n" +
                            "kodeProv : " + prov.get(i).getAttributes().getKodeProvi() + "\n" +
                            "Provinsi : " + prov.get(i).getAttributes().getProvinsi());
                    namaprov[i] = prov.get(i).getAttributes().getProvinsi();
                }
                imgsearch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String check = select_prov.getText().toString();
                        String strsembuh;
                        float posiprog = 0;
                        float meniprog = 0;
                        float sembuprog = 0;
                        float hasiprogsembu = 0;
                        float hasiprogmeni = 0;
                        float hasiprogposi = 0;
//                        Toast.makeText(getContext(), select_prov.getText(), Toast.LENGTH_SHORT).show();
//                        tv_sembuh_prov.setText("69");

                        for (int i = 0; i < prov.size(); i++) {
                            String provnama = prov.get(i).getAttributes().getProvinsi();
                            if (check.equals(provnama)) {
                                tv_sembuh_prov.setText(prov.get(i).getAttributes().getKasusSemb());
                                tv_meninggal_prov.setText(prov.get(i).getAttributes().getKasusMeni());
                                tv_positif_prov.setText(prov.get(i).getAttributes().getKasusPosi());
                                posiprog = Float.parseFloat(prov.get(i).getAttributes().getKasusPosi());
                                meniprog = Float.parseFloat(prov.get(i).getAttributes().getKasusMeni());
                                sembuprog = Float.parseFloat(prov.get(i).getAttributes().getKasusSemb());
                                hasiprogsembu = sembuprog / posiprog * 100;
                                hasiprogmeni = meniprog / posiprog * 100;
                                hasiprogposi = posiprog / posiprog * 100;
                                prog_sembuh.setProgress(Math.round(hasiprogsembu));
                                prog_meni.setProgress(Math.round(hasiprogmeni));
                                prog_posi.setProgress(Math.round(hasiprogposi));
                            }
//                            else {
//                                Toast.makeText(getContext(), "FAILL", Toast.LENGTH_SHORT).show();
//                            }

                        }
                    }
                });
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_dropdown_item_1line, namaprov);
                select_prov.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<Provinsi>> call, Throwable t) {
                Toast.makeText(mContext, "Ada yang salah : " + t.getMessage() + " \n Coba lagi nanti", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setData() {
        Call<List<Provinsi>> call = jsonPlaceHolderApi.getProv();

        call.enqueue(new Callback<List<Provinsi>>() {
            @Override
            public void onResponse(Call<List<Provinsi>> call, Response<List<Provinsi>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Code : " + response.code(), Toast.LENGTH_SHORT).show();
                }
                final List<Provinsi> prov = response.body();
                String check = select_prov.getText().toString();
                float posiprog = 0;
                float meniprog = 0;
                float sembuprog = 0;
                float hasiprogsembu = 0;
                float hasiprogmeni = 0;
                float hasiprogposi = 0;

                for (int i = 0; i < prov.size(); i++) {
                    String provnama = prov.get(i).getAttributes().getProvinsi();
                    if (check.equals(provnama)) {
                        tv_sembuh_prov.setText(prov.get(i).getAttributes().getKasusSemb());
                        tv_meninggal_prov.setText(prov.get(i).getAttributes().getKasusMeni());
                        tv_positif_prov.setText(prov.get(i).getAttributes().getKasusPosi());
                        posiprog = Float.parseFloat(prov.get(i).getAttributes().getKasusPosi());
                        meniprog = Float.parseFloat(prov.get(i).getAttributes().getKasusMeni());
                        sembuprog = Float.parseFloat(prov.get(i).getAttributes().getKasusSemb());
                        hasiprogsembu = sembuprog / posiprog * 100;
                        hasiprogmeni = meniprog / posiprog * 100;
                        hasiprogposi = posiprog / posiprog * 100;
                        prog_sembuh.setProgress(Math.round(hasiprogsembu));
                        prog_meni.setProgress(Math.round(hasiprogmeni));
                        prog_posi.setProgress(Math.round(hasiprogposi));
                    }

                }

            }

            @Override
            public void onFailure(Call<List<Provinsi>> call, Throwable t) {

            }
        });
    }

    private void getPosts() {
        Call<List<Post>> call = jsonPlaceHolderApi.getPosts();

        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (!response.isSuccessful()) {
                    tv_sembuh.setText("Code :" + response.code());
                    return;
                }
                List<Post> posts = response.body();
                for (Post post : posts) {
//                    String content = "";
//                    content += "Negara: " + post.getName() + "\n";
//                    content += "Positif: " + post.getPositif() + "\n";
//                    content += "Sembuh: " + post.getSembuh() + "\n";
//                    content += "Meninggal: " + post.getMeninggal() + "\n";

                    tv_sembuh.setText(post.getSembuh());
                    tv_meninggal.setText(post.getMeninggal());
                    tv_positif.setText(post.getPositif());
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                tv_sembuh.setText(t.getMessage());
            }
        });
    }

    public void flipperImages(int image) {
        ImageView imageView = new ImageView(getContext());
        imageView.setBackgroundResource(image);

        v_flipper.addView(imageView);
        v_flipper.setFlipInterval(4000);
        v_flipper.setAutoStart(true);

        v_flipper.setInAnimation(getContext(), android.R.anim.slide_in_left);
        v_flipper.setOutAnimation(getContext(), android.R.anim.slide_out_right);
    }

}
