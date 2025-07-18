import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JSONManager {
    private static final String FILE_NAME = "alumnos.json";
    private List<Alumno> alumnos;
    private Gson gson;

    public JSONManager() {
        gson = new GsonBuilder().setPrettyPrinting().create();
        alumnos = cargarAlumnos();
    }
    
    public void eliminarAlumno(String nombre) {
        alumnos.removeIf(alumno -> alumno.getNombre().equalsIgnoreCase(nombre));
        guardarAlumnos(); // Guardar los cambios en JSON
        System.out.println("Alumno eliminado: " + nombre);
    }

    private List<Alumno> cargarAlumnos() {
        try (Reader reader = new FileReader(FILE_NAME)) {
            Type listType = new TypeToken<ArrayList<Alumno>>() {}.getType();
            return gson.fromJson(reader, listType);
        } catch (FileNotFoundException e) {
            return new ArrayList<>(); // Si no hay archivo, devuelve una lista vacía
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void guardarAlumnos() {
        try (Writer writer = new FileWriter(FILE_NAME)) {
            gson.toJson(alumnos, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void agregarAlumno(String nombre, String curso) {
        alumnos.add(new Alumno(nombre, curso));
        guardarAlumnos();
        System.out.println("Alumno agregado correctamente.");
    }

    public void mostrarAlumnos() {
        if (alumnos.isEmpty()) {
            System.out.println("No hay alumnos registrados.");
            return;
        }
        for (Alumno alumno : alumnos) {
            System.out.println(alumno);
        }
    }

    public Alumno buscarAlumno(String nombre) {
        for (Alumno alumno : alumnos) {
            if (alumno.getNombre().equalsIgnoreCase(nombre)) {
                return alumno;
            }
        }
        return null;
    }

    public void guardarResultadoLectura(String nombre, String resultado) {
        Alumno alumno = buscarAlumno(nombre);
        if (alumno != null) {
            // Extraer solo el PPM del resultado original
            String[] partes = resultado.split("\\|");
            String ppm = partes[0].trim(); // "PPM: X"

            // Obtener la fecha actual
            String fecha = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
            String nuevoResultado = fecha + " - " + ppm;

            // Si ya existe un test en la misma fecha, se reemplaza con el nuevo
            List<String> resultados = alumno.getResultadosLectura();
            resultados.removeIf(r -> r.startsWith(fecha)); // Elimina registros previos de la misma fecha
            resultados.add(nuevoResultado); // Agrega el nuevo resultado

            guardarAlumnos();
            System.out.println("Resultado actualizado correctamente.");
        } else {
            System.out.println("Alumno no encontrado.");
        }
    }
    public List<Alumno> getAlumnos() {
        return alumnos;
    }
}
