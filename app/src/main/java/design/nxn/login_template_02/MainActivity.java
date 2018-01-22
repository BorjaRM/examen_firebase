package design.nxn.login_template_02;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser firebaseUser;
    private DatabaseReference bbddProductos;
    private String categoriaSeleccionada;
    private RecyclerView lstLista;
    private Spinner sp_categorias;
    private TextView msj;
    private RelativeLayout consola;
    private ArrayList<Producto> datos = new ArrayList<>();
    private ArrayList<Producto> datosFiltradosPorCategoria = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtenemos una referencia a la base de datos
        bbddProductos = FirebaseDatabase.getInstance().getReference() //getReference nos proporciona una referencia al nodo raiz
                .child(getString(R.string.nodo_productos)); // bajamos al nodo productos

        // Obtenemos una referencia al objeto que maneja la autenticacion en Firebase
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser == null) {
                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(i);
                } else {
                    rellenaCabeceraPanelLateralConInfoUsuario();
                }
            }
        };

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, NuevoProducto.class);
                startActivity(i);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main_menu_superior, menu);
        return true;
    }

    public void getProductos() {
        if(categoriaSeleccionada.equalsIgnoreCase(getString(R.string.array_categorias_titulo))) {
            getAllProducts();
        } else {
            filtraPorCategoria();
        }
    }

    private void getAllProducts() {
        // Escuchamos cualquier cambio que se produzca en el nodo "productos"
        bbddProductos.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    consola.setVisibility(View.GONE);
                    lstLista.setVisibility(View.VISIBLE);
                    AdaptadorProductos adaptador;
                    datos.clear();
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
                            i.putExtra("clasePrevia",MainActivity.this.getClass().getSimpleName());
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

    private void filtraPorCategoria() {
        Query q = bbddProductos.orderByChild(getString(R.string.campo_categoria)).equalTo(categoriaSeleccionada);

        // Escuchamos cualquier cambio que se produzca en el nodo "productos"
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    consola.setVisibility(View.GONE);
                    lstLista.setVisibility(View.VISIBLE);
                    AdaptadorProductos adaptador;
                    datosFiltradosPorCategoria.clear();
                    for(DataSnapshot d : dataSnapshot.getChildren()){
                        Producto p = d.getValue(Producto.class);
                        //if (Integer.parseInt(p.getPrecio()) < 10)
                        datosFiltradosPorCategoria.add(p);
                    }
                    adaptador = new AdaptadorProductos(datosFiltradosPorCategoria);
                    adaptador.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(getApplicationContext(),VerProducto.class);
                            i.putExtra("idProducto",datosFiltradosPorCategoria.get(lstLista.getChildAdapterPosition(v)).getId().toString());
                            i.putExtra("clasePrevia",MainActivity.this.getClass().getSimpleName());
                            startActivity(i);
                        }
                    });
                    lstLista.setAdapter(adaptador);
                    lstLista.setItemAnimator(new DefaultItemAnimator());
                }  else {
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();
        if (id == R.id.action_cerrar_sesion) {
            mAuth.signOut();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_subir_producto) {
            Intent i = new Intent(MainActivity.this, NuevoProducto.class);
            startActivity(i);
        } else if (id == R.id.nav_mis_productos) {
            Intent i = new Intent(MainActivity.this, MisProductos.class);
            startActivity(i);
        } else if (id == R.id.nav_mis_favoritos) {
            Intent i = new Intent(MainActivity.this, MisFavoritos.class);
            startActivity(i);
        } else if (id == R.id.nav_ver_usuarios) {
            Intent i = new Intent(MainActivity.this, UsuariosActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_cerrar_sesion) {
            mAuth.signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void verPerfil(View view) {
        Intent i = new Intent(MainActivity.this, PerfilActivity.class);
        startActivity(i);
    }

    private void rellenaCabeceraPanelLateralConInfoUsuario() {
        NavigationView navigationView= (NavigationView) findViewById (R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        TextView text = (TextView) header.findViewById(R.id.cabecera_nav_nombre);
        text.setText(firebaseUser.getDisplayName());
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
