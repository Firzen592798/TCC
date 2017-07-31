package firebasedao;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Firzen on 25/06/2017.
 */

public class UsuarioFB {
    private DatabaseReference mDatabase;
    public UsuarioFB(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

}
