package Coria.controladores;

import Coria.busqueda.ResultadoBusquedaDTO;
import Coria.servicios.BusquedaServicio;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/resultados")
public class BusquedaControlador {

    @Autowired
    private BusquedaServicio busquedaServicio;

    // Maneja las solicitudes GET a la ruta "/resultados"
    @GetMapping
    public String buscar(@RequestParam(name = "terminoBusqueda") String terminoBusqueda, Model model) {
        // Realiza la búsqueda con el término ingresado por el usuario
        List<ResultadoBusquedaDTO> resultados = busquedaServicio.buscar(terminoBusqueda);

        // Agrega los resultados al modelo para ser mostrados en la página
        model.addAttribute("resultados", resultados);

        // Devuelve el nombre de la página de resultados
        return "resultados.html";
    }
}

