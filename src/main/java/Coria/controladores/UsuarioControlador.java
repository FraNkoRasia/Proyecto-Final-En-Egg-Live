package Coria.controladores;

import Coria.Enumeracion.Rol;
import Coria.entidad.Proveedor;
import Coria.entidad.Usuario;
import Coria.excepciones.MiException;
import Coria.repositorios.UsuarioRepositorio;
import Coria.servicios.ProveedorServicio;
import Coria.servicios.UsuarioServicio;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/") //localhost:8080
public class UsuarioControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private ProveedorServicio provServ;

    @GetMapping("/")//localhost:8080
    public String index(ModelMap modelo, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuariosession");
        if (usuario != null) {
            modelo.put("usuario", usuario);//usuarioServicio.getOne(usuario.getId())
        }
        return "index.html";

    }

    ///////////////////////////
    //RECUPERACION DE PASSWORD
    @GetMapping("/forgot-password")
    public String showForgotPasswordPage() {
        return "forgot-password"; // Renderiza la vista para solicitar el restablecimiento de contraseña
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, RedirectAttributes redirectAttributes) {
        try {
            usuarioServicio.generarTokenYEnviarCorreo(email);
            redirectAttributes.addFlashAttribute("mensaje", "Se ha enviado un correo electrónico para restablecer la contraseña.");
        } catch (MiException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/login"; // Redirige de vuelta a la página de inicio de sesión
    }

    @GetMapping("/reset-password")
    public String showResetPasswordPage(@RequestParam String token, Model model, RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuarioOptional = usuarioRepositorio.findByResetToken(token);
        if (usuarioOptional.isPresent()) {
            model.addAttribute("token", token);
            return "reset-password"; // Renderiza la vista para cambiar la contraseña
        } else {
            redirectAttributes.addFlashAttribute("error", "Token inválido para restablecer la contraseña.");
            return "redirect:/login"; // O muestra un mensaje de error y redirige a la página de inicio de sesión
        }
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token, @RequestParam String newPassword, RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuarioOptional = usuarioRepositorio.findByResetToken(token);
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            try {
                usuario.setPassword(new BCryptPasswordEncoder().encode(newPassword));
                usuario.setResetToken(null); // Borra el token después de cambiar la contraseña
                usuarioRepositorio.save(usuario);
                redirectAttributes.addFlashAttribute("mensaje", "Contraseña restablecida con éxito. Inicia sesión con tu nueva contraseña.");
                return "redirect:/"; // Redirige a la página de inicio de sesión
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Hubo un error al restablecer la contraseña. Inténtalo de nuevo más tarde.");
                return "redirect:/"; // O muestra un mensaje de error y redirige a la página de inicio de sesión
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Token inválido para restablecer la contraseña.");
            return "redirect:/"; // O muestra un mensaje de error y redirige a la página de inicio de sesión
        }
    }
    //FIN DE RECUPERACION DE PASSWORD
    //////////////////////////////////

    @GetMapping("/registrarP")
    public String registrarProveedor(Model model) {
        model.addAttribute("proveedor", new Proveedor());
        model.addAttribute("rol", Rol.USER); // Puedes establecer el rol predeterminado según tus necesidades
        model.addAttribute("nombreEmpresa", ""); // O el valor por defecto
        model.addAttribute("tipoServicio", "Gasista"); // O el valor por defecto
        return "registroP";
    }

    @PostMapping("/registroP")
    public String registro(@RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String telefono,
            @RequestParam Rol rol,
            @RequestParam(required = false) String tipoServicio,
            @RequestParam(required = false) String nombreEmpresa,
            @RequestParam(required = false) MultipartFile archivo,
            RedirectAttributes redirectAttributes) throws MiException {
        try {
            usuarioServicio.registrar(nombre, archivo, apellido, email, telefono, password, nombreEmpresa, tipoServicio, rol);;
            redirectAttributes.addFlashAttribute("mensaje", "Registo completado con Exito");
            return "redirect:/";
        } catch (MiException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/";
        }
    }

    //CORRESPONDE AL ADMIN
    @GetMapping("/lista")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String listarUsuarios(ModelMap modelo) {
        List<Usuario> listaUsuarios = usuarioServicio.listarUsuarios();
        modelo.addAttribute("listaUsuarios", listaUsuarios);
        return "listaUsuarios.html";
    }
    //CORRESPONDE AL ADMIN

    @PostMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable String id, ModelMap model) {
        try {
            usuarioServicio.eliminarUsuario(id);
            return "redirect:../lista";
        } catch (Exception e) {
            model.put("error", e.getMessage());
        }
        return "redirect:../lista";
    }

    @GetMapping("/modificar/{id}")
    public String mostrarFormularioModificarContrasena(ModelMap modelo) {
        // Lógica para cargar datos necesarios, si es necesario
        modelo.addAttribute("mensaje", "¡Bienvenido al formulario de modificación de contraseña!");
        return "modificar";
    }

    @PostMapping("/modificar/{id}")
    public String modificar(@PathVariable String id,
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String email,
            @RequestParam String telefono,
            @RequestParam(required = false) String tipoServicio,
            @RequestParam(required = false) String nombreEmpresa,
            @RequestParam String currentPassword,
            RedirectAttributes redirectAttributes) throws MiException {
        try {
            usuarioServicio.modificarUsuario(id, nombre, apellido, email, telefono, currentPassword, tipoServicio, nombreEmpresa);
            redirectAttributes.addFlashAttribute("mensaje", "Usuario modificado Correctamente");
            return "redirect:/perfil/{id}";
        } catch (MiException ex) {
            System.out.println(ex.getMessage());
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/perfil/{id}";
        }
    }

    @PostMapping("/modificarFotoPerfil/{id}")
    public String modificarFotoPerfil(@PathVariable String id,
            @RequestParam("imagen") MultipartFile imagen,
            RedirectAttributes redirectAttributes) throws MiException {
        try {
            // Verificar el tamaño del archivo
            if (imagen.getSize() > 5 * 1024 * 1024) { // 5MB en bytes
                throw new MiException("El tamaño de la imagen excede el límite de 5MB.");
            }

            usuarioServicio.modificarFotoPerfil(id, imagen);
            redirectAttributes.addFlashAttribute("mensaje", "Foto de perfil modificada correctamente");
            return "redirect:/perfil/{id}";
        } catch (MiException ex) {
            System.out.println(ex.getMessage());
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/perfil/{id}";
        }
    }

    @GetMapping("/modificar1/{id}")
    public String mostrarFormularioModificarContrasena1(ModelMap modelo) {
        // Lógica para cargar datos necesarios, si es necesario
        modelo.addAttribute("mensaje", "¡Bienvenido al formulario de modificación de contraseña!");
        return "modificar1";
    }

    @PostMapping("/modificar1/{id}")
    public String modificar1(
            @PathVariable String id,
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String newPassword2,
            RedirectAttributes redirectAttributes
    ) throws MiException {
        try {
            if (!newPassword.equals(newPassword2)) {
                throw new MiException("Las contraseñas no coinciden");
            }

            usuarioServicio.actualizarPassword(id, currentPassword, newPassword);
            redirectAttributes.addFlashAttribute("mensaje", "Contraseña modificada correctamente");
            return "redirect:../perfil1/{id}";
        } catch (MiException ex) {
            System.out.println(ex.getMessage());
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:../perfil1/{id}";
        }
    }

    @GetMapping("/modificarAdmin/{id}")
    public String modificarAdmin(ModelMap modelo) {
        // Lógica para cargar datos necesarios, si es necesario
        modelo.addAttribute("mensaje", "¡Bienvenido al formulario de modificación de contraseña!");
        return "modificarAdmin";
    }

    @PostMapping("/modificarAdmin/{id}")
    public String modificarAdmin(
            @PathVariable String id,
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String email,
            @RequestParam String telefono,
            @RequestParam Rol nuevoRol,
            RedirectAttributes redirectAttributes
    ) throws MiException {
        try {
            usuarioServicio.AdministradorModifica(id, nombre, apellido, email, telefono, nuevoRol);
            redirectAttributes.addFlashAttribute("mensaje", "Usuario modificado Correctamente");
            return "redirect:../perfilAdmin/{id}";
        } catch (MiException ex) {
            System.out.println(ex.getMessage());
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:../perfilAdmin/{id}";
        }
    }

    @GetMapping("/registrar")//localhost:8080/registrar
    public String registrar() {
        return "registro.html";
    }

    @GetMapping("/login")//localhost:8080/login
    public String login(@RequestParam(required = false) String error, ModelMap modelo) {
        if (error != null) {
            modelo.put("error", "Usuario o Contraseña Invalidos!");
        }
        return "login.html";
    }

    @GetMapping("/perfil/{id}")
    public String mostrarFormulario(@PathVariable String id, ModelMap modelo) {
        // Lógica para obtener el usuario por ID y agregarlo al modelo
        Usuario usuario = usuarioServicio.getOne(id);
        modelo.addAttribute("usuario", usuario);
        return "perfil_usuario.html";
    }

    @PostMapping("/perfil/{id}")
    public String actualizar(@PathVariable String id, @RequestParam String nombre, @RequestParam String apellido,
            @RequestParam String email, @RequestParam String password, @RequestParam String telefono, MultipartFile archivo, ModelMap modelo,
            HttpSession session) throws Exception {
        try {
            System.out.println("Controlador de perfil ejecutado. ID: " + id);

            Usuario usuario = usuarioServicio.actualizar(archivo, id, nombre, apellido, email, telefono, password);
            session.setAttribute("usuariosession", usuario);
            return "redirect:/";
        } catch (MiException ex) {
            System.out.println("Error en el controlador de perfil: " + ex.getMessage());
            modelo.put("error", ex.getMessage());
            ex.printStackTrace();
            Usuario usuario = usuarioServicio.getOne(id);
            modelo.put("usuario", usuario);
            return "redirect:/perfil/{id}";
        }
    }

    @GetMapping("/perfil1/{id}")
    public String mostrarFormulario1(@PathVariable String id, ModelMap modelo) {
        // Lógica para obtener el usuario por ID y agregarlo al modelo
        Usuario usuario = usuarioServicio.getOne(id);
        modelo.addAttribute("usuario", usuario);
        return "perfil_usuario1.html";
    }

    @GetMapping("/cambiarRol/{id}")
    public String mostrarFormularioCambiarRol(@PathVariable String id, ModelMap modelo) {
        Usuario usuario = usuarioServicio.getOne(id);
        modelo.addAttribute("usuario", usuario);
        modelo.addAttribute("roles", Rol.values()); // Asumiendo que Rol es un enum con los posibles roles

        return "rol.html";
    }

    @PostMapping("/modificarRol/{id}")
    public String modificarRolYTipoServicio(@PathVariable String id,
            @RequestParam String nuevoRol,
            @RequestParam String tipoServicio,
            @RequestParam String nombreEmpresa,
            RedirectAttributes redirectAttributes) throws MiException {

        usuarioServicio.modificarRolYTipoServicio(id, nuevoRol, tipoServicio, nombreEmpresa);
        redirectAttributes.addFlashAttribute("mensaje", "Rol cambiado exitosamente");
        return "redirect:/cambiarRol/{id}";
    }

    @PostMapping("/perfil1/{id}")
    public String actualizarContraseña(@PathVariable String id, @RequestParam String password, ModelMap modelo,
            HttpSession session) throws Exception {
        try {
            System.out.println("Controlador de perfil ejecutado. ID: " + id);

            Usuario usuario = usuarioServicio.actualizarPassword(id, password, password);
            session.setAttribute("usuariosession", usuario);
            return "redirect:/";
        } catch (MiException ex) {
            System.out.println("Error en el controlador de perfil: " + ex.getMessage());
            modelo.put("error", ex.getMessage());
            ex.printStackTrace();
            Usuario usuario = usuarioServicio.getOne(id);
            modelo.put("usuario", usuario);
            return "redirect:/perfil1/{id}";
        }
    }

    @GetMapping("/perfilAdmin/{id}")
    public String mostrarFormularioAdmin(@PathVariable String id, ModelMap modelo) {
        // Lógica para obtener el usuario por ID y agregarlo al modelo
        Usuario usuario = usuarioServicio.getOne(id);
        modelo.addAttribute("usuario", usuario);
        return "adminModifica.html";
    }

    @PostMapping("/perfilAdmin/{id}")
    public String mostrarFormularioAdmin(@PathVariable String id, @RequestParam String password, ModelMap modelo,
            HttpSession session) throws Exception {
        try {
            System.out.println("Controlador de perfil ejecutado. ID: " + id);

            Usuario usuario = usuarioServicio.actualizarPassword(id, password, password);
            session.setAttribute("usuariosession", usuario);
            return "redirect:/";
        } catch (MiException ex) {
            System.out.println("Error en el controlador de perfil: " + ex.getMessage());
            modelo.put("error", ex.getMessage());
            ex.printStackTrace();
            Usuario usuario = usuarioServicio.getOne(id);
            modelo.put("usuario", usuario);
            return "redirect:/adminModifica/{id}";
        }
    }

    @GetMapping("/informacion") // Ruta modificada para evitar ambigüedad
    public String obtenerInformacion(ModelMap modelo, HttpSession session) {
        // Lógica para obtener información del usuario
        Usuario usuario = (Usuario) session.getAttribute("usuariosession");

        // Verificar si el usuario está autenticado
        if (usuario != null) {
            // Aquí puedes acceder a los atributos del usuario y agregarlos al modelo
            modelo.addAttribute("nombre", usuario.getNombre());
            modelo.addAttribute("apellido", usuario.getApellido());
            modelo.addAttribute("email", usuario.getEmail());
            modelo.addAttribute("telefono", usuario.getTelefono());
            // ... y otros atributos que desees mostrar en la página

            return "informacion.html"; // Nombre de la vista (puede ser "informacion.html" en tu caso)
        } else {
            // Manejar el caso en el que el usuario no esté autenticado
            // Puedes redirigirlo a una página de inicio de sesión, por ejemplo
            modelo.addAttribute("mensaje", "Debes iniciar sesión para ver esta información.");

            return "redirect:/login";
        }
    }

    @GetMapping("/contacto")
    public String mostrarFormularioContacto(ModelMap modelo, HttpSession session) {
        // Verificar si hay un usuario autenticado
        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuariosession");

        // Si hay un usuario autenticado, prellenar el formulario con su información
        if (usuarioAutenticado != null) {
            modelo.addAttribute("nombre", usuarioAutenticado.getNombre());
            modelo.addAttribute("email", usuarioAutenticado.getEmail());
            modelo.addAttribute("telefono", usuarioAutenticado.getTelefono());
        }

        // Puedes agregar lógica adicional aquí si es necesario
        return "contacto.html";
    }

    @PostMapping("/darDeBaja")
    public String darDeBaja(@RequestParam String motivo, @RequestParam String id) {
        // Lógica para dar de baja aquí
        try {
            usuarioServicio.darDeBaja(id, motivo);
            return "redirect:/login?bajaExitosa=true";
        } catch (MiException e) {
            // Manejo de excepciones si es necesario
            return "redirect:/perfil_usuario?error=true";
        }
    }
}

//@RequestParam("archivo") MultipartFile archivo,
