package firzen.tcc;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import adapters.CategoriaAdapter;
import dominio.Categoria;

public class CategoriaListActivity extends GenericActivity {
    final Categoria categoria = new Categoria();
    RecyclerView mRecyclerView;
    FloatingActionButton novoButton;
    private CategoriaAdapter adapter;
    List<Categoria> categorias = new ArrayList<Categoria>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria_list);
        setTitle("Minhas Coleções");
        carregarExtras();
        carregarListView();
        carregarColecoes();
        novoButton = (FloatingActionButton) findViewById(R.id.novo);
        novoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CategoriaListActivity.this, CadastroCategoriaActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    void carregarColecoes(){
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Query query = mDatabase.child("usuario/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/categoria");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                categorias.clear();
                for (DataSnapshot child: dataSnapshot.getChildren()){
                    Categoria categoria = child.getValue(Categoria.class);
                    categoria.setId(child.getKey());
                    categorias.add(categoria);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @NonNull
    private void carregarListView() {
        mRecyclerView = (RecyclerView)findViewById(R.id.categoriaRecycler);
        mRecyclerView.setHasFixedSize(true);

        adapter = new CategoriaAdapter(this, categorias);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    @Override
    public void onBackPressed() {
        FirebaseAuth.getInstance().signOut();
        super.onBackPressed();
    }
}
