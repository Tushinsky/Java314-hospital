/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package menu;

import connection.Runquery;
import java.util.Arrays;
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
    private static List<Object[]> entities;

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
        entities = rq.getQueryEntities(sql);
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
                System.out.println("Выберите врача для записи на приём (для отмены введите 0): ");
                int id = scanner.nextInt();
                if(id != 0) {
                    // проверяем существование
                    if(isExistDictor(id) == false) {
                        System.out.println("Такого врача не существует. Сделайте правильный выбор!");

                    } else {
                        // выводим расписание выбранного врача с свободными датами
                        // и получаем выбор пользователя
                        int idSchedule = showFreeSchedule(id);
                        if(idSchedule != 0) {
                            // пользователь сделал свой выбор
                            String fieldName = "idPatient, idSchedule";
                            String fieldValue = "?,?";
                            int[] param = {patID, idSchedule};
                            Runquery rq = new Runquery("admission");
                            if(rq.addEntity(fieldName, fieldValue, param)) {
                                // устанавливаем флаг того, что данная дата занята
                                Runquery runquery = new Runquery();
                                String query = "update schedule set free = 1 where "
                                        + "id=" + idSchedule + ";";
                                if(runquery.updateFieldValue(query)) {
                                    System.out.println("Запись на приём успешна!");
                                    // выводим информацию по истории
                                    showAdmission(patID);
                                } else {
                                    System.out.println("Что-то пошло не так! Ошибка");
                                }

                            } else {
                                System.out.println("Что-то пошло не так! Ошибка");
                            }

                        }
                    }
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
    
    private void showAdmission(int idPatient) {
        String query = "select patients.name, patients.address, doctors.name," +
        " specialization.name, schedule.admissiondate," +
        " schedule.admissiontime from admission inner join" +
        " patients on admission.idPatient = patients.id inner join schedule on" +
        " schedule.id = admission.idSchedule inner join doctors on" +
        " schedule.idDoctor = doctors.id inner join" +
        " specialization on doctors.idSpecialization = specialization.id "
                + "where admission.idPatient=" + idPatient + ";";
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
    
    /**
     * Выводим свободные даты из расписания выбранного врача
     * @param id идентификатор выбранного врача
     * @return возвращает код выбранной записи
     */
    private int showFreeSchedule(int id) {
        String query = "SELECT name FROM DOCTORS WHERE ID=" + id;
        Runquery rq = new Runquery();
        Object name = rq.getFieldValue(query, 1);
        
        System.out.println("Расписание доктора: " + name);
        // запрос на выборку
        query = "SELECT id as ИДЕНТИФИКАТОР, admissiondate AS 'ДАТА ПРИЁМА', "
                + "admissiontime AS 'ВРЕМЯ ПРИЁМА' FROM schedule "
                + "WHERE idDoctor = " + id +" and free = 0;";
        outData(query);
        if(!entities.isEmpty()) {
            // если данные есть в наборе
            System.out.println("Выберите время: ");
            int idSchedule = scanner.nextInt();
            // проверяем выбор пользователя
            try{
                
                Object[] entity = entities.get(idSchedule - 1);// получаем выбранный элемент списка
                System.out.println("entity: " + Arrays.toString(entity));
                return Integer.parseInt(entity[1].toString());// возвращаем первый элемент в массиве - код

            } catch (IndexOutOfBoundsException ex) {
                System.out.println("Неверный выбор.");
                return 0;
            }
        }
        
        return 0;
    }
}
