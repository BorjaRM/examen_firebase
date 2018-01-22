package design.nxn.login_template_02;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import design.nxn.login_template_02.model.Producto;

public class MisFavoritos extends AppCompatActivity implements View.OnClickListener {
    private DatabaseReference bbddProductos;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private RecyclerView lstLista;
    private ArrayList<Producto> datos = new ArrayList<>();
    private RelativeLayout consola;
    private TextView msj;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_favoritos);

        // Obtenemos una referencia al objeto que maneja la autenticacion en Firebase
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        //App bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Detecta si el usuario hace click en la barra superior
        toolbar.setNavigationOnClickListener(this);

        //RecyclerView
        lstLista = (RecyclerView)findViewById(R.id.listaProductos);

        int numberOfColumns = 2;
        lstLista.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        msj = (TextView) findViewById(R.id.mensajes);
        consola = (RelativeLayout) findViewById(R.id.consola);

        getFavoritos();
    }

    public void onClick(View v) {
        finish();
    }

    public void getFavoritos() {
        // Obtenemos una referencia a la base de datos
        bbddProductos = FirebaseDatabase.getInstance().getReference(getString(R.string.nodo_favoritos));

        Query q = bbddProductos.orderByKey().equalTo(firebaseUser.getUid());

        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    consola.setVisibility(View.GONE);
                    lstLista.setVisibility(View.VISIBLE);
                    final AdaptadorProductos adaptador;

                    for(DataSnapshot d : dataSnapshot.getChildren()){
                        /* Pretendia obtener el id de cada producto para en la segunda query obtener la info de cada uno pero
                        no se porque me devuelve todos los resultados juntos asi que no puedo utilizarlo como queria
                         */
                        String clave = d.getValue().toString();

                        Toast.makeText(MisFavoritos.this, clave.toString(), Toast.LENGTH_LONG).show();

                        bbddProductos = FirebaseDatabase.getInstance().getReference().child(getString(R.string.nodo_productos));

                        Query q2 = bbddProductos.orderByKey().equalTo(clave);

                        q2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot d : dataSnapshot.getChildren()){
                                    Producto p = d.getValue(Producto.class);
                                    datos.add(p);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }
                    adaptador = new AdaptadorProductos(datos);
                    adaptador.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(getApplicationContext(),VerProducto.class);
                            i.putExtra("idProducto",datos.get(lstLista.getChildAdapterPosition(v)).getId().toString());
                            //i.putExtra("clasePrevia",MisProductos.this.getClass().getSimpleName());
                            startActivity(i);
                        }
                    });
                    lstLista.setAdapter(adaptador);
                    lstLista.setItemAnimator(new DefaultItemAnimator());
                } else {
                    consola.setVisibility(View.VISIBLE);
                    msj.setText("No hay productos para mostrar");
                    lstLista.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
