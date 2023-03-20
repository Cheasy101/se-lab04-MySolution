package client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 34522;
    private static DataOutputStream dataOutputStream;
    private static DataInputStream dataInputStream;

    static String pathToClientData = "C://Users//Bulat//IdeaProjects//SaveAnything//se-lab04-MySolution//src//main//java//client//data";

    public static void main(String[] args) throws IOException {

        try {
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataInputStream = new DataInputStream(socket.getInputStream());

            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter action (1 - get a file, 2 - save a file, 3 - delete a file): > ");
            int action = scanner.nextInt();
            dataOutputStream.writeInt(action); //отправили значение серверу

            switch (action) {
                case (1):
                    // TODO: 19.03.2023  make an action
                    break;
                case (2):
                    // TODO: 19.03.2023  make an action
                    System.out.println("Enter name of the file: > ");
                    String nameOfCurrentClientFile = scanner.next();
                    System.out.println("Enter name of the file to be saved on server: > ");
                    String nameOfNewFIle = scanner.next();

                    if (nameOfNewFIle.equals("")) {
                        nameOfNewFIle = nameOfCurrentClientFile;
                    }

                    dataOutputStream.writeUTF(nameOfNewFIle);
                    System.out.println("Отправили имя ");

                    // TODO: 20.03.2023 обработать исключения. я неправильно сделал 
                    
//                    if (dataInputStream.readUTF().equals("Status Code 403")) {
//                        System.out.println("Ошибка, такое имя уже есть в базе");
//                    } else {
                        sendFile(nameOfCurrentClientFile);
                        System.out.println("The request was sent.");
//                    }


//                    dataInputStream.close();
//                    dataOutputStream.close();

                    // должен считать имя файла и по байтам передать его на сервер
                    /**
                     * Сейчас я должен как-то грамотно ообработать на сервере все это
                     */
                    break;
                case (3):
                    // TODO: 19.03.2023  make an action
                    break;
            }

            // TODO: 19.03.2023  прописать логику для того, как сервер команты обрабатывает.

        } catch (
                IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sendFile(String fileName) throws IOException {

        int bytes = 0;

        File file = new File(pathToClientData, fileName);
        FileInputStream fileInput = new FileInputStream(file);

        dataOutputStream.writeLong(file.length()); // отправляем байтовую длинну файла

        byte[] buffer = new byte[4 * 1024];
        while ((bytes = fileInput.read(buffer)) != -1) {
            dataOutputStream.write(buffer, 0, bytes);
            dataOutputStream.flush();
        }
        dataInputStream.close();
    }

}




