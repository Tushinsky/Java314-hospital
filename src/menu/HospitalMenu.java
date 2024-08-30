/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package menu;

import connection.Runquery;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sergii.Tushinskyi
 */
public class HospitalMenu {
    private final Scanner scanner;// чтение данных с клавиатуры
    private int menuItem = 1;

    public HospitalMenu(Scanner scanner) {
        this.scanner = scanner;
    }
    
    /**
     * Отображение меню
     */
    public void showMenu() {
        
        showMainMenuItem();
    }
    
    public int getMenuItem() {
        return menuItem;
    }
    
    /**
     * Закрываем меню
     */
    public void closeMenu() {
        scanner.close();
    }
    
    private void showMainMenuItem() {
        System.out.println("+---+------------------------+\n" +
        "| # | Наименование           |\n" +
        "+---+------------------------+\n" +
        "| 1 | Регистрация пациента   |\n" +
        "| 2 | Просмотр пациентов     |\n" +
        "| 3 | Просмотр врачей        |\n" +
        "| 4 | Запись на приём        |\n" +
        "+---+------------------------+\n" +
        "| 0 | Выход                  |\n" +
        "+---+------------------------+\n");
        doChoice();
    }
    
    private void doChoice() {
//        scanner.nextInt();
        System.out.println("Введите: ");
        int choice = scanner.nextInt();
        scanner.nextLine();
        menuItem = choice;
        switch(choice) {
            case 1:
                // регистрация пациента
                registration();
                showMainMenuItem();
                break;
            case 2:
                // просмотр списка зарегистрированнх пациентов
                
                showPatients();
                break;
            case 3:
                // просморт списка врачей
                showDoctors();
                showMainMenuItem();
                break;
            case 4:
                // запись на приём к врачу
                appointment();
                
                break;
                
        }
        
    }

    /**
     * Вывод данных, полученных в результате запроса
     * @param sql строка-запрос на получение данных
     */
    private static void outData(String sql) {
        Runquery rq = new Runquery();// объект для получения данных
        List<Object[]> entities = rq.getQueryEntities(sql);
        System.out.println("Вывод данных:");
        String[] columnname = rq.getColumnName();// получаем наименования столбцов запроса
        int[] maxWidth = new int[columnname.length];// массив размеров столбцов
        for(int i = 0; i < columnname.length; i++) {
            maxWidth[i] = columnname[i].length();// заполняем массив начальными значениями
        }
        // проходим по всем записям, находим максимальную ширину для каждого столбца
        entities.forEach((entity) -> {
            for(int i = 0; i < entity.length; i++) {
                if(entity[i].toString().length() > maxWidth[i]) {
                    maxWidth[i] = entity[i].toString().length();
                }
            }
        });
        int sum = 0;
        for(int w : maxWidth) {
            sum += w + 2;// подсчитываем ширину будущей таблицы
        }
        sum += columnname.length - 1;
        // рисуем таблицу
        String line = "+";
        for(int i = 0; i < sum; i++) {
            line = line.concat("-");
        }
        line = line.concat("+\n");
        System.out.print(line);
        // заголовок
        System.out.print("|");
        for(int i = 0; i < columnname.length; i++) {
            int count = maxWidth[i] - columnname[i].length();
            System.out.print(" " + columnname[i] + getNumSpace(count) + " |");
        }
        System.out.println();
        System.out.print(line);
        // данные
        entities.forEach((entity) -> {
            System.out.print("|");
            for(int i = 0;i < entity.length; i++) {
                int count = maxWidth[i] - entity[i].toString().length();
                System.out.print(" " + entity[i].toString() + getNumSpace(count) + " |");
            }
            System.out.println("");
        });
        System.out.print(line);
    }
    
    /**
     * Возвращает строку, состоящую из заданного количества пробелов
     * @param count
     * @return 
     */
    private static String getNumSpace(int count) {
        if(count == 0) {
            return "";
        }
        String retval = "";
        for(int i = 0; i < count; i++) {
            retval =  retval.concat(" ");
        }
        return retval;
    }

    private void registration() {
        String name;
        String address;
        String password;
        String passrepeat;
        System.out.println("Введите Ваше ФИО: ");
        name = scanner.nextLine();
        System.out.println("Введите Ваш адрес: ");
        address = scanner.nextLine();
        System.out.println("Придумайте пароль для доступа: ");
        password = scanner.nextLine();
        System.out.println("Повторите пароль: ");
        passrepeat = scanner.nextLine();
        if(Objects.equals(password, passrepeat)) {
            // если пароль введён корректно записываем в базу данных
            String fieldname = "name, address, password";// имена полей для добавления
            String fieldValue = "?,?,?";// значения полей для добавления
            String[] param = {name, address, password};
            Runquery rq = new Runquery("patients");// объект для выполнения запроса к базе данных
            if(rq.addEntity(fieldname, fieldValue, param)) {
                // если все прошло нормально, извещаем пользователя
                System.out.println("Регистрация прошла успешно!");

            } else {
                System.out.println("Ошибка! Что-то не сложилось!");

            }
        } else {
            System.out.println("Проверьте корректность ввода данных!");
        }
        
    }

    /**
     * Выводит информацию по зарегистрированным пациентам
     */
    private void showPatients() {
        String query = "SELECT name as ИМЯ, address as АДРЕС FROM PATIENTS;";
        outData(query);
        showMainMenuItem();
    }

    /**
     * Выводит информацию по врачам
     */
    private void showDoctors() {
        String query = "SELECT doctors.ID AS ИДЕНТИФИКАТОР, doctors.NAME AS ИМЯ, " +
        "specialization.NAME AS СПЕЦИАЛИЗАЦИЯ FROM doctors INNER JOIN specialization ON " +
        "specialization.ID=doctors.IDSPECIALIZATION;";
        outData(query);
        
    }

    
    private void appointment() {
        Object idPatient = getIdPatient();
//        System.out.println("id= " + idPatient);
        if(idPatient != null) {
            int patID = Integer.parseInt(idPatient.toString());
            // если пациент зарегистрирован, выводим его данные
            getPatient(patID);
            // выводим врачей
            showDoctors();
            
            try {
                String choice;
                while(true) {
                    System.out.println("Выберите врача для записи на приём (для отмены введите 0): ");
                    choice = scanner.nextLine();
                    if(choice.equals("0")) break;
                    int id = Integer.parseInt(choice);
                    if(isExistDictor(id) == false) {
                        System.out.println("Такого врача не существует. Сделайте правильный выбор!");
                        break;
                    }
                    System.out.println("Выберите дату (yyyy-mm-dd): ");
                    choice = scanner.nextLine();
                    if(choice.equals("0")) break;
                    String date = choice;
                    System.out.println("Выберите время (hh:mm): ");
                    choice = scanner.nextLine();
                    if(choice.equals("0")) break;
                    String time = choice;
                    String fieldName = "idDoctor, idPatient, admission_date";
                    String fieldValue = "?,?,?";
                    Object[] param = {id, patID, (date + " " + time)};
//                        Runquery rq = new Runquery("admission");
//                        if(rq.addEntity(fieldName, fieldValue, new Class[]{Integer.class, 
//                            Integer.class, String.class}, param)) {
//                            System.out.println("Запись на приём успешна!");
//                            // выводим информацию по истории
//                            showAdmission();
//                            showMainMenuItem();
//                        }


                }
            } catch (InputMismatchException ex) {
                // вывод ошибок
                Logger.getLogger(HospitalMenu.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("Пациент не найден в системе. Зарегистрируйтесь, пожалуйста\n"
                    + "для дальнейшего пользования системой.");
            
        }
        showMainMenuItem();
    }
    
    /**
     * Возвращает идентификатор пациента, записывающегося на приём к врачу
     * @return id - идентификатор пациента или null, если не найден
     */
    private Object getIdPatient() {
        try {
            System.out.println("Для записи введите пароль: ");
            String password = scanner.nextLine();
            String query = "SELECT ID FROM PATIENTS WHERE PASSWORD='" + password + "'";// строка-запрос на выборку
            Runquery rq = new Runquery();
            Object id = rq.getFieldValue(query, 1);
            System.out.println("ID=" + id);
            return id; 
        } catch(InputMismatchException ex) {
            // вывод ошибок
            Logger.getLogger(HospitalMenu.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    private void showAdmission() {
        String query = "select patients.name, patients.address, doctors.name, "
                + "specialization.name, admission.admission_date from "
                + "admission inner join patients on admission.idPatient = patients.id "
                + "inner join doctors on doctors.id=admission.idDoctor "
                + "inner join specialization on doctors.idSpecialization= specialization.id;";
        outData(query);
    }
    
    /**
     * Проверяет, существует ли запись в таблице врачей с данным идентификатором
     * @param id идентификатор врача
     * @return true, если запись есть, иначе false
     */
    private boolean isExistDictor(int id) {
        String query = "SELECT ID FROM DOCTORS WHERE ID=" + id;
        Runquery rq = new Runquery();
        Object idDoctor = rq.getFieldValue(query, 1);
        if(idDoctor == null) {
            return false;
        }
        return Integer.parseInt(idDoctor.toString())== id;
    }
    
    private void getPatient(int id) {
        String query = "SELECT NAME FROM PATIENTS WHERE ID=" + id + ";";// строка-запрос на выборку
        Runquery rq = new Runquery();
        Object patientName = rq.getFieldValue(query, 1);
        System.out.println("Запись на приём: пациент " + patientName.toString());
    }
}
