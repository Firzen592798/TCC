package adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import dao.FirebaseFacade;
import dominio.Categoria;
import dominio.Objeto;
import firzen.tcc.ObjetoCadastroActivity;
import firzen.tcc.R;
import firzen.tcc.SubListaObjetoActivity;

/**
 * Created by Firzen on 27/06/2017.
 */

public class ObjetoAdapter extends RecyclerView.Adapter<ObjetoAdapter.ObjetoViewHolder>{
    private final Context mContext;
    private DatabaseReference mDatabase;

    private Categoria categoria;
    List<Objeto> mList = new ArrayList<Objeto>();

    public ObjetoAdapter(Context context, List<Objeto> mList, Categoria categoria, DatabaseReference mDatabase) {
        this.mContext = context;
        this.mList = mList;
        this.mDatabase = mDatabase;
        this.categoria = categoria;
    }

    /*@Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.objeto_layout, parent, false);
        TextView nome = (TextView) rowView.findViewById(nome);
        ImageButton button = (ImageButton)rowView.findViewById(R.id.imageButton);
        nome.setText(mList.get(position).getDescricao());

        final TextView items = (TextView) rowView.findViewById(R.id.textViewNumItems);
        String lol = "";
        mList.get(position).setCategoria(categoria);
        if(mList.get(position).getCategoria() != null && mList.get(position).getCategoria().isEnumeravel()){
            lol = String.valueOf(mList.get(position).getNumItems());
            items.setText(lol);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mList.get(position).setNumItems(mList.get(position).getNumItems()+1);
                    int soma = mList.get(position).getNumItems()+1;
                    items.setText(String.valueOf(soma));
                    mDatabase.child("usuario/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/categoria/" + categoria.getId() + "/mList").child(mList.get(position).getId()).setValue(mList.get(getAdapterPosition()));

                }
            });
        }else{
            button.setVisibility(View.GONE);
            items.setVisibility(View.GONE);
        }
        return rowView;
    }*/

    @Override
    public ObjetoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.objeto_layout, parent, false);
        return new ObjetoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ObjetoViewHolder viewHolder, final int position) {
        viewHolder.nome.setText(mList.get(position).getDescricao());

        mList.get(position).setCategoria(categoria);
        if(mList.get(position).getCategoria() != null && mList.get(position).getCategoria().isEnumeravel()){
            String lol = String.valueOf(mList.get(position).getNumItems());
            viewHolder.numItems.setText(lol);
            viewHolder.buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mList.get(position).setNumItems(mList.get(position).getNumItems()+1);
                    int soma = mList.get(position).getNumItems();
                    viewHolder.numItems.setText("Último Adquirido: "+soma);
                    mDatabase.child("usuario/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/categoria/" + categoria.getId() + "/mList").child(mList.get(position).getId()).setValue(mList.get(position));
                    mDatabase.child("objetos/" +  categoria.getId() + "/" +  mList.get(position).getId() + "/numItems").setValue(mList.get(position).getNumItems());
                }
            });
        }else{
            viewHolder.buttonAdd.setVisibility(View.GONE);
            viewHolder.numItems.setVisibility(View.GONE);
        }


        Objeto objeto = mList.get(position);
        viewHolder.nome.setText(objeto.getDescricao());
        if(objeto.getFoto() != null && objeto.getFoto() != "") {
            byte[] imageAsBytes = Base64.decode(objeto.getFoto(), Base64.DEFAULT);
            viewHolder.imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
        }else{
            viewHolder.imageView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.padrao));
        }
        viewHolder.numItems.setText("Último Adquirido: "+objeto.getNumItems());
    }

    @Override
    public int getItemCount() {
        if(mList == null)
            return 0;
        else
            return mList.size();
    }

    public class ObjetoViewHolder  extends RecyclerView.ViewHolder{
        protected TextView nome;
        protected TextView numItems;
        protected ImageButton buttonAdd;
        protected ImageView imageView;
        protected ImageButton maisButton;
        protected CardView cardView;
        public ObjetoViewHolder(View itemView) {
            super(itemView);
            buttonAdd = (ImageButton) itemView.findViewById(R.id.buttonAdd);
            maisButton = (ImageButton) itemView.findViewById(R.id.mais);
            nome = (TextView) itemView.findViewById(R.id.nome);
            numItems = (TextView) itemView.findViewById(R.id.textViewNumItems);
            CardView cardView = (CardView) itemView.findViewById(R.id.card_view);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            maisButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopupMenu(v, getAdapterPosition());
                }
            });
            buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mList.get(getAdapterPosition()).setNumItems(mList.get(getAdapterPosition()).getNumItems()+1);
                    Log.w("Items", String.valueOf(mList.get(getAdapterPosition()).getNumItems()));
                    numItems.setText("Items na coleção: "+String.valueOf(mList.get(getAdapterPosition()).getNumItems()));
                    FirebaseFacade.getInstance().salvarObjeto(mList.get(getAdapterPosition()), mList.get(getAdapterPosition()).getCategoria());
                    //mDatabase.child("usuario/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/categoria/" + categoria.getId() + "/mList").child(mList.get(getAdapterPosition()).getId()).setValue(mList.get(getAdapterPosition()));
                }
            });
            cardView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Objeto objeto = mList.get(getAdapterPosition());
                    if(objeto.getCategoria().isEnumeravel()) {
                        objeto.setCategoria(categoria);
                        Intent intent = new Intent(mContext, SubListaObjetoActivity.class);
                        intent.putExtra("objeto", objeto);
                        mContext.startActivity(intent);
                    }

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
                    Intent intent = new Intent (mContext, ObjetoCadastroActivity.class);
                    Objeto objetoSelecionado = mList.get(position);
                    objetoSelecionado.setCategoria(categoria);
                    intent.putExtra("categoria", categoria);
                    intent.putExtra("objeto", objetoSelecionado);
                    mContext.startActivity (intent);
                    break;
                case R.id.remover:
                    new AlertDialog.Builder(mContext)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Excluir Item")
                            .setMessage("Tem certeza que deseja excluir esse item?")
                            .setPositiveButton("Sim", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseFacade.getInstance().removerObjeto(mList.get(position), mList.get(position).getCategoria());
                                    Toast.makeText(mContext, "Objeto removido com sucesso", Toast.LENGTH_SHORT).show();
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

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public List<Objeto> getmList() {
        return mList;
    }

    public void setmList(List<Objeto> mList) {
        this.mList = mList;
    }

}
