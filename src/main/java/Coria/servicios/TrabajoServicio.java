package Coria.servicios;

import Coria.entidad.Proveedor;
import Coria.entidad.Trabajo;
import Coria.excepciones.MiException;
import Coria.repositorios.ProveedorRepositorio;
import Coria.repositorios.TrabajoRepositorio;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TrabajoServicio {

    @Autowired
    private TrabajoRepositorio traRepo;

    @Autowired
    private ProveedorRepositorio proRepo;

    @Transactional
    public void registrarTrabajo(String idUsuario, String idProveedor, String nombre,
            String apellido, String telefono, String descripcion,
            String estado, Integer calificacion, String comentario) throws MiException {

        Trabajo tra = new Trabajo();

        Proveedor pro = proRepo.getOne(idProveedor);
        tra.setUsuarioId(idUsuario);
        tra.setProveedorId(idProveedor);
        tra.setEstado(estado);
        tra.setNombre(nombre);
        tra.setApellido(apellido);
        tra.setTelefono(telefono);
        tra.setDescripcion(descripcion);
        tra.setCalificacion(calificacion);
        tra.setComentario("");

        traRepo.save(tra);
    }

    public Trabajo getOne(String id) {
        return traRepo.getOne(id);
    }

    @Transactional
    public void eliminarTrabajo(String id) throws MiException {

        if (id == null || id.isEmpty()) {
            throw new MiException("El id ingresado no es correcto");
        }

        traRepo.deleteById(id);
    }

    @Transactional
    public void eliminarComentario(String id) throws MiException {

        Optional<Trabajo> respuesta = traRepo.findById(id);

        if (respuesta.isPresent()) {
            Trabajo tra = respuesta.get();

            tra.setComentario("Comentario Eliminado");
            traRepo.save(tra);
        }
    }

    public List<Trabajo> listarTrabajos() {

        return traRepo.findAll();

    }

    public List<Trabajo> listarTrabajosPorIdUsuario(String id) {

        return traRepo.findByUsuarioId(id);
    }

    public List<Trabajo> listarTrabajosPorIdProveedor(String id) {

        return traRepo.findByProveedorId(id);
    }
    
       public List<Trabajo> listarTrabajosPorProveedor(String idProveedor) {

        return traRepo.findByProveedorId(idProveedor);
    }

    @Transactional
    public void modificar(String id,
            Integer calificacion, String comentario) throws MiException {

        Optional<Trabajo> respuesta = traRepo.findById(id);

        if (respuesta.isPresent()) {
            Trabajo tra = respuesta.get();

            if (calificacion == null) {
                calificacion = tra.getCalificacion(); 
            }

            tra.setCalificacion(calificacion);

            tra.setComentario(comentario);

            traRepo.save(tra);
        }
    }

    @Transactional
    public void cambiarEstado(String id,
            String estado) throws MiException {

        Optional<Trabajo> respuesta = traRepo.findById(id);

        if (respuesta.isPresent()) {
            Trabajo tra = respuesta.get();

            tra.setEstado(estado);

            traRepo.save(tra);
        }
    }

}
