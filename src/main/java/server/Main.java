package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class Main {
    private static final int PORT = 34522;
    static String pathToServerData = "C://Users//Bulat//IdeaProjects//SaveAnything//se-lab04-MySolution//src//main//java//server//data";

    static String pathToServerTextDatabase = "C:\\Users\\Bulat\\IdeaProjects\\SaveAnything\\se-lab04-MySolution\\src\\main\\java\\server\\database.txt";

    static HashMap<String, String> serverDataBase = new HashMap<>();

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

            switch (actionVar) {
                case (1): {
                    // TODO: 19.03.2023  make an action

                    updateHasMup();
                    String idOrName = dataInputStream.readUTF();
                    if (isNumber(idOrName)) { //если число

                        if (Main.serverDataBase.containsValue(idOrName)) {
                            File file = new File(Main.pathToServerData, getKey(Main.serverDataBase, idOrName));
                            String key = getKey(Main.serverDataBase, idOrName);

                            calltheSolution(key);

                            sendFileServerVersion(file.getPath());

                            System.out.println("code 200 " + key);
                        }
                    } else {
                        File file = new File(Main.pathToServerData, idOrName);
                        sendFileServerVersion(file.getPath());
                        System.out.println("code 200");
                    }
                    break;
                }


                case (2): {
                    updateHasMup(); //обновляем базу
                    String nameOfFile = dataInputStream.readUTF();
                    if (nameOfFile.equals("exit")) {
                        socket.close();
                        dataOutputStream.close();
                        dataInputStream.close();
                        break;
                    }
                    /**
                     * Типа если такое имя уже есть в базе, то ошибка
                     */
                    if (Main.serverDataBase.containsKey(nameOfFile)) {
                        System.out.println("Status code 403");
                        dataOutputStream.writeUTF("Status Code 403");
                        break;
                    } else {
                        dataOutputStream.writeUTF("200");
                        acceptAndSaveFile(nameOfFile);
                        updateHasMup();
//                        dataOutputStream.writeUTF("code 200");
                        System.out.println("code 200 = sucess ");
                    }
                    break;
                }
                case (3): {
                    // TODO: 19.03.2023  make an action
                    System.out.println("попали в состояние удалить");
                    updateHasMup();
                    String nameOrId = dataInputStream.readUTF();
                    System.out.println("имя файла " + nameOrId);

                    if (isNumber(nameOrId)) { //если число

                        if (Main.serverDataBase.containsValue(nameOrId)) {
                            System.out.println("поняли, что по айди работаем");
                            File file = new File(Main.pathToServerData, getKey(Main.serverDataBase, nameOrId));
                            if (file.delete()) { // если успешно удалили, то перезаписываем базу
                                updateHasMup();
                                System.out.println("ну вот хэшмап должен был обновиться");
                                Main.serverDataBase = rewriteHashMup(nameOrId);
                                rewriteDatabaseFile();
                                dataOutputStream.writeUTF("The response says that this file with id > " + nameOrId + " deleted successfully!");
                            } else System.out.println("The response says that this file is not found!");
                        }
                    } else if (!isNumber(nameOrId)) {
                        System.out.println("поняли, что по имени работаем");
                        File file = new File(Main.pathToServerData, nameOrId);
                        if (file.delete()) { // если успешно удалили, то перезаписываем базу
                            updateHasMup();
                            Main.serverDataBase = rewriteHashMup(nameOrId);
                            rewriteDatabaseFile();
                            dataOutputStream.writeUTF("The response says that this file with id > " + nameOrId + " deleted successfully!");
                        } else System.out.println("The response says that this file is not found!");
                    }
                    break;
                }

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

    private void calltheSolution(String key) throws IOException {

        dataOutputStream.flush();
        dataOutputStream.writeUTF(key);

    }


    public static void acceptAndSaveFile(String fileName) throws IOException {
//        Random random = new Random();
//        int bytes = 0;
//
//        File file = new File(Main.pathToServerData, fileName); //сложили место и имя файла
//        System.out.println("сложили место и имя файла");
//        FileOutputStream fileOutputStream = new FileOutputStream(file);
//
//
//        int uniqId = random.nextInt(0, 100);
//        boolean flag = true;
//
//        while (flag) {
//            if (Main.serverDataBase.containsValue(uniqId)) {
//                uniqId = random.nextInt(0, 100);
//            } else flag = false;
//        }
//
//        Main.serverDataBase.put(fileName, String.valueOf(uniqId)); // добавили значение в хешмап
//        System.out.println("добавили значение в хешмап");
//        addFileToTextServerDatabase(); // добавили эти значения в файл
//
//
//        /**
//         * т.е грубо говоря пока я не создам уникальный айди - я продолжаю его генерить.
//         */
//
//        System.out.println("Дошли до переноса файла");
//
//        long size = dataInputStream.readLong();
//        byte[] buffer =  new byte[(int) file.length()];
//
//        System.out.println("надеюсь дошел дальше");
//
//        while (size > 0
//                && (bytes = dataInputStream.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
//            fileOutputStream.write(buffer, 0, bytes);
//            size -= bytes;
//        }
//
//        System.out.println("File is Received");
//
//        dataOutputStream.writeInt(uniqId); // отправляем айдишник пользователю
//        System.out.println("status code 200");
////        fileOutputStream.close();

        int bytes = 0;
        Random random = new Random();
        File file = new File(Main.pathToServerData, fileName); //сложили место и имя файла
        FileOutputStream fileOutputStream = new FileOutputStream(file);

        long size = dataInputStream.readLong();
        byte[] buffer = new byte[4 * 1024];
        while (size > 0
                && (bytes = dataInputStream.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
            fileOutputStream.write(buffer, 0, bytes);
            size -= bytes;
        }

        System.out.println("File is Received");

        /**
         * т.е грубо говоря пока я не создам уникальный айди - я продолжаю его генерить.
         */
        int uniqId = random.nextInt(0, 100);
        boolean flag = true;

        while (flag) {
            if (Main.serverDataBase.containsValue(uniqId)) {
                uniqId = random.nextInt(0, 100);
            } else flag = false;
        }
        Main.serverDataBase.put(fileName, String.valueOf(uniqId)); // добавили значение в хешмап
        addFileToTextServerDatabase(); // добавили эти значения в файл

        System.out.println("типа отправил ");

        //проблема в том, что я вроде поток то очищаю, но все равно

//        dataOutputStream.flush();
//        dataOutputStream.write(uniqId);
//        System.out.println("типа отправил айди ");
    }

    static File file = new File(Main.pathToServerTextDatabase);

    public static void updateHasMup() { // получаем хешмап со всеми значениями, что есть в файле

        try (Scanner sc = new Scanner(file, StandardCharsets.UTF_8)) {
// тут проблема в том, что мне надо грамотно распарсить файл, но сейчас я попоробую хешмапом сделать

            while (sc.hasNext()) {
                String currentLine = sc.nextLine();
                String[] splitCurrentLine = currentLine.split(" ");
                Main.serverDataBase.put(splitCurrentLine[0], splitCurrentLine[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void addFileToTextServerDatabase() throws IOException {
        try (FileWriter pathToServerTextDatabase = new FileWriter(Main.pathToServerTextDatabase);) {
            for (Map.Entry<String, String> entry : Main.serverDataBase.entrySet()) {
                pathToServerTextDatabase.write(entry.getKey() + " " + entry.getValue() + "\n"); //записали имя файла в текст
            }
            updateHasMup();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    public static void sendFileServerVersion(String fileName, String pathToClientData) throws IOException {
//
//        int bytes = 0;
//
//        File file = new File(pathToClientData, fileName);
//        FileInputStream fileInput = new FileInputStream(file);
//
//
//        dataOutputStream.writeLong(file.length());
//        // тут массив может быть очень большой, необходимо будет это учитывать потом
//        byte[] buffer = new byte[(int) file.length()];
//        // в переменнную буфер считываем все байты из файла
//        fileInput.read(buffer);
//        for (int i = 0; i < buffer.length; i++) {
//            System.out.println(buffer[i]);
//        }
//        // отправили через сокет
//        dataOutputStream.write(buffer);
//
////        dataOutputStream.writeLong(file.length()); // отправляем байтовую длинну файла
////
////        byte[] buffer = new byte[(int) file.length()];
////
////        while ((bytes = fileInput.read(buffer)) != -1) {
////            dataOutputStream.write(buffer, 0, bytes);
////            dataOutputStream.flush();
////        }
//    }

    public static void sendFileServerVersion(String fileName) throws IOException {

        File file = new File(fileName);
        FileInputStream fileInputStream = new FileInputStream(file);

        dataOutputStream.writeLong(file.length());
        byte[] buffer = new byte[(int) file.length()];
        fileInputStream.read(buffer);

        for (int i = 0; i < buffer.length; i++) {
            System.out.println(buffer[i]);// просто проверка, что отправляется все
        }
        dataOutputStream.write(buffer);

    }

    public static boolean isNumber(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static <K, V> K getKey(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static void rewriteDatabaseFile() throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(Main.pathToServerTextDatabase);
        writer.print("");
        writer.close();

        try (FileWriter pathToServerTextDatabase = new FileWriter(Main.pathToServerTextDatabase);) {

            for (Map.Entry<String, String> entry : Main.serverDataBase.entrySet()) {
                pathToServerTextDatabase.write(entry.getKey() + " " + entry.getValue() + "\n"); //записали имя файла в текст
            }
            updateHasMup();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static HashMap<String, String> rewriteHashMup(String nameOrId) {
        HashMap<String, String> map = new HashMap<>();
        List<String> keys = new ArrayList<String>(Main.serverDataBase.keySet());
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = Main.serverDataBase.get(key);
            if (!(key.equals(nameOrId) || value.equals(nameOrId))) {
                map.put(key, value);
            }
        }
        return map;
    }
}
