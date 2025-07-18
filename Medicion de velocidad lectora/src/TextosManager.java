import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

public class TextosManager {
	private static final String FILE_NAME = System.getProperty("user.dir") + "/textos.json";
    private List<Texto> textos;
    private Gson gson;

    public TextosManager() {
        gson = new GsonBuilder().setPrettyPrinting().create();
        textos = cargarTextos();
    }

    // Clase interna para representar un texto con título y contenido
    public static class Texto {
        private String titulo;
        private String contenido;

        // ✅ Constructor vacío requerido por Gson
        public Texto() {
        }
        
        public Texto(String titulo, String contenido) {
            this.titulo = titulo;
            this.contenido = contenido;
        }

        public String getTitulo() {
            return titulo;
        }

        public String getContenido() {
            return contenido;
        }

        @Override
        public String toString() {
            return titulo; // Se mostrará en el JComboBox solo el título
        }
    }

    private List<Texto> cargarTextos() {
        try (Reader reader = new FileReader(FILE_NAME)) {
            Type listType = new TypeToken<ArrayList<Texto>>() {}.getType();
            List<Texto> textosCargados = gson.fromJson(reader, listType);
            
            // Si el archivo estaba vacío o no tenía contenido válido, inicializamos una lista vacía
            return textosCargados != null ? textosCargados : new ArrayList<>();
            
        } catch (FileNotFoundException e) {
            System.out.println("Archivo de textos no encontrado. Creando uno nuevo.");
            return new ArrayList<>(); // Si el archivo no existe, devuelve una lista vacía
        } catch (JsonSyntaxException e) {
            System.err.println("Error en el formato del JSON. Revisar el archivo textos.json");
            e.printStackTrace();
            return new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Guardar textos en JSON
    private void guardarTextos() {
        try (Writer writer = new FileWriter(FILE_NAME)) {
            gson.toJson(textos, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Agregar un nuevo texto con título y contenido
    public void agregarTexto(String titulo, String contenido) {
        textos.add(new Texto(titulo, contenido));
        guardarTextos();
        System.out.println("Texto agregado correctamente.");
    }

    public List<String> getTitulos() {
        // Ordenar los textos por cantidad de palabras
        textos.sort(Comparator.comparingInt(texto -> contarPalabras(texto.getContenido())));

        List<String> titulos = new ArrayList<>();
        for (Texto texto : textos) {
            int cantidadPalabras = contarPalabras(texto.getContenido());
            titulos.add(texto.getTitulo() + " (" + cantidadPalabras + " palabras)");
        }
        return titulos;
    }

    // Método auxiliar para contar palabras en un texto
    private int contarPalabras(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return 0;
        }
        texto = texto.replace(".", " . ").replace(",", " , ");
        return texto.trim().split("\\s+").length;
    }


    // Obtener el contenido de un texto dado su título
    public String getContenidoPorTitulo(String titulo) {
        for (Texto texto : textos) {
            if (texto.getTitulo().equals(titulo)) {
                return texto.getContenido();
            }
        }
        return "";
    }
    public void eliminarTexto(String titulo) {
        boolean eliminado = textos.removeIf(texto -> texto.getTitulo().equalsIgnoreCase(titulo));
        if (eliminado) {
            guardarTextos();
            System.out.println("Texto eliminado correctamente.");
        } else {
            System.out.println("No se encontró el texto a eliminar.");
        }
    }


}

