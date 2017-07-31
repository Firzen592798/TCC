package firzen.tcc;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import dao.FirebaseFacade;
import dominio.Categoria;

public class CadastroCategoriaActivity extends GenericActivity {
    private Categoria categoria;
    private EditText editNome;
    private EditText editUnit;
    private TextView unidadeTxt;
    private RadioButton radioButtonNao;
    private RadioButton radioButtonSim;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_categoria);

        if(getIntent().hasExtra("categoria")) {
            categoria = (Categoria) getIntent().getExtras().get("categoria");
            setTitle("Editar Coleção");
        }else{
            setTitle("Nova Coleção");
        }
        editUnit = (EditText)findViewById(R.id.editUnit);
        editNome = (EditText)findViewById(R.id.editCategoria);
        unidadeTxt = (TextView)findViewById(R.id.unidadeTxt);
        radioButtonSim = (RadioButton) findViewById(R.id.radio_sim);
        radioButtonNao = (RadioButton) findViewById(R.id.radio_nao);
        editUnit.setHint("Volume, Capítulo, Edição, Número...");
        carregarExtras();
        if(categoria != null){
            editNome.setText(categoria.getDescricao());
            if(categoria.isEnumeravel()) {
                editUnit.setVisibility(View.VISIBLE);
                editUnit.setText(categoria.getUnidade());
                radioButtonNao.setChecked(false);
                radioButtonSim.setChecked(true);
            }else{
                editUnit.setVisibility(View.GONE);
                radioButtonNao.setChecked(true);
                radioButtonSim.setChecked(false);
            }
        }else{
            categoria = new Categoria();
        }
    }

    public void cadastrar(View view){
        boolean valid = true;
        if(!editNome.getText().toString().trim().equals("")) {
            categoria.setDescricao(editNome.getText().toString());
        }else{
            editNome.setError("Campo obrigatório");
            editNome.setFocusable(true);
            editNome.requestFocus();
            valid = false;
        }
        if(radioButtonSim.isChecked()){
            if (editUnit.getText().toString().trim().equals("")) {
                editUnit.setError("Campo obrigatório");
                valid = false;
            }else{
                categoria.setUnidade(editUnit.getText().toString());
            }
        }
        if(valid) {
            FirebaseFacade.getInstance().salvarCategoria(categoria);
            Toast.makeText(CadastroCategoriaActivity.this, "Gravado com sucesso", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void onRadioButtonClicked(View view){
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_nao:
                if (checked) {
                    radioButtonSim.setChecked(false);
                    categoria.setEnumeravel(false);
                    editUnit.setVisibility(View.GONE);
                    unidadeTxt.setVisibility(View.GONE);
                }
                break;
            case R.id.radio_sim:
                if (checked) {
                    radioButtonNao.setChecked(false);
                    categoria.setEnumeravel(true);
                    editUnit.setVisibility(View.VISIBLE);
                    unidadeTxt.setVisibility(View.VISIBLE);
                }
                break;
        }
    }
}
