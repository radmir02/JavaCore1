package a2netChatWithPrStage.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Java Core. Продвинутый уровень.
 * Вебинар 03 июня 2019 MSK (UTC+3).
 * Урок 8. Написание сетевого чата. Часть II.
 * Home work.
 * @author Yuriy Litvinenko.
 * DONE 1. Форматирование сообщения, свой-чужой. Свои справа, чужие слева.
 * DONE 2. Регистрация нового пользователя. Через БД.
 * DONE 3. Переподключение клиента при падении сервера.
 * DONE 4. Хранить черный список в БД
 * DONE + проверка л/с(чтобы нельзя было отправлять общее или личное сообщение, если отправитель
 *     в черном списке у отправителя и получателя). Добавить исключение получения сообщения всем на стороне клиента,
 *     у которого отправитель в черном списке.
 * 5. Переработать отправку л/с с учетом он-лайн списка (разработать диалоговое окно).
 */
public class Main extends Application {

    //создаем экземпляр контроллера
    Controller contr;

    @Override
    public void start(Stage primaryStage) throws Exception{
        //чтобы получить доступ к контроллеру
        //лоадер вынесли отдельно, чтобы с ним удобнее было работать
        FXMLLoader loader = new FXMLLoader();
        //с помощью метода getResourceAsStream извлекаем данные из лоадера, чтобы
        //вызвать метод getController для получения контроллера
        Parent root = loader.load(getClass().getResourceAsStream("sample.fxml"));
        contr = loader.getController();

        primaryStage.setTitle("Chat 2k19");
        Scene scene = new Scene(root, 350, 350);
        primaryStage.setScene(scene);
        primaryStage.show();

        //определяем действия по событию закрыть окно по крестику через лямбда
        //лямбда здесь - это замена анонимного класса типа new Runnable
        //в лямбда event - аргумент(здесь некое событие), {тело лямбды - операции}
        primaryStage.setOnCloseRequest(event -> {
            contr.dispose();//dispose - располагать, размещать
            //сворачиваем окно
            Platform.exit();
            //указываем системе, что выход без ошибки
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
