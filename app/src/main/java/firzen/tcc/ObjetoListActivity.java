package firzen.tcc;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

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
    Categoria categoria;
    List<Objeto> objetos = new ArrayList<Objeto>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_objeto_list);

        categoria = (Categoria)getIntent().getExtras().get("categoria");
        setTitle("Lista "+categoria.getDescricao()+"s");
        FloatingActionButton novoButton = (FloatingActionButton) findViewById(R.id.novo);
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

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_categoria, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.novo:
                Intent intent = new Intent (ObjetoListActivity.this, ObjetoCadastroActivity.class);
                intent.putExtra("categoria", categoria);
                startActivityForResult(intent, 1);
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_objeto_click, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();

        final Objeto objetoSelecionado = objetos.get(info.position);
        //posicaoSelecionada = info.position;
        switch (item.getItemId()) {

            case R.id.editar:
                    Intent intent = new Intent (ObjetoListActivity.this, ObjetoCadastroActivity.class);
                    objetoSelecionado.setCategoria(categoria);
                    intent.putExtra("categoria", categoria);
                    intent.putExtra("objeto", objetoSelecionado);
                    startActivity (intent);
                break;
            case R.id.remover:
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Excluir Item")
                        .setMessage("Tem certeza que deseja excluir esse item?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) { 
                                mDatabase.child("usuario/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/categoria/"+categoria.getId()+"/objetos/"+objetoSelecionado.getId()).removeValue();
                                categoria.setCount(categoria.getCount()-1);
                                mDatabase.child("usuario/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/categoria").child(categoria.getId()).child("count").setValue(categoria.getCount());
                                Toast.makeText(ObjetoListActivity.this, "Objeto removido com sucesso", Toast.LENGTH_SHORT).show();
                                objetos.clear();
                            }

                        })
                        .setNegativeButton("Não", null)
                        .show();

                break;
            case R.id.cancelar:

                break;

            default:
                break;
        }
        return super.onContextItemSelected(item);
    }*/

}
