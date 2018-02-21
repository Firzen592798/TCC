package dao;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import dominio.Categoria;
import dominio.Objeto;

/**
 * Created by Firzen on 25/07/2017.
 */

public class FirebaseFacade {

    private static FirebaseFacade instance;
    private static DatabaseReference mDatabase;;
    private FirebaseFacade(){

    }
    public static FirebaseFacade getInstance(){
        if(instance == null){
            instance = new FirebaseFacade();
            mDatabase = FirebaseDatabase.getInstance().getReference();
        }
        return instance;
    }

    public void salvarCategoria(Categoria categoria){
        if(categoria.getId() == null) {
            String key = mDatabase.child("usuario/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/categoria").push().getKey();
            categoria.setId(key);
            mDatabase.child("usuario/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/categoria").child(categoria.getId()).setValue(categoria);
        }else{
            mDatabase.child("usuario/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/categoria").child(categoria.getId()).child("descricao").setValue(categoria.getDescricao());
            mDatabase.child("usuario/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/categoria").child(categoria.getId()).child("enumeravel").setValue(categoria.isEnumeravel());
            mDatabase.child("usuario/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/categoria").child(categoria.getId()).child("unidade").setValue(categoria.getUnidade());
        }
    }

    public void salvarObjeto(Objeto objeto, Categoria categoria){
        //if(objeto.getId() == null) {
        String key = mDatabase.child("objetos/"+categoria.getId()).push().getKey();
        objeto.setId(key);
        categoria.setCount(categoria.getCount()+1);
        mDatabase.child("usuario/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/categoria").child(categoria.getId()).child("count").setValue(categoria.getCount());
        /*}else{
            objeto.removeAllAfter(objeto.getNumItems());
        }*/
        mDatabase.child("objetos/"+categoria.getId()+"/"+objeto.getId()).setValue(objeto);
    }

    public void atualizarDadosPessoais(String nome, String foto){
        mDatabase.child("usuario/"+FirebaseAuth.getInstance().getCurrentUser().getUid()).child("foto").setValue(foto);
        mDatabase.child("usuario/"+FirebaseAuth.getInstance().getCurrentUser().getUid()).child("nome").setValue(nome);
    }


    public void removerObjeto(Objeto objeto, Categoria categoria){
        mDatabase.child("objetos/"+categoria.getId()+"/"+objeto.getId()).removeValue();
        categoria.setCount(categoria.getCount()-1);
        mDatabase.child("usuario/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/categoria").child(categoria.getId()).child("count").setValue(categoria.getCount());
    }

    public void removerCategoria(Categoria categoria){
        mDatabase.child("usuario/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/categoria/" + categoria.getId()).removeValue();
    }
}
