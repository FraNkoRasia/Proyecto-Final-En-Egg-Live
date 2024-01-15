package Coria.servicios;

import Coria.Enumeracion.Rol;
import Coria.entidad.Imagen;
import Coria.entidad.Proveedor;
import Coria.entidad.Usuario;
import Coria.excepciones.MiException;
import Coria.repositorios.ProveedorRepositorio;
import Coria.repositorios.UsuarioRepositorio;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UsuarioServicio implements UserDetailsService {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private ProveedorRepositorio proveedorRepositorio;

    @Autowired
    private ImagenServicio imagenServicio;

    @Autowired
    private EmailServicio emailServicio;

    @Autowired
    private ProveedorRepositorio provRep;

    @Transactional
    public void registrar(String nombre, MultipartFile archivo, String apellido, String email, String telefono, String password, String nombreEmpresa, String tipoServicio, Rol rol) throws MiException {

        if (nombre.isEmpty() || nombre == null) {
            throw new MiException("el nombre no puede ser nulo o estar vacío");
        }
        if (apellido.isEmpty() || apellido == null) {
            throw new MiException("el apellido no puede ser nulo o estar vacío");
        }

        if (email.isEmpty() || email == null) {
            throw new MiException("el email no puede ser nulo o estar vacio");
        }
        if (telefono.isEmpty()) {
            throw new MiException("el telefono no puede estar vacio");
        }
        if (password.isEmpty() || password == null || password.length() <= 5) {
            throw new MiException("La contraseña no puede estar vacía, y debe tener más de 5 dígitos");
        }
        Proveedor usuarioExistente = provRep.buscarPorEmail(email);
        if (usuarioExistente != null) {
            throw new MiException("El correo electrónico ya está en uso.");
        }

        Proveedor prov = new Proveedor();
        prov.setNombre(nombre);
        prov.setApellido(apellido);
        prov.setEmail(email);
        prov.setTelefono(telefono);
        prov.setPassword(new BCryptPasswordEncoder().encode(password));
        prov.setRol(rol);
        Imagen imagen = imagenServicio.guardar(archivo);
        prov.setImagen(imagen);
        prov.setTipoServicio(tipoServicio != null ? tipoServicio : "");
        prov.setNombreEmpresa(nombreEmpresa != null ? nombreEmpresa : "");

        provRep.save(prov);
    }

    ////////////// 
    // RECUPERACION DE PASSWORD
    @Transactional
    public void generarTokenYEnviarCorreo(String email) throws MiException {
        Usuario usuario = usuarioRepositorio.buscarPorEmail(email);
        if (usuario != null) {
            String token = UUID.randomUUID().toString();
            usuario.setResetToken(token);
            usuarioRepositorio.save(usuario);

            // Aquí llama al servicio de correo para enviar el correo electrónico con el token
            String mensaje = "Utilice este enlace para restablecer su contraseña: http://localhost:8080/reset-password?token=" + token;
            emailServicio.sendEmail("noreply@tuapp.com", email, "Restablecimiento de contraseña", mensaje);
        } else {
            throw new MiException("El correo electrónico proporcionado no está asociado a ninguna cuenta.");
        }
    }
    //FIN DE RECUPERACION DE PASSWORD
    ////////////////

    @Transactional
    public Usuario actualizar(MultipartFile archivo, String id, String nombre, String apellido, String email, String telefono, String password) throws MiException {
        validar(nombre, apellido, email, telefono, password);

        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);

        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();

            if (!usuario.getEmail().equals(email) && usuarioRepositorio.buscarPorEmail(email) != null) {
                throw new MiException("El email ya está en uso");
            }

            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setEmail(email);
            usuario.setTelefono(telefono);
            usuario.setPassword(new BCryptPasswordEncoder().encode(password));
            usuario.setRol(usuario.getRol());

            String idImagen = null;

            if (usuario.getImagen() != null) {
                idImagen = usuario.getImagen().getId();
            }
            Imagen imagen = imagenServicio.actualizar(archivo, idImagen);
            usuario.setImagen(imagen);
            usuarioRepositorio.save(usuario);

            return usuarioRepositorio.save(usuario);
        } else {
            throw new MiException("Usuario no encontrado"); // Manejo de caso donde el usuario no se encuentra
        }
    }

    public Usuario obtenerInformacionUsuario(String id) {
        // Utiliza el método findById de tu repositorio para obtener un Optional<Usuario> por ID
        return usuarioRepositorio.findById(id).orElse(null);
    }

    @Transactional
    public Usuario modificarUsuario(String id, String nombre, String apellido, String email, String telefono, String currentPassword,
            String tipoServicio, String nombreEmpresa) throws MiException {
        if (nombre.isEmpty() || nombre == null) {
            throw new MiException("el nombre no puede ser nulo o estar vacio");
        }
        if (apellido.isEmpty() || apellido == null) {
            throw new MiException("el apellido no puede ser nulo o estar vacio");
        }
        if (email.isEmpty() || email == null) {
            throw new MiException("el email no puede ser nulo o estar vacio");
        }
        if (telefono.isEmpty()) {
            throw new MiException("el telefono no puede estar vacio");
        }
        Optional<Proveedor> respuesta = proveedorRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Proveedor usuario = respuesta.get();

            // Verificar si la contraseña actual ingresada es correcta
            if (!new BCryptPasswordEncoder().matches(currentPassword, usuario.getPassword())) {
                throw new MiException("La contraseña actual no es correcta");
            }
            // Update user information
            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setEmail(email);
            usuario.setTelefono(telefono);
            usuario.setTipoServicio(tipoServicio);
            usuario.setNombreEmpresa(nombreEmpresa);

            usuarioRepositorio.save(usuario);
            return usuario; // Returns the modified user
        } else {
            throw new MiException("Usuario no encontrado");
        }
    }

    @Transactional
    public void modificarRolYTipoServicio(String id, String nuevoRol, String tipoServicio, String nombreEmpresa) throws MiException {
        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);

        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();

            // Validar que el nuevo rol sea válido según tus reglas de negocio
            // Puedes agregar más validaciones según sea necesario...
            usuario.setRol(Rol.valueOf(nuevoRol));

            if (usuario instanceof Proveedor) {
                // Si el usuario es un Proveedor, establecer el tipo de servicio
                ((Proveedor) usuario).setTipoServicio(tipoServicio);
                ((Proveedor) usuario).setNombreEmpresa(nombreEmpresa);
            } else {
                // Si no es un Proveedor, reiniciar el tipo de servicio
                if (usuario.getRol() != Rol.PROVEEDOR) {
                    ((Proveedor) usuario).setTipoServicio(null);
                    ((Proveedor) usuario).setNombreEmpresa(null);
                }
            }

            usuarioRepositorio.save(usuario);
        } else {
            throw new MiException("Usuario no encontrado");
        }
    }

    @Transactional
    public Usuario AdministradorModifica(String id, String nombre, String apellido, String email, String telefono, Rol nuevoRol) throws MiException {
        if (nombre == null || nombre.isEmpty()) {
            throw new MiException("el nombre no puede ser nulo o estar vacío");
        }
        if (apellido.isEmpty() || apellido == null) {
            throw new MiException("el apellido no puede ser nulo o estar vacio");
        }
        if (email.isEmpty() || email == null) {
            throw new MiException("el email no puede ser nulo o estar vacio");
        }
        if (telefono.isEmpty()) {
            throw new MiException("el telefono no puede estar vacio");
        }
        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();

            // Update user information
            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setEmail(email);
            usuario.setTelefono(telefono);

            // Modificar el rol
            usuario.setRol(nuevoRol);

            usuarioRepositorio.save(usuario);
            return usuario; // Returns the modified user
        } else {
            throw new MiException("Usuario no encontrado");
        }
    }

    @Transactional
    public Usuario actualizarPassword(String id, String currentPassword, String newPassword) throws MiException {
        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);

        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();

            // Verificar si la contraseña actual ingresada es correcta
            if (!new BCryptPasswordEncoder().matches(currentPassword, usuario.getPassword())) {
                throw new MiException("La contraseña actual no es correcta");
            }

            // Actualizar la contraseña con la nueva
            usuario.setPassword(new BCryptPasswordEncoder().encode(newPassword));

            // Guardar el usuario actualizado
            return usuarioRepositorio.save(usuario);
        } else {
            throw new MiException("Usuario no encontrado");
        }
    }

    public Usuario getOne(String id) {
        return usuarioRepositorio.getOne(id);
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarUsuarios() {

        List<Usuario> usuarios = new ArrayList();
        usuarios = usuarioRepositorio.findAll();
        return usuarios;
    }

    @Transactional
    public void cambiarRol(String id, Rol nuevoRol) {
        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);

        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();
            if (usuario.getRol().equals(Rol.USER)) {
                usuario.setRol(Rol.ADMIN);
            } else if (usuario.getRol().equals(Rol.ADMIN)) {
                usuario.setRol(Rol.USER);
            } else if (usuario.getRol().equals(Rol.PROVEEDOR)) {
                usuario.setRol(Rol.USER);
            }

            // Guardar los cambios en el repositorio
            usuarioRepositorio.save(usuario);
        }
    }

    public void darDeBajaAdmin(String Id, String motivo) {
        Usuario admin = usuarioRepositorio.findById(Id)
                .orElseThrow(() -> new RuntimeException("No se ha encontrado al administrador con ID: " + Id));

        if (admin.getRol().equals(Rol.ADMIN)) {

            admin.setFechaBaja(LocalDate.MAX);

            admin.setMotivoBaja(motivo);

            usuarioRepositorio.save(admin);
        } else {
            // El usuario no es un administrador, lanzar una excepción
            throw new RuntimeException("El usuario no es un administrador");
        }
    }

    public void eliminarUsuario(String id) throws MiException {
        if (id.isEmpty() || id.equals("")) {
            throw new MiException("el id proporcionado es nulo");
        } else {
            Optional<Usuario> respuesta = usuarioRepositorio.findById(id);
            if (respuesta.isPresent()) {
                Usuario usuario = respuesta.get();
                usuarioRepositorio.delete(usuario);
            }
        }
    }

    @Transactional
    public void darDeBaja(String id, String motivo) throws MiException {
        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);

        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();

            // Ensure that usuarioRepositorio is not null
            if (usuarioRepositorio != null) {
                // Verificar si el usuario es un administrador
                if (usuario.getRol() != null && usuario.getRol().equals(Rol.ADMIN)) {
                    // Si es administrador, registrar la fecha de baja en lugar de eliminarlo
                    usuario.setFechaBaja(LocalDate.now());
                    usuario.setBaja(true);
                    usuario.setMotivoBaja(motivo);
                } else {
                    // Si no es administrador, dar de baja al usuario sin eliminar físicamente
                    usuario.setBaja(true);
                    usuario.setFechaBaja(LocalDate.now());
                    usuario.setMotivoBaja(motivo);
                    usuarioRepositorio.save(usuario); // Actualizar en lugar de eliminar
                }
            } else {
                throw new MiException("UsuarioRepositorio no inicializado");
            }
        } else {
            throw new MiException("Usuario no encontrado");
        }
    }

    public List<Usuario> listaUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        for (Usuario usuario : usuarioRepositorio.findAll()) {
            if (usuario.getFechaBaja() != null) {
                usuarios.add(usuario);
            }
        }
        return usuarios;
    }

    private void validar(String nombre, String apellido, String email, String telefono, String password) throws MiException {

        if (nombre.isEmpty() || nombre == null) {
            throw new MiException("el nombre no puede ser nulo o estar vacío");
        }
        if (apellido.isEmpty() || apellido == null) {
            throw new MiException("el apellido no puede ser nulo o estar vacío");
        }

        if (email.isEmpty() || email == null) {
            throw new MiException("el email no puede ser nulo o estar vacio");
        }

        if (telefono.isEmpty()) {
            throw new MiException("el telefono no puede estar vacio");
        }
        if (password.isEmpty() || password == null || password.length() <= 5) {
            throw new MiException("La contraseña no puede estar vacía, y debe tener más de 5 dígitos");
        }

    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Usuario usuario = usuarioRepositorio.buscarPorEmail(email);

        if (usuario != null) {

            List<GrantedAuthority> permisos = new ArrayList();
            GrantedAuthority p = new SimpleGrantedAuthority("ROLE_" + usuario.getRol().toString());
            permisos.add(p);
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession(true);
            session.setAttribute("usuariosession", usuario);

            return new User(usuario.getEmail(), usuario.getPassword(), permisos);
        } else {
            return null;
        }

    }

    @Transactional
    public void modificarFotoPerfil(String id, MultipartFile imagen) throws MiException {
        Optional<Proveedor> respuesta = proveedorRepositorio.findById(id); // Manejar la excepción, puedes lanzar una nueva excepción personalizada si es necesario.
        if (respuesta.isPresent()) {
            Proveedor usuario = respuesta.get();

            // Puedes agregar lógica para validar y procesar la imagen antes de guardarla.
            // Asegúrate de validar que la imagen no sea nula y tiene contenido.
            String idImagen = null;

            if (usuario.getImagen() != null) {
                idImagen = usuario.getImagen().getId();
            }

            Imagen nuevaImagen = imagenServicio.actualizar(imagen, idImagen);
            usuario.setImagen(nuevaImagen);

            proveedorRepositorio.save(usuario);
        } else {
            throw new MiException("Usuario no encontrado");
        }
    }
}
