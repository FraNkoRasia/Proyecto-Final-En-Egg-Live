package Coria.entidad;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.annotations.GenericGenerator;

@Entity
public class Oficio {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String idOficio;
    private String nombreOficio;
    private String comentarioOficio;

    public Oficio() {
    }

    public Oficio(String idOficio, String nombreOficio, String comentarioOficio) {
        this.idOficio = idOficio;
        this.nombreOficio = nombreOficio;
        this.comentarioOficio = comentarioOficio;
    }

    public String getIdOficio() {
        return idOficio;
    }

    public void setIdOficio(String idOficio) {
        this.idOficio = idOficio;
    }

    public String getNombreOficio() {
        return nombreOficio;
    }

    public void setNombreOficio(String nombreOficio) {
        this.nombreOficio = nombreOficio;
    }

    public String getComentarioOficio() {
        return comentarioOficio;
    }

    public void setComentarioOficio(String comentarioOficio) {
        this.comentarioOficio = comentarioOficio;
    }
    
    

    
}
