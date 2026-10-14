
package org.example.proyectocrud.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.proyectocrud.database.DatabaseConnection;
import org.example.proyectocrud.model.Productos;

import java.sql.*;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class GestionProductoController {

    // Guardado de productos
    @FXML
    private TextField txtCodigo;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtPrecio;
    @FXML
    private ComboBox<String> cmbCategoria;
    @FXML
    private DatePicker dtpFecha;
    @FXML
    private TextField txtStock;
    @FXML
    private ComboBox<String> cmbMarca;
    @FXML
    private TextField txtDescripcion;

    // Busqueda y filtros
    @FXML
    private TextField txtFiltroNombre;
    @FXML
    private DatePicker dtpFiltroDesde;
    @FXML
    private TextField txtFiltroPrecioMin;
    @FXML
    private TextField txtFiltroPrecioMax;
    @FXML
    private ComboBox<String> cmbFiltroCategoria;
    @FXML
    private DatePicker dtpFiltroHasta;
    @FXML
    private ComboBox<String> cmbFiltroOrden;
    @FXML
    private CheckBox chkProductoExistencia;

    // Tabla
    @FXML
    private TableView<Productos> tblProducto;
    @FXML
    private TableColumn<Productos, String> colCodigo;
    @FXML
    private TableColumn<Productos, String> colNombre;
    @FXML
    private TableColumn<Productos, String> colCategoria;
    @FXML
    private TableColumn<Productos, String> colMarca;
    @FXML
    private TableColumn<Productos, Double> colPrecio;
    @FXML
    private TableColumn<Productos, Integer> colStock;
    @FXML
    private TableColumn<Productos, String> colFechaCreacion;

    private final ObservableList<Productos> producto =
            FXCollections.observableArrayList();

    private final Map<String, Integer> categoriasIds = new HashMap<>();
    private final Map<String, Integer> marcasIds = new HashMap<>();


    @FXML
    public void initialize() {

        configurarTable();
        tblProducto.setItems(producto);

        cargarCombos();

        cmbFiltroOrden.getItems().addAll(
                "Código",
                "Nombre",
                "Precio menor a mayor",
                "Precio mayor a menor",
                "Stock mayor a menor",
                "Fecha reciente"
        );

        cmbFiltroOrden.setValue("Código");

        tblProducto.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        cargarProductoSeleccionado(newValue);
                    }
                });

        txtFiltroNombre.textProperty().addListener(
                (obs, anterior, nuevo) -> clickCargar());

        txtFiltroPrecioMin.textProperty().addListener(
                (obs, anterior, nuevo) -> clickCargar());

        txtFiltroPrecioMax.textProperty().addListener(
                (obs, anterior, nuevo) -> clickCargar());

        cmbFiltroCategoria.setOnAction(e -> clickCargar());
        cmbFiltroOrden.setOnAction(e -> clickCargar());
        dtpFiltroDesde.setOnAction(e -> clickCargar());
        dtpFiltroHasta.setOnAction(e -> clickCargar());
        chkProductoExistencia.setOnAction(e -> clickCargar());

        clickCargar();
    }



    // CONFIGURAR TABLA

    public void configurarTable() {

        colCodigo.setCellValueFactory(
                new PropertyValueFactory<>("codigo"));

        colNombre.setCellValueFactory(
                new PropertyValueFactory<>("nombre"));

        colCategoria.setCellValueFactory(
                new PropertyValueFactory<>("categoria"));

        colMarca.setCellValueFactory(
                new PropertyValueFactory<>("marca"));

        colPrecio.setCellValueFactory(
                new PropertyValueFactory<>("precio"));

        colStock.setCellValueFactory(
                new PropertyValueFactory<>("stock"));

        colFechaCreacion.setCellValueFactory(
                new PropertyValueFactory<>("fechaCreacion"));
    }



    // CARGAR CATEGORIAS Y MARCAS

    private void cargarCombos() {

        String sqlCategorias =
                "SELECT id_categoria, nombre " +
                        "FROM catalogos.categorias WHERE estado = 1 " +
                        "ORDER BY nombre";

        String sqlMarcas =
                "SELECT id_marca, nombre " +
                        "FROM catalogos.marcas WHERE estado = 1 " +
                        "ORDER BY nombre";

        categoriasIds.clear();
        marcasIds.clear();

        cmbCategoria.getItems().clear();
        cmbMarca.getItems().clear();
        cmbFiltroCategoria.getItems().clear();
        cmbFiltroCategoria.getItems().add("Todas");

        try (Connection connection = DatabaseConnection.getConnection()) {

            try (PreparedStatement statement =
                         connection.prepareStatement(sqlCategorias);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    String nombre = resultSet.getString("nombre");
                    int id = resultSet.getInt("id_categoria");

                    categoriasIds.put(nombre, id);
                    cmbCategoria.getItems().add(nombre);
                    cmbFiltroCategoria.getItems().add(nombre);
                }
            }

            try (PreparedStatement statement =
                         connection.prepareStatement(sqlMarcas);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    String nombre = resultSet.getString("nombre");
                    int id = resultSet.getInt("id_marca");

                    marcasIds.put(nombre, id);
                    cmbMarca.getItems().add(nombre);
                }
            }

            cmbFiltroCategoria.setValue("Todas");

        } catch (SQLException ex) {
            ex.printStackTrace();

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error",
                    "Error al cargar ComboBox",
                    ex.getMessage()
            );
        }
    }



    // CARGAR PRODUCTO SELECCIONADO


    private void cargarProductoSeleccionado(Productos p) {

        txtCodigo.setText(p.getCodigo());
        txtNombre.setText(p.getNombre());
        txtDescripcion.setText(p.getDescripcion());
        txtPrecio.setText(String.valueOf(p.getPrecio()));
        txtStock.setText(String.valueOf(p.getStock()));

        cmbCategoria.setValue(p.getCategoria());
        cmbMarca.setValue(p.getMarca());

        dtpFecha.setValue(
                LocalDate.parse(p.getFechaCreacion())
        );
    }



    // GUARDAR PRODUCTO

    @FXML
    public void clickGuardar(ActionEvent actionEvent) {

        if (!validarFormulario()) {
            return;
        }

        String sql =
                "INSERT INTO catalogos.productos " +
                        "(codigo, nombre, descripcion, precio, stock, " +
                        "fecha_creacion, id_categoria, id_marca, estado) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 1)";

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, txtCodigo.getText().trim());
            statement.setString(2, txtNombre.getText().trim());
            statement.setString(3, txtDescripcion.getText().trim());

            statement.setBigDecimal(4,
                    new BigDecimal(txtPrecio.getText().trim()));

            statement.setInt(5,
                    Integer.parseInt(txtStock.getText().trim()));

            statement.setDate(6,
                    Date.valueOf(dtpFecha.getValue() != null
                            ? dtpFecha.getValue()
                            : LocalDate.now()));

            statement.setInt(7,
                    categoriasIds.get(cmbCategoria.getValue()));

            statement.setInt(8,
                    marcasIds.get(cmbMarca.getValue()));

            int filas = statement.executeUpdate();

            if (filas > 0) {

                clickLimpiar();
                clickCargar();

                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Registro exitoso",
                        "Producto guardado",
                        "El producto se ha guardado correctamente."
                );
            }

        } catch (SQLException ex) {
            ex.printStackTrace();

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error",
                    "No se pudo guardar",
                    ex.getMessage()
            );
        }
    }


    // ACTUALIZAR PRODUCTO

    @FXML
    public void clickActualizar() {

        Productos seleccionado =
                tblProducto.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Advertencia",
                    "No hay producto seleccionado",
                    "Seleccione un producto de la tabla."
            );
            return;
        }

        if (!validarFormulario()) {
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Actualizar");
        confirmacion.setHeaderText("Confirmar actualización");
        confirmacion.setContentText(
                "¿Está seguro de actualizar el producto seleccionado?"
        );

        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL)
                != ButtonType.OK) {
            return;
        }

        String sql =
                "UPDATE catalogos.productos SET " +
                        "codigo = ?, nombre = ?, descripcion = ?, " +
                        "precio = ?, stock = ?, fecha_creacion = ?, " +
                        "id_categoria = ?, id_marca = ? " +
                        "WHERE id_producto = ? AND estado = 1";

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, txtCodigo.getText().trim());
            statement.setString(2, txtNombre.getText().trim());
            statement.setString(3, txtDescripcion.getText().trim());

            statement.setBigDecimal(4,
                    new BigDecimal(txtPrecio.getText().trim()));

            statement.setInt(5,
                    Integer.parseInt(txtStock.getText().trim()));

            statement.setDate(6,
                    Date.valueOf(dtpFecha.getValue() != null
                            ? dtpFecha.getValue()
                            : LocalDate.now()));

            statement.setInt(7,
                    categoriasIds.get(cmbCategoria.getValue()));

            statement.setInt(8,
                    marcasIds.get(cmbMarca.getValue()));

            statement.setInt(9,
                    Integer.parseInt(seleccionado.getId_producto()));

            int filas = statement.executeUpdate();

            if (filas > 0) {

                clickLimpiar();
                clickCargar();

                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Registro actualizado",
                        "Producto actualizado",
                        "El producto se ha actualizado correctamente."
                );

            } else {
                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Advertencia",
                        "Producto no actualizado",
                        "El producto no existe o está inactivo."
                );
            }

        } catch (SQLException ex) {
            ex.printStackTrace();

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error",
                    "No se pudo actualizar",
                    ex.getMessage()
            );
        }
    }


    // ELIMINAR PRODUCTO

    @FXML
    public void clickEliminar() {

        Productos seleccionado =
                tblProducto.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Advertencia",
                    "No hay producto seleccionado",
                    "Seleccione un producto de la tabla para eliminarlo."
            );
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Eliminar");
        confirmacion.setHeaderText("Confirmar eliminación");
        confirmacion.setContentText(
                "¿Está seguro de desactivar el producto seleccionado?"
        );

        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL)
                != ButtonType.OK) {
            return;
        }

        String sql =
                "UPDATE catalogos.productos SET estado = 0 " +
                        "WHERE id_producto = ? AND estado = 1";

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1,
                    Integer.parseInt(seleccionado.getId_producto()));

            int filas = statement.executeUpdate();

            if (filas > 0) {

                clickLimpiar();
                clickCargar();

                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Registro eliminado",
                        "Producto desactivado",
                        "El producto se ha desactivado correctamente."
                );

            } else {

                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Advertencia",
                        "Producto no eliminado",
                        "El producto ya está inactivo o no existe."
                );
            }

        } catch (SQLException ex) {
            ex.printStackTrace();

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error",
                    "No se pudo eliminar",
                    ex.getMessage()
            );
        }
    }


    // CARGAR PRODUCTOS Y APLICAR FILTROS

    @FXML
    public void clickCargar() {

        StringBuilder sql = new StringBuilder(
                "SELECT p.*, c.nombre AS categoria, " +
                        "m.nombre AS marca " +
                        "FROM catalogos.productos p " +
                        "INNER JOIN catalogos.categorias c " +
                        "ON p.id_categoria = c.id_categoria " +
                        "INNER JOIN catalogos.marcas m " +
                        "ON p.id_marca = m.id_marca " +
                        "WHERE p.estado = 1"
        );

        String busqueda = txtFiltroNombre.getText().trim();
        String minimo = txtFiltroPrecioMin.getText().trim();
        String maximo = txtFiltroPrecioMax.getText().trim();

        String categoriaFiltro = cmbFiltroCategoria.getValue();

        Integer idCategoriaFiltro =
                categoriasIds.get(categoriaFiltro);

        BigDecimal precioMinimo = null;
        BigDecimal precioMaximo = null;

        try {
            if (!minimo.isEmpty()) {
                precioMinimo = new BigDecimal(minimo);
            }

            if (!maximo.isEmpty()) {
                precioMaximo = new BigDecimal(maximo);
            }
        } catch (NumberFormatException ex) {
            // Esperar hasta que el usuario escriba un número válido
            return;
        }

        if (!busqueda.isEmpty()) {
            sql.append(
                    " AND (p.codigo ILIKE ? OR p.nombre ILIKE ?)"
            );
        }

        if (idCategoriaFiltro != null) {
            sql.append(" AND p.id_categoria = ?");
        }

        if (dtpFiltroDesde.getValue() != null) {
            sql.append(" AND p.fecha_creacion >= ?");
        }

        if (dtpFiltroHasta.getValue() != null) {
            sql.append(" AND p.fecha_creacion <= ?");
        }

        if (precioMinimo != null) {
            sql.append(" AND p.precio >= ?");
        }

        if (precioMaximo != null) {
            sql.append(" AND p.precio <= ?");
        }

        if (chkProductoExistencia.isSelected()) {
            sql.append(" AND p.stock > 0");
        }

        // Ordenar resultados
        String orden = cmbFiltroOrden.getValue();

        if ("Nombre".equals(orden)) {
            sql.append(" ORDER BY p.nombre ASC");

        } else if ("Precio menor a mayor".equals(orden)) {
            sql.append(" ORDER BY p.precio ASC");

        } else if ("Precio mayor a menor".equals(orden)) {
            sql.append(" ORDER BY p.precio DESC");

        } else if ("Stock mayor a menor".equals(orden)) {
            sql.append(" ORDER BY p.stock DESC");

        } else if ("Fecha reciente".equals(orden)) {
            sql.append(" ORDER BY p.fecha_creacion DESC");

        } else {
            sql.append(" ORDER BY p.codigo ASC");
        }

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql.toString())
        ) {

            int posicion = 1;

            if (!busqueda.isEmpty()) {
                statement.setString(posicion++, "%" + busqueda + "%");
                statement.setString(posicion++, "%" + busqueda + "%");
            }

            if (idCategoriaFiltro != null) {
                statement.setInt(posicion++, idCategoriaFiltro);
            }

            if (dtpFiltroDesde.getValue() != null) {
                statement.setDate(posicion++,
                        Date.valueOf(dtpFiltroDesde.getValue()));
            }

            if (dtpFiltroHasta.getValue() != null) {
                statement.setDate(posicion++,
                        Date.valueOf(dtpFiltroHasta.getValue()));
            }

            if (precioMinimo != null) {
                statement.setBigDecimal(posicion++, precioMinimo);
            }

            if (precioMaximo != null) {
                statement.setBigDecimal(posicion++, precioMaximo);
            }

            ObservableList<Productos> datos =
                    FXCollections.observableArrayList();

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    Productos p = new Productos(
                            resultSet.getString("id_producto"),
                            resultSet.getString("codigo"),
                            resultSet.getString("nombre"),
                            resultSet.getString("categoria"),
                            resultSet.getString("marca"),
                            resultSet.getString("descripcion"),
                            resultSet.getDouble("precio"),
                            resultSet.getInt("stock"),
                            resultSet.getString("fecha_creacion"),
                            resultSet.getString("id_categoria"),
                            resultSet.getString("id_marca"),
                            resultSet.getInt("estado") == 1
                    );

                    datos.add(p);
                }
            }

            producto.setAll(datos);

        } catch (SQLException ex) {
            ex.printStackTrace();

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error",
                    "No se pudieron cargar los productos",
                    ex.getMessage()
            );
        }
    }


    // LIMPIAR FORMULARIO


    @FXML
    public void clickLimpiar() {

        tblProducto.getSelectionModel().clearSelection();

        txtCodigo.clear();
        txtNombre.clear();
        txtDescripcion.clear();
        txtPrecio.clear();
        txtStock.clear();

        cmbCategoria.setValue(null);
        cmbMarca.setValue(null);
        dtpFecha.setValue(null);
    }



    // MOSTRAR ALERTAS


    private void mostrarAlerta(
            Alert.AlertType tipo,
            String titulo,
            String encabezado,
            String mensaje) {

        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(encabezado);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }



    // VALIDAR FORMULARIO


    @FXML
    private boolean validarFormulario() {

        if (txtCodigo.getText().isBlank() ||
                txtNombre.getText().isBlank() ||
                txtPrecio.getText().isBlank() ||
                txtStock.getText().isBlank() ||
                cmbCategoria.getValue() == null ||
                cmbMarca.getValue() == null) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Registro incompleto",
                    "Datos faltantes",
                    "Complete todos los campos obligatorios."
            );

            return false;
        }

        try {

            BigDecimal precio =
                    new BigDecimal(txtPrecio.getText().trim());

            int stock =
                    Integer.parseInt(txtStock.getText().trim());

            if (precio.compareTo(BigDecimal.ZERO) <= 0 ||
                    stock < 0) {

                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Dato inválido",
                        "Precio o stock incorrecto",
                        "El precio debe ser mayor a cero y " +
                                "el stock no puede ser negativo."
                );

                return false;
            }

        } catch (NumberFormatException ex) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Dato inválido",
                    "Valores incorrectos",
                    "El precio y el stock deben ser numéricos."
            );

            return false;
        }

        if (!categoriasIds.containsKey(cmbCategoria.getValue()) ||
                !marcasIds.containsKey(cmbMarca.getValue())) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Selección inválida",
                    "Categoría o marca incorrecta",
                    "Seleccione una categoría y marca existentes."
            );

            return false;
        }

        return true;
    }
}
