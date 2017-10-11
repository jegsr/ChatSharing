package sd_tp1;

import java.io.File;
import java.util.LinkedList;

// class para gerir conteudo para backup
public class Queue_To_Backup {

    private LinkedList<File> orderQueue = new LinkedList<File>();
    
    // adiciona no fim
    public synchronized void pushOrder(File to_backup_tmp) {
        orderQueue.addLast(to_backup_tmp);
        notifyAll();
    }

    // remove do inicio
    public synchronized File pullOrder() {
        while (orderQueue.size() == 0) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        return orderQueue.removeFirst();
    }

    // verifica se está vazia
    public synchronized boolean empty() {
        if (this.orderQueue.isEmpty()) {
            return true;
        }
        return false;
    }

    // percorre a lista e lista o conteudo
    public synchronized void list() {
        for (File x : this.orderQueue) {
            System.out.println(x.getPath());
        }
    }

    // verifica o tamanho
    public synchronized int size() {
        return this.orderQueue.size();
    }

    // devolve a lista
    public synchronized LinkedList<File> getLista() {
        return this.orderQueue;
    }
}
