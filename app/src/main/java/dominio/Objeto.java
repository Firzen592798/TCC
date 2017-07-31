package dominio;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

/**
 * Created by Firzen on 17/06/2017.
 */

public class Objeto implements Serializable {
    private String id;
    @Exclude
    private Categoria categoria;
    private String descricao;
    private Integer numItems;//Se for 0 ou null não é enumerável
    private String foto;
    private String itemsFaltantes = "";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getNumItems() {
        return numItems;
    }

    public void setNumItems(Integer numItems) {
        this.numItems = numItems;
    }

    public String getItemsFaltantes() {
        return itemsFaltantes;
    }

    public void setItemsFaltantes(String itemsFaltantes) {
        this.itemsFaltantes = itemsFaltantes;
    }
    public void addItemFaltante(String item){
        if(itemsFaltantes.isEmpty()){
            itemsFaltantes+=item;
        }else{
            itemsFaltantes+=","+item;
        }
    }
    public void removeItemFaltante(String item){
        String arr[] = itemsFaltantes.split(",");
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for(String s : arr) {
            if(!s.equals(item)) {
                if(!first){
                    builder.append(",");
                }
                builder.append(s);
                first = false;
            }
        }
        itemsFaltantes = builder.toString();
    }

    public void removeAllAfter(Integer tamanho){
        String arr[] = itemsFaltantes.split(",");
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for(String s : arr) {
            if(!s.equals("")) {
                if (Integer.valueOf(s) <= tamanho) {
                    if (!first) {
                        builder.append(",");
                    } else {
                        builder.append(s);
                        first = false;
                    }
                }
            }
        }
        itemsFaltantes = builder.toString();
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getFoto(){
        return foto;
    }
}
