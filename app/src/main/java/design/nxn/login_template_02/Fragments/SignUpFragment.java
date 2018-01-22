package design.nxn.login_template_02.Fragments;


import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import design.nxn.login_template_02.LoginActivity;
import design.nxn.login_template_02.R;
import design.nxn.login_template_02.customfonts.MyEditText;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {
    private ImageView imageView;
    private DatabaseReference bbdd;
    private boolean ok = true;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_sign_up, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MyEditText nick = (MyEditText) getView().findViewById(R.id.nick_signup);

        // Obtenemos la referencia a la base de datos a la que estamos conectados
        bbdd = FirebaseDatabase.getInstance().getReference(getString(R.string.nodo_usuarios));

        getView().findViewById(R.id.signup_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((MyEditText) getView().findViewById(R.id.nick_signup)).getText().length() == 0) {
                    ((MyEditText) getView().findViewById(R.id.nick_signup)).setError(getString(R.string.empty));
                    ok = false;
                }
                if (((MyEditText) getView().findViewById(R.id.username_signup)).getText().length() == 0) {
                    ((MyEditText) getView().findViewById(R.id.username_signup)).setError(getString(R.string.empty));
                    ok = false;
                }
                if (((MyEditText) getView().findViewById(R.id.surname_signup)).getText().length() == 0) {
                    ((MyEditText) getView().findViewById(R.id.surname_signup)).setError(getString(R.string.empty));
                    ok = false;
                }
                if (((MyEditText) getView().findViewById(R.id.email_signup)).getText().length() == 0) {
                    ((MyEditText) getView().findViewById(R.id.email_signup)).setError(getString(R.string.empty));
                    ok = false;
                }
                if (((MyEditText) getView().findViewById(R.id.direccion_signup)).getText().length() == 0) {
                    ((MyEditText) getView().findViewById(R.id.direccion_signup)).setError(getString(R.string.empty));
                    ok = false;
                }
                if (((MyEditText) getView().findViewById(R.id.pass_signup)).getText().length() == 0) {
                    ((MyEditText) getView().findViewById(R.id.pass_signup)).setError(getString(R.string.empty));
                    ok = false;
                }
                if (ok)
                    ((LoginActivity) getActivity()).signUpButtonClicked();
            }
        });
        imageView = (ImageView) getView().findViewById(R.id.profile_picture);
        getView().findViewById(R.id.photo_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageButtonClick();
            }
        });
        nick.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                compruebaUsuario();
            }
        });
    }

    public void compruebaUsuario() {
        String nick = ((MyEditText) getView().findViewById(R.id.nick_signup)).getText().toString();
        Query q = bbdd.orderByChild(getString(R.string.campo_nick)).equalTo(nick);

        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    ((MyEditText) getView().findViewById(R.id.nick_signup)).setError("nick ya utilizado");
                    ok = false;
                } else {
                    ok = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    public void imageButtonClick() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, 1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case 0:
                if (resultCode == getActivity().RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    imageView.setImageURI(selectedImage);
                }

                break;
            case 1:
                if (resultCode == getActivity().RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    imageView.setImageURI(selectedImage);
                }
                break;
        }
    }

}
