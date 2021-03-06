package Lesson_6._HW.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {//Initializable - для автоматического подключения клиента
    @FXML
    TextArea textArea;

    @FXML
    TextField textField;

    @FXML
    Button btn1;

    Socket socket;
    DataInputStream in;
    DataOutputStream out;

    final String IP_ADRESS = "localhost";
    final int PORT = 8189;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            socket = new Socket(IP_ADRESS, PORT);

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            //TODO UPD HW.Есть у препода
                            /*//TODO Исправление err1.java.io.EOFException.Удалил.Работает
                            //String str = in.readUTF();

                            //TODO UPD HW.Добавил чтобы клиенты не висели после закрытия сервера
                            //контроллер клиента принимает сообщение от ClientHandler сервера
                            //if(str.equals("/serverclosed")) break;

                            //textArea.appendText(str + "\n");*/

                            //TODO UPD HW.Нет у препода. Не нужно было бороться с исплючением в клиенте
                            //TODO Исправление err1.java.io.EOFException.Добавил.Работает
                            try{
                                String str = in.readUTF();
                                textArea.appendText(str + "\n");
                            } catch (EOFException e) {
                                //TODO временно
                                System.out.println("Приложение закрыто по EOFException");

                                //TODO закрыть приложение(окно) после остановки.Добавил.Работает
                                Platform.exit();

                                //TODO удалять нельзя, иначе бесконечный цикл
                                break;
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg() {
        try {
            out.writeUTF(textField.getText());
            textField.clear();
            textField.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //TODO UPD HW. Пример идеи - как закрыть клиента по нажатию крестика закрыть окно
    /*@FXML
    private void closeButtonAction(){
        // get a handle to the stage
        Stage stage = (Stage) closeButton.getScene().getWindow();
        out.writeUTF("/close");
        // do what you have to do
        stage.close();
    }*/

}
