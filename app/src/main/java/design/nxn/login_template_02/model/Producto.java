package design.nxn.login_template_02.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Borja on 05/01/2018.
 */

public class Producto implements Parcelable{
    private String id;
    private String nombre;
    private String descripcion;
    private String categoria;
    private String precio;
    private String usuarioRef;

    public Producto() {

    }

    public Producto(String id, String nombre, String descripcion, String categoria, String precio, String usuarioRef) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.precio = precio;
        this.usuarioRef = usuarioRef;
    }

    protected Producto(Parcel in) {
        id = in.readString();
        nombre = in.readString();
        descripcion = in.readString();
        categoria = in.readString();
        precio = in.readString();
        usuarioRef = in.readString();
    }

    public static final Creator<Producto> CREATOR = new Creator<Producto>() {
        @Override
        public Producto createFromParcel(Parcel in) {
            return new Producto(in);
        }

        @Override
        public Producto[] newArray(int size) {
            return new Producto[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getUsuarioRef() {
        return usuarioRef;
    }

    public void setUsuarioRef(String usuarioRef) {
        this.usuarioRef = usuarioRef;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(nombre);
        dest.writeString(descripcion);
        dest.writeString(categoria);
        dest.writeString(precio);
        dest.writeString(usuarioRef);
    }
}
