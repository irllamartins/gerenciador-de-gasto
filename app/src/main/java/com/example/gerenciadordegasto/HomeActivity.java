package com.example.gerenciadordegasto;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.gerenciadordegasto.R;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gerenciadordegasto.databinding.ActivityHomeBinding;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;

    RecyclerView recyclerMovimentos;
    TextView tvEntradas, tvSaidas, tvTotal;

    ArrayList<MovimentoObj> movimentoObjs;

    DataBaseHelper helper;

    private String ARQUIVO_PREFERENCIA = "Arquivo_preferencias";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle(getTitle());

        recyclerMovimentos = findViewById(R.id.rcw_listagem);
        tvEntradas = findViewById(R.id.tv_entradas);
        tvSaidas = findViewById(R.id.tv_saidas);
        tvTotal = findViewById(R.id.tv_total);

        FloatingActionButton fab = binding.fab;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show();*/
                Intent i = new Intent(HomeActivity.this, MovimentoActivity.class);
                startActivity(i);
            }
        });
        helper = new DataBaseHelper(getApplicationContext());
        carregarMovimentos();
    }

    private void carregarMovimentos() {
        movimentoObjs = new ArrayList<MovimentoObj>();
        movimentoObjs = helper.carregarMovimentos();


        recyclerMovimentos.setAdapter(null);

        System.out.println("!" + movimentoObjs.size());
        if (movimentoObjs.size() > 0) {

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                    this,
                    LinearLayoutManager.VERTICAL,
                    false
            );
            //carrega para vizualização
            recyclerMovimentos.setLayoutManager(layoutManager);

            recyclerMovimentos.setAdapter(new AdapterHome(movimentoObjs));
        }
        //carregar o total de entradas
        String strEntradas = helper.total("Entrada");
        double entradaValor = 0.0;
        try {
            entradaValor = Double.parseDouble(strEntradas);
            tvEntradas.setText("Entrada: " + new DecimalFormat("R$ #,##0.00").format(entradaValor));

        } catch (Exception e) {
            e.printStackTrace();
            tvEntradas.setText("Entrada: R$ 0,00");

        }

        //carregar o total de saidas
        String strSaida = helper.total("Saida");
        double saidaValor = 0.0;
        try {
            saidaValor = Double.parseDouble(strSaida);
            tvSaidas.setText("Saida: " + new DecimalFormat("R$ #,##0.00").format(saidaValor));

        } catch (Exception e) {
            e.printStackTrace();
            tvEntradas.setText("Saidas: R$ 0,00");

        }

        tvTotal.setText("Saldo: " + new DecimalFormat("R$ #,##0.00").format(entradaValor - saidaValor));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_signout) {
            System.out.println("! entrou");
            // limpar o arquivo de preferencia
            SharedPreferences preferences = getSharedPreferences(ARQUIVO_PREFERENCIA, 0);
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove("usuario");
            editor.remove("senha");
            editor.commit();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public class AdapterHome extends RecyclerView.Adapter {

        ArrayList<MovimentoObj> movimentoObjs;

        public AdapterHome(ArrayList<MovimentoObj> movimentoObjs) {
            this.movimentoObjs = movimentoObjs;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.row_movimento, parent, false);
            return new ViewHolderMovimentos(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = null;

            try {
                date = inputFormat.parse(movimentoObjs.get(position).getData());
                String dataFormatada = null;
                if (date != null) {
                    dataFormatada = outputFormat.format(date);
                }
                ((ViewHolderMovimentos) holder).tvData.setText(dataFormatada);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            ((ViewHolderMovimentos) holder).tvDescricao.setText(movimentoObjs.get(position).getDescricao());

            double valor = Double.parseDouble(movimentoObjs.get(position).getValor());
            ((ViewHolderMovimentos) holder).tvValor.setText(new DecimalFormat("R$ #,##0.00").format(valor));

            String strTipo = movimentoObjs.get(position).getTipo();
            if (strTipo.equals("Saida")) {
                ((ViewHolderMovimentos) holder).imgTipo.setImageResource(android.R.drawable.button_onoff_indicator_off);
            } else {
                ((ViewHolderMovimentos) holder).imgTipo.setImageResource(android.R.drawable.ic_input_add);
            }
            ((ViewHolderMovimentos) holder).btnEditar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // editar o registro. Leva as informações para proxima tela
                    Intent i = new Intent(HomeActivity.this, MovimentoActivity.class);
                    i.putExtra("id", movimentoObjs.get(position).getId());
                    i.putExtra("data", movimentoObjs.get(position).getData());
                    i.putExtra("valor", movimentoObjs.get(position).getValor());
                    i.putExtra("descricao", movimentoObjs.get(position).getDescricao());
                    i.putExtra("tipo", movimentoObjs.get(position).getTipo());
                    startActivity(i);
                }
            });
            ((ViewHolderMovimentos) holder).btnExcluir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //excluir registro
                    AlertDialog.Builder dialog = new AlertDialog.Builder(HomeActivity.this);
                    dialog.setTitle("Comfirmar exclusão?");
                    dialog.setMessage("Deseja excluir a movimentação " + movimentoObjs.get(position).getDescricao() + "?");
                    dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Fechar dialogo SEM excluir
                        }
                    });
                    dialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Fechar dialogo e excluir
                            if (helper.excluirLancamento(movimentoObjs.get(position).id)) {
                                carregarMovimentos();
                                Toast.makeText(HomeActivity.this, "Lançamento excluido!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(HomeActivity.this, "Falha ao excluir", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    dialog.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return movimentoObjs.size();
        }
    }

    public class ViewHolderMovimentos extends RecyclerView.ViewHolder {
        TextView tvData, tvValor, tvDescricao;
        ImageView imgTipo;
        ImageButton btnEditar, btnExcluir;

        public ViewHolderMovimentos(@NonNull View itemView) {
            super(itemView);
            tvData = itemView.findViewById(R.id.tvData);
            tvValor = itemView.findViewById(R.id.tvValor);
            tvDescricao = itemView.findViewById(R.id.tvDescricao);
            imgTipo = itemView.findViewById(R.id.imageTipo);
            btnEditar = itemView.findViewById(R.id.btn_edit);
            btnEditar.setImageResource(android.R.drawable.ic_menu_edit);
            btnExcluir = itemView.findViewById(R.id.btn_delete);
            btnExcluir.setImageResource(android.R.drawable.ic_menu_delete);
        }
    }

    @Override
    protected void onResume() {
        carregarMovimentos();
        super.onResume();

    }
}