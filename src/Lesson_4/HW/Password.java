package Lesson_4.HW;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Java Core. Продвинутый уровень.
 * Вебинар 03 июня 2019 MSK (UTC+3).
 * Урок 4. Регулярные выражения.
 * Home Work. Additional task. Регулярные выражения.
 * @author Yuriy Litvinenko.
 * Сделать проверку введенного пароля по слудующим критериям.
 * 1. Должна быть хотя бы одна заглавная буква
 * 2. Должна быть хотя бы одна цифра
 * 3. Минимум 8 символов
 * 4. Используем только Латиницу
 * 5. Должен быть хотя бы один спец. символ.
 * Реализация:
 * Проверяем первым делом, входят ли введенные символы в диапазон кодов(только латиница, цифры и
 *  спецсимволы в латинской раскладке клавиатуры) и заканчиваем проверку с выводом предупреждения;
 * Проверяем каждое отдельно и выдаем сообщения о каждом не соответствии требованиям:
 * - проверка релевантности длины пароля;
 * - проверка на присутствие в пароле хотя бы одной латинской буквы в нижнем регистре;
 * - проверка на присутствие в пароле хотя бы одной латинской буквы в верхнем регистре;
 * - проверка на присутствие в пароле хотя бы одной цифры;
 * - проверка на присутствие в пароле хотя бы одного спецсимвола.
 */
public class Password {
    static Scanner sc = new Scanner(System.in);
    static final int MIN_PASS_LENGTH = 8; //минимальная длина пароля
    static final int CHAR_CODE_START = 33; //начало диапазона кодов разрешенных символов
    static final int CHAR_CODE_END = 126; //конец диапазона кодов разрешенных символов
    static String pass; //пароль пользователя
    static String requirements = "Пароль должен состоять из латинских букв в верхнем и нижнем регистре, " +
            "цифр или спецсимволов.\n" +
            "Длина пароля не менее 8 символов.";//сообщение о требованиях к паролю

    public static void main(String[] args){
        showMessage(
                "Придумайте и введите пароль.\n" + requirements);
        do{
            pass = sc.nextLine();
        }
        while (!isPasswordValid(pass));
        showMessage("Новый пароль создан успешно!");
        sc.close();
    }

    static boolean isPasswordValid (String text){
        boolean flag = true;
        StringBuilder msg = new StringBuilder();

        //проверка на присутствие в пароле неразрешенных символов или нелатинских букв
        //принимаем только символы в диапазоне
        //TODO Не нашел такое в регулярных выражениях
        for (int i = 0; i < text.length(); i++) {
            if(text.charAt(i) < CHAR_CODE_START || text.charAt(i) > CHAR_CODE_END){
                msg.replace(msg.length(), msg.length(), "- присутствуют неразрешенные символы или нелатинские буквы\n");
                flag = false;
                break;
            }
        }
        if(flag){//если все символы из разрешенного диапазона кодов
            //проверка релевантности длины пароля
            if(text.length() < 8){
                msg.replace(msg.length(), msg.length(), "- длина пароля менее " + MIN_PASS_LENGTH + " символов\n");
                flag = false;
            }
            //проверка на присутствие в пароле хотя бы одной латинской буквы в нижнем регистре
            if(!matcher("[a-z]", text)){
                msg.replace(msg.length(), msg.length(), "- нет латинских букв в нижнем регистре\n");
                flag = false;
            }
            //проверка на присутствие в пароле хотя бы одной латинской буквы в верхнем регистре
            if(!matcher("[A-Z]", text)){
                msg.replace(msg.length(), msg.length(), "- нет латинских букв в верхнем регистре\n");
                flag = false;
            }
            //проверка на присутствие в пароле хотя бы одной цифры
            if(!matcher("\\d", text)){// ключ "\\d" заменяет "[0-9]"
                msg.replace(msg.length(), msg.length(), "- нет цифр\n");
                flag = false;
            }
            //проверка на присутствие в пароле хотя бы одного спецсимвола
            if(!matcher("\\W|_", text)){ //"\\W|_" заменяет "[^a-z&&[^A-Z]&&[^0-9]]"
                msg.replace(msg.length(), msg.length(), "- нет спецсимволов\n");
                flag = false;
            }
        }
        //если хотя бы одна проверка не пройдена выводим предупреждение
        if(!flag){
            msg.replace(0, 0, "Пароль не отвечает требованиям:\n");
            showMessage(msg.toString());
        }
        return flag;
    }

    /**
     * Метод проверки строки по заданному ключу паттерна.
     * @param key - ключ паттерна
     * @param text - проверяемая строка
     * @return true - проверка строки прошла успешно
     */
    static boolean matcher (String key, String text){
        Pattern pattern = Pattern.compile(key);
        Matcher matcher = pattern.matcher(text);
        return matcher.find();
    }

    /**
     * Метод вывода сообщений.
     * @param msg - строка сообщения.
     */
    static void showMessage(String msg){
        System.out.println(msg);
    }
}
