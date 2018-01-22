package design.nxn.login_template_02;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import design.nxn.login_template_02.model.Usuario;

public class UsuariosActivity extends AppCompatActivity implements View.OnClickListener {
    private DatabaseReference bbddUsuarios;
    private Toolbar toolbar;
    private RecyclerView listaUsuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuarios);

        //App bar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Detecta si el usuario hace click en el icono "flecha"
        toolbar.setNavigationOnClickListener(this);

        //RecyclerView
        listaUsuarios = (RecyclerView)findViewById(R.id.listaUsuarios);

        // Obtenemos la referencia a la base de datos a la que estamos conectados
        bbddUsuarios = FirebaseDatabase.getInstance().getReference(getString(R.string.nodo_usuarios));

        bbddUsuarios.addValueEventListener(new ValueEventListener() {
            // onDataChange se ejecutara cada vez que haya un cambio en el nodo que estamos escuchando
            // dataSnapshot es el conjunto de objetos que hay en ese nodo
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                AdaptadorUsuarios adaptador;
                ArrayList<Usuario> usuarios = new ArrayList<>();

                for (DataSnapshot d : dataSnapshot.getChildren()) { //recorre todos los usuarios del nodo usuarios
                    Usuario unUsuario = d.getValue(Usuario.class); //obtenemos un usuario
                    usuarios.add(unUsuario);
                }
                adaptador = new AdaptadorUsuarios(usuarios);
                listaUsuarios.setAdapter(adaptador);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

        listaUsuarios.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        listaUsuarios.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        listaUsuarios.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onClick(View v) {
        finish();
    }
}
