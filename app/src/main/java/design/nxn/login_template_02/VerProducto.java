package design.nxn.login_template_02;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import design.nxn.login_template_02.model.Producto;

/**
 * Created by Borja on 14/01/2018.
 */

public class VerProducto extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private TextView txtnombre, txtprecio, txtdescripcion;
    private String nombre, precio, descripcion;
    private Spinner sp_categorias;
    private LinearLayout botonera,btnGuardar, btnCancelar, btnEditar, btnBorrar, btnBack, btnfav;
    private DatabaseReference bbddProductos;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private String categoria;
    private String idProducto, clasePrevia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto_detalle);

        idProducto = getIntent().getStringExtra("idProducto");
        clasePrevia = getIntent().getStringExtra("clasePrevia");

        getViews();
        setListeners();
        disableViews();

        // Obtenemos una referencia al objeto que maneja la autenticacion en Firebase
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        // Obtenemos una referencia a la base de datos
        bbddProductos = FirebaseDatabase.getInstance().getReference() //getReference nos proporciona una referencia al nodo raiz
                .child(getString(R.string.nodo_productos)) // bajamos al nodo productos
                .child(idProducto); // bajamos al nodo especifico para el producto

        // Escuchamos cualquier cambio que se produzca en el nodo
        bbddProductos.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bindInfoProducto(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void getViews() {
        txtnombre = (TextView) findViewById(R.id.prod_nombre_edittext);
        txtprecio = (TextView) findViewById(R.id.precio_edittext);
        txtdescripcion = (TextView) findViewById(R.id.descripcion_edittext);
        sp_categorias = (Spinner) findViewById(R.id.sp_categorias);
        botonera = (LinearLayout) findViewById(R.id.botonera);
        btnBack = (LinearLayout) findViewById(R.id.btn_volver_producto);
        btnBorrar = (LinearLayout) findViewById(R.id.btn_borrar_producto);
        btnEditar = (LinearLayout) findViewById(R.id.btn_editar_producto);
        btnGuardar = (LinearLayout) findViewById(R.id.btnGuardar);
        btnCancelar = (LinearLayout) findViewById(R.id.btnCancelar);
        btnfav =  (LinearLayout) findViewById(R.id.btn_fav);
    }

    private void setListeners() {
        btnBack.setOnClickListener(this);
        btnBorrar.setOnClickListener(this);
        btnEditar.setOnClickListener(this);
        btnGuardar.setOnClickListener(this);
        btnCancelar.setOnClickListener(this);
        btnfav.setOnClickListener(this);
        sp_categorias.setOnItemSelectedListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_editar_producto : editarProducto(); break;
            case R.id.btn_borrar_producto: borrarProducto(); break;
            case R.id.btn_volver_producto : finish(); break;
            case R.id.btnGuardar : guardarProductoModificado(); break;
            case R.id.btn_fav : favoritos(); break;
            case R.id.btnCancelar : recreate(); break;
        }
    }

    private void editarProducto() {
        Toast.makeText(this, "editar producto", Toast.LENGTH_SHORT).show();
        enableViews();
    }

    private void borrarProducto() {
        Toast.makeText(this, "borrar producto", Toast.LENGTH_SHORT).show();
        bbddProductos.removeValue();
        Intent i = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(i);

    }

    private void guardarProductoModificado() {
        Toast.makeText(this, "modificar producto", Toast.LENGTH_SHORT).show();

        if(categoria.equalsIgnoreCase(getString(R.string.array_categorias_titulo))) {
            Toast.makeText(this, "Elija una categoria", Toast.LENGTH_SHORT).show();
        } else {
            nombre = txtnombre.getText().toString();
            precio = txtprecio.getText().toString();
            descripcion = txtdescripcion.getText().toString();

            // Modificamos el producto con la nueva inforamción
            bbddProductos.child(getString(R.string.campo_nombre_prod)).setValue(nombre);
            bbddProductos.child(getString(R.string.campo_precio)).setValue(precio);
            bbddProductos.child(getString(R.string.campo_descripcion)).setValue(descripcion);
            bbddProductos.child(getString(R.string.campo_categoria)).setValue(categoria);
        }
        Intent i = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(i);
    }

    private void disableViews() {
        txtnombre.setEnabled(false);
        txtprecio.setEnabled(false);
        txtdescripcion.setEnabled(false);
        sp_categorias.setEnabled(false);
        botonera.setVisibility(View.GONE);
        if (clasePrevia.equalsIgnoreCase("MainActivity")) {
            btnBorrar.setVisibility(View.GONE);
            btnEditar.setVisibility(View.GONE);
        }
    }

    private void enableViews() {
        txtnombre.setEnabled(true);
        txtprecio.setEnabled(true);
        txtdescripcion.setEnabled(true);
        sp_categorias.setEnabled(true);
        botonera.setVisibility(View.VISIBLE);
    }

    private void bindInfoProducto(DataSnapshot dataSnapshot) {
        if(dataSnapshot.exists()){
            Producto unProducto = dataSnapshot.getValue(Producto.class);
            txtnombre.setText(unProducto.getNombre());
            txtprecio.setText(unProducto.getPrecio());
            txtdescripcion.setText(unProducto.getDescripcion());
            sp_categorias.setSelection(((ArrayAdapter)sp_categorias.getAdapter()).getPosition(unProducto.getCategoria()));
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        categoria = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void favoritos() {
        Toast.makeText(this, "favvvvv", Toast.LENGTH_SHORT).show();

        // Obtenemos una referencia a la base de datos
        bbddProductos = FirebaseDatabase.getInstance().getReference() //getReference nos proporciona una referencia al nodo raiz
                .child(getString(R.string.nodo_favoritos)); // bajamos al nodo favoritos

        // Creamos un nuevo nodo
        bbddProductos.child(firebaseUser.getUid()).child(idProducto).setValue(true);
    }
}
