package design.nxn.login_template_02;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import design.nxn.login_template_02.Tools.CustomViewPager;
import design.nxn.login_template_02.Tools.ViewPagerAdapter;
import design.nxn.login_template_02.customfonts.MyEditText;
import design.nxn.login_template_02.model.Usuario;

public class LoginActivity extends AppCompatActivity {
    private static final int LOGIN_FRAGMENT = 0;
    private static final int SIGNUP_FRAGMENT = 1;
    private static final int RESET_PASSWORD_FRAGMENT = 2;
    private CustomViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private String nick, nombre, apellidos, email, direccion, password;
    private DatabaseReference bbddUsuarios;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        viewPager = (CustomViewPager) findViewById(R.id.viewpager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setPagingEnabled(false);
        changeFragment(LOGIN_FRAGMENT);

        // Obtenemos una referencia al objeto que maneja la autenticacion en Firebase
        mAuth = FirebaseAuth.getInstance();
    }

    private void changeFragment(int fragmentType) {
        switch (fragmentType) {
            case LOGIN_FRAGMENT:
                viewPager.setCurrentItem(LOGIN_FRAGMENT);
                break;
            case SIGNUP_FRAGMENT:
                viewPager.setCurrentItem(SIGNUP_FRAGMENT);
                break;
            case RESET_PASSWORD_FRAGMENT:
                viewPager.setCurrentItem(RESET_PASSWORD_FRAGMENT);
                break;
            default:
                viewPager.setCurrentItem(LOGIN_FRAGMENT);
                break;
        }
    }

    public void signUpClick(View view) {
        changeFragment(SIGNUP_FRAGMENT);
    }

    public void signInClick(View view) {
        changeFragment(LOGIN_FRAGMENT);
    }

    public void resetPasswordClick(View view) {
        changeFragment(RESET_PASSWORD_FRAGMENT);
    }

    public void backClick(View view) {
        changeFragment(LOGIN_FRAGMENT);
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == LOGIN_FRAGMENT)
            super.onBackPressed();
        else {
            changeFragment(LOGIN_FRAGMENT);
        }
    }

    public void logInButtonClicked() {
        email = ((MyEditText) findViewById(R.id.email_login)).getText().toString();
        password = ((MyEditText) findViewById(R.id.password_login)).getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in firebaseUser's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(i);
                        } else {
                            // If sign in fails, display a message to the firebaseUser.
                            Toast.makeText(LoginActivity.this, "Authentication failed." + task.getException(), Toast.LENGTH_LONG).show();

                            try {
                                throw task.getException();
                            } catch(FirebaseAuthWeakPasswordException e) {
                                ((MyEditText) findViewById(R.id.pass_signup)).setError(getString(R.string.excepcion_pass_corta));
                                findViewById(R.id.pass_signup).requestFocus();
                            } catch(FirebaseAuthInvalidCredentialsException e) {
                                ((MyEditText) findViewById(R.id.email_signup)).setError(getString(R.string.excepcion_email_invalido));
                                findViewById(R.id.email_signup).requestFocus();
                            } catch(FirebaseAuthUserCollisionException e) {
                                ((MyEditText) findViewById(R.id.email_signup)).setError(getString(R.string.excepcion_email_existente));
                                findViewById(R.id.email_signup).requestFocus();
                            } catch(Exception e) {
                                Log.e("excepcion login", e.getMessage());
                            }
                        }
                    }
                });
    }

    public void signUpButtonClicked() {
        // Obtenemos los inputs para realizar el registro
        email = ((MyEditText) findViewById(R.id.email_signup)).getText().toString();
        password = ((MyEditText) findViewById(R.id.pass_signup)).getText().toString();

        // Obtenemos los inputs para rellenar el perfil del usuario
        nick = ((MyEditText) findViewById(R.id.nick_signup)).getText().toString();
        nombre = ((MyEditText) findViewById(R.id.username_signup)).getText().toString();
        apellidos = ((MyEditText) findViewById(R.id.surname_signup)).getText().toString();
        direccion = ((MyEditText) findViewById(R.id.direccion_signup)).getText().toString();

        //Registra al usuario con email y contraseña y lo mantiene logueado
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Obtenemos una referencia a la base de datos
                            bbddUsuarios = FirebaseDatabase.getInstance().getReference() //getReference nos proporciona una referencia al nodo raiz
                                    .child(getString(R.string.nodo_usuarios)); // bajamos al nodo usuarios

                            firebaseUser = mAuth.getCurrentUser();

                            // Obtenemos la clave que Firebase genera automáticamente
                            //String clave = bbddUsuarios.push().getKey();

                            // Obtenemos el UID del usuario para usarlo como clave del nuevo nodo
                            String clave = firebaseUser.getUid();

                            // Creamos un nuevo usuario con estos datos
                            Usuario u = new Usuario(nick,nombre,apellidos,direccion);

                            // Creamos un nuevo nodo con la clave obtenida donde guardamos el objeto usuario que hemos creado
                            bbddUsuarios.child(clave).setValue(u);

                            Toast.makeText(LoginActivity.this, getString(R.string.registro_ok), Toast.LENGTH_LONG).show();

                            // Seteamos el display name para el nuevo usuario
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(nombre + " " + apellidos).build();
                            firebaseUser.updateProfile(profileUpdates);

                            mAuth.signOut();

                            changeFragment(LOGIN_FRAGMENT);

                        } else {
                            // Si la autenticacion falla, gestionamos las posibles excepciones
                            try {
                                throw task.getException();
                            } catch(FirebaseAuthWeakPasswordException e) {
                                ((MyEditText) findViewById(R.id.pass_signup)).setError(getString(R.string.excepcion_pass_corta));
                                findViewById(R.id.pass_signup).requestFocus();
                            } catch(FirebaseAuthInvalidCredentialsException e) {
                                ((MyEditText) findViewById(R.id.email_signup)).setError(getString(R.string.excepcion_email_invalido));
                                findViewById(R.id.email_signup).requestFocus();
                            } catch(FirebaseAuthUserCollisionException e) {
                                ((MyEditText) findViewById(R.id.email_signup)).setError(getString(R.string.excepcion_email_existente));
                                findViewById(R.id.email_signup).requestFocus();
                            } catch(Exception e) {
                                Log.e("excepcion login", e.getMessage());
                            }
                        }
                    }
                });
    }

    public void resetPasswordButtonClicked() {
        Toast.makeText(this, R.string.reset_password_button_clicked, Toast.LENGTH_SHORT).show();
    }
}
