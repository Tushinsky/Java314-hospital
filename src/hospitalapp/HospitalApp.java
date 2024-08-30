/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hospitalapp;

import connection.JDBCConnection;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import menu.HospitalMenu;

/**
 *
 * @author Sergii.Tushinskyi
 */
public class HospitalApp {

    private static JDBCConnection connection;
    private static HospitalMenu hospitalMenu;
    /**
     * @param args the command line arguments
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        try {
            // открываем соединение с базой данных
            System.out.print("Открывается соединение");
            int i = 0;
            while(i < 5) {
                System.out.print(".");
                Thread.sleep(500);
                i++;
            }
            System.out.println("");
            if(openConnection()) {
                // всё нормально, соединение установлено, отображаем меню
                System.out.println("Соединение открыто");
                System.out.println("");
                hospitalMenu = new HospitalMenu(new Scanner(System.in, "Windows-1251"));
                hospitalMenu.showMenu();
                while(true) {
//                    System.out.println("menuitem=" + carMenu.getMenuItem());
//                    carMenu.doChoice();
                    if(hospitalMenu.getMenuItem() == 0){
                        hospitalMenu.closeMenu();
                        break;
                    }
                }
                System.out.println("Спасибо за пользование системой управления больницей");
                i = 0;
                System.out.print("Соединение закрывается");
                while(i < 5) {
                    System.out.print(".");
                    Thread.sleep(500);
                    i++;
                }
                System.out.println("");
                closeConnection();
            } else {
                // если что-то пошло не так
                System.out.println("Что-то не сложилось.");
            }
        } catch (IOException | ClassNotFoundException ex) {
            // вывод ошибок
            Logger.getLogger(HospitalApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static boolean openConnection() throws IOException, 
            FileNotFoundException, ClassNotFoundException {
        try {
            // set drivername
            String driver = "com.mysql.cj.jdbc.Driver";
            String url = "jdbc:mysql://localhost:3306/hospital";
            // создаём соединение, проверяем его сосотяние
            connection = new JDBCConnection(driver, url, "root", "masterkey");
            return connection.isClosedConn() != true;
        } catch (SQLException ex){
            return false;
        }
    }
    
    private static void closeConnection() {
        try {
            if (connection != null && !connection.isClosedConn()) {
                // если соединение было создано и открыто, закрываем его
                JDBCConnection.getConn().close();
                if(connection.isClosedConn()){
                    System.out.println("Соединение закрыто!");
                }
            }
        } catch (SQLException ex) {
            // вывод ошибок
            Logger.getLogger(HospitalApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
