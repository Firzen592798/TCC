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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import dao.FirebaseFacade;

import static firzen.tcc.ObjetoCadastroActivity.CROP_IMAGE;
import static firzen.tcc.ObjetoCadastroActivity.REQUEST_IMAGE_CAPTURE;
import static firzen.tcc.ObjetoCadastroActivity.REQUEST_PICK_GALLERY;

public class AlterarDadosActivity extends GenericActivity {
    private EditText editNome;
    private ImageView imageView;
    private String foto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar_dados);
        setTitle("Alterar Dados Pessoais");
        carregarExtras();
        editNome = (EditText)findViewById(R.id.nomeExibicao);
        imageView = (ImageView)findViewById(R.id.imageView);

        editNome.setText(usuarioLogado.getNome());
        if (usuarioLogado.getFoto() != null && usuarioLogado.getFoto() != "") {
            byte[] imageAsBytes = Base64.decode(usuarioLogado.getFoto(), Base64.DEFAULT);
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
            imageView.setBackground(null);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(AlterarDadosActivity.this);
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
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageBitmap = crop(imageBitmap);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imagem = baos.toByteArray();
            foto = Base64.encodeToString(imagem, Base64.DEFAULT);
            imageView.setImageBitmap(imageBitmap);
        }else if(requestCode == REQUEST_PICK_GALLERY  && resultCode == RESULT_OK){
            Uri selectedImage = data.getData();
            InputStream imageStream = null;
            try {
                imageStream = getContentResolver().openInputStream(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
            bitmap = crop(bitmap);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imagem = baos.toByteArray();
            foto = Base64.encodeToString(imagem, Base64.DEFAULT);
            imageView.setImageBitmap(bitmap);
        }else if(requestCode == CROP_IMAGE  && resultCode == RESULT_OK){

        }
    }

    public void cadastrar(View view){
        String nome = editNome.getText().toString();
        if(foto != null && foto != "")
            usuarioLogado.setFoto(foto);
        usuarioLogado.setNome(nome);
        FirebaseFacade.getInstance().atualizarDadosPessoais(usuarioLogado.getNome(), usuarioLogado.getFoto());
        Toast.makeText(this, "Dados Atualizados com Sucesso", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(AlterarDadosActivity.this, CategoriaListActivity.class);
        startActivity(intent);
        finish();
    }
}
