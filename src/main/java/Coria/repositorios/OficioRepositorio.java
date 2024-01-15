package Coria.repositorios;

import Coria.entidad.Oficio;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface OficioRepositorio extends JpaRepository<Oficio, String> {
    
    @Query("SELECT o FROM Oficio o WHERE o.idOficio = :idOficio")
    public Optional<Oficio> buscarPorIdOficio(@Param("idOficio") String idOficio);

    @Query("SELECT o FROM Oficio o WHERE o.nombreOficio = :nombreOficio")
    public Optional<Oficio> buscarPorNombreOficio(@Param("nombreOficio") String nombreOficio);
    
    @Query("SELECT o FROM Oficio o WHERE LOWER(o.idOficio) LIKE LOWER(CONCAT('%', :terminoBusqueda , '%')) OR LOWER(o.nombreOficio) LIKE LOWER(CONCAT('%', :terminoBusqueda , '%'))")
    List<Oficio> findByNombreOficioContainingOrIDOficioContaining(@Param("terminoBusqueda") String terminoBusqueda);
   
}
