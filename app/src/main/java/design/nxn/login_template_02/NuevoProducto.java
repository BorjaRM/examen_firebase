package design.nxn.login_template_02;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import design.nxn.login_template_02.model.Producto;

public class NuevoProducto extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private TextView txtnombre, txtprecio, txtdescripcion;
    private String nombre, precio, descripcion;
    private LinearLayout btnGuardar, btnCancelar;
    private Spinner sp_categorias;
    private DatabaseReference bbddProductos;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private String categoria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto_detalle);

        hideViews();
        getViews();
        setListeners();

        // Obtenemos una referencia al objeto que maneja la autenticacion en Firebase
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        // Obtenemos una referencia a la base de datos
        bbddProductos = FirebaseDatabase.getInstance().getReference() //getReference nos proporciona una referencia al nodo raiz
                .child(getString(R.string.nodo_productos)); // bajamos al nodo productos
    }

    private void hideViews() {
        findViewById(R.id.bar).setVisibility(View.GONE);
    }

    private void setListeners() {
        sp_categorias.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        categoria = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void getViews() {
        txtnombre = (TextView) findViewById(R.id.prod_nombre_edittext);
        txtprecio = (TextView) findViewById(R.id.precio_edittext);
        txtdescripcion = (TextView) findViewById(R.id.descripcion_edittext);
        sp_categorias = (Spinner) findViewById(R.id.sp_categorias);
        btnGuardar = (LinearLayout) findViewById(R.id.btnGuardar);
        btnCancelar = (LinearLayout) findViewById(R.id.btnCancelar);

    }

    public void backClick(View view) {
        finish();
    }

    public void guardarClick(View view){
        if(categoria.equalsIgnoreCase(getString(R.string.array_categorias_titulo))) {
            Toast.makeText(this, "Elija una categoria", Toast.LENGTH_SHORT).show();
        } else {
            getInfoProducto();

            // Obtenemos la clave que Firebase genera automáticamente
            String clave = bbddProductos.push().getKey();

            // Creamos un nuevo usuario con estos datos
            Producto p = new Producto(clave,nombre,descripcion,categoria,precio,firebaseUser.getUid());

            // Creamos un nuevo nodo con la clave obtenida donde guardamos el objeto usuario que hemos creado
            bbddProductos.child(clave).setValue(p);

            finish();
        }
    }

    private void getInfoProducto() {
        nombre = txtnombre.getText().toString();
        precio = txtprecio.getText().toString();
        descripcion = txtdescripcion.getText().toString();
    }

    public void cancelarClick(View view) {
        finish();
    }


}
