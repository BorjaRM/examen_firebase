package design.nxn.login_template_02;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import design.nxn.login_template_02.model.Usuario;


public class PerfilActivity extends AppCompatActivity {
    private TextView txtNombre,txtApellidos,txtCorreo,txtDireccion;
    private LinearLayout botonera, btnGuardar, btnCancelar;
    private DatabaseReference bbddUsuarios;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        getViews();
        setListeners();

        // Obtenemos una referencia al objeto que maneja la autenticacion en Firebase
        mAuth = FirebaseAuth.getInstance();

        firebaseUser = mAuth.getCurrentUser();

        // Obtenemos una referencia a la base de datos
        bbddUsuarios = FirebaseDatabase.getInstance().getReference() //getReference nos proporciona una referencia al nodo raiz
                .child(getString(R.string.nodo_usuarios)) // bajamos al nodo usuarios
                .child(firebaseUser.getUid()); // accedemos al nodo del usuario en concreto ya que la key es igual que el UID del usuario

        // Escuchamos cualquier cambio que se produzca en el nodo
        bbddUsuarios.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bindInfoUsuario(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getViews() {
        txtNombre = (TextView) findViewById(R.id.username_edittext);
        txtApellidos = (TextView) findViewById(R.id.surname_edittext);
        txtCorreo = (TextView) findViewById(R.id.email_edittext);
        txtDireccion = (TextView) findViewById(R.id.direccion_edittext);
        botonera = (LinearLayout) findViewById(R.id.botonera);
        btnGuardar = (LinearLayout) findViewById(R.id.btnGuardar);
        btnCancelar = (LinearLayout) findViewById(R.id.btnCancelar);
    }

    private void setListeners() {
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modificarInfoUsuario();
                disableViews();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableViews();
                recreate(); // recargamos la activity para que vuelva a su estado inicial
            }
        });
    }

    private void bindInfoUsuario(DataSnapshot dataSnapshot) {
        Usuario unUsuario = dataSnapshot.getValue(Usuario.class);
        txtNombre.setText(unUsuario.getNombre());
        txtApellidos.setText(unUsuario.getApellidos());
        txtCorreo.setText(firebaseUser.getEmail());
        txtDireccion.setText(unUsuario.getDireccion());
    }

    private void modificarInfoUsuario() {
        // Obtenemos los inputs del usuario
        String nombre = txtNombre.getText().toString();
        String apellidos = txtApellidos.getText().toString();
        String direccion = txtDireccion.getText().toString();
        String correo = txtCorreo.getText().toString();

        // Modificamos el perfil del usuario con la nueva inforamción
        bbddUsuarios.child(getString(R.string.campo_nombre)).setValue(nombre);
        bbddUsuarios.child(getString(R.string.campo_apellidos)).setValue(apellidos);
        bbddUsuarios.child(getString(R.string.campo_direccion)).setValue(direccion);

        // Actualizamos el email en la autenticacion
        firebaseUser.updateEmail(correo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(PerfilActivity.this, "email modificado", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(PerfilActivity.this, "error "+task.getException(), Toast.LENGTH_LONG).show();
                }
            }
        });

        modificarDisplayName(nombre, apellidos);
    }

    private void modificarDisplayName(String nombre, String apellidos) {
        // Actualizamos el display name del usuario
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(nombre + " " + apellidos).build();
        firebaseUser.updateProfile(profileUpdates);
    }

    private void disableViews() {
        txtNombre.setEnabled(false);
        txtApellidos.setEnabled(false);
        txtCorreo.setEnabled(false);
        txtDireccion.setEnabled(false);
        botonera.setVisibility(View.GONE);
    }

    private void enableViews() {
        txtNombre.setEnabled(true);
        txtApellidos.setEnabled(true);
        txtCorreo.setEnabled(true);
        txtDireccion.setEnabled(true);
        botonera.setVisibility(View.VISIBLE);
    }

    public void backClick(View view) {
        finish();
    }

    public void editarPerfil(View view) {
        Toast.makeText(this, "editar perfil", Toast.LENGTH_SHORT).show();
        enableViews();
    }

    public void guardarClick(View view){
        modificarInfoUsuario();
        disableViews();
    }

    public void cancelarClick(View view) {
        disableViews();
    }
}
