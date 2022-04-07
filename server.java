import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;



public class server {

    public static void main(String[] args) throws IOException {

        // Kluc servra
        int serKey = 3;
        // System.out.println("sukromny kluc servra= " + serKey);

        // Klient, p, g a kluc
        double cP, cG, cA, B, secSKey;
        String Bstr;

        ServerSocket server = new ServerSocket(8080);
        System.out.println("Server bezi na porte 8080...");
        while (true) {
            Socket clientSocket = server.accept();
            System.out.println("Pripojil sa klient " + clientSocket.getRemoteSocketAddress());

            //Data od klienta
            DataInputStream input = new DataInputStream(clientSocket.getInputStream());

            cP = Integer.parseInt(input.readUTF()); // spracovanie p klienta
            //System.out.println("p klienta= " + cP);

            cG = Integer.parseInt(input.readUTF()); // spracovanie g klienta
            //System.out.println("g klienta= " + cG);

            cA = Double.parseDouble(input.readUTF()); // spracovanie a (verejneho kluca) klienta
            // System.out.println("verejny kluc klienta= " + cA);

            B = ((Math.pow(cG, serKey)) % cP); // vypocet verejneho kluca servra
            Bstr = Double.toString(B);

            //Data klientovi
            DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());
            output.writeUTF(Bstr);

            secSKey = ((Math.pow(cA, serKey)) % cP); //vypocet tajneho kluca pre symetricke sifrovanie

            //kontrola tajneho kluca
            // System.out.println("Tajny kluc pre symetricke sifrovanie= " + secSKey);

            // vytovrenie HTMLka pre poslanie klientovi
            String htmlString = "<html>"
                    + "<body>"
                    + "<h1>Vitaj klient! Ak sa ti tento text zobrazil zrozumitelne, tak si opravneny ho vidiet.</h1>"
                    + "<h2>Tieto udaje boli uspesne zasifrovane a desifrovane pomocou modifikovanej cezarovej sifry</h2>"
                    + "<p>Klucom pre tuto sifru je vysledok Diffie-Hellmanovej vymeny klucov medzi klientom(tebou) a servrom(mnou)</p>"
                    + "</body>";

            String encrypted = ownEncrypt(htmlString, secSKey).toString();
            output.writeUTF(encrypted);

        }
    }

    public static StringBuffer ownEncrypt(String plainText, double key) {
        StringBuffer result = new StringBuffer();
        int stringLength = plainText.length();
        for (int i = 0; i < stringLength; i++) {
            char character = plainText.charAt(i);
            int ascii = (int) character;
            if ((ascii + key + i) > 126) {
                char nch;
                if (((ascii + key + i) % 94) < 32) {
                    nch = (char) (((ascii + key + i) % 94) + 94);
                } else {
                    nch = (char) (((ascii + key + i) % 94));
                }
                result.append(nch);
            } else {
                char nch = (char) ((ascii + key + i));
                result.append(nch);
            }
        }
        return result;
    }
}