package br.com.hbird.whatstatus;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.io.comparator.LastModifiedFileComparator;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.hbird.whatstatus.dominio.adapters.ItemAdapter;

import static br.com.hbird.whatstatus.SliderActivity.ITEM_EXCLUIDO;

public class MainActivity extends AppCompatActivity implements ItemAdapter.ItemClickListener {

    private BottomNavigationView navigation;

    private RecyclerView recyclerItens;

    private FloatingActionButton fabTopo;

    private ImageView imgSemResultados;

    private TextView txtSemResultados;

    private ItemAdapter itemAdapter;

    private List<File> itens;

    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 0;
    private static final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 1;

    public static final int ITENS_TEMPORARIOS = 0;
    public static final int ITENS_SALVOS = 1;

    private int yScroll = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        verificarPermissoes();

        initDir();

        exibirNovidades();

        navigation = findViewById(R.id.navigation);

        recyclerItens = findViewById(R.id.recycler_itens);

        fabTopo = findViewById(R.id.fab_topo);

        imgSemResultados = findViewById(R.id.img_sem_resultados);

        txtSemResultados = findViewById(R.id.txt_sem_resultados);

        recyclerItens.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                yScroll = yScroll + dy;

                if (yScroll > 10) {
                    fabTopo.show();
                } else {
                    fabTopo.hide();
                }
            }
        });

        fabTopo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GridLayoutManager layoutManager = (GridLayoutManager) recyclerItens.getLayoutManager();
                try {
                    layoutManager.scrollToPositionWithOffset(0, 0);
                    yScroll = 0;
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

        intent.putExtra("itens", new ArrayList<>(this.itens));
        intent.putExtra("posicao_inicial", position);

        if (navigation.getSelectedItemId() == R.id.navigation_salvos) {
            startActivityForResult(intent, ITENS_SALVOS);
        } else {
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_atualizar:
                atualizarItens();
                break;
        }

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ITENS_SALVOS && resultCode == ITEM_EXCLUIDO) {
            atualizarItens();
        }
    }

    private void atualizarItens() {
        navigation.setSelectedItemId(navigation.getSelectedItemId());
    }

    private void carregarItens(int tipo) {
        yScroll = 0;

        File dir = new File(Environment.getExternalStorageDirectory() + ((tipo == ITENS_SALVOS) ? "/WhatStatus/" : "/WhatsApp/Media/.Statuses/"));
        File[] itens = dir.listFiles();

        Arrays.sort(itens, LastModifiedFileComparator.LASTMODIFIED_REVERSE);

        this.itens = Arrays.asList(itens);

        int numberOfColumns = 4;

        DisplayUtils displayUtils = new DisplayUtils(this, numberOfColumns);

        recyclerItens.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        itemAdapter = new ItemAdapter(this, new ArrayList<>(this.itens), displayUtils.getItemWidthDP());
        itemAdapter.setClickListener(this);
        recyclerItens.setAdapter(itemAdapter);

        if (this.itens.size() == 0) {
            imgSemResultados.setVisibility(View.VISIBLE);
            txtSemResultados.setVisibility(View.VISIBLE);
        } else {
            imgSemResultados.setVisibility(View.GONE);
            txtSemResultados.setVisibility(View.GONE);
        }
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

    public void exibirNovidades() {
        SharedPreferences prefs = getSharedPreferences("app_info", Context.MODE_PRIVATE);

        long versaoAtual = 1;

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versaoAtual = Long.valueOf(pInfo.versionName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (prefs.getLong("VERSAO", 0) < versaoAtual) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong("VERSAO", versaoAtual);
            editor.commit();

            AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).create();
            dialog.setTitle("Novidades da última versão");
            dialog.setMessage("\n" +
                    "* Correção de bugs\n\n" +
                    "* Melhorias na interface\n\n" +
                    "* Agora é possível salvar os status permanentemente\n");
            dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Fechar",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            dialog.show();
        }
    }
}