package com.comedorucv.modelos;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.comedorucv.utils.DataPathResolver;

public class UsuarioDAO {
    private final String FOLDER_DB;
    private final String FILE_USUARIOS;
    private final String FILE_SECRETARIA;

    public UsuarioDAO() {
        this.FOLDER_DB = DataPathResolver.getDataFolder();
        this.FILE_USUARIOS = DataPathResolver.resolve("usuarios_sistema.txt").toString();
        this.FILE_SECRETARIA = DataPathResolver.resolve("secretaria.txt").toString();
    }

    public String[] verificarEnSecretariaDatos(String cedula) {
        File archivo = new File(FILE_SECRETARIA);
        if (!archivo.exists()) {
            System.err.println("¡ERROR! No se encuentra el archivo: " + archivo.getAbsolutePath());
            return null;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty())
                    continue;
                String[] datos = linea.split(",");
                if (datos.length >= 3) {
                    String cedulaEnArchivo = datos[0].replaceAll("[^0-9]", "");
                    if (cedulaEnArchivo.equals(cedula.trim())) {
                        return new String[] { datos[1].trim(), datos[2].trim() }; // [Nombre, Rol]
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer secretaria: " + e.getMessage());
        }
        return null;
    }

    public boolean registrar(Usuario u) {
        // Verificar si existe en la base de datos y traer su nombre y rol
        String[] datosDetectados = verificarEnSecretariaDatos(u.getCedula());

        if (datosDetectados == null)
            return false;

        u.setNombre(datosDetectados[0]); // Nombre original de secretaria
        u.setRol(datosDetectados[1]); // Rol asignado

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_USUARIOS, true))) {
            String linea = String.format("%s,%s,%s,%s,%s",
                    u.getCedula(), u.getNombre(), u.getCorreo(), u.getPassword(), u.getRol());
            bw.write(linea);
            bw.newLine();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public Usuario login(String correo, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_USUARIOS))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length >= 5) {
                    if (datos[2].equals(correo) && datos[3].equals(password)) {
                        Usuario u = new Usuario();
                        u.setCedula(datos[0]);
                        u.setNombre(datos[1]);
                        u.setCorreo(datos[2]);
                        u.setPassword(datos[3]);
                        u.setRol(datos[4]);
                        return u;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error en login: " + e.getMessage());
        }
        return null;
    }

    public boolean verificarUsuario(String cedula, String correo) {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_USUARIOS))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length >= 3 && datos[0].trim().equals(cedula) && datos[2].trim().equals(correo)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("Error verificando usuario: " + e.getMessage());
        }
        return false;
    }

    public boolean actualizarContrasena(String cedula, String nuevaContrasena) {
        File archivo = new File(FILE_USUARIOS);
        File temp = new File(FOLDER_DB + File.separator + "temp_usuarios.txt");
        boolean actualizado = false;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo));
                BufferedWriter bw = new BufferedWriter(new FileWriter(temp))) {

            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length >= 5 && datos[0].trim().equals(cedula)) {
                    datos[3] = nuevaContrasena;
                    linea = String.join(",", datos);
                    actualizado = true;
                }
                bw.write(linea);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error actualizando contraseña: " + e.getMessage());
            return false;
        }

        if (actualizado) {
            if (archivo.delete()) {
                temp.renameTo(archivo);
            }
        } else {
            temp.delete();
        }
        return actualizado;
    }

    public Usuario buscarPorCedula(String cedula) {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_USUARIOS))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length >= 5 && datos[0].trim().equals(cedula)) {
                    Usuario u = new Usuario();
                    u.setCedula(datos[0]);
                    u.setNombre(datos[1]);
                    u.setCorreo(datos[2]);
                    u.setPassword(datos[3]);
                    u.setRol(datos[4]);
                    return u;
                }
            }
        } catch (IOException e) {
            System.err.println("Error buscando usuario: " + e.getMessage());
        }
        return null;
    }

    public Usuario buscarPorCorreo(String correo) {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_USUARIOS))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length >= 5 && datos[2].trim().equalsIgnoreCase(correo.trim())) {
                    Usuario u = new Usuario();
                    u.setCedula(datos[0]);
                    u.setNombre(datos[1]);
                    u.setCorreo(datos[2]);
                    u.setPassword(datos[3]);
                    u.setRol(datos[4]);
                    return u;
                }
            }
        } catch (IOException e) {
            System.err.println("Error buscando usuario por correo: " + e.getMessage());
        }
        return null;
    }

    public List<Usuario> obtenerTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_USUARIOS))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length >= 5) {
                    Usuario u = new Usuario();
                    u.setCedula(datos[0]);
                    u.setNombre(datos[1]);
                    u.setCorreo(datos[2]);
                    u.setPassword(datos[3]);
                    u.setRol(datos[4]);
                    usuarios.add(u);
                }
            }
        } catch (IOException e) {
            System.err.println("Error obteniendo usuarios: " + e.getMessage());
        }
        return usuarios;
    }

    public boolean actualizarRolEstudiante(String cedula, String nuevoRol) {
        // Primero, verificamos que sea un estudiante y que exista en el sistema
        Usuario u = buscarPorCedula(cedula);
        if (u == null) {
            return false; // No existe
        }
        if (u.getRol().equalsIgnoreCase("Profesor") || u.getRol().equalsIgnoreCase("Empleado") || u.getRol().equalsIgnoreCase("Administrador")) {
            return false; // No es estudiante regular o becario, etc. (En caso de que en el futuro haya más roles)
        }

        boolean actualizadoUsuarios = false;
        boolean actualizadoSecretaria = false;

        // Actualizar en usuarios_sistema.txt
        File archivoUsr = new File(FILE_USUARIOS);
        File tempUsr = new File(FOLDER_DB + File.separator + "temp_usuarios.txt");
        try (BufferedReader br = new BufferedReader(new FileReader(archivoUsr));
             BufferedWriter bw = new BufferedWriter(new FileWriter(tempUsr))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length >= 5 && datos[0].trim().equals(cedula)) {
                    datos[4] = nuevoRol;
                    linea = String.join(",", datos);
                    actualizadoUsuarios = true;
                }
                bw.write(linea);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error actualizando rol en usuarios: " + e.getMessage());
        }
        if (actualizadoUsuarios) {
            archivoUsr.delete();
            tempUsr.renameTo(archivoUsr);
        } else {
            tempUsr.delete();
        }

        // Actualizar en secretaria.txt
        File archivoSec = new File(FILE_SECRETARIA);
        File tempSec = new File(FOLDER_DB + File.separator + "temp_secretaria.txt");
        try (BufferedReader br = new BufferedReader(new FileReader(archivoSec));
             BufferedWriter bw = new BufferedWriter(new FileWriter(tempSec))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] datos = linea.split(",");
                if (datos.length >= 3 && datos[0].trim().equals(cedula)) {
                    datos[2] = nuevoRol;
                    linea = String.join(",", datos);
                    actualizadoSecretaria = true;
                }
                bw.write(linea);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error actualizando rol en secretaria: " + e.getMessage());
        }
        if (actualizadoSecretaria) {
            archivoSec.delete();
            tempSec.renameTo(archivoSec);
        } else {
            tempSec.delete();
        }

        return actualizadoUsuarios || actualizadoSecretaria;
    }
}
