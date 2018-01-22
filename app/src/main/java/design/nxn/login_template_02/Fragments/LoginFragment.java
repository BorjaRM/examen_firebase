package design.nxn.login_template_02.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import design.nxn.login_template_02.LoginActivity;
import design.nxn.login_template_02.R;
import design.nxn.login_template_02.customfonts.MyEditText;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {


    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getView().findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean ok = true;
                if (((MyEditText) getView().findViewById(R.id.email_login)).getText().length() == 0) {
                    ((MyEditText) getView().findViewById(R.id.email_login)).setError(getString(R.string.empty));
                    ok = false;
                }
                if (((MyEditText) getView().findViewById(R.id.password_login)).getText().length() == 0) {
                    ((MyEditText) getView().findViewById(R.id.password_login)).setError(getString(R.string.empty));
                    ok = false;
                }
                if (ok)
                    ((LoginActivity)getActivity()).logInButtonClicked();
            }
        });
    }
}
