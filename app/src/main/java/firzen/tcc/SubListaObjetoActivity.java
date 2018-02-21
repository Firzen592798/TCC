package firzen.tcc;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import java.util.Arrays;
import java.util.List;

import dao.FirebaseFacade;
import dominio.Objeto;

public class SubListaObjetoActivity extends GenericActivity {
    private Objeto objeto;
    //List<Integer> itemsFaltantes = new ArrayList<Integer>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_lista_objeto);
        objeto = (Objeto)getIntent().getExtras().get("objeto");
        setTitle(objeto.getDescricao());
        /*mDatabase = FirebaseDatabase.getInstance().getReference();
        Query query = mDatabase.child("usuario/1/categoria");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //categorias.cleacr();
                for (DataSnapshot child: dataSnapshot.getChildren()){
                    Categoria categoria = child.getValue(Categoria.class);
                    categoria.setId(child.getKey());
                    //categorias.add(categoria);
                }

                //adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
        LinearLayout ll = (LinearLayout)findViewById(R.id.listaCheckbox);
        carregarExtras();
        //add checkboxes
        String[] faltantesArr = objeto.getItemsFaltantes().split(",");
        List<String> list = Arrays.asList(faltantesArr);
        for(int i = 0; i < objeto.getNumItems(); i++) {
            CheckBox cb = new CheckBox(this);
            int ordem = i + 1;
            if(!objeto.getCategoria().getUnidade().equals(""))
                cb.setText(objeto.getCategoria().getUnidade() +" "+ ordem);
            else
                cb.setText("Item "+ ordem);
            cb.setId(i+1);
            if(list.contains(String.valueOf(ordem))) {
                cb.setChecked(false);
            }else{
                cb.setChecked(true);
            }
            ll.addView(cb);
            cb.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (((CheckBox)v).isChecked()) {
                        objeto.removeItemFaltante(String.valueOf(v.getId()));
                    }else{
                        objeto.addItemFaltante(String.valueOf(v.getId()));
                    }
                    FirebaseFacade.getInstance().salvarObjeto(objeto, objeto.getCategoria());
                    //mDatabase.child("usuario/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/categoria/" + objeto.getCategoria().getId() + "/objetos").child(objeto.getId()).setValue(objeto);
                }
            });
        }
    }
}
