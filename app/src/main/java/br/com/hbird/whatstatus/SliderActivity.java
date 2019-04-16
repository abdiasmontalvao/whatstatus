package br.com.hbird.whatstatus;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;

import br.com.hbird.whatstatus.dominio.adapters.ItemPagerAdapter;
import br.com.hbird.whatstatus.dominio.classes.Media;

import static br.com.hbird.whatstatus.MainActivity.ITENS_SALVOS;

public class SliderActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private ViewPager viewPagerSlider;

    private FloatingActionButton fabEnviar;

    private ArrayList<Media> itens;

    private Menu menu;

    public static final int ITEM_EXCLUIDO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPagerSlider = findViewById(R.id.viewpager_slider);

        fabEnviar = findViewById(R.id.fab_enviar);

        Bundle parametros = getIntent().getExtras();

        itens = (ArrayList<Media>) parametros.get("itens");
        int posicaoInicial = parametros.getInt("posicao_inicial");

        viewPagerSlider.setAdapter(new ItemPagerAdapter(this, itens));
        viewPagerSlider.setCurrentItem(posicaoInicial);
        viewPagerSlider.addOnPageChangeListener(this);

        fabEnviar.setOnClickListener(this);

        atualizarTitulo();
    }

    private void atualizarTitulo() {
        getSupportActionBar().setTitle("Exibindo " + (viewPagerSlider.getCurrentItem() + 1) + " de " + itens.size());
    }

    private void exibirMenu() {
        File file = new File(Environment.getExternalStorageDirectory() + "/WhatStatus/" + itens.get(viewPagerSlider.getCurrentItem()).getArquivo().getName());

        if (file.exists()) {
            menu.findItem(R.id.action_excluir).setVisible(true);
            menu.findItem(R.id.action_salvar).setVisible(false);
        } else {
            menu.findItem(R.id.action_salvar).setVisible(true);
            menu.findItem(R.id.action_excluir).setVisible(false);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        atualizarTitulo();
        exibirMenu();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_enviar:
                File item = itens.get(viewPagerSlider.getCurrentItem()).getArquivo();
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(item.getAbsolutePath()));
                shareIntent.setType((item.getName().contains(".mp4")) ? "video/mp4" : "image/*");
                startActivity(Intent.createChooser(shareIntent, "Enviar status"));
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_slider, menu);

        this.menu = menu;

        exibirMenu();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_salvar:
                try {
                    FileUtils.copyFile(itens.get(viewPagerSlider.getCurrentItem()).getArquivo(), new File(Environment.getExternalStorageDirectory() + "/WhatStatus/", itens.get(viewPagerSlider.getCurrentItem()).getArquivo().getName()));
                    exibirMenu();
                    Toast.makeText(this,"Adicionado aos status salvos", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this,"Não foi possível adicionar aos status salvos", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.action_excluir:
                File file = new File(Environment.getExternalStorageDirectory() + "/WhatStatus/" + itens.get(viewPagerSlider.getCurrentItem()).getArquivo().getName());
                file.delete();
                Toast.makeText(this,"Removido dos status salvos", Toast.LENGTH_SHORT).show();
                exibirMenu();
                if (itens.get(viewPagerSlider.getCurrentItem()).getArquivo().getAbsolutePath().contains("WhatStatus")) {
                    //((ItemPagerAdapter) viewPagerSlider.getAdapter()).removeItem(viewPagerSlider.getCurrentItem());
                    setResult(ITEM_EXCLUIDO);
                    finish();
                }
                break;
        }

        return true;
    }
}
