package br.com.hbird.whatstatus;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenu;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import org.apache.commons.io.comparator.LastModifiedFileComparator;

import java.io.File;
import java.util.Arrays;

import br.com.hbird.whatstatus.dominio.adapters.ItemAdapter;

public class MainActivity extends AppCompatActivity implements ItemAdapter.ItemClickListener {

    private BottomNavigationView navigation;

    private RecyclerView recyclerItens;

    private FloatingActionButton fabTopo;

    private ItemAdapter itemAdapter;

    private File[] itens;

    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 0;
    private static final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 1;
    private static final int ITENS_TEMPORARIOS = 0;
    private static final int ITENS_SALVOS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        verificarPermissoes();

        initDir();

        navigation = findViewById(R.id.navigation);

        recyclerItens = findViewById(R.id.recycler_itens);

        fabTopo = findViewById(R.id.fab_topo);

        fabTopo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GridLayoutManager layoutManager = (GridLayoutManager) recyclerItens.getLayoutManager();
                try {
                    layoutManager.scrollToPositionWithOffset(0, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_temporarios:
                        carregarItens(ITENS_TEMPORARIOS);
                        return true;
                    case R.id.navigation_salvos:
                        carregarItens(ITENS_SALVOS);
                        return true;
                }

                return false;
            }
        });

        navigation.setSelectedItemId(R.id.navigation_temporarios);
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(this, SliderActivity.class);

        intent.putExtra("itens", itens);
        intent.putExtra("posicao_inicial", position);

        startActivity(intent);
    }

    public void carregarItens(int tipo) {
        File dir = new File(Environment.getExternalStorageDirectory() + ((tipo == ITENS_SALVOS) ? "/WhatStatus/" : "/WhatsApp/Media/.Statuses/"));
        itens = dir.listFiles();

        Arrays.sort(itens, LastModifiedFileComparator.LASTMODIFIED_REVERSE);

        int numberOfColumns = 4;

        DisplayUtils displayUtils = new DisplayUtils(this, numberOfColumns);

        recyclerItens.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        itemAdapter = new ItemAdapter(this, itens, displayUtils.getItemWidthDP());
        itemAdapter.setClickListener(this);
        recyclerItens.setAdapter(itemAdapter);
    }

    public void verificarPermissoes() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
            }
        }

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_STORAGE);
            }
        }
    }

    public void initDir() {
        File dir = new File(Environment.getExternalStorageDirectory() + "/WhatStatus");

        if (!dir.exists()) {
            dir.mkdir();
        }
    }
}