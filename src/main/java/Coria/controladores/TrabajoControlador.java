package Coria.controladores;

import Coria.entidad.Proveedor;
import Coria.entidad.Trabajo;
import Coria.excepciones.MiException;
import Coria.servicios.ProveedorServicio;
import Coria.servicios.TrabajoServicio;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/trabajo")
public class TrabajoControlador {

    @Autowired
    private TrabajoServicio trabServ;

    @Autowired
    private ProveedorServicio proServ;

    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @GetMapping("/contratar/{id}")
    public String registrar(@PathVariable String id, ModelMap modelo) {

        try {
            Proveedor pro = proServ.getOne(id);
            modelo.put("proveedor", pro);
        } catch (Exception e) {
            modelo.put("error", e.getMessage());
        }

        return "contratar.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @PostMapping("/registro")
    public String registro(@RequestParam(required = false) String idUsuario, String idProveedor,
            String nombre, String apellido, String telefono, String descripcion,
            String estado, Integer calificacion, String comentario, ModelMap modelo) {

        try {
            trabServ.registrarTrabajo(idUsuario, idProveedor, nombre, apellido,
                    telefono, descripcion, estado, calificacion, comentario);

            modelo.put("mensaje", "¡Trabajo solicitado con éxito!");

        } catch (MiException e) {
            modelo.put("error", e.getMessage());
            return "contratar";
        }

        return "informacion.html";
    }

    @GetMapping("/listaTrabajo/{id}")
    public String listarTrabajos(@PathVariable String id, ModelMap modelo) {
        List<Trabajo> trabajos = trabServ.listarTrabajos();
        modelo.addAttribute("trabajos", trabajos);

        return "ordenTrabajo";
    }

    @GetMapping("/listaTrabajoUsuario/{id}")
    public String listaTrabajoUsuario(@PathVariable String id, ModelMap modelo) {
        List<Trabajo> trabajos = trabServ.listarTrabajosPorIdUsuario(id);
        modelo.addAttribute("trabajos", trabajos);

        return "ordenTrabajoUsuario";
    }

    @GetMapping("/listaTrabajoProveedor/{id}")
    public String listaTrabajoProveedor(@PathVariable String id, ModelMap modelo) {
        List<Trabajo> trabajos = trabServ.listarTrabajosPorIdProveedor(id);
        modelo.addAttribute("trabajos", trabajos);

        return "ordenTrabajoProveedor";
    }

    @GetMapping("/calificar/{id}")
    public String calificar(@PathVariable String id, ModelMap modelo) {

        try {
            Trabajo trabajo = trabServ.getOne(id);
            String estado = trabajo.getEstado().toLowerCase();

            if (estado.equals("finalizado") || estado.equals("cancelado")) {
                modelo.addAttribute("trabajo", trabajo);
                return "calificarTrabajo.html";
            } else {
                modelo.put("error", "Aguarde a que su trabajo esté finalizado o cancelado para poder calificar.");
            }

        } catch (Exception e) {
            modelo.put("error", e.getMessage());
        }
        return "informacion";
    }

    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    @PostMapping("/modificar/{id}")
    public String calificado(@PathVariable String id,
            String estado, Integer calificacion, String comentario,
            ModelMap modelo) {

        try {
            trabServ.modificar(id, calificacion, comentario);
            modelo.put("mensaje", "Calificación exitosa");

            proServ.calificarProveedor(id, calificacion);
            return "redirect:/informacion";
        } catch (MiException e) {

            modelo.put("exito", "Calificacion exitosa");
            return "informacion";
        }

    }

    @GetMapping("/estado/{id}")
    public String estado(@PathVariable String id, ModelMap modelo) {

        try {
            Trabajo trabajo = trabServ.getOne(id);
            modelo.addAttribute("trabajo", trabajo);

        } catch (Exception e) {
            modelo.put("error", e.getMessage());
        }

        return "cambioEstado";
    }

    @PostMapping("/cambiar/{id}")
    public String cambiarEstado(@PathVariable String id,
            String estado,
            RedirectAttributes redirectAttributes) {

        try {
            trabServ.cambiarEstado(id, estado);
            redirectAttributes.addFlashAttribute("mensaje", "Estado cambiado con Éxito");
        } catch (MiException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/informacion";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/eliminarComentario/{id}")
    public String cambiarEstado(@PathVariable String id,
            ModelMap modelo) {

        try {
            trabServ.eliminarComentario(id);
            modelo.put("exito", "Comentario eliminado");
            return "redirect:/trabajo/listaTrabajo/{id}";
        } catch (MiException e) {

            modelo.put("error", e.getMessage());
            return "listaTrabajosAdmin";
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable String id, ModelMap modelo) {
        try {
            trabServ.eliminarTrabajo(id);
            modelo.put("exito", "Trabajo eliminado");
            return "redirect:/trabajo/listaTrabajo/{id}";
        } catch (MiException e) {
            System.out.println("Error al eliminar");
            modelo.put("error", e.getMessage());

        }
        return "listaTrabajosAdmin";
    }

}
