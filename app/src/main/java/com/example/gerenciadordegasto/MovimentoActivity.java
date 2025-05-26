package com.example.gerenciadordegasto;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MovimentoActivity extends AppCompatActivity {

    Button btnSalvar;

    EditText editData, editValor, editDescription;
    RadioGroup rgbTipo;
    RadioButton radiusSelect;

    int dia, mes, ano, hora, minuto, segundo;

    String dataFomatada;

    DataBaseHelper helper;

    String operacao = "inserir";

    String data, valor, descricao, tipo;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_movimento);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        helper = new DataBaseHelper(getApplicationContext());

        editData = findViewById(R.id.editData);
        editValor = findViewById(R.id.editValor);
        editDescription = findViewById(R.id.editDescricao);

        rgbTipo = findViewById(R.id.radioGroupTipo);
        /*rgbTipo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radiusSelect = findViewById(checkedId);
            }
        });*/

        Calendar calendar = Calendar.getInstance();
        ano = calendar.get(Calendar.YEAR);
        mes = calendar.get(Calendar.MONTH) + 1;
        dia = calendar.get(Calendar.DAY_OF_MONTH);
        hora = calendar.get(Calendar.HOUR_OF_DAY);
        minuto = calendar.get(Calendar.MINUTE);
        segundo = calendar.get(Calendar.SECOND);

        editData.setText(dia + "/" + mes + "/" + ano + " " + hora + ":" + minuto + ":" + segundo);
        dataFomatada = ano + "-" + mes + "-" + dia + " " + hora + ":" + minuto + ":" + segundo;
        editData.setFocusable(false);
        editData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(MovimentoActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String textDate = dayOfMonth + "/" + (month + 1) + "/" + year + " " + hora + ":" + minuto + ":" + segundo;
                        ;
                        dataFomatada = year + "-" + (month + 1) + "-" + dayOfMonth + " " + hora + ":" + minuto + ":" + segundo;
                        editData.setText(textDate);
                    }
                }, ano, mes, dia);
                datePickerDialog.show();
            }
        });

        Bundle bundle = getIntent().getExtras();

        //substituir os valores padrao com os valores do editar
        if (bundle != null) {
            operacao = "alterar";
            id = bundle.getInt("id");
            String dataBanco = bundle.getString("data");
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

            Date date = null;
            try {
                date = inputFormat.parse(dataBanco);
                String dataFormatada = outputFormat.format(date);
                editData.setText(dataFormatada);
            } catch (RuntimeException e) {
                throw new RuntimeException(e);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            valor = bundle.getString("valor");
            editValor.setText(valor);
            descricao = bundle.getString("descricao");
            editDescription.setText(descricao);
            tipo = bundle.getString("tipo");
            if (tipo.equals("Entrada")) {
                rgbTipo.check(R.id.rb_entrada);
            } else {
                rgbTipo.check(R.id.rb_saida);
            }
        }

        btnSalvar = findViewById(R.id.btn_salve_movimentacao);
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strValor = editValor.getText().toString();
                String strDescricao = editDescription.getText().toString();
                if (strValor.equals("")) {
                    editValor.setError("Campo vazio");
                } else if (strDescricao.equals("")) {
                    editDescription.setError("Campo vazio");
                } else {
                    int radioButtonId = rgbTipo.getCheckedRadioButtonId();
                    View radioButton = rgbTipo.findViewById(radioButtonId);
                    int idRb = rgbTipo.indexOfChild(radioButton);
                    RadioButton radio = (RadioButton) rgbTipo.getChildAt(idRb);
                    String strTipo = radio.getText().toString();

                    salvar(dataFomatada, strValor, strDescricao, strTipo);

                }

            }
        });
    }

    private void salvar(String dataFomatada, String strValor, String strDescricao, String strTipo) {
      if(operacao.equals("inserir")) {
          // inserir registro no banco
          if (helper.inserirMovimentacao(dataFomatada, strValor, strDescricao, strTipo)) {
              Toast.makeText(this, "Movimento inserido", Toast.LENGTH_SHORT).show();
              finish();
          } else {
              Toast.makeText(this, "Falha ao inserir movimento", Toast.LENGTH_SHORT).show();
          }
      }else{
          // atualizar registro no banco
          if (helper.atualizarMovimentacao(dataFomatada, strValor, strDescricao, strTipo,id)) {
              Toast.makeText(this, "Movimento atualizado", Toast.LENGTH_SHORT).show();
              finish();
          } else {
              Toast.makeText(this, "Falha ao atualizar movimento", Toast.LENGTH_SHORT).show();
          }
      }
    }
}