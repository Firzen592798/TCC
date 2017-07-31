package firzen.tcc;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import dominio.Usuario;

/**
 * Created by Firzen on 02/07/2017.
 */

public class GenericActivity extends AppCompatActivity {
    protected DatabaseReference mDatabase;
    protected Firebase firebase;
    protected DrawerLayout mDrawerLayout;
    protected ActionBarDrawerToggle mDrawerToggle;
    protected ListView mDrawerList;
    protected TextView nome;
    protected ImageView foto;
    protected static Usuario usuarioLogado;
    private Toolbar myToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void carregarExtras(){
        myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        carregarNavigator();
    }

    public void carregarNavigator(){
        String[] lista = {"Minhas Coleções", "Nova Coleção", "Alterar Nome/Foto", "Sair"};
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,   /* nav drawer icon to replace 'Up' caret */
                R.string.app_name,  /* "open drawer" description */
                R.string.app_name /* "close drawer" description */
        ) {

        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, lista));

        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup)inflater.inflate(R.layout.drawer_header, mDrawerList, false);
        mDrawerList.addHeaderView(header, null, false);
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        nome = (TextView)header.findViewById(R.id.nome);
        foto = (ImageView)header.findViewById(R.id.foto);
        carregarDadosUsuario();
        mDrawerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ColecoesApplication.getContext(), AlterarDadosActivity.class);
                mDrawerLayout.closeDrawer(mDrawerList);
                startActivity(intent);
            }
        });
        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ColecoesApplication.getContext(), AlterarDadosActivity.class);
                mDrawerLayout.closeDrawer(mDrawerList);
                startActivity(intent);
            }
        });
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = null;
            Toast.makeText(ColecoesApplication.getContext(), "Posicao - "+position, Toast.LENGTH_SHORT).show();
            switch(position) {
                case 1:
                    intent = new Intent(ColecoesApplication.getContext(), CategoriaListActivity.class);
                    break;
                case 2:
                    intent = new Intent(ColecoesApplication.getContext(), CadastroCategoriaActivity.class);
                    break;
                case 3:
                    intent = new Intent(ColecoesApplication.getContext(), AlterarDadosActivity.class);
                    break;
                case 4:
                    intent = new Intent(ColecoesApplication.getContext(), LoginActivity.class);
                    FirebaseAuth.getInstance().signOut();

            }
            mDrawerLayout.closeDrawer(mDrawerList);
            startActivity(intent);
            if(position == 4)
                finish();
        }
    }

    private void carregarDadosUsuario(){
        if(usuarioLogado == null) {
            Query query = mDatabase.child("usuario/" + FirebaseAuth.getInstance().getCurrentUser().getUid());
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    usuarioLogado = dataSnapshot.getValue(Usuario.class);
                    nome.setText(usuarioLogado.getNome());
                    if (usuarioLogado.getFoto() != null && usuarioLogado.getFoto() != "") {
                        byte[] imageAsBytes = Base64.decode(usuarioLogado.getFoto(), Base64.DEFAULT);
                        foto.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
                        foto.setBackground(null);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else{
            nome.setText(usuarioLogado.getNome());
            if (usuarioLogado.getFoto() != null && usuarioLogado.getFoto() != "") {
                byte[] imageAsBytes = Base64.decode(usuarioLogado.getFoto(), Base64.DEFAULT);
                foto.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
                foto.setBackground(null);
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        Toast.makeText(ColecoesApplication.getContext(), menuItem.getItemId(), Toast.LENGTH_SHORT);
        if (mDrawerToggle.onOptionsItemSelected(menuItem)) {
            return true;
        }
        return (super.onOptionsItemSelected(menuItem));
    }


    public Bitmap crop(Bitmap original) {
        int targetWidth = 120;
        int targetHeight = 180;
        int width = original.getWidth();
        int height = original.getHeight();

        float widthScale = (float) targetWidth / (float) width;
        float heightScale = (float) targetHeight / (float) height;
        float scaledWidth;
        float scaledHeight;

        int startY = 0;
        int startX = 0;

        if (widthScale > heightScale) {
            scaledWidth = targetWidth;
            scaledHeight = height * widthScale;
            startY = (int) ((scaledHeight - targetHeight) / 2);
        } else {
            scaledHeight = targetHeight;
            scaledWidth = width * heightScale;
            //crop width by..
            startX = (int) ((scaledWidth - targetWidth) / 2);
        }

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(original, (int) scaledWidth, (int) scaledHeight, true);

        Bitmap resizedBitmap = Bitmap.createBitmap(scaledBitmap, startX, startY, targetWidth, targetHeight);
        return resizedBitmap;
    }
}
