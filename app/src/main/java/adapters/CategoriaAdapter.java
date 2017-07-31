package adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import dao.FirebaseFacade;
import dominio.Categoria;
import firzen.tcc.CadastroCategoriaActivity;
import firzen.tcc.ObjetoListActivity;
import firzen.tcc.R;

/**
 * Created by Firzen on 22/07/2017.
 */

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder> {

    private List<Categoria> mList;
    private Context mContext;
    private DatabaseReference mDatabase;
    public CategoriaAdapter(Context context, List<Categoria> list){
        this.mList = list;
        this.mContext = context;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }


    @Override
    public CategoriaViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.categoria_layout, viewGroup, false);
        return new CategoriaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CategoriaViewHolder viewHolder, int position) {
        Categoria categoria = mList.get(position);
        viewHolder.nome.setText(categoria.getDescricao());
        viewHolder.numItems.setText("Items na coleção: "+categoria.getCount());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class CategoriaViewHolder  extends RecyclerView.ViewHolder{

        protected TextView nome;
        protected TextView numItems;
        protected ImageButton imageButtonMenu;


        public CategoriaViewHolder(View itemView) {
            super(itemView);
            nome = (TextView) itemView.findViewById(R.id.nome);
            numItems = (TextView) itemView.findViewById(R.id.numItems);
            imageButtonMenu = (ImageButton) itemView.findViewById(R.id.menu);
            CardView cardView = (CardView) itemView.findViewById(R.id.card_view);
            cardView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Categoria item = mList.get(getAdapterPosition());
                    Intent intent = new Intent(context, ObjetoListActivity.class);
                    intent.putExtra("categoria", item);
                    context.startActivity(intent);
                }
            });

            imageButtonMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopupMenu(imageButtonMenu,getAdapterPosition());
                }
            });
        }
    }

    private void showPopupMenu(View view,int position) {
        // inflate menu
        PopupMenu popup = new PopupMenu(view.getContext(),view );
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_categoria, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(position));
        popup.show();
    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private int position;
        public MyMenuItemClickListener(int positon) {
            this.position=positon;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {

                case R.id.editar:
                    Intent intent = new Intent(mContext, CadastroCategoriaActivity.class);
                    intent.putExtra("categoria", mList.get(position));
                    mContext.startActivity(intent);
                    break;
                case R.id.remover:
                    new AlertDialog.Builder(mContext)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Excluir Item")
                            .setMessage("Tem certeza que deseja excluir esse item?")
                            .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseFacade.getInstance().removerCategoria(mList.get(position));
                                    Toast.makeText(mContext, "Categoria removida com sucesso", Toast.LENGTH_SHORT);
                                    mList.clear();
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
            return false;
        }
    }
}
