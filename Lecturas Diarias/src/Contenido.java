public class Contenido {
    public String lectura = "";
    public Map<String, List<String>> actividades = new LinkedHashMap<>();
    
    public static Contenido cargarDesdeTXT(File archivo) throws IOException {
        Contenido contenido = new Contenido();
        BufferedReader br = new BufferedReader(new FileReader(archivo));
        StringBuilder sb = new StringBuilder();
        String linea;
        boolean enLectura = true;
        String diaActual = "";

        while ((linea = br.readLine()) != null) {
            if (linea.startsWith("________________________________________")) {
                enLectura = false;
                continue;
            }

            if (enLectura) {
                sb.append(linea).append("\n");
            } else {
                if (linea.matches("^[A-Z][a-z]+:.*")) { // "Lunes:", etc.
                    diaActual = linea.split(":")[0];
                    contenido.actividades.put(diaActual, new ArrayList<>());
                } else if (!linea.trim().isEmpty() && !diaActual.isEmpty()) {
                    contenido.actividades.get(diaActual).add(linea.trim());
                }
            }
        }

        contenido.lectura = sb.toString().trim();
        return contenido;
    }
}
