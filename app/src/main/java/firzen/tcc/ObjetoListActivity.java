package firzen.tcc;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import adapters.ObjetoAdapter;
import dominio.Categoria;
import dominio.Objeto;

public class ObjetoListActivity extends GenericActivity {
    final Objeto objeto= new Objeto();
    private DatabaseReference mDatabase;
    private Categoria categoria;
    private TextView listEmpty;
    List<Objeto> objetos = new ArrayList<Objeto>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_objeto_list);

        categoria = (Categoria)getIntent().getExtras().get("categoria");
        setTitle("Lista de "+categoria.getDescricao()+"s");
        FloatingActionButton novoButton = (FloatingActionButton) findViewById(R.id.novo);
        listEmpty = (TextView) findViewById(R.id.list_empty);
        novoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (ObjetoListActivity.this, ObjetoCadastroActivity.class);
                intent.putExtra("categoria", categoria);
                startActivityForResult(intent, 1);
            }
        });
        carregarExtras();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final ObjetoAdapter adapter =carregarListView();
        Query query = mDatabase.child("objetos/"+categoria.getId());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                objetos.clear();
                for (DataSnapshot child: dataSnapshot.getChildren()){
                    final Objeto objeto = child.getValue(Objeto.class);
                    objeto.setId(child.getKey());
                    objetos.add(objeto);
                }
                adapter.notifyDataSetChanged();
                if(!objetos.isEmpty()){
                    listEmpty.setVisibility(View.GONE);
                }else{
                    listEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private ObjetoAdapter carregarListView() {
        RecyclerView mRecyclerView = (RecyclerView)findViewById(R.id.listObjeto);
        registerForContextMenu(mRecyclerView);
        final ObjetoAdapter adapter = new ObjetoAdapter(this, objetos, categoria, mDatabase);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        return adapter;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(data != null && data.hasExtra("categoria"))
                categoria = (Categoria)data.getExtras().get("categoria");
        }
    }
}
