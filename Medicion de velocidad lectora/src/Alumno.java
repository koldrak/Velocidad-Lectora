import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Alumno implements Serializable {
    private String nombre;
    private String curso;
    private List<String> resultadosLectura; // Lista de resultados

    // ✅ Constructor vacío requerido por Gson
    public Alumno() {
        this.resultadosLectura = new ArrayList<>();
    }
    
    public Alumno(String nombre, String curso) {
        this.nombre = nombre;
        this.curso = curso;
        this.resultadosLectura = new ArrayList<>();
    }

    public String getNombre() {
        return nombre;
    }

    public String getCurso() {
        return curso;
    }

    public List<String> getResultadosLectura() {
        return resultadosLectura;
    }

    public void agregarResultado(String resultado) {
        resultadosLectura.add(resultado);
    }

    @Override
    public String toString() {
        return "Alumno: " + nombre + " | Curso: " + curso + " | Test Lectura: " + resultadosLectura;
    }
}
