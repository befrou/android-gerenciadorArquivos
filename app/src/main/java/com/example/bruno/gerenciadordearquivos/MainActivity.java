package com.example.bruno.gerenciadordearquivos;

import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import Adapter.CustomAdapter;
import dialog.NovaPastaDialog;
import utils.RunTimePermission;

public class MainActivity extends AppCompatActivity implements  NovaPastaDialog.OnCompleteListener {

    TextView txtPath;
    ListView lstArvore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RunTimePermission.verifyStoragePermissions(this);

        this.txtPath = findViewById(R.id.txtPath);
        this.lstArvore = findViewById(R.id.lstArvore);

        lstArvore.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String path = txtPath.getText().toString();
                String item = lstArvore.getItemAtPosition(position).toString();

                if(!item.equals("..")) {
                    File file = new File(Environment.getExternalStorageDirectory().getPath() + path + "/" + item);

                    if(file.isDirectory()) {
                        txtPath.setText(path.endsWith("/") ? path + item : path + "/" + item);
                        atualizarArvore();
                    } else if(file.getName().endsWith(".txt")) {
                        Intent intent = new Intent(MainActivity.this, FormActivity.class);
                        intent.putExtra("path", file.getAbsolutePath());
                        intent.putExtra("edit", true);
                        startActivity(intent);
                    } else {
                        Toast.makeText(view.getContext(), "Arquivo não suportado", Toast.LENGTH_LONG).show();
                    }
                } else {
                    if(!path.equals("/")) {
                        String[] partes = path.split("/");
                        String abovePath = path.replace(partes[partes.length - 1], "");
                        if(abovePath.endsWith("/") && abovePath.length() > 1) {
                            abovePath = abovePath.substring(0, abovePath.length() - 1);
                        }

                        txtPath.setText(abovePath);
                        atualizarArvore();
                    }
                }
                registerForContextMenu(lstArvore);
            }
        });
        atualizarArvore();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(v.getId() == R.id.lstArvore) {
            getMenuInflater().inflate(R.menu.menu_context, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        final String itemSelecionado = lstArvore.getItemAtPosition(info.position).toString();
        final String raiz = Environment.getExternalStorageDirectory().getPath();
        final String path = raiz + txtPath.getText().toString() + "/" + itemSelecionado;

        int id = item.getItemId();

        if(id == R.id.menu_ctx_editar)  {
            Intent intent = new Intent(MainActivity.this, FormActivity.class);
            intent.putExtra("path", path);
            intent.putExtra("edit", true);
            startActivity(intent);
            return true;
        }
        else if(id == R.id.menu_ctx_excluir) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Excluir")
                    .setMessage("Tem certeza que deseja excluir " + itemSelecionado + "?")
                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            File file = new File(path);
                            boolean deleted;
                            if(file.isDirectory()) {
                                deleted = deleteDirectory(file);
                            } else {
                                deleted = file.delete();
                            }

                            if(deleted) {
                                Toast.makeText(getBaseContext(), "Excluído com sucesso!", Toast.LENGTH_SHORT).show();
                                atualizarArvore();
                            } else {
                                Toast.makeText(getBaseContext(), "Falha ao tentar excluir item.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .create()
                    .show();

            return true;
        }

        return super.onContextItemSelected(item);
    }

    private boolean deleteDirectory(File file) {
        File[] items = file.listFiles();

        if(items != null) {
            for (File item : items) {
                deleteDirectory(item);
            }
        }
        return file.delete();
    }

    public void btnNovoArquivo_OnClick(View v) {
        Intent intent = new Intent(this, FormActivity.class);
        intent.putExtra("edit", false);
        intent.putExtra("path", this.txtPath.getText().toString());
        startActivity(intent);
    }

    public void btnNovaPasta_OnClick(View v) {
        DialogFragment fragment = new NovaPastaDialog(txtPath.getText().toString());
        fragment.show(getSupportFragmentManager(), "NovaPastaDialog");
    }

    @Override
    protected void onResume() {
        super.onResume();
        atualizarArvore();
    }

    //    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        atualizarArvore();
//    }

    public void atualizarArvore() {
        String raiz = Environment.getExternalStorageDirectory().getPath();
        String path = txtPath.getText().toString();

        File file = new File(raiz + path);

        String[] tree = file.list();

        if(tree != null) {
            ArrayList<String> arr = new ArrayList<>(Arrays.asList(tree));
            Collections.sort(arr);

            if(!path.equals("/")) {
                arr.add(0, "..");
            }

            int[] drawableIds = new int[arr.size()];
            String[] titlesArr = new String[arr.size()];
            int index = 0;

            for(String str : arr) {
                String fPath = raiz + path + "/" + str;
                File currFile = new File(fPath);

                titlesArr[index] = str;
                if(str.equals("..")) {
                    drawableIds[index] = 0;
                    ++index;
                    continue;
                }
                if(currFile.getName().endsWith(".txt")) {
                    drawableIds[index] = R.drawable.ic_edit_file;
                } else if(currFile.isDirectory()) {
                    drawableIds[index] = R.drawable.ic_folder;
                } else {
                    drawableIds[index] = R.drawable.ic_android_black_24dp;
                }

                ++index;
            }
//            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arr);

            CustomAdapter adapter = new CustomAdapter(this,  titlesArr, drawableIds);
            adapter.notifyDataSetChanged();
            lstArvore.setAdapter(adapter);
        }
    }

    @Override
    public void onComplete() {
        atualizarArvore();
    }
}
