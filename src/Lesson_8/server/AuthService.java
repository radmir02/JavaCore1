package Lesson_8.server;

import java.sql.*;

/**
 * Класс для организации сервиса авторизации и связи с БД
 * Связь БД и приложения осуществляется через посредника, JDBC драйвер(библиотека).
 */
public class AuthService {

    //объект для установления связи
    private static Connection connection;
    //объект для отправки запросов в JDBC драйвер(библиотека) с помощью метода connect(),
    // который переправляет его в БД.
    // И получает результат (объект класса ResultSet) с помощью executeQuery(sql)
    private static Statement stmt;

    /**
     * Метод подключения к БД
     * //@throws SQLException
     */
    public static void connect() throws SQLException {
        try {
            // обращение к драйверу. просто инициализирует класс, с которым потом будем работать
            Class.forName("org.sqlite.JDBC");
            // установка подключения
            connection = DriverManager.getConnection("jdbc:sqlite:mainDB.db");
            // создание Statement для возможности отправки запросов
            stmt = connection.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод запрашивающий в БД nickname по совпадению логина и пароля.
     * @param login - логин
     * @param pass - пароль
     * @return значение колонки nickname, если сопадение
     */
    public static String getNickByLoginAndPass(String login, String pass) {
        // формирование запроса. '%s' - для последовательного подставления значений в соотвествующее место
        String sql = String.format("SELECT nickname FROM main where login = '%s' and password = '%s'", login, pass);

        try {
            // оправка запроса и получение ответа из БД
            ResultSet rs = stmt.executeQuery(sql);

            // если есть строка возвращаем результат, если нет, то вернеться null
            if(rs.next()) {
                //индекс колонки в запросе (здесь 1 - это nickname). Но индексация в БД начинается с 1
                //можно также вызвать и по columnLabel (здесь было бы "nickname"), но по индексу быстрее
                return rs.getString(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Метод отключения от БД
     */
    public static void disconnect() {
        try {
            // закрываем соединение
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
