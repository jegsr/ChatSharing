package sd_tp1;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client_Main {

    public static void main(String[] args) throws IOException {

        Socket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        int fla2;
        String outputLine, inputLine, inputLine2, inputLine3;

        Queue_To_Backup queue_backup = new Queue_To_Backup();
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

        try {
            socket = new Socket("127.0.0.1", 3000);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection.");
            System.exit(1);
        }
        try {
            System.out.println("APP Client\n===============");
            OUTER:
            do {
                out.flush();
                System.out.print("User ID: ");
                if ((outputLine = stdIn.readLine()) != null) {
                    out.println(outputLine);
                }
                if ((inputLine = in.readLine()) != null) {  //recebe o codigo de resultado da tarefa de login devolvido pelo server

                    switch (inputLine) {
                        case "30":
                            System.out.println("ERRO: Utilizador nao tem conta criada...");
                            break;

                        case "20":
                            System.out.println("ERRO: Utilizador em uso...");
                            break;

                        case "10":  //login efetuado
                            String op;
                            System.out.println("\nID valido...");
                            Thread.sleep(1500);
                            OutputStream os = socket.getOutputStream();

                            do {
                                System.out.print(Utils.CLEAR);
                                System.out.print(Utils.MENU);
                                System.out.flush();
                                op = stdIn.readLine();
                                switch (op) {
                                    case "1":   // Backup
                                        ByteStream.toStream(os, "backup");
                                        do {    //valida o caminho
                                            fla2 = 0;
                                            System.out.print("\nCaminho: ");
                                            inputLine2 = stdIn.readLine();
                                            inputLine2 = inputLine2.replace("\\", "\\\\");

                                            if (inputLine2.endsWith("\\") || !new File(inputLine2).exists()) {
                                                fla2 = 1;
                                                System.out.println("ERRO: Introduza um caminho valido...");
                                            };
                                        } while (fla2 == 1);
                                        out.flush();

                                        FileSender file_send = new FileSender(socket, queue_backup, new File(inputLine2).getParent());
                                        Thread newThread = new Thread(file_send);
                                        newThread.start();

                                        if (new File(inputLine2).isFile()) {    //caso seja só um ficheiro

                                            queue_backup.pushOrder(new File(inputLine2));

                                        } else if (new File(inputLine2).isDirectory()) {    //caso seja um directorio

                                            ListFiles list_files_tmp = new ListFiles();
                                            list_files_tmp.ListFiles(inputLine2);
                                            System.out.println("Processo: ");
                                            queue_backup.pushOrder(new File(inputLine2));   //insere a pasta no topo
                                            System.out.print("*");
                                            for (File x : list_files_tmp.getList_files()) {
                                                queue_backup.pushOrder(x);
                                                System.out.print("*");
                                            }

                                        }

                                        inputLine3 = stdIn.readLine();
                                        do {
                                            if (inputLine3.toString().contentEquals("y")) {
                                                queue_backup.list();
                                                System.out.print("\nListar novamente?(y/N): ");
                                                inputLine3 = stdIn.readLine();
                                            } else {
                                                break;
                                            }
                                        } while (true);
                                        break;

                                    case "2":   // Restore
                                        ByteStream.toStream(os, "restore");
                                        do {    //valida o caminho
                                            fla2 = 0;
                                            System.out.print("\nCaminho a Restaurar: ");
                                            inputLine2 = stdIn.readLine();
                                            inputLine2 = inputLine2.replace("\\", "\\\\");

                                            if (inputLine2.endsWith("\\") || !new File(inputLine2).exists()) {
                                                fla2 = 1;
                                                System.out.println("ERRO: Introduza um caminho valido...");
                                            };
                                        } while (fla2 == 1);
                                        out.flush();
                                        System.out.println("\n:::::::::::FALTA CODIGO:::::::::::");
                                        Thread.sleep(1500);
                                        break;

                                    case "3":   // Logout                                       
                                        ByteStream.toStream(os, "exit");
                                        System.out.println("\n:::::::::::LOGOUT:::::::::::");
                                        Thread.sleep(1500);
                                        break;

                                    default:
                                        System.out.println("\n--> Opcao invalida");
                                        Thread.sleep(1000);
                                }
                                System.out.print(Utils.CLEAR);
                            } while (!op.equals("3"));

                        default:
                            System.out.print(Utils.APPCLIENTEXIT);
                            break OUTER;
                    }
                } else {
                    System.out.println("Valor invalido");
                }
            } while (true);
            out.close();
            in.close();
            stdIn.close();
            socket.close();

        } catch (Exception e) {
            out.close();
            in.close();
            stdIn.close();
            socket.close();
        }
    }
}
