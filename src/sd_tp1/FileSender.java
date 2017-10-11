package sd_tp1;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileSender extends Thread {

    private Socket tmp_client_socket = null;
    private Queue_To_Backup tmp_fila_ficheiros;
    private String tmp_pasta_core;

    public FileSender(Socket a, Queue_To_Backup b, String c) {

        this.tmp_client_socket = a;
        this.tmp_fila_ficheiros = b;
        this.tmp_pasta_core = c;
    }

    @Override
    public void run() {

        int flag = 0;
        int cnt_files = 0;
        String[] caminhos;
        String path;

        while (flag != 2) {

            if (this.tmp_fila_ficheiros.empty()) {
                try {
                    flag++;
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Server_Thread.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else {
                try {

                    OutputStream os = this.tmp_client_socket.getOutputStream();
                    caminhos = new String[this.tmp_fila_ficheiros.size()];
                    cnt_files = caminhos.length;
                    ByteStream.toStream(os, cnt_files); //numero de ficheiros

                    System.out.println("");
                    for (int h = 0; h < cnt_files; h++) {

                        File file_tmp = this.tmp_fila_ficheiros.pullOrder();

                        if (file_tmp.isDirectory()) {
                            ByteStream.toStream(os, "0600");    //codigo para sinalisar envio de pasta
                            path = file_tmp.getAbsolutePath();
                            path = path.replace(this.tmp_pasta_core, "");
                            ByteStream.toStream(os, path);
                        }

                        if (file_tmp.isFile()) {
                            ByteStream.toStream(os, "0500");    //codigo para sinalisar envio de ficheiro
                            path = file_tmp.getAbsolutePath();
                            path = path.replace(this.tmp_pasta_core, "");
                            ByteStream.toStream(os, path);
                            ByteStream.toStream(os, file_tmp);

                        }
                        System.out.print("*");

                    }

                } catch (IOException e) {
                    System.out.println("Problema a lançar o thread");
                }
            }
        }
        System.out.println("\n\nBackup terminado");
        System.out.print("\nListar ficheiros não copiados?(y/N): ");
    }
}
