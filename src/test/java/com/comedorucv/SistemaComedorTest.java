package com.comedorucv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.comedorucv.controladores.GestorCostos;
import com.comedorucv.controladores.GestorMenu;
import com.comedorucv.modelos.Plato;
import com.comedorucv.modelos.PlatoCosto;
import com.comedorucv.modelos.Usuario;
import com.comedorucv.utils.GestorSaldosYMenu;

public class SistemaComedorTest {

    private String testCedula = "88888888";

    @Before
    public void setUp() {
        GestorSaldosYMenu.borrarHistorial(testCedula);
    }

    @After
    public void tearDown() {
        GestorSaldosYMenu.borrarHistorial(testCedula);
        GestorSaldosYMenu.borrarHistorial(""); // Por las pruebas de casos borde
        GestorSaldosYMenu.borrarHistorial(null);
    }

    // --- PRUEBAS NORMALES ---

    @Test
    public void testGuardarYLeerSaldoMonedero() {
        GestorSaldosYMenu.guardarSaldo(testCedula, 100.50, "Recarga inicial de prueba");
        double saldoLeido = GestorSaldosYMenu.leerSaldo(testCedula);
        assertEquals("El saldo leído debe coincidir con el saldo guardado.", 100.50, saldoLeido, 0.01);
    }

    @Test
    public void testGestorCostos() {
        GestorCostos gestorCostos = new GestorCostos();
        PlatoCosto platoCosto = new PlatoCosto("Pabellón", 45.0);
        gestorCostos.setCosto("Lunes", platoCosto);
        PlatoCosto obtenido = gestorCostos.getCosto("Lunes");

        assertNotNull("El costo del plato no debería ser null para el Lunes.", obtenido);
        assertEquals("El costo del plato debe ser 45.0.", 45.0, obtenido.getCosto(), 0.01);
    }

    @Test
    public void testAgregarPlatoConfiguracionMenu() {
        GestorMenu gestorMenu = new GestorMenu();
        Plato plato = new Plato("Almuerzo Completo", "Arroz", "Caraotas", "Jugo", "Fruta", 20, 100);
        gestorMenu.agregarPlato("Miercoles", plato);

        Plato platoGuardado = gestorMenu.obtenerPlato("Miercoles");
        assertNotNull("El plato agregado debe encontrarse en el menú del Miércoles.", platoGuardado);
    }

    @Test
    public void testGestionUsuariosMetodos() {
        Usuario usuario = new Usuario();
        usuario.setNombre("Carlos Medina");
        usuario.setRol("Estudiante");

        assertEquals("El nombre del usuario debe coincidir con el asignado.", "Carlos Medina", usuario.getNombre());
    }

    // --- PRUEBAS DE CASOS BORDE (Para romper el código y saber qué arreglar) ---

    // 1. Casos borde de clases relacionadas al saldo del monedero
    @Test
    public void testMonederoCasosBorde() {
        // Borde 1: Intentar guardar un saldo negativo. El sistema debería impedir esta
        // acción o arrojar un error, pero el código lo permite.
        GestorSaldosYMenu.guardarSaldo(testCedula, -500.0, "Saldo negativo malicioso");
        double saldoActual = GestorSaldosYMenu.leerSaldo(testCedula);
        assertTrue(
                "FALLA DETECTADA: El sistema permite saldos negativos en el monedero. Se debe añadir validación en guardarSaldo().",
                saldoActual >= 0);

        // Borde 2: Intentar leer saldo de un usuario sin número de cédula (vacío)
        GestorSaldosYMenu.guardarSaldo("", 100.0, "Prueba vacía");
        double saldoVacio = GestorSaldosYMenu.leerSaldo("");
        assertTrue("FALLA DETECTADA: El código acepta cédulas vacías y crea archivos 'monedero_.txt'.",
                saldoVacio == 0.0);
    }

    // 2. Casos borde de clases relacionadas al cálculo del CCB
    @Test
    public void testCostosCasosBorde() {
        GestorCostos gestorCostos = new GestorCostos();
        PlatoCosto platoCosto = new PlatoCosto("Pabellón", 45.0);
        gestorCostos.setCosto("Lunes", platoCosto);

        // Borde 1: Intentar modificar el costo a un valor negativo
        gestorCostos.modificarCosto("Lunes", -15.0);
        PlatoCosto obtenido = gestorCostos.getCosto("Lunes");
        assertTrue("FALLA DETECTADA: Se permitió establecer un precio negativo para un plato.",
                obtenido.getCosto() >= 0);

        // Borde 2: Intentar buscar un día que está nulo (puede causar
        // NullPointerException dependiendo del Map)
        // Esto podría romper la ejecución de la prueba si hay problemas en los hash.
        PlatoCosto platoNulo = gestorCostos.getCosto(null);
        assertNull("Solicitar un día null debería retornar null sin explotar, pero también amerita validar inputs.",
                platoNulo);
    }

    // 3. Casos borde de clases relacionadas a la configuración de Menu
    @Test
    public void testMenuCasosBorde() {
        GestorMenu gestorMenu = new GestorMenu();

        // Borde 1: Crear un plato con disponibilidad negativa (imposible físicamente)
        Plato platoRoto = new Plato("Plato fantasma", "Nada", "Nada", "Agua", "Aire", -5, -40);
        gestorMenu.agregarPlato("Jueves", platoRoto);

        Plato obtenido = gestorMenu.obtenerPlato("Jueves");
        assertTrue("FALLA DETECTADA: El constructor del Plato acepta disponibilidad y número de insumos negativos.",
                obtenido.getDisponibilidad() >= 0);
        assertTrue("FALLA DETECTADA: El constructor del Plato acepta insumos negativos.", obtenido.getInsumos() >= 0);

        // Borde 2: Insertar el día como texto vacío o blanco
        gestorMenu.agregarPlato("   ", platoRoto);
        assertNull("FALLA DETECTADA: GestorMenu permite registrar platos en días inválidos o llenos de espacios.",
                gestorMenu.obtenerPlato("   "));
    }

    // 4. Casos borde de clases relacionadas a gestión de usuarios
    @Test
    public void testUsuariosCasosBorde() {
        Usuario usuario = new Usuario();

        // Borde 1: Setear un correo electrónico totalmente inválido o nulo. El programa
        // confía ciegamente.
        usuario.setCorreo("esto-no-es-un-correo");
        // Debería existir en la clase Usuario una validación que compruebe si tiene
        // "@ucv.ve" por ejemplo.
        assertTrue(
                "FALLA DETECTADA: El modelo Usuario no valida si el formato de correo es correcto o si pertenece a la ucv.",
                usuario.getCorreo().contains("@"));

        // Borde 2: Agregar cédula con letras cuando debería ser sólo números.
        usuario.setCedula("V-ABC12345");
        boolean isNumeric = usuario.getCedula().matches("\\d+"); // Revisa si son solo números
        assertTrue(
                "FALLA DETECTADA: La cédula de la clase Usuario permite letras y caracteres especiales en lugar de sólo números.",
                isNumeric);
    }
}
