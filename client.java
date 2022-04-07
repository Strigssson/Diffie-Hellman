import javax.swing.*;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class client {

    public static void main(String[] args) throws IOException {

        String strp, strg, strcKey;

        //parametre p, g, kluc klienta, verejny kluc servra
        int p = 23;
        int g = 9;
        int cKey = 4;
        double A, secCKey, serverB;


        Socket client = new Socket("localhost", 8080);
        DataOutputStream output = new DataOutputStream(client.getOutputStream());


        strp = Integer.toString(p);
        output.writeUTF(strp); // odoslanie p

        strg = Integer.toString(g);
        output.writeUTF(strg); // odoslanie g

        A = (Math.pow(g, cKey) % p); //vypocet verejneho kluca klienta
        strcKey = Double.toString(A);
        output.writeUTF(strcKey); // odoslanie verejneho kluca klienta

        //klientov sukromny kluc
        //System.out.println("Sukromny kluc klienta " + cKey);

        //prijatie dat od servra
        DataInputStream input = new DataInputStream(client.getInputStream());

        serverB = Double.parseDouble(input.readUTF());
       // System.out.println("Od servera - jeho verejny kluc= " + serverB);

        secCKey = ((Math.pow(serverB, cKey)) % p); //vypocet spolocneho kluca pre symetricke sifrovanie
        //System.out.println("Tajny kluc pre symetricke sifrovanie= " + secCKey);

        String textToDecrypt = input.readUTF();

        String decryptedHtml = ownDecrypt(textToDecrypt, secCKey).toString();

        System.out.println((int) textToDecrypt.charAt(59));
        int test = 94 * ((28/94) + 1);
        System.out.println(28 + test);

        new client(decryptedHtml);

        client.close();
    }


    public client(String html){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //vytvorenie JEditor
                JEditorPane jEditorPane = new JEditorPane();

                //bez moznosti editovania
                jEditorPane.setEditable(false);

                //vytvorenie scrollpane
                JScrollPane scrollPane = new JScrollPane(jEditorPane);

                //pridanie htmleditorkit
                HTMLEditorKit kit = new HTMLEditorKit();
                jEditorPane.setEditorKit(kit);

                //styl pre HTMLko
                StyleSheet styleSheet = kit.getStyleSheet();
                styleSheet.addRule("body {color:#000; font-family:times; margin: 4px; }");
                styleSheet.addRule("h1 {color:red; font-size: 15vw; }");
                styleSheet.addRule("h2 {color:green; font-size: 12vw; }");
                styleSheet.addRule("p {color:purple; font-size: 10vw; }");

                //vytvorenie dokumentu, priradenie jeditorpane, priradenie HTMLka
                Document document = kit.createDefaultDocument();
                jEditorPane.setDocument(document);
                jEditorPane.setText(html);

                //pridanie vsetkeho do framu
                JFrame jFrame = new JFrame("Vykreselenie prijateho HTML klientom");
                jFrame.getContentPane().add(scrollPane, BorderLayout.CENTER);

                //pridanie zavretia aplikacie
                jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                //zobrazenie JFrame
                jFrame.setSize(new Dimension(600,400));

                //vycentrovanie JFrame pre viditelnost
                jFrame.setLocationRelativeTo(null);
                jFrame.setVisible(true);



            }
        });
    }


    public static StringBuffer ownDecrypt(String encryptedText, double key) {
        StringBuffer result = new StringBuffer();
        int stringLength = encryptedText.length();
        for (int i = 0; i < stringLength; i++) {
            char character = encryptedText.charAt(i);
            int ascii = (int) character;
            if ((ascii - key - i) < 32) {
                double decipher = ascii - key - i;
                while(decipher < 32){
                    decipher += 94;
                }
                result.append((char) decipher);
            }
            if((ascii - key - i >= 32)) {
                char nch = (char) ((ascii - key - i));
                result.append(nch);
            }
        }
        return result;
    }
}
