package com.madalin.licenta.controllers.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madalin.licenta.R;
import com.madalin.licenta.adapters.BannerSolicitareAdapter;
import com.madalin.licenta.controllers.MainActivity;
import com.madalin.licenta.models.Solicitare;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SolicitariStadiuFragment extends Fragment {

    private RecyclerView recyclerView;

    private static final String KEY_STADIU = "key_stadiu"; // cheia la care se va adauga argumentul (stadiul solicitarii) in Bundle
    private String stadiuSolicitare; // memoreaza stadiul solicitarii la crearea fragmentului
    private List<Solicitare> listaSolicitari; // lista pentru memorarea datelor solicitarilor din baza de date

    public SolicitariStadiuFragment() {
        // constructor folosit atunci cand Android recreeaza fragmentul
    }

    /**
     * Creeaza si returneaza un nou {@link SolicitariStadiuFragment} dupa adaugarea intr-un
     * {@link Bundle} a argumentelor oferite. {@link Bundle}-ul se seteaza ca argumente pentru noul
     * fragment pentru retinerea si reutilizarea acestora in cazul distrugerii si al crearii
     * fragmentului.
     *
     * @param stadiu argumentul oferit la crearea fragmentului
     * @return un nou {@link SolicitariStadiuFragment}
     */
    public static SolicitariStadiuFragment newInstance(String stadiu) {
        SolicitariStadiuFragment fragment = new SolicitariStadiuFragment();
        Bundle argumente = new Bundle();
        argumente.putString(KEY_STADIU, stadiu); // adauga argumentul in Bundle la cheia KEY_STADIU
        fragment.setArguments(argumente); // seteaza Bundle-ul ca argumente pentru noul fragment pentru retinere
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // obtine tipul de stadiu al solicitarii din lista de argumente
        if (getArguments() != null) {
            stadiuSolicitare = getArguments().getString(KEY_STADIU);
        }

        listaSolicitari = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        switch (stadiuSolicitare) {
            case Solicitare.ACCEPTATA:
                return inflater.inflate(R.layout.fragment_solicitari_acceptate, container, false);

            case Solicitare.RESPINSA:
                return inflater.inflate(R.layout.fragment_solicitari_respinse, container, false);

            default:
                return inflater.inflate(R.layout.fragment_solicitari_neevaluate, container, false);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // obtine vederea RecyclerView-ului specific stadiului solicitarii
        switch (stadiuSolicitare) {
            case Solicitare.ACCEPTATA:
                recyclerView = view.findViewById(R.id.solicitari_acceptate_recyclerView);
                break;

            case Solicitare.RESPINSA:
                recyclerView = view.findViewById(R.id.solicitari_respinse_recyclerView);
                break;

            default:
                recyclerView = view.findViewById(R.id.solicitari_neevaluate_recyclerView);
                break;
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext())); // seteaza LayoutManager-ul pe care recyclerView-ul il va folosi

        FirebaseDatabase.getInstance().getReference("solicitari")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listaSolicitari.clear(); // curata lista curenta cu solicitari

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) { // parcurge toti descendentii nodului "solicitari" din baza de date
                            Solicitare solicitare = dataSnapshot.getValue(Solicitare.class); // adauga valorile descendentului in obiect
                            solicitare.setCheie(dataSnapshot.getKey()); // memoreaza cheia descendentului

                            // daca cheia solicitantului sau a artistului din solicitare este egala cu cheia utilizatorului curent
                            if (Objects.equals(solicitare.getCheieSolicitant(), MainActivity.utilizator.getCheie())
                                    || Objects.equals(solicitare.getCheieArtist(), MainActivity.utilizator.getCheie())) {

                                // daca stadiul solicitarii este egal cu stadiul furnizat la crearea fragmentului
                                if (Objects.equals(solicitare.getStadiu(), stadiuSolicitare)) {
                                    listaSolicitari.add(solicitare); // adauga obiectul in lista
                                }
                            }
                        }

                        Collections.reverse(listaSolicitari); // reordoneaza lista cu solicitari
                        recyclerView.setAdapter(new BannerSolicitareAdapter(getContext(), listaSolicitari)); // creeeaza si seteaza adapter pe recyclerView pentru a furniza child views la cerere
                        afiseazaMesaj(view);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Eroare: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Ascunde {@link com.airbnb.lottie.LottieAnimationView}-ul specific layout-ului stadiului
     * solicitarii dupa afisarea banner-elor.
     */
    private void ascundeLottie(View view) {
        switch (stadiuSolicitare) {
            case Solicitare.ACCEPTATA:
                view.findViewById(R.id.solicitari_acceptate_lottieAnimationView).setVisibility(View.GONE);
                break;

            case Solicitare.RESPINSA:
                view.findViewById(R.id.solicitari_respinse_lottieAnimationView).setVisibility(View.GONE);
                break;

            default:
                view.findViewById(R.id.solicitari_neevaluate_lottieAnimationView).setVisibility(View.GONE);
                break;
        }
    }

    /**
     * Afiseaza un {@link android.widget.TextView} daca nu exista solicitari in stadiul respectiv in
     * {@link #listaSolicitari}.
     * Apeleaza {@link #ascundeLottie(View)} pentru ascunderea animatiei.
     *
     * @param view vederea fragmentului
     */
    private void afiseazaMesaj(View view) {
        switch (stadiuSolicitare) {
            case Solicitare.ACCEPTATA:
                ascundeLottie(view); // ascunde animatia

                // afiseaza un mesaj daca lista solicitarilor acceptate este goala
                if (listaSolicitari.size() == 0) {
                    view.findViewById(R.id.solicitari_acceptate_textView).setVisibility(View.VISIBLE);
                }
                break;

            case Solicitare.RESPINSA:
                ascundeLottie(view); // ascunde animatia

                // afiseaza un mesaj daca lista solicitarilor respinse este goala
                if (listaSolicitari.size() == 0) {
                    view.findViewById(R.id.solicitari_respinse_textView).setVisibility(View.VISIBLE);
                }
                break;

            default:
                ascundeLottie(view); // ascunde animatia

                // afiseaza un mesaj daca lista solicitarilor neevaluate este goala
                if (listaSolicitari.size() == 0) {
                    view.findViewById(R.id.solicitari_neevaluate_textView).setVisibility(View.VISIBLE);
                }
                break;
        }
    }
}