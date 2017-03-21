package ch.tofind.commusica.network;

class Test {
    public static void main(String... args) {
        NetworkManager nm = new NetworkManager(8080);
        nm.serveClients();
    }
}
