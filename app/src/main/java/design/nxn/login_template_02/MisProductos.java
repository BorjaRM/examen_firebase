package design.nxn.login_template_02;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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

import design.nxn.login_template_02.model.Producto;

public class MisProductos extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser firebaseUser;
    private DatabaseReference bbddProductos;
    private RecyclerView lstLista;
    private Spinner sp_categorias;
    private TextView msj;
    private String categoriaSeleccionada;
    private ArrayList<Producto> datos = new ArrayList<>();
    private ArrayList<Producto> datosFiltradosPorCategoria = new ArrayList<>();
    private RelativeLayout consola;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_productos);

        // Obtenemos una referencia a la base de datos
        bbddProductos = FirebaseDatabase.getInstance().getReference() //getReference nos proporciona una referencia al nodo raiz
                .child(getString(R.string.nodo_productos)); // bajamos al nodo productos

        // Obtenemos una referencia al objeto que maneja la autenticacion en Firebase
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        //App bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Detecta si el usuario hace click en la barra superior
        toolbar.setNavigationOnClickListener(this);

        //Spinner categorias
        sp_categorias = (Spinner) findViewById(R.id.sp_categorias);
        sp_categorias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoriaSeleccionada = parent.getItemAtPosition(position).toString();
                getProductos();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //RecyclerView
        lstLista = (RecyclerView)findViewById(R.id.listaProductos);

        int numberOfColumns = 2;
        lstLista.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        msj = (TextView) findViewById(R.id.mensajes);
        consola = (RelativeLayout) findViewById(R.id.consola);

    }

    @Override
    public void onClick(View v) {
        finish();
    }

    public void getProductos() {
        if(categoriaSeleccionada.equalsIgnoreCase(getString(R.string.array_categorias_titulo))) {
            Toast.makeText(this, "sin filtrar", Toast.LENGTH_SHORT).show();
            getAllProductsFromUser();
        } else {
            Toast.makeText(this, "Filtrando por categoria: " + categoriaSeleccionada, Toast.LENGTH_SHORT).show();
            filtraPorCategoria();
        }
    }

    public void getAllProductsFromUser() {
        // Buscamos los productos cuyo userRef coincide con el id del usuario
        Query q = bbddProductos.orderByChild(getString(R.string.campo_ref_usuario)).equalTo(firebaseUser.getUid());

        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    consola.setVisibility(View.GONE);
                    lstLista.setVisibility(View.VISIBLE);
                    final AdaptadorProductos adaptador;

                    for(DataSnapshot d : dataSnapshot.getChildren()){
                        Producto p = d.getValue(Producto.class);
                        datos.add(p);
                    }
                    adaptador = new AdaptadorProductos(datos);
                    adaptador.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(getApplicationContext(),VerProducto.class);
                            i.putExtra("idProducto",datos.get(lstLista.getChildAdapterPosition(v)).getId().toString());
                            i.putExtra("clasePrevia",MisProductos.this.getClass().getSimpleName());
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

    public void filtraPorCategoria() {
        /* Como ya he filtrado por usuarioRef ahora filtro el array que contiene los datos y me quedo solo con aquellos
        productos que sean de la categoria seleccionada
        Al hacerlo de esta manera los datos no se actualizaran en tiempo real, solo cuando se inicia la Activity
         */
        datosFiltradosPorCategoria.clear();
        for(Producto p : datos){
            if (p.getCategoria().equalsIgnoreCase(categoriaSeleccionada)){
                datosFiltradosPorCategoria.add(p);
            }
        }

        if(datosFiltradosPorCategoria.size() != 0) {
            consola.setVisibility(View.GONE);
            lstLista.setVisibility(View.VISIBLE);
            AdaptadorProductos adaptador = new AdaptadorProductos(datosFiltradosPorCategoria);
            adaptador.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(),VerProducto.class);
                    i.putExtra("idProducto",datosFiltradosPorCategoria.get(lstLista.getChildAdapterPosition(v)).getId().toString());
                    i.putExtra("clasePrevia",MisProductos.this.getClass().getSimpleName());
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

}
