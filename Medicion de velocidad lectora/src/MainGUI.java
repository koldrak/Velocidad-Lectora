import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Comparator;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;

public class MainGUI extends JFrame {
    private JSONManager db;
    private TextosManager textosManager;
    private JDialog dialogAlumnos; // Variable global para almacenar la ventana de la lista
    
    public MainGUI() {
        db = new JSONManager();
        textosManager = new TextosManager();
        setTitle("Gestión de Alumnos - Lectura");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 1));

        JButton btnMostrar = new JButton("Gestionar Alumnos");
        JButton btnTestLectura = new JButton("Realizar Test de Lectura");
        JButton btnGestionarTextos = new JButton("Gestionar Textos");
        JButton btnSalir = new JButton("Salir");

        btnMostrar.addActionListener(e -> mostrarAlumnos());
        btnTestLectura.addActionListener(e -> realizarTest());
        btnGestionarTextos.addActionListener(e -> agregarTexto());
        btnSalir.addActionListener(e -> System.exit(0));

        add(btnMostrar);
        add(btnTestLectura);
        add(btnGestionarTextos);
        add(btnSalir);

        setVisible(true);
    }

    private void agregarAlumno() {
        String nombre = JOptionPane.showInputDialog(this, "Nombre del alumno:");
        String curso = JOptionPane.showInputDialog(this, "Curso:");
        if (nombre != null && curso != null && !nombre.isEmpty() && !curso.isEmpty()) {
            db.agregarAlumno(nombre, curso);
            JOptionPane.showMessageDialog(this, "Alumno agregado exitosamente.");
        }
    }

    private void mostrarAlumnos() {
        actualizarListaAlumnos(); // 🔄 Se actualiza la lista antes de mostrarla
    }
    
    private void actualizarListaAlumnos() {
        List<Alumno> alumnos = db.getAlumnos();

        // Cerrar la ventana anterior si ya está abierta
        if (dialogAlumnos != null && dialogAlumnos.isVisible()) {
            dialogAlumnos.dispose();
        }

        // Ordenar alumnos por curso y luego por nombre
        alumnos.sort(Comparator.comparing(Alumno::getCurso).thenComparing(Alumno::getNombre));

        // Obtener la cantidad máxima de evaluaciones para definir columnas dinámicas
        int maxEvaluaciones = alumnos.stream()
                .mapToInt(a -> a.getResultadosLectura().size())
                .max().orElse(0);

        // Definir nombres de columnas
        String[] columnNames = new String[1 + maxEvaluaciones];
        columnNames[0] = "Alumno (Curso)";
        for (int i = 0; i < maxEvaluaciones; i++) {
            columnNames[i + 1] = "Evaluación " + (i + 1);
        }

        // Crear los datos para la tabla
        Object[][] data = new Object[alumnos.size()][1 + maxEvaluaciones];
        for (int i = 0; i < alumnos.size(); i++) {
            Alumno alumno = alumnos.get(i);
            data[i][0] = alumno.getCurso() + " " + alumno.getNombre();
            List<String> resultados = alumno.getResultadosLectura();
            for (int j = 0; j < resultados.size(); j++) {
                data[i][j + 1] = resultados.get(j);
            }
        }

        // Crear la tabla
        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);

        // 🔹 Fuente inicial
        Font fuenteTabla = new Font("Arial", Font.PLAIN, 14);
        table.setFont(fuenteTabla);
        
     // 🔹 Agregar renderizador de celdas para colorear los resultados según la tabla de referencia
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (column > 0 && value instanceof String) { // Solo afecta las columnas de evaluaciones
                    String resultado = (String) value;
                    String[] partes = resultado.split("PPM: ");

                    if (partes.length > 1) {
                        try {
                            int ppm = Integer.parseInt(partes[1].trim().split(" ")[0]); // Extraer solo el número de PPM
                            
                            // Extraer correctamente el curso desde la columna 0
                            String alumnoInfo = (String) table.getValueAt(row, 0); 
                            String curso = alumnoInfo.split(" ")[0]; // Extrae solo el primer valor (curso)

                            Color color = obtenerColorPorCurso(ppm, curso);
                            cell.setBackground(color);

                        } catch (NumberFormatException e) {
                            cell.setBackground(Color.WHITE);
                        }
                    } else {
                        cell.setBackground(Color.WHITE);
                    }
                } else {
                    cell.setBackground(Color.WHITE);
                }
                return cell;
            }
        });
     // Crear un panel para los botones
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new GridLayout(1, 4)); // 🔹 4 columnas para que estén en la misma línea

        JButton btnAgregar = new JButton("Agregar Alumno");
        JButton btnEliminar = new JButton("Eliminar Alumno");
        JButton btnAumentarFuente = new JButton("Aumentar Fuente");
        JButton btnDisminuirFuente = new JButton("Disminuir Fuente");

        // Acción del botón "Agregar Alumno"
        btnAgregar.addActionListener(e -> {
            agregarAlumno();
            actualizarListaAlumnos(); // 🔄 Refresca la lista después de agregar
        });

        // Acción del botón "Eliminar Alumno"
        btnEliminar.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String alumnoSeleccionado = (String) table.getValueAt(selectedRow, 0);
                String nombreAlumno = alumnoSeleccionado.substring(alumnoSeleccionado.indexOf(" ") + 1);
                eliminarAlumno(nombreAlumno);
                actualizarListaAlumnos(); // 🔄 Refresca la lista después de eliminar
            } else {
                JOptionPane.showMessageDialog(null, "Por favor, selecciona un alumno para eliminar.");
            }
        });

        // Acción de cambiar el tamaño de fuente
        btnAumentarFuente.addActionListener(e -> cambiarTamanoFuente(table, +2));
        btnDisminuirFuente.addActionListener(e -> cambiarTamanoFuente(table, -2));

        // Agregar botones al panel en la misma fila
        panelBotones.add(btnAgregar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnAumentarFuente);
        panelBotones.add(btnDisminuirFuente);

        // Crear el diálogo con los alumnos y agregar los botones
        dialogAlumnos = new JDialog(this, "Lista de Alumnos", true);
        dialogAlumnos.setLayout(new BorderLayout());
        dialogAlumnos.add(scrollPane, BorderLayout.CENTER);
        dialogAlumnos.add(panelBotones, BorderLayout.SOUTH); // 🔹 Todos los botones están juntos en el mismo panel
        dialogAlumnos.setSize(700, 400);  // 🔹 Ajusté el tamaño para más espacio
        dialogAlumnos.setLocationRelativeTo(this);
        dialogAlumnos.setVisible(true);
    }

 // Método para determinar el color según el curso y la tabla de referencia
    private Color obtenerColorPorCurso(int ppm, String curso) {
        if (curso.matches("^1.*")) { // 1° Básico
            if (ppm <= 21) return Color.RED;
            if (ppm >= 22 && ppm <= 28) return Color.RED;
            if (ppm >= 29 && ppm <= 37) return Color.ORANGE;
            if (ppm >= 38 && ppm <= 46) return Color.YELLOW;
            if (ppm >= 47 && ppm <= 55) return Color.GREEN;
            if (ppm >= 56) return Color.GREEN;
        } else if (curso.matches("^2.*")) { // 2° Básico
            if (ppm <= 42) return Color.RED;
            if (ppm >= 43 && ppm <= 53) return Color.RED;
            if (ppm >= 54 && ppm <= 63) return Color.ORANGE;
            if (ppm >= 64 && ppm <= 73) return Color.YELLOW;
            if (ppm >= 74 && ppm <= 83) return Color.GREEN;
            if (ppm >= 84) return Color.GREEN;
        } else if (curso.matches("^3.*")) { // 3° Básico
            if (ppm <= 63) return Color.RED;
            if (ppm >= 64 && ppm <= 75) return Color.RED;
            if (ppm >= 76 && ppm <= 87) return Color.ORANGE;
            if (ppm >= 88 && ppm <= 99) return Color.YELLOW;
            if (ppm >= 100 && ppm <= 111) return Color.GREEN;
            if (ppm >= 112) return Color.GREEN;
        } else if (curso.matches("^4.*")) { // 4° Básico
            if (ppm <= 84) return Color.RED;
            if (ppm >= 85 && ppm <= 96) return Color.RED;
            if (ppm >= 97 && ppm <= 110) return Color.ORANGE;
            if (ppm >= 111 && ppm <= 124) return Color.YELLOW;
            if (ppm >= 125 && ppm <= 139) return Color.GREEN;
            if (ppm >= 140) return Color.GREEN;
        } else if (curso.matches("^5.*")) { // 5° Básico
            if (ppm <= 103) return Color.RED;
            if (ppm >= 104 && ppm <= 119) return Color.RED;
            if (ppm >= 120 && ppm <= 135) return Color.ORANGE;
            if (ppm >= 136 && ppm <= 149) return Color.YELLOW;
            if (ppm >= 150 && ppm <= 167) return Color.GREEN;
            if (ppm >= 168) return Color.GREEN;
        } else if (curso.matches("^6.*")) { // 6° Básico
            if (ppm <= 124) return Color.RED;
            if (ppm >= 125 && ppm <= 142) return Color.RED;
            if (ppm >= 143 && ppm <= 160) return Color.ORANGE;
            if (ppm >= 161 && ppm <= 177) return Color.YELLOW;
            if (ppm >= 178 && ppm <= 195) return Color.GREEN;
            if (ppm >= 196) return Color.GREEN;
        } else if (curso.matches("^7.*")) { // 7° Básico
            if (ppm <= 134) return Color.RED;
            if (ppm >= 135 && ppm <= 153) return Color.RED;
            if (ppm >= 154 && ppm <= 173) return Color.ORANGE;
            if (ppm >= 174 && ppm <= 193) return Color.YELLOW;
            if (ppm >= 194 && ppm <= 213) return Color.GREEN;
            if (ppm >= 214) return Color.GREEN;
        } else if (curso.matches("^8.*")) { // 8° Básico
            if (ppm <= 134) return Color.RED;
            if (ppm >= 135 && ppm <= 153) return Color.RED;
            if (ppm >= 154 && ppm <= 173) return Color.ORANGE;
            if (ppm >= 174 && ppm <= 193) return Color.YELLOW;
            if (ppm >= 194 && ppm <= 213) return Color.GREEN;
            if (ppm >= 214) return Color.GREEN;
        }
        return Color.WHITE; // Si no hay coincidencia, fondo normal
    }
    
    private void cambiarTamanoFuente(JTable table, int cambio) {
        Font fuenteActual = table.getFont();
        int nuevoTamano = fuenteActual.getSize() + cambio;

        // Limitar el tamaño de la fuente entre 10 y 30 para evitar problemas
        if (nuevoTamano >= 10 && nuevoTamano <= 30) {
            table.setFont(new Font(fuenteActual.getFontName(), Font.PLAIN, nuevoTamano));
            table.setRowHeight(nuevoTamano + 5); // Ajustar altura de las filas
        }
    }

    private void eliminarAlumno(String nombre) {
        int confirm = JOptionPane.showConfirmDialog(this, 
                "¿Estás seguro de que deseas eliminar a " + nombre + "?", 
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            db.eliminarAlumno(nombre);
            JOptionPane.showMessageDialog(this, "Alumno eliminado correctamente.");
            mostrarAlumnos(); // Refrescar la lista
        }
    }

    private void realizarTest() {
        List<Alumno> alumnosOrdenados = db.getAlumnos()
                .stream()
                .sorted((a, b) -> a.getCurso().compareToIgnoreCase(b.getCurso()))
                .collect(Collectors.toList());

        if (alumnosOrdenados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay alumnos registrados.");
            return;
        }

        String[] nombres = alumnosOrdenados.stream().map(a -> a.getNombre() + " (" + a.getCurso() + ")").toArray(String[]::new);
        String seleccionado = (String) JOptionPane.showInputDialog(this, "Selecciona un alumno:", 
                "Seleccionar Alumno", JOptionPane.QUESTION_MESSAGE, null, nombres, nombres[0]);

        if (seleccionado != null) {
            String nombreAlumno = seleccionado.split(" \\(")[0]; 
            new TestLecturaGUI(db, textosManager, nombreAlumno);
        }
    }
    private void agregarTexto() {
        // Crear cuadro de diálogo personalizado
        JDialog dialog = new JDialog(this, "Gestión de Textos", true);
        dialog.setSize(500, 500);
        dialog.setLayout(new BorderLayout());

        // Panel para ingresar el título del nuevo texto
        JPanel panelTitulo = new JPanel(new BorderLayout());
        JLabel lblTitulo = new JLabel("Título:");
        JTextField txtTitulo = new JTextField();
        panelTitulo.add(lblTitulo, BorderLayout.WEST);
        panelTitulo.add(txtTitulo, BorderLayout.CENTER);

        // JTextArea para ingresar el contenido del texto
        JTextArea textoArea = new JTextArea(10, 40);
        textoArea.setLineWrap(true);
        textoArea.setWrapStyleWord(true);
        textoArea.setFont(new Font("Arial", Font.PLAIN, 14));

        // JScrollPane para permitir desplazamiento en el área de texto
        JScrollPane scrollPane = new JScrollPane(textoArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Panel de botones
        JPanel panelBotones = new JPanel();
        JButton btnGuardar = new JButton("Guardar Texto");
        JButton btnEliminar = new JButton("Eliminar Texto");
        JButton btnCancelar = new JButton("Cancelar");

        // Acción del botón "Guardar Texto"
        btnGuardar.addActionListener(e -> {
            String titulo = txtTitulo.getText().trim();
            String contenido = textoArea.getText().trim();
            if (!titulo.isEmpty() && !contenido.isEmpty()) {
                textosManager.agregarTexto(titulo, contenido);
                JOptionPane.showMessageDialog(dialog, "Texto guardado correctamente.");
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Título y contenido no pueden estar vacíos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Acción del botón "Eliminar Texto"
        btnEliminar.addActionListener(e -> {
            // Crear nuevo cuadro de diálogo para la eliminación
            JDialog eliminarDialog = new JDialog(dialog, "Eliminar Texto", true);
            eliminarDialog.setSize(250, 100);
            eliminarDialog.setLayout(new BorderLayout());

            // Obtener la lista de títulos
            List<String> titulosDisponibles = textosManager.getTitulos();
            if (titulosDisponibles.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "No hay textos disponibles para eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JComboBox<String> comboTextos = new JComboBox<>(titulosDisponibles.toArray(new String[0]));

            JPanel panelEliminar = new JPanel(new BorderLayout());
            panelEliminar.add(new JLabel("Selecciona un texto a eliminar:"), BorderLayout.NORTH);
            panelEliminar.add(comboTextos, BorderLayout.CENTER);

            JButton btnConfirmarEliminar = new JButton("Eliminar");
            btnConfirmarEliminar.addActionListener(ev -> {
                String textoSeleccionado = (String) comboTextos.getSelectedItem();
                if (textoSeleccionado != null) {
                    String tituloReal = textoSeleccionado.replaceAll("\\s*\\(\\d+ palabras\\)", ""); // Extraer título real
                    textosManager.eliminarTexto(tituloReal);
                    JOptionPane.showMessageDialog(eliminarDialog, "Texto eliminado correctamente.");
                    eliminarDialog.dispose();
                    dialog.dispose(); // Cerrar también el diálogo principal
                }
            });

            eliminarDialog.add(panelEliminar, BorderLayout.CENTER);
            eliminarDialog.add(btnConfirmarEliminar, BorderLayout.SOUTH);
            eliminarDialog.setLocationRelativeTo(dialog);
            eliminarDialog.setVisible(true);
        });

        // Acción del botón "Cancelar"
        btnCancelar.addActionListener(e -> dialog.dispose());

        panelBotones.add(btnGuardar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnCancelar);

        // Agregar componentes al diálogo
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.add(panelTitulo, BorderLayout.NORTH);
        panelCentral.add(scrollPane, BorderLayout.CENTER);

        dialog.add(panelCentral, BorderLayout.CENTER);
        dialog.add(panelBotones, BorderLayout.SOUTH);

        // Mostrar el diálogo
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainGUI::new);
    }
}
