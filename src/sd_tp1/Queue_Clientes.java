package sd_tp1;

import java.net.Socket;
import java.util.LinkedList;

// class para gerir sockets dos clientes
public class Queue_Clientes {

    private LinkedList<Socket> orderQueue = new LinkedList<Socket>();

    // adiciona no fim
    public synchronized void pushOrder(Socket socket_tmp) {
        orderQueue.addLast(socket_tmp);
        notifyAll();
    }

    // remove do inicio
    public synchronized Socket pullOrder() {
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
}
