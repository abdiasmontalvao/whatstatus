package br.com.hbird.whatstatus;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.apache.commons.io.comparator.LastModifiedFileComparator;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import br.com.hbird.whatstatus.dominio.adapters.ItemAdapter;

public class MainActivity extends AppCompatActivity implements ItemAdapter.ItemClickListener {

    private RecyclerView recyclerItens;

    private ItemAdapter itemAdapter;

    private File[] itens;

    private static final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        verificarPermissoes();

        recyclerItens = findViewById(R.id.recycler_itens);

        File dir = new File(Environment.getExternalStorageDirectory() + "/WhatsApp/Media/.Statuses/");
        itens = dir.listFiles();

        Arrays.sort(itens, LastModifiedFileComparator.LASTMODIFIED_REVERSE);

        int numberOfColumns = 4;

        DisplayUtils displayUtils = new DisplayUtils(this, numberOfColumns);

        recyclerItens.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        itemAdapter = new ItemAdapter(this, itens, displayUtils.getItemWidthDP());
        itemAdapter.setClickListener(this);
        recyclerItens.setAdapter(itemAdapter);
    }


    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(this, SliderActivity.class);

        intent.putExtra("itens", itens);
        intent.putExtra("posicao_inicial", position);

        startActivity(intent);
    }

    public void verificarPermissoes() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_STORAGE);
            }
        }
    }
}
