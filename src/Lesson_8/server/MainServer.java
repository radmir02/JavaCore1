package Lesson_8.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Vector;

/**
 * Java Core. Продвинутый уровень.
 * Вебинар 03 июня 2019 MSK (UTC+3).
 * Урок 8. Написание сетевого чата. Часть II.
 * @author Artem Evdokimov.
 * Improving the home work of Lesson_7.
 * DONE 1. Закрывать сокет, если нажать крестик закрытия окна в клиенте. Сейчас - исключение.
 * DONE 2. Добавить "черный" список каждому клиенту. Чтобы нельзя было отправлять общее
 *    или личное сообщение, если отправитель в черном списке у получателя - режим Антиспам.
 * DONE 3. Добавить отображение списка авторизованных пользователей.
 * DONE ERR1. В клиенте возникает java.net.SocketException: Socket closed. В коде урока таже проблема.
 * Это происходит только в следующем случае,
 * клиент авторизовался;
 * отлогинился, отправив /end (сообщение на отключение в консоли не появилось);
 * и закрыл окно по кресту.
 * Но, если не авторизовываться и закрыть окно или не отправлять /end и закрыть окно, то все работает.
 */
public class MainServer {
    private Vector<ClientHandler> clients;

    public MainServer() throws SQLException {

        //создаем список клиентов в виде синхронизированного ArrayList
        clients = new Vector<>();
        //инициализируем объекты с пустыми значениями, чтобы не получить исключение, что объекта нет
        ServerSocket server = null;
        Socket socket = null;

        try {
            //устанавливаем связь с БД в момент запуска сервера
            AuthService.connect();

            //создаем сокет для серверной части
            server = new ServerSocket(8189);
            System.out.println("Сервер запущен");

            while (true) {
                //создаем сокет для клиентской части. При создании объекта типа Socket неявно
                //устанавливается соединение клиента с сервером
                socket = server.accept();

                //TODO ERR. Server always write Client have connected.Удалил
                //System.out.println("Клиент подключился");

                //создаем объект нового клиента
                new ClientHandler(socket, this);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //отключаем БД при закрытии серверного приложения
            AuthService.disconnect();
        }
    }

    public Vector<ClientHandler> getClients() {
        return clients;
    }

    /**
     * Метод добавления клиента в списочный массив
     * @param client - подключивщийся клиент
     */
    public void subscribe(ClientHandler client){
        clients.add(client);

        //TODO hwImproving3.Добавил
        //рассылаем новый список клиентов
        broadcastClientList();
    }

    /**
     * Метод удаления клиента из списочного массива
     * @param client - отключивщийся клиент
     */
    public void unsubscribe(ClientHandler client){
        clients.remove(client);

        //TODO hwImproving3.Добавил
        //рассылаем новый список клиентов
        broadcastClientList();
    }

    //TODO hwImproving2.Удавил
    /**
     * Метод отправки всем одного сообщения
     * @param msg
     */
    /*public void broadcastMsg(String msg) {
        for (ClientHandler o : clients) {
            o.sendMsg(msg);
        }
    }*/
    //TODO hwImproving2.Добавил
    /**
     * Метод отправки всем одного сообщения с проверкой черного списка отправителя
     * @param from - отправитель
     * @param msg - рассылаемое сообщение
     */
    public void broadcastMsg(ClientHandler from, String msg) {
        for (ClientHandler o : clients) {
            if (!o.checkBlackList(from.getNick())) {
                o.sendMsg(msg);
            }
        }
    }

    /**
     * Метод сортировки и отправки персональных сообщений
     * @param str
     */
    public void sendMsgToNick(ClientHandler sender, String str){
        //TODO когда добавится адресная книга, этот блок не понадобится
        String nickOfRecipient;//ник адресата
        String msg;//текст сообщения адресату
        //разделяем по пробелу на splitLimit ячеек массива,
        //чтобы избежать ошибки при неполном вводе сервисного сообщения
        //limit = splitLimit - количество возвращаемых строк.
        int splitLimit = 3;
        String[] temp = str.split(" ", splitLimit);

        //проверка корректности синтаксиса сервисного сообщения
        if(temp.length >= splitLimit){
            //выделяем ник адресата
            nickOfRecipient = temp[1];
            //выделяем собственно текст сообщения
            msg = temp[2];

            //проверка не отправляется ли сообщение самому себе
            if(!sender.getNick().equals(nickOfRecipient)){
                for (ClientHandler c: clients) {
                    //в списке авторизованных ищем адресата по нику
                    if(c.getNick().equals(nickOfRecipient)){

                        //отправляем сообщение адресату
                        c.sendMsg("from " + sender.getNick() + ": " + msg);
                        //отправляем сообщение отправителю
                        sender.sendMsg("to " + nickOfRecipient + ": " + msg);
                        return;
                    }
                }
                //если в списке не нашлось клиента с таким ником (цикл не прервался по return)
                sender.sendMsg("Адресат с ником " + nickOfRecipient + " не найден в чате!");
            }
            else{
                //отправка предупреждения отправителю
                sender.sendMsg("Нельзя отправлять самому себе!");
            }
        }
        else{
            //отправка предупреждения отправителю
            sender.sendMsg("Неверный синтаксис сервисного сообщения!");
        }
    }

    //TODO hw7Update.Можно по другому, если проверку реализовать в ClientHandler перед вызовом sendMsgToNick
    /*public void sendPersonalMsg(ClientHandler from, String nickTo, String msg) {
        for (ClientHandler o : clients) {
            if (o.getNick().equals(nickTo)) {
                o.sendMsg("from " + from.getNick() + ": " + msg);
                from.sendMsg("to " + nickTo + ": " + msg);
                return;
            }
        }
        from.sendMsg("Клиент с ником " + nickTo + " не найден в чате");
    }*/

    /**
     * Метод проверки не авторизовался ли кто-то уже под этим ником(есть ли в списке клиент с таким ником)
     * @param nick - проверяемый ник
     * @return true, если такой клиент с таким ником уже авторизован
     */
    boolean isThisNickAuthorized(String nick){
        for (ClientHandler c: clients) {
            if(c.getNick().equals(nick)){
                return true;
            }
        }
        return false;
    }

    //TODO hwImproving3.Добавил
    //Метод отправки списка пользователей в виде строки всем клиентам
    public void broadcastClientList() {
        StringBuilder sb = new StringBuilder();
        sb.append("/clientlist ");
        //дополняем строку списком ников подключенных клиентов
        for (ClientHandler o : clients) {
            sb.append(o.getNick() + " ");
        }
        //собираем окончательное сообщение
        String out = sb.toString();
        //отправляем список каждому пользователю
        for (ClientHandler o : clients) {
            o.sendMsg(out);
        }
    }
}
