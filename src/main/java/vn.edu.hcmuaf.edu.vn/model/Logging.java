package vn.edu.hcmuaf.edu.vn.model;

public class Logging {
    private static Logging install;
    private static Logging me() {
        if(install == null)  install = new Logging();
        return install;
    }
    private Logging() {

    }

    private void log(int level, int userID,String src, String content) {

    }
}
