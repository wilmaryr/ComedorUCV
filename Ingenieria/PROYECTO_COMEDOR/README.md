# PROYECTO_COMEDOR — Data centralizada

Este proyecto usa una carpeta única para datos en runtime: `Ingenieria/PROYECTO_COMEDOR/data`.

Resumen:
- Todos los ficheros de datos (ej.: `usuarios_sistema.txt`, `secretaria.txt`, `menu_save.txt`, `monedero_save.txt`, `costos_save.txt`) deben residir en `Ingenieria/PROYECTO_COMEDOR/data`.
- `Main.java` fija la propiedad de sistema `data.dir` al iniciar para asegurar que la aplicación lea/escriba desde esa carpeta.

Ejecución:

Desde la raíz del workspace:

```powershell
cd Ingenieria\PROYECTO_COMEDOR
mvn -DskipTests package
java -jar target\PROYECTO_COMEDOR.jar
```

Alternativa (usar `DATA_DIR` o `-Ddata.dir`):

```powershell
$env:DATA_DIR = 'C:\ruta\a\otra\carpeta\data'
java -Ddata.dir='C:\ruta\a\otra\carpeta\data' -jar target\PROYECTO_COMEDOR.jar
```

Nota: No dejes copias paralelas de la carpeta `data` fuera de `PROYECTO_COMEDOR/data` para evitar inconsistencias.
