package Coria.repositorios;

import Coria.entidad.Trabajo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrabajoRepositorio extends JpaRepository<Trabajo, String> {

    List<Trabajo> findByUsuarioId(String idUsuario);

    List<Trabajo> findByProveedorId(String idProveedor);

    List<Trabajo> findByEstado(String estado);

    Optional<Trabajo> findById(String idTrabajo);

    List<Trabajo> findByProveedorIdAndEstado(String proveedorId, String estado);
}
