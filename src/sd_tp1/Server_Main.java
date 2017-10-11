package sd_tp1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server_Main {

    public static void main(String[] args) throws IOException {

        Lista_Clientes_Ativos clientes_ativos = new Lista_Clientes_Ativos();
        Queue_Clientes queue_clientes = new Queue_Clientes();
        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        int i = 0;

        try {
            serverSocket = new ServerSocket(3000);
        } catch (IOException e) {
            System.err.println("Could not listen on port:" + serverSocket.getLocalPort());
            System.exit(1);
        }

        System.out.println("Server ON" + "\n" + "======================");

        while (true) {

            clientSocket = serverSocket.accept();   
            queue_clientes.pushOrder(clientSocket);

            Server_Thread x = new Server_Thread(queue_clientes, ++i, clientes_ativos);  // lança um thread para atender de cada cliente
            Thread new_client_thread = new Thread(x);
            new_client_thread.start();
        }

    }
}