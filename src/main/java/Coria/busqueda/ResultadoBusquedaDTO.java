package Coria.busqueda;

public class ResultadoBusquedaDTO {

    private String id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String tipoEntidad; // Puede ser "Usuario" o "Proveedor"
    private String nombreEmpresa; // Solo para proveedores
    private Double calificacionPromedio;
    private Integer numeroCalificaciones;
    private String idOficio;
    private String nombreOficio;
    private String comentarioOficio;

    // Constructor, getters y setters
    public ResultadoBusquedaDTO() {
    }

    public ResultadoBusquedaDTO(String id, String nombre, String apellido, String email, String telefono, String tipoEntidad, String nombreEmpresa, Double calificacionPromedio, Integer numeroCalificaciones, String idOficio, String nombreOficio, String comentarioOficio) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.telefono = telefono;
        this.tipoEntidad = tipoEntidad;
        this.nombreEmpresa = nombreEmpresa;
        this.calificacionPromedio = calificacionPromedio;
        this.numeroCalificaciones = numeroCalificaciones;
        this.idOficio = idOficio;
        this.nombreOficio = nombreOficio;
        this.comentarioOficio = comentarioOficio;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getTipoEntidad() {
        return tipoEntidad;
    }

    public void setTipoEntidad(String tipoEntidad) {
        this.tipoEntidad = tipoEntidad;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public Double getCalificacionPromedio() {
        return calificacionPromedio;
    }

    public void setCalificacionPromedio(Double calificacionPromedio) {
        this.calificacionPromedio = calificacionPromedio;
    }

    public Integer getNumeroCalificaciones() {
        return numeroCalificaciones;
    }

    public void setNumeroCalificaciones(Integer numeroCalificaciones) {
        this.numeroCalificaciones = numeroCalificaciones;
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
