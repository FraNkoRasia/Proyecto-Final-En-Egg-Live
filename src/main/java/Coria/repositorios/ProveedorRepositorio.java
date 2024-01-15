package Coria.repositorios;

import Coria.entidad.Proveedor;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProveedorRepositorio extends JpaRepository<Proveedor, String> {

    @Query("SELECT p FROM Proveedor p WHERE p.email = :email")
    Proveedor buscarPorEmail(@Param("email") String email);

    @Query("SELECT p FROM Proveedor p WHERE LOWER(p.nombreEmpresa) LIKE LOWER(CONCAT('%', :terminoBusqueda , '%')) OR LOWER(p.tipoServicio) LIKE LOWER(CONCAT('%', :terminoBusqueda , '%'))")
    List<Proveedor> findByNombreEmpresaContainingOrTipoServicioContaining(@Param("terminoBusqueda") String terminoBusqueda);

    List<Proveedor> findAllByOrderByCalificacionPromedioDesc();

    Proveedor findByNombreEmpresa(String nombreEmpresa);

    void deleteByNombreEmpresa(String nombreEmpresa);

    List<Proveedor> findByTipoServicio(String tipoServicio);

}
