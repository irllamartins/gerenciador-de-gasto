package com.example.gerenciadordegasto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    Button btnCriarConta, btnEntar;
    EditText editUser, editPassword;
    CheckBox ckLembrar;

    SharedPreferences preferences;
    private String ARQUIVO_PREFERENCIA = "Arquivo_preferencias";

    DataBaseHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editUser = findViewById(R.id.text_input_user);
        editPassword = findViewById(R.id.text_input_password);
        ckLembrar = findViewById(R.id.ck_lembrar);

        // criar o banco de dados
        helper = new DataBaseHelper(getApplicationContext());
        helper.criarBanco();

        preferences = getSharedPreferences(ARQUIVO_PREFERENCIA, 0);
        if (preferences.contains("usuario")) {
            // carrega as informações de login nos campos
            String strUsuario = preferences.getString("usuario", "sem usuario");
            String strSenha = preferences.getString("senha", "sem senha");
            editUser.setText(strUsuario);
            editPassword.setText(strSenha);
            ckLembrar.setChecked(true);

            // consulatar no BD
            int usuarios = helper.consultarUsuario(strUsuario, strSenha);

            if (usuarios > 0) {
                // faz login
                Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(i);
                finish();
            }
        }


        btnCriarConta = findViewById(R.id.btn_register_account);
        btnCriarConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegisterAccountActivity.class);
                startActivity(i);
            }
        });
        btnEntar = findViewById(R.id.btn_login);
        btnEntar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strUsuario = editUser.getText().toString();
                String strSenha = editPassword.getText().toString();
                if (strUsuario.equals("")) {
                    editUser.setError("Campo vazio");
                } else if (strSenha.equals("")) {
                    editPassword.setError("Campo vazio");
                } else {
                    // consultar no banco de dados
                    int usuarios = helper.consultarUsuario(strUsuario, strSenha);

                    if (usuarios > 0) {
                        // verifica se salva para trazer os dadps dp usuario
                        if (ckLembrar.isChecked()) {
                            // saida dos dadps no SP
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("usuario", strUsuario);
                            editor.putString("senha", strSenha);
                            editor.commit();
                        }else{
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.remove("usuario");
                            editor.remove("senha");
                            editor.commit();
                        }


                        // faz login
                        Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(i);
                        finish();
                    } else {

                        Toast.makeText(LoginActivity.this, "Usuario ou senha incorretas", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

    }

}