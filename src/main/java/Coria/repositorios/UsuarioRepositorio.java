package Coria.repositorios;

import Coria.entidad.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, String> {

    @Query("SELECT u FROM Usuario u WHERE u.email = :email")
    public Usuario findOptionalByEmail(@Param("email") String email);

    @Query("SELECT u FROM Usuario u WHERE u.email = :email")
    public Usuario buscarPorEmail(@Param("email") String email);

    @Query("SELECT u FROM Usuario u WHERE u.email = :email")
    public Optional<Usuario> findByEmail(String email);

    @Query("SELECT u FROM Usuario u WHERE u.nombre = :nombre")
    public Optional<Usuario> findByNombre(String nombre);

    @Query("SELECT u FROM Usuario u WHERE LOWER(u.nombre) LIKE LOWER(CONCAT('%', :terminoBusqueda , '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :terminoBusqueda , '%'))")
    List<Usuario> findByNombreContainingOrEmailContaining(@Param("terminoBusqueda") String terminoBusqueda);

    @Query("SELECT u FROM Usuario u WHERE u.resetToken = :resetToken")
     Optional <Usuario> findByResetToken(@Param("resetToken") String resetToken);

}
