package br.com.hbird.whatstatus;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import java.io.File;

import br.com.hbird.whatstatus.dominio.adapters.ItemPagerAdapter;

public class SliderActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private ViewPager viewPagerSlider;

    private FloatingActionButton fabEnviar;

    private File[] itens;

    private int quantidadeItens;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPagerSlider = findViewById(R.id.viewpager_slider);

        fabEnviar = findViewById(R.id.fab_enviar);

        Bundle parametros = getIntent().getExtras();

        itens = (File[]) parametros.get("itens");
        int posicaoInicial = parametros.getInt("posicao_inicial");
        quantidadeItens = itens.length;

        viewPagerSlider.setAdapter(new ItemPagerAdapter(this, itens));
        viewPagerSlider.setCurrentItem(posicaoInicial);
        viewPagerSlider.addOnPageChangeListener(this);

        fabEnviar.setOnClickListener(this);

        atualizarTitulo();
    }

    public void atualizarTitulo() {
        getSupportActionBar().setTitle("Exibindo " + (viewPagerSlider.getCurrentItem() + 1) + " de " + quantidadeItens);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        atualizarTitulo();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_enviar:
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(itens[viewPagerSlider.getCurrentItem()].getAbsolutePath()));
                shareIntent.setType((itens[viewPagerSlider.getCurrentItem()].getName().contains(".mp4")) ? "video/mp4" : "image/*");
                startActivity(Intent.createChooser(shareIntent, "Enviar status"));
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return true;
    }
}
