package com.example.gerenciadordegasto;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class DataBaseHelper {
    SQLiteDatabase database;
    Context context;

    public DataBaseHelper(Context context) {
        this.context = context;
    }

    public void criarBanco() {
        database = context.openOrCreateDatabase("banco_de_dados", context.MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS usuarios(id INTEGER PRIMARY KEY,"
                + "nome VARCHAR," +
                "usuario VARCHAR," +
                "senha VARCHAR" + ")");
        database.execSQL("CREATE TABLE IF NOT EXISTS movimentos(id INTEGER PRIMARY KEY,"
                + "data VARCHAR," +
                "valor VARCHAR," +
                "descricao VARCHAR," +
                "tipo VARCHAR)");
        database.close();
    }

    public int consultarUsuario(String strUsuario, String strSenha) {
        int usuarios = 0;
        database = context.openOrCreateDatabase("banco_de_dados", context.MODE_PRIVATE, null);
        String selectQuery = "SELECT id, nome, usuario, senha FROM usuarios WHERE usuario=? AND senha=?";

        Cursor cursor = database.rawQuery(selectQuery, new String[]{strUsuario, strSenha});
        if (cursor.moveToFirst()) {
            usuarios = cursor.getCount();
        }
        cursor.close();
        database.close();
        return usuarios;
    }

    public boolean inserirUsuario(String strName, String strUsuario, String strSenha) {
        boolean operacao = false;
        try {
            database = context.openOrCreateDatabase("banco_de_dados", context.MODE_PRIVATE, null);
            database.execSQL("INSERT INTO usuarios(nome,usuario,senha) VALUES ('" +
                    strName + "','" + strUsuario + "','" + strSenha + "')");
            operacao = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return operacao;
    }

    public boolean inserirMovimentacao(String dataFomatada, String strValor, String strDescricao, String strTipo) {
        boolean operacao = false;
        try {
            database = context.openOrCreateDatabase("banco_de_dados", context.MODE_PRIVATE, null);
            database.execSQL("INSERT INTO movimentos(data,valor,descricao,tipo) VALUES ('" +
                    dataFomatada + "','" + strValor + "','" + strDescricao + "','" + strTipo + "')");
            operacao = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return operacao;
    }

    public ArrayList<MovimentoObj> carregarMovimentos() {
        ArrayList<MovimentoObj> lista = new ArrayList<MovimentoObj>();
        try {
            database = context.openOrCreateDatabase("banco_de_dados", context.MODE_PRIVATE, null);
            String selectQuery = "SELECT id, data, valor, descricao, tipo FROM movimentos ORDER BY data DESC";
            Cursor cursor = database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(0);
                    String data = cursor.getString(1);
                    String valor = cursor.getString(2);
                    String descricao = cursor.getString(3);
                    String tipo = cursor.getString(4);
                    MovimentoObj movimentoObj = new MovimentoObj(id, valor, data, descricao, tipo);
                    lista.add(movimentoObj);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public String total(String strTipo) {
        String total = "0";
        try {
            database = context.openOrCreateDatabase("banco_de_dados", context.MODE_PRIVATE, null);
            String selectQuery = "SELECT  sum(valor) FROM movimentos WHERE  tipo=?";
            Cursor cursor = database.rawQuery(selectQuery, new String[]{strTipo});
            cursor.moveToFirst();
            if (cursor.getString(0) != null) {
                total = cursor.getString(0);
            }
            database.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }


    public boolean excluirLancamento(int id) {
        boolean operacao = false;
        try {
            database = context.openOrCreateDatabase("banco_de_dados", context.MODE_PRIVATE, null);
            database.execSQL("DELETE FROM movimentos WHERE ID=" + id);
            database.close();
            operacao = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return operacao;
    }

    public boolean atualizarMovimentacao(String dataFomatada, String strValor, String strDescricao, String strTipo, int id) {
        boolean operacao = false;
        try {
            database = context.openOrCreateDatabase("banco_de_dados", context.MODE_PRIVATE, null);
            database.execSQL("UPDATE movimentos SET data=?,valor=?, descricao=?,tipo=? WHERE id=?",
                    new String[]{dataFomatada, strValor, strDescricao, strTipo, String.valueOf(id)}
            );
            database.close();
            operacao = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return operacao;
    }
}
