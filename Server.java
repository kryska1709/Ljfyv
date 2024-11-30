import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class Server {
    static final int SIZE = 10;
    char[][] field = new char[SIZE][SIZE];
    int totalShips; // Общее количество кораблей
    int hitCount; // Количество попаданий

    public static void main(String[] args) throws IOException {
        new Server().start();
    }

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Сервер запущен и ждет подключения...");

        try (Socket clientSocket = serverSocket.accept();
             InputStream inputStream = clientSocket.getInputStream();
             OutputStream outputStream = clientSocket.getOutputStream()) {

            System.out.println("Подключен: " + clientSocket.getInetAddress().toString());
            initializeField();

            // Основной игровой цикл
            while (true) {
                int request = inputStream.read();
                if (request == -1) break;

                int row = request / SIZE;
                int col = request % SIZE;
                System.out.println("Получен ход от клиента: " + request);

                if (field[row][col] == 'X') {
                    outputStream.write(1); // Попадание
                    field[row][col] = 'O'; // Занятое место (метка)
                    hitCount++; // Увеличиваем счетчик попаданий
                    System.out.println("Попадание по координатам: " + row + ", " + col);

                    // Проверка на победу
                    if (hitCount == totalShips) {
                        outputStream.write(2); // Все корабли найдены
                        System.out.println("Все корабли найдены! Игрок победил.");
                        break; // Выход из цикла
                    }
                } else {
                    outputStream.write(0); // Мимо
                    System.out.println("Мимо по координатам: " + row + ", " + col);
                }
                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeField() {
        // Инициализация поля
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                field[i][j] = ' ';
            }
        }

        totalShips = 5; // Определяем количество кораблей
        placeRandomShips(totalShips); // Разместим 5 кораблей
    }

    private void placeRandomShips(int numberOfShips) {
        Random random = new Random();
        int placedShips = 0;

        while (placedShips < numberOfShips) {
            int row = random.nextInt(SIZE);
            int col = random.nextInt(SIZE);

            // Проверка, можно ли разместить корабль
            if (field[row][col] == ' ') {
                field[row][col] = 'X'; // Установка корабля
                placedShips++;
                System.out.println("Корабль установлен на: " + row + ", " + col);
            }
        }
    }
}
