package design.nxn.login_template_02;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import design.nxn.login_template_02.model.Producto;

/**
 * Created by Borja on 05/01/2018.
 */

public class AdaptadorProductos extends RecyclerView.Adapter<AdaptadorProductos.ProductosViewHolder> implements View.OnClickListener{
    private ArrayList<Producto> datos;
    private View.OnClickListener listener;

    public AdaptadorProductos(ArrayList<Producto> datos) {
        this.datos = datos;
    }

    // Introduce el layout creado en xml
    @Override
    public ProductosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista_productos, parent, false);
        itemView.setOnClickListener(this);
        ProductosViewHolder pvh = new ProductosViewHolder(itemView);
        return pvh;
    }

    // Obtiene la información del elemento del arraylist y lo enlaza con un item según hemos definido en el metodo bindProducto
    @Override
    public void onBindViewHolder(ProductosViewHolder viewHolder, int pos) {
        Producto item = datos.get(pos);
        viewHolder.bindProducto(item);
    }

    // Devuelve el numero de items
    @Override
    public int getItemCount() {
        return datos.size();
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if(listener != null)
            listener.onClick(view);
    }

    // Almacena y recicla los views conforme salen de la pantalla cuando hacemos scroll
    public static class ProductosViewHolder extends RecyclerView.ViewHolder {
        private TextView txtNombre,txtDescripcion,txtCategoria,txtPrecio;

        public ProductosViewHolder(View v) {
            super(v);
            txtNombre = (TextView) v.findViewById(R.id.nombre);
            txtDescripcion = (TextView) v.findViewById(R.id.descripcion);
            txtCategoria = (TextView) v.findViewById(R.id.apellidos);
            txtPrecio = (TextView) v.findViewById(R.id.precio);
        }

        // Obtiene los datos del producto p y los coloca en los campos de texto que representan un producto en nuestra aplicación
        public void bindProducto(Producto p) {
            txtNombre.setText(p.getNombre());
            txtDescripcion.setText(p.getDescripcion());
            txtCategoria.setText(p.getCategoria());
            txtPrecio.setText(String.valueOf(p.getPrecio()) + "€");
        }
    }

}
