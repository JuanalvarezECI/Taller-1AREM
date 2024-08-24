package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * La clase SimpleHttpServer implementa un servidor HTTP simple que puede manejar solicitudes GET, POST y DELETE.
 * El servidor opera en el puerto especificado en el constructor y sirve archivos desde el directorio "src/webroot".
 */
public class SimpleHttpServer {
    /**
     * El puerto en el que el servidor escucha las solicitudes.
     */
    private final int port;
    /**
     * Crea una nueva instancia de SimpleHttpServer que escucha en el puerto especificado.
     *
     * @param port el puerto en el que el servidor escuchará las solicitudes.
     */
    public SimpleHttpServer(int port) {
        this.port = port;
    }
    /**
     * Inicia el servidor. El servidor continuará escuchando y manejando las solicitudes hasta que el programa se detenga.
     */

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                try (Socket socket = serverSocket.accept()) {
                    handleRequest(socket);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Maneja una solicitud HTTP entrante. Este método lee la solicitud del socket, determina el tipo de solicitud
     * (GET, POST, DELETE) y responde en consecuencia.
     *
     * @param socket el socket desde el cual se lee la solicitud y se escribe la respuesta.
     * @throws IOException si ocurre un error de entrada/salida al leer la solicitud o escribir la respuesta.
     */
    public void handleRequest(Socket socket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream());

        String line;
        while (!(line = in.readLine()).isEmpty()) {
            System.out.println(line);

            if (line.startsWith("GET")) {
                String filePath = line.split(" ")[1];
                if (filePath.equals("/")) {
                    filePath = "src/webroot/index.html";
                } else {
                    filePath = "src/webroot" + filePath;
                }

                Path path = Paths.get(filePath);
                if (Files.exists(path)) {
                    byte[] data = Files.readAllBytes(path);
                    out.println("HTTP/1.1 200 OK");
                    out.println("Content-Length: " + data.length);
                    out.println();
                    out.flush();
                    socket.getOutputStream().write(data);
                } else {
                    out.println("HTTP/1.1 404 Not Found");
                    out.println();
                }
            } else if (line.startsWith("POST") || line.startsWith("DELETE")) {
                out.println("HTTP/1.1 200 OK");
                out.println();
                out.println("Request received");
            }

            out.flush();
        }
    }
    /**
     * El punto de entrada principal del programa. Crea una nueva instancia de SimpleHttpServer y la inicia.
     *
     * @param args los argumentos de la línea de comandos. No se utilizan en este programa.
     */
    public static void main(String[] args) {
        SimpleHttpServer server = new SimpleHttpServer(8080);
        server.start();
    }
}