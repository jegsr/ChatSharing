package sd_tp1;

import java.util.LinkedList;

// class para gerir estados(login/logout) dos utilizadores
public class Lista_Clientes_Ativos {

    private LinkedList<String> clientes_ativos = new LinkedList<String>();

    // faz login do utilizador
    public synchronized void loginClient(String tmp_client) {
        clientes_ativos.add(tmp_client);
        notifyAll();
    }

    // faz logout do utilizador
    public synchronized void logoutClient(String tmp_client) {
        clientes_ativos.remove(tmp_client);
        notifyAll();
    }

    // verifica se existe utilizadores ativos
    public synchronized boolean empty() {
        if (this.clientes_ativos.isEmpty()) {
            return true;
        }
        return false;
    }

    // percorre e lista os clientes ativos 
    public synchronized void list() {
        for (String x : this.clientes_ativos) {
            System.out.println(x);
        }
    }

    // verifica e devolve o numero de utilizadores ativos
    public synchronized int size() {
        return this.clientes_ativos.size();
    }

    // devolve a lista
    public synchronized LinkedList<String> getLista() {
        return this.clientes_ativos;
    }

    // verifica se está ativo
    public synchronized boolean ativo(String tmp_client) {
        for (String y : this.clientes_ativos) {
            if (y.equals(tmp_client)) {
                return true;
            }
        }
        return false;
    }
}
