package com.madalin.licenta.controllers.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.madalin.licenta.adapters.CategorieCarduriAdapter;
import com.madalin.licenta.interfaces.MadalinApi;
import com.madalin.licenta.R;
import com.madalin.licenta.models.CardMelodie;
import com.madalin.licenta.models.CategorieCarduri;
import com.madalin.licenta.models.Melodie;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AcasaFragment extends Fragment {

    public static List<Melodie> listaMelodii; // lista pentru memorarea datelor melodiilor din request API

    RecyclerView recyclerView;
    ArrayList<CategorieCarduri> categorieCarduriArrayList; // pentru memorarea categoriilor

    public static ArrayList<CardMelodie> melodiiGitArrayList; // pentru memorarea melodiilor din categoria "X"
    public static ArrayList<CardMelodie> electronicaArrayList;
    public static ArrayList<CardMelodie> favoriteArrayList;

    private static final String ARG_PARAM1 = "param1"; // parametru de initializare a fragmentului
    private String mParam1;

    // constructor gol folosit atunci cand Android decide sa recreeze fragmentul
    public AcasaFragment() {
    }

    // metoda pentru crearea unei noi instante a fragmentului folosind parametrii
    public static AcasaFragment newInstance(String param1) {
        AcasaFragment fragment = new AcasaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);

        return fragment; // returneaza noua instanta a fragmentului
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }

        categorieCarduriArrayList = new ArrayList<>();
        melodiiGitArrayList = new ArrayList<>();
        electronicaArrayList = new ArrayList<>();
        favoriteArrayList = new ArrayList<>();

/*
        electronicaArrayList.clear();
        electronicaArrayList.add(new CardMelodie(R.drawable.background_logo, "My Way", "Max Cameron"));
        electronicaArrayList.add(new CardMelodie(R.drawable.logo_music, "Johnny Wants to Fight", "BADFLOWER"));
        electronicaArrayList.add(new CardMelodie(R.drawable.ic_setari, "NUMB", "Chri$tian Gate$"));
        categorieCarduriArrayList.add(new CategorieCarduri("Yah Yah!", electronicaArrayList));

        favoriteArrayList.clear();
        favoriteArrayList.add(new CardMelodie(R.drawable.background_logo, "Oki doki", "DAZN"));
        favoriteArrayList.add(new CardMelodie(R.drawable.ic_meniu, "Numa Numa", "OOO ZONIII"));
        favoriteArrayList.add(new CardMelodie(R.drawable.logo_music, "AZNAEB", "GTA SA"));
        categorieCarduriArrayList.add(new CategorieCarduri("Favorite", favoriteArrayList));

        electronicaArrayList.clear();
        electronicaArrayList.add(new CardMelodie(R.drawable.background_logo, "My Way", "Max Cameron"));
        electronicaArrayList.add(new CardMelodie(R.drawable.logo_music, "Johnny Wants to Fight", "BADFLOWER"));
        electronicaArrayList.add(new CardMelodie(R.drawable.ic_setari, "NUMB", "Chri$tian Gate$"));
        categorieCarduriArrayList.add(new CategorieCarduri("Yah Yah!", electronicaArrayList));

        favoriteArrayList.clear();
        favoriteArrayList.add(new CardMelodie(R.drawable.background_logo, "Oki doki", "DAZN"));
        favoriteArrayList.add(new CardMelodie(R.drawable.ic_meniu, "Numa Numa", "OOO ZONIII"));
        favoriteArrayList.add(new CardMelodie(R.drawable.logo_music, "AZNAEB", "GTA SA"));
        categorieCarduriArrayList.add(new CategorieCarduri("Favorite", favoriteArrayList));
    */
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewFragmentAcasa = inflater.inflate(R.layout.fragment_acasa, container, false); // obtinere vedere fragment_acasa din MainActivity
/*
        CategorieCarduriAdapter categorieCarduriAdapter;
        categorieCarduriAdapter = new CategorieCarduriAdapter(this.getContext(), categorieCarduriArrayList);

        recyclerView = viewFragmentAcasa.findViewById(R.id.acasa_recyclerViewCategoriiCarduri); // obtinere vedere RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(viewFragmentAcasa.getContext()));
        recyclerView.setAdapter(categorieCarduriAdapter);
        categorieCarduriAdapter.notifyDataSetChanged();
*/
        return viewFragmentAcasa;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://raw.githubusercontent.com/MadalinDolca/APIs/main/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MadalinApi madalinApi = retrofit.create(MadalinApi.class);
        Call<List<Melodie>> call = madalinApi.getMelodii();

        call.enqueue(new Callback<List<Melodie>>() {
            @Override
            public void onResponse(Call<List<Melodie>> call, Response<List<Melodie>> response) {
                if (!response.isSuccessful()) { // daca raspunsul nu se afla in intervalul [200..300)
                    Toast.makeText(AcasaFragment.this.getContext(), "Cod raspuns: " + response.code(), Toast.LENGTH_LONG).show();
                    return; // se iese din metoda
                }

                listaMelodii = response.body(); // adaugare body raspuns in lista de obiecte Melodie

                // golire liste pentru evitarea duplicarii datelor
                melodiiGitArrayList.clear();
                electronicaArrayList.clear();
                favoriteArrayList.clear();

                // iterare melodii si adaugare date in cardul din lista respectiva
                for (Melodie melodie : listaMelodii) {
                    melodiiGitArrayList.add(new CardMelodie(melodie.getImagineMelodie(), melodie.getNumeMelodie(), melodie.getNumeArtist()));
                    electronicaArrayList.add(new CardMelodie(melodie.getImagineMelodie(), melodie.getNumeMelodie(), melodie.getNumeArtist()));
                    favoriteArrayList.add(new CardMelodie(melodie.getImagineMelodie(), melodie.getNumeMelodie(), melodie.getNumeArtist()));
                }

                categorieCarduriArrayList.add(new CategorieCarduri("Mada API #1", melodiiGitArrayList)); // adaugare lista carduri melodii in lista categoriilor
                categorieCarduriArrayList.add(new CategorieCarduri("Mada API #2", electronicaArrayList)); // adaugare lista carduri melodii in lista categoriilor
                categorieCarduriArrayList.add(new CategorieCarduri("Mada API #3", favoriteArrayList)); // adaugare lista carduri melodii in lista categoriilor

                CategorieCarduriAdapter categorieCarduriAdapter = new CategorieCarduriAdapter(/*this.*/getContext(), categorieCarduriArrayList); // creare adapter categorie carduri
                recyclerView = view.findViewById(R.id.acasa_recyclerViewCategoriiCarduri); // obtinere vedere RecyclerView
                recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext() /*getActivity()*/));
                recyclerView.setAdapter(categorieCarduriAdapter); // setare adapter pe recyclerView pentru a furniza child views la cerere

                //categorieCarduriAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Melodie>> call, Throwable t) {
                Log.e("RETROFAIL", "Eroare: " + t.getMessage());
            }
        });
    }
}