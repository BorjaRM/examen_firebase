package design.nxn.login_template_02.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Borja on 04/01/2018.
 */

public class Usuario implements Parcelable {
    //private String id;
    private String apellidos;
    //private String correo;
    private String direccion;
    private String nick;
    private String nombre;
    //private String pass;

    public Usuario() {
        // Constructor por defecto necesario para las llamadas a DataSnapshot.getValue(Usuario.class)
    }

    public Usuario(String nick, String nombre, String apellidos, String dirección) {
        //this.id = id;
        this.nick = nick;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.direccion = dirección;
        //this.correo = correo;
        //this.pass = pass;
    }

    protected Usuario(Parcel in) {
        //id = in.readString();
        apellidos = in.readString();
        //correo = in.readString();
        direccion = in.readString();
        nick = in.readString();
        nombre = in.readString();
        //pass = in.readString();
    }

    public static final Creator<Usuario> CREATOR = new Creator<Usuario>() {
        @Override
        public Usuario createFromParcel(Parcel in) {
            return new Usuario(in);
        }

        @Override
        public Usuario[] newArray(int size) {
            return new Usuario[size];
        }
    };




    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //dest.writeString(id);
        dest.writeString(apellidos);
        //dest.writeString(correo);
        dest.writeString(direccion);
        dest.writeString(nick);
        dest.writeString(nombre);
        //dest.writeString(pass);
    }
}
