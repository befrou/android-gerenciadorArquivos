package com.example.bruno.gerenciadordearquivos;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class FormActivity extends AppCompatActivity {

    private TextView txtPath;
    private EditText txtNomeArquivo, txtConteudoArquivo;
    private boolean edit;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        // verifyStoragePermissions(this);

        Intent intent = getIntent();
        txtPath = findViewById(R.id.txtPath);
        txtNomeArquivo = findViewById(R.id.txtNomeArquivo);
        txtConteudoArquivo = findViewById(R.id.txtConteudoArquivo);
        edit = intent.getBooleanExtra("edit", false);
        path = intent.getStringExtra("path");

        if(edit) {
            String[] partes = path.split("/");
            String arquivo = partes[partes.length - 1];
            txtNomeArquivo.setText(arquivo);
            txtPath.setText(path.replace(arquivo, ""));
            txtConteudoArquivo.setText(lerArquivo(path));
        } else {
            txtPath.setText(path);
        }
    }

    private String lerArquivo(String path) {
        File file = new File(path);
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while((line = br.readLine()) != null) {
                text.append(line + "\n");
            }
            br.close();
        } catch(IOException e) {
            Toast.makeText(this, "Não foi possível ler o arquivo!", Toast.LENGTH_LONG).show();
        }

        return text.toString();
    }

    public void btnSalvar_OnClick(View v) {
        try {
            CharSequence ch = txtPath.getText();
            String lastCharacter = String.valueOf(ch.charAt(ch.length() - 1));
            String delimiter = (lastCharacter.equals("/")) ? "" : "/";
            String arquivo = txtPath.getText() + delimiter + txtNomeArquivo.getText();
            File file = new File(Environment.getExternalStorageDirectory().getPath() + arquivo);

            if(!file.exists()) {
                file.createNewFile();
                FileOutputStream fOut = new FileOutputStream(file);
                fOut.write(txtConteudoArquivo.getText().toString().getBytes());
                fOut.close();
                finish();
            }
        } catch (IOException e) {
            Toast.makeText(this, "Não foi possível salvar o arquivo!", Toast.LENGTH_LONG).show();
        }
    }

    public void btnCancelar_OnClick(View v) {
        finish();
    }
}
