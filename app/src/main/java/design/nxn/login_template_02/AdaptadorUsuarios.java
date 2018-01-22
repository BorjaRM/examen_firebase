package design.nxn.login_template_02;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import design.nxn.login_template_02.model.Usuario;

/**
 * Created by Borja on 04/01/2018.
 */

public class AdaptadorUsuarios extends RecyclerView.Adapter<AdaptadorUsuarios.UsuariosViewHolder> {

    private ArrayList<Usuario> datos;

    public AdaptadorUsuarios(ArrayList<Usuario> datos) {
        this.datos = datos;
    }

    @Override
    public UsuariosViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_lista_usuario, viewGroup, false);
        UsuariosViewHolder uvh = new UsuariosViewHolder(itemView);
        return uvh;
    }

    @Override
    public void onBindViewHolder(UsuariosViewHolder viewHolder, int pos) {
        Usuario item = datos.get(pos);
        viewHolder.bindUsuario(item);
    }

    @Override
    public int getItemCount() {
        return datos.size();
    }

    public static class UsuariosViewHolder extends RecyclerView.ViewHolder {
        private TextView txtNick;
        private TextView txtNombre;
        private TextView txtApellidos;

        public UsuariosViewHolder(View itemView) {
            super(itemView);
            txtNick = (TextView) itemView.findViewById(R.id.nick);
            txtNombre = (TextView) itemView.findViewById(R.id.nombre);
            txtApellidos = (TextView) itemView.findViewById(R.id.apellidos);
        }

        public void bindUsuario(Usuario u) {
            txtNick.setText(u.getNick());
            txtNombre.setText(u.getNombre() + " ");
            txtApellidos.setText(u.getApellidos());
        }
    }

}