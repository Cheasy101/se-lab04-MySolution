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
                    Scanner sc = new Scanner(System.in);
                    System.out.println("Do you want to get the file by name or by id (1 - name, 2 - id):");

                    String recieveAction = sc.nextLine();
                    if (recieveAction.equals("1")) {

                        System.out.println("Enter name of file");
                        Scanner sc1 = new Scanner(System.in);
                        String a = sc1.nextLine();
                        dataOutputStream.writeUTF(a);

                        writeBytesInNewFile(a, getArrayOfBytes()); //почему-то если все оставлять в 1 методе, то выполняется очень долго
                        System.out.println(dataInputStream.readUTF());
                        // получаем по имени

                    } else if (recieveAction.equals("2")) {
                        // получаем по id

                        System.out.println("Enter id of file");
                        Scanner sc1 = new Scanner(System.in);
                        String a = sc1.nextLine();
                        dataOutputStream.writeUTF(a);

                        writeBytesInNewFile(dataInputStream.readUTF(), getArrayOfBytes());
                        System.out.println(dataInputStream.readUTF());
                    }

                    break;
                case (2):
                    // TODO: 19.03.2023  make an action
                    System.out.println("Enter name of the file: > ");
                    String nameOfCurrentClientFile = scanner.next();
                    System.out.println("Enter name of the file to be saved on server: > ");
                    String nameOfNewFIle = scanner.next();

                    dataOutputStream.writeUTF(nameOfNewFIle);
                    System.out.println("Отправили имя ");


                    // TODO: 20.03.2023 обработать исключения. я неправильно сделал

                    if (dataInputStream.readUTF().equals("Status Code 403")) {
                        System.out.println("Ошибка, такое имя уже есть в базе");
                    } else {
                        sendFile(nameOfCurrentClientFile, pathToClientData);

                        System.out.println("The request was sent.");

                        int getId = dataInputStream.readInt();
                        System.out.println("Response says that file is saved! ID > " + getId);

                        break;
                    }
                case (3): {
                    // TODO: 19.03.2023  make an action
                    System.out.println("Do you want to delete the file by name or by id (1 - name, 2 - id): > ");

                    String deleteAction = scanner.next();
                    switch (deleteAction) {
                        case "1" -> {
                            System.out.println("Enter name of file");
                            Scanner sc1 = new Scanner(System.in);
                            String a = sc1.nextLine();
                            dataOutputStream.writeUTF(a);
                            System.out.println("отправили имя " + a);
                            System.out.println(dataInputStream.readUTF());
                        }
                        case "2" -> { //почемуто когда работаешь через 1 сканнер все ломается
                            System.out.println("Enter id of file");
                            Scanner sc2 = new Scanner(System.in);
                            String b = sc2.nextLine();
                            dataOutputStream.writeUTF(b);
                            System.out.println(dataInputStream.readUTF());
                        }
                    }
                }
            }

            // TODO: 19.03.2023  прописать логику для того, как сервер команты обрабатывает.

        } catch (
                IOException e) {
            throw new RuntimeException(e);
        }
    }

//    private static void doFirstAction(int nextStep) throws IOException { //1 - name, 2 - id
//        Scanner scanner = new Scanner(System.in);
//        switch (nextStep) {
//            case 1:
//                Scanner sc = new Scanner(System.in);
//                System.out.println("Do you want to get the file by name or by id (1 - name, 2 - id):");
//                String recieveAction = sc.nextLine();
//                if (recieveAction.equals("1")) {
//                    System.out.println("Enter name of file");
//                    Scanner sc1 = new Scanner(System.in);
//                    String a = sc1.nextLine();
//                    dataOutputStream.writeUTF(a);
//                    writeBytesInNewFile(a, getArrayOfBytesFromSocketStream());
//                    System.out.println(dataInputStream.readUTF());
//                    // получаем по имени
//                } else if (recieveAction.equals("2")) {
//                    // получаем по id
//                    System.out.println("Enter id of file");
//                    Scanner sc1 = new Scanner(System.in);
//                    String a = sc1.nextLine();
//                    dataOutputStream.writeUTF(a);
//                    //  String name = "C:\\Users\\fikus\\IdeaProjects\\se-lab04-tmp2223\\src\\main\\java\\client\\data\\" + a;
//
//                    writeBytesInNewFile(dataInputStream.readUTF(), getArrayOfBytesFromSocketStream());
//                    System.out.println(dataInputStream.readUTF());
//                }
//
//            case 2:
//                System.out.println("Enter id: > ");
//                // тут сейчас принимать файл будем
//                int fileId = scanner.nextInt();
////                receiveData(nameOfFile);
//
//        }
//    }

//    public static void receiveData(String nameOfFile) throws IOException {
//        int bytes = 0;
//        File file = new File(pathToClientData, nameOfFile); //сложили место и имя файла
//        FileOutputStream fileOutputStream = new FileOutputStream(file);
//
//        long size = dataInputStream.readLong();
//        byte[] buffer = new byte[4 * 1024];
//        while (size > 0
//                && (bytes = dataInputStream.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
//            fileOutputStream.write(buffer, 0, bytes);
//            size -= bytes;
//            System.out.println("переносим переносим");
//        }
//        System.out.println("File is Received");
//    }

    private static byte[] getArrayOfBytes() throws IOException {
        int length = (int) dataInputStream.readLong();
        byte[] resultBytes = new byte[length];
        dataInputStream.readFully(resultBytes, 0, length);
        return resultBytes; //получаю как бы файл из текущего потока
    }

    // по заданному имени создает файл в data и байты из массива записывает в новый файл
    private static void writeBytesInNewFile(String nameOfFile, byte[] bytes) throws FileNotFoundException {

        File file = new File(pathToClientData, nameOfFile);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        try {
            fileOutputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendFile(String fileName, String pathToClientData) throws IOException {

        int bytes = 0;

        File file = new File(pathToClientData, fileName);
        FileInputStream fileInputStream = new FileInputStream(file);


        dataOutputStream.writeLong(file.length());
        // тут массив может быть очень большой, необходимо будет это учитывать потом
        byte[] buffer = new byte[(int) file.length()];
        // в переменнную буфер считываем все байты из файла
        fileInputStream.read(buffer);
        for (int i = 0; i < buffer.length; i++) {
            System.out.println(buffer[i]);
        }
        // отправили через сокет
        dataOutputStream.write(buffer);

//        dataOutputStream.writeLong(file.length()); // отправляем байтовую длинну файла
//
//        byte[] buffer = new byte[(int) file.length()];
//
//        while ((bytes = fileInput.read(buffer)) != -1) {
//            dataOutputStream.write(buffer, 0, bytes);
//            dataOutputStream.flush();
//        }
    }
}




