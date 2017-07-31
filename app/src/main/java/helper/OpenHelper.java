package helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Firzen on 20/06/2017.
 */

public class OpenHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "banco";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_CREATE_CATEGORIA = "CREATE TABLE categoria ("+
            "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+
            "descricao TEXT NOT NULL);";
    private static final String TABLE_CREATE_OBJETO = "CREATE TABLE objeto ("+
            "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+
            "descricao TEXT NOT NULL, "+
            "num_items INTEGER NULL, "+
            "id_categoria TEXT NULL," +
            "FOREIGN KEY(id_categoria) REFERENCES categoria(id));";
    private static final String TABLE_CREATE_USUARIO= "CREATE TABLE usuario ("+
            "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+
            "login TEXT NOT NULL, "+
            "senha TEXT NOT NULL, "+
            "nome TEXT NOT NULL);";
    private static final String TABLE_CREATE_USUARIO_OBJETO = "CREATE TABLE usuario_objeto ("+
            "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+
            "id_usuario INTEGER NOT NULL, "+
            "id_objeto INTEGER NOT NULL, "+
            "itens_possuidos INTEGER NULL," +
            "FOREIGN KEY(id_usuario) REFERENCES usuario(id), "+
            "FOREIGN KEY(id_objeto) REFERENCES objeto(id));";


    public OpenHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.execSQL(TABLE_CREATE_CATEGORIA);
        //db.execSQL(TABLE_CREATE_OBJETO);
        //db.execSQL(TABLE_CREATE_USUARIO);
        //db.execSQL(TABLE_CREATE_USUARIO_OBJETO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS objeto");
        //db.execSQL("DROP TABLE IF EXISTS categoria");
        //db.execSQL("DROP TABLE IF EXISTS usuario");
        //db.execSQL("DROP TABLE IF EXISTS usuario_objeto");
        //onCreate(db);
    }
}
