package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CL_Logger {
  private BufferedWriter writer;
  private final String filePath;

  public CL_Logger(String logDirectory) throws IOException {
    this.filePath = logDirectory + File.separator + "transitions.log";

    // Crear el directorio si no existe
    Path dirPath = Paths.get(logDirectory);
    if (!Files.exists(dirPath)) {
      Files.createDirectories(dirPath);
    }

    try {
      this.writer = new BufferedWriter(new FileWriter(filePath, false));
    } catch (IOException e) {
      System.err.println("Error al crear el logger: " + e.getMessage());
      throw e;
    }
  }

  public synchronized void logTransition(int transicion) {
    try {
      writer.write("T" + transicion);
      writer.flush();
    } catch (IOException e) {
      System.err.println("Error al escribir en el log de transiciones: " + e.getMessage());
    }
  }

  public synchronized void close() {
    if (writer != null) {
      try {
        writer.close();
      } catch (IOException e) {
        System.err.println("Error al cerrar el logger: " + e.getMessage());
      }
    }
  }
}
