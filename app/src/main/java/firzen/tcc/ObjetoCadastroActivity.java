package firzen.tcc;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import dao.FirebaseFacade;
import dominio.Categoria;
import dominio.Objeto;

import static firzen.tcc.R.id.imageView;

public class ObjetoCadastroActivity extends GenericActivity {
    private Objeto objeto;
    private Categoria categoria;
    private EditText editNome;
    private EditText editNumItems;
    private EditText txtUnidade;
    private TextView txtNumItems;
    private ImageView mImageView;
    private Button btnFoto;
    String imagemBase64;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_PICK_GALLERY = 2;
    static final int CROP_IMAGE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_objeto_cadastro);

        categoria = (Categoria)getIntent().getExtras().get("categoria");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        editNome = (EditText)findViewById(R.id.editObjeto);
        editNumItems= (EditText)findViewById(R.id.editNumItems);
        txtNumItems = (TextView)findViewById(R.id.txtNumItems);
        mImageView = (ImageView) findViewById(imageView);
        if(savedInstanceState != null) {
            Bitmap bitmap = savedInstanceState.getParcelable("image");
            mImageView.setImageBitmap(bitmap);
        }
        carregarExtras();

        editNumItems.setHint("Último item adquirido da sua coleção");
        if(objeto == null)
        objeto = (Objeto)getIntent().getExtras().get("objeto");

        if(objeto != null && objeto.getFoto() != null && objeto.getFoto() != "") {
            byte[] imageAsBytes = Base64.decode(objeto.getFoto(), Base64.DEFAULT);
            mImageView.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
            mImageView.setVisibility(View.VISIBLE);
        }
        if(categoria.isEnumeravel()) {
            editNumItems.setVisibility(View.VISIBLE);
            txtNumItems.setVisibility(View.VISIBLE);
        }else{
            editNumItems.setVisibility(View.GONE);
            txtNumItems.setVisibility(View.GONE);
        }
        if(objeto != null){
            setTitle("Editar "+categoria.getDescricao());
            editNome.setText(objeto.getDescricao());
            editNumItems.setText(String.valueOf(objeto.getNumItems()));
        }else{
            setTitle("Novo(a) "+categoria.getDescricao());
            objeto = new Objeto();
        }
    }

    public void cadastrar(View view){
        boolean valid = true;
        if(!editNome.getText().toString().trim().equals("")) {
            objeto.setDescricao(editNome.getText().toString());
        }else{
            editNome.setError("Campo obrigatório");
            editNome.setFocusable(true);
            editNome.requestFocus();
            valid = false;
        }
        if(categoria.isEnumeravel()){
            if(editNumItems.getText().toString().equals("") || (Integer.valueOf(editNumItems.getText().toString()) > 100 && Integer.valueOf(editNumItems.getText().toString()) < 0)){
                editNumItems.setError("O valor precisa ser entre 0 e 100");
                editNumItems.setFocusable(true);
                editNumItems.requestFocus();
                valid = false;
            }else{
                objeto.setNumItems(Integer.valueOf(Integer.valueOf(editNumItems.getText().toString())));
            }
        }else{
            objeto.setNumItems(0);
        }
        if(valid) {
            FirebaseFacade.getInstance().salvarObjeto(objeto, categoria);
            Toast.makeText(ObjetoCadastroActivity.this, "Objeto salvo com sucesso", Toast.LENGTH_SHORT).show();
            Intent devolve = new Intent();
            devolve.putExtra("categoria", categoria);
            setResult(RESULT_OK, devolve);
            finish();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void escolherGaleria(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_PICK_GALLERY);
    }

    public void tirarFoto(View view){
        String[] arr = {"Tirar uma Foto", "Escolher da Galeria", "Fechar"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ObjetoCadastroActivity.this);
        builder.setTitle("Escolha um método")
                .setItems(arr, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which){
                            case 0:
                                dispatchTakePictureIntent();
                                break;
                            case 1:
                                escolherGaleria();
                                break;
                            default:
                                break;
                        }
                    }
                });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap imageBitmap = null;
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            //imageBitmap = Bitmap.createScaledBitmap(imageBitmap, 80, 120, false);
        }else if(requestCode == REQUEST_PICK_GALLERY  && resultCode == RESULT_OK){
            Uri selectedImage = data.getData();
            InputStream imageStream = null;
            try {
                imageStream = getContentResolver().openInputStream(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            imageBitmap = BitmapFactory.decodeStream(imageStream);
            
        }
        if(requestCode == REQUEST_PICK_GALLERY || requestCode == REQUEST_IMAGE_CAPTURE) {
            imageBitmap = crop(imageBitmap);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            byte[] imagem = baos.toByteArray();
            imagemBase64 = Base64.encodeToString(imagem, Base64.DEFAULT);
            objeto.setFoto(imagemBase64);
            mImageView.setImageBitmap(imageBitmap);
            mImageView.setBackground(null);
            mImageView.setVisibility(View.VISIBLE);
        }
    }
}
