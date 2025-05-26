package com.example.gerenciadordegasto;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterAccountActivity extends AppCompatActivity {

    EditText editNome, editUsuario, editSenha;

    Button btnRegistrar;

    DataBaseHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        editNome = findViewById(R.id.edit_name_input);
        editUsuario = findViewById(R.id.text_user_input);
        editSenha = findViewById(R.id.text_password_input);

        btnRegistrar = findViewById(R.id.btn_register_account);
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strName = editNome.getText().toString();
                String strUsuario = editUsuario.getText().toString();
                String strSenha = editSenha.getText().toString();

                if (strName.equals("") || strUsuario.equals("") || strSenha.equals("")) {
                    Toast.makeText(RegisterAccountActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                } else {
                    // criar um usuario no banco
                    helper = new DataBaseHelper(getApplicationContext());
                    if (helper.inserirUsuario(strName, strUsuario, strSenha)) {
                        Toast.makeText(RegisterAccountActivity.this, "Usuario criado com sucesso!", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(RegisterAccountActivity.this, LoginActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(RegisterAccountActivity.this, "Falha a criar usuario", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}