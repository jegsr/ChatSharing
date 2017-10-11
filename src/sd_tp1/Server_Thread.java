package sd_tp1;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server_Thread extends Thread {

    private Socket tmp_client_socket = null;
    private Queue_Clientes tmp_fila_clientes = new Queue_Clientes();
    private int tmp_thread_id = 0;
    private Lista_Clientes_Ativos tmp_clientes_ativos = new Lista_Clientes_Ativos();

    public Server_Thread(Queue_Clientes a, int y, Lista_Clientes_Ativos x) {

        this.tmp_fila_clientes = a;
        this.tmp_thread_id = y;
        this.tmp_clientes_ativos = x;

    }

    @Override
    public void run() {

        String tarefa;
        String file_name;
        String folder_name;
        File file;
        File folder;
                
        int nof_files=0;

        if (this.tmp_fila_clientes.empty()) {
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(Server_Thread.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            try {
                this.tmp_client_socket = this.tmp_fila_clientes.pullOrder();
                PrintWriter out = new PrintWriter(this.tmp_client_socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(this.tmp_client_socket.getInputStream()));
                String inputLine;
                inputLine = null;

                System.out.println("Thread/Client " + this.tmp_thread_id + " - Started");

                while (true) {
                    out.flush();

                    inputLine = in.readLine();
                    try {
                        File user_folder = new File("");
                        if (inputLine != null) {
                            user_folder = new File(Utils.STORAGE + "\\" + inputLine);

                            if (!user_folder.exists() || user_folder.getAbsolutePath().equals(new File(Utils.STORAGE + "\\").getAbsolutePath())) {
                                out.println("30");  //codigo erro, caso a conta não exista
                            } else if (this.tmp_clientes_ativos.ativo(inputLine)) {
                                out.println("20");  //codigo erro, caso a conta esteja em uso
                            } else {
                                out.println("10");  //codigo ok, conta existe e não está em uso
                                this.tmp_clientes_ativos.loginClient(inputLine);

                                InputStream in1 = this.tmp_client_socket.getInputStream();

                                do {

                                    tarefa = ByteStream.toString(in1);
                                    switch (tarefa) {
                                        case "backup":  //tarefa de backup
                                            nof_files = ByteStream.toInt(in1);
                                            System.out.println("Thread/Client " + this.tmp_thread_id + " - " + nof_files + " ficheiros copiados");
                                            for (int cur_file = 0; cur_file < nof_files; cur_file++) {
                                                String code = ByteStream.toString(in1);
                                                switch (code) {
                                                    case "0500":
                                                        file_name = ByteStream.toString(in1);
                                                        file = new File(user_folder + "\\" + file_name);   //cria o ficheiro na pasta do utilizador
                                                        ByteStream.toFile(in1, file);
                                                        break;
                                                    case "0600":
                                                        folder_name = ByteStream.toString(in1);
                                                        folder = new File(user_folder + "\\" + folder_name);   //cria o diretorio na pasta do utilizador
                                                        folder.mkdirs();
                                                        break;
                                                }
                                            }
                                            break;

                                        case "restore": //tarefa de restore
                                            //falta codigo    
                                            break;

                                        default:    //tarefa de logout                                      
                                            this.tmp_clientes_ativos.logoutClient(inputLine);
                                            System.out.println("Thread/Client " + this.tmp_thread_id + " - Logout");
                                            break;
                                    }

                                } while (!tarefa.equals("exit"));

                            }
                        }

                    } catch (Exception e) {
                        System.out.println("Erro na comunicação com o Client " + this.tmp_thread_id + " - Ligação terminada");
                        this.tmp_clientes_ativos.logoutClient(inputLine);
                        System.out.println("\nThread/Client " + this.tmp_thread_id + " - Logout");
                        this.tmp_client_socket.close();
                        break;
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Server_Thread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                out.close();
                in.close();
                this.tmp_client_socket.close();

            } catch (IOException e) {
                System.out.println("Problema no Client " + this.tmp_thread_id + " - Ligação terminada");
            }

        }
    }
}
