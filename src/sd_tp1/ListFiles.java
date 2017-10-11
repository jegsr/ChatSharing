package sd_tp1;

import java.io.File;
import java.util.ArrayList;

// class para obter a arvore de ficheiros de um determinado diretorio
public class ListFiles {

    private ArrayList<File> list_files = new ArrayList<File>();

    public void ListFiles() {
    }

    public void ListFiles(String folder) {

        File file = new File(folder);
        this.getList(file);
    }

    public void getList(File folder) {

        ArrayList<File> folderList = new ArrayList<File>();
        folderList.addAll(getSubs(folder));
        for (int i = 0; i < folderList.size(); i++) {
            if (folderList.get(i).isDirectory()) {
                folderList.addAll(getSubs(folderList.get(i)));
            }
            this.getList_files().add(folderList.get(i));
        }
    }

    // Devolve os sub ficheiros de uma pasta
    private ArrayList<File> getSubs(File dest) {
        File[] subfiles = dest.listFiles();
        ArrayList<File> subFolders = new ArrayList<File>();
        if (subfiles != null) {
            for (int i = 0; i < subfiles.length; i++) {
                subFolders.add(subfiles[i]);
            }
        } else {
            subFolders.add(dest);
        }
        return subFolders;
    }

    /**
     * @return the list_files
     */
    public ArrayList<File> getList_files() {
        return list_files;
    }

    /**
     * @param list_files the list_files to set
     */
    public void setList_files(ArrayList<File> list_files) {
        this.list_files = list_files;
    }
}
