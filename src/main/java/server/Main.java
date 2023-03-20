package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class Main {
    private static final int PORT = 34522;
    static String pathToServerData = "C://Users//Bulat//IdeaProjects//SaveAnything//se-lab04-MySolution//src//main//java//server//data";

    static String pathToServerTextDatabase = "C:\\Users\\Bulat\\IdeaProjects\\SaveAnything\\se-lab04-MySolution\\src\\main\\java\\server\\database.txt";

    static HashMap<String, Integer> serverDataBase = new HashMap<>();

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(PORT)) {

            System.out.println("Сервер запущен и готов к работе...");
            while (true) {
                Session session = new Session(server.accept());
                session.start();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

class Session extends Thread {
    private final Socket socket;
    private static DataOutputStream dataOutputStream;
    private static DataInputStream dataInputStream;

    public Session(Socket socketForClient) {
        this.socket = socketForClient;
    }

    public void run() {
        try {
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            int actionVar = dataInputStream.readInt();
//            String nameOfUser = dataInputStream.readUTF(); //читаем че юзер говорит
            System.out.println("Получили action: " + actionVar);
//            actions(actionVar);

            switch (actionVar) {
                case (1):
                    // TODO: 19.03.2023  make an action
                    break;
                case (2):
                    // TODO: 19.03.2023  make an action

                    updateHasMup(); //обновляем базу

                    for (Map.Entry<String, Integer> entry : Main.serverDataBase.entrySet()) {
                        System.out.println(entry.getKey() + " " + entry.getValue() + "\n");
                    }

                    String nameOfFile = dataInputStream.readUTF();

                    /**
                     * Типа если такое имя уже есть в базе, то ошибка
                     */

//                    if (Main.serverDataBase.containsKey(nameOfFile)) {
//                        System.out.println("Status code 403");
//                        dataOutputStream.writeUTF("Status Code 403");
//                        break;
//                    } else { todo я неправильно обрабатываю исключения

                    System.out.println("Получили имя " + nameOfFile);
                    acceptAndSaveFile(nameOfFile);
//                    }
                    break;
                case (3):
                    // TODO: 19.03.2023  make an action
                    break;
            }

//            output.writeUTF(nameOfUser);
//            System.out.println("Отправили обратно ");
//
//            double a = dataInputStream.readDouble();
//            System.out.println("получили первое число");
//            double b = dataInputStream.readDouble();
//            System.out.println("Получили второе число");
//
//            Double afterMath = (3 * Math.pow(Math.cos(a - (Math.PI / 6)), 2)) / (1 / 2 + Math.sin(b * b));
//            output.writeDouble(afterMath);
////            output.writeUTF(String.valueOf(afterMath));
//
//            System.out.println("Отправили значение ");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void acceptAndSaveFile(String fileName) throws IOException {

        int bytes = 0;
        Random random = new Random();
        File file = new File(Main.pathToServerData, fileName); //сложили место и имя файла
        System.out.println("сложили место и имя файла");
        FileOutputStream fileOutputStream = new FileOutputStream(file);


        int uniqId = random.nextInt(0, 100);
        boolean flag = true;

        while (flag) {
            if (Main.serverDataBase.containsValue(uniqId)) {
                uniqId = random.nextInt(0, 100);
            } else flag = false;
        }

        Main.serverDataBase.put(fileName, uniqId); // добавили значение в хешмап
        System.out.println("добавили значение в хешмап");
        addFileToTextServerDatabase(); // добавили эти значения в файл


        /**
         * т.е грубо говоря пока я не создам уникальный айди - я продолжаю его генерить.
         */

        System.out.println("Дошли до переноса файла");

        long size = dataInputStream.readLong();
        byte[] buffer = new byte[4 * 1024];
        while (size > 0
                && (bytes = dataInputStream.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
            fileOutputStream.write(buffer, 0, bytes);
            size -= bytes;
            System.out.println("переносим переносим");
        }

        System.out.println("File is Received");
//        fileOutputStream.close();
    }

    static File file = new File(Main.pathToServerTextDatabase);

    public static void updateHasMup() { // получаем хешмап со всеми значениями, что есть в файле

        try (Scanner sc = new Scanner(file, StandardCharsets.UTF_8)) {
// тут проблема в том, что мне надо грамотно распарсить файл, но сейчас я попоробую хешмапом сделать

            while (sc.hasNext()) {
                String currentLine = sc.nextLine();
                String[] splitCurrentLine = currentLine.split(" ");
                Main.serverDataBase.put(splitCurrentLine[0], Integer.valueOf(splitCurrentLine[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void addFileToTextServerDatabase() throws IOException {
        try (FileWriter pathToServerTextDatabase = new FileWriter(Main.pathToServerTextDatabase);) {
            for (Map.Entry<String, Integer> entry : Main.serverDataBase.entrySet()) {
                pathToServerTextDatabase.write(entry.getKey() + " " + entry.getValue() + "\n"); //записали имя файла в текст
            }
            updateHasMup();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
