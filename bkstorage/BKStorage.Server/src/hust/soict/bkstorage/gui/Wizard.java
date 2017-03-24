/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.gui;

import hust.soict.bkstorage.bll.CommandLineBll;
import hust.soict.bkstorage.cli.CommandLine;
import hust.soict.bkstorage.cli.CommandParser;
import hust.soict.bkstorage.constants.Options;
import hust.soict.bkstorage.exceptions.ErrorCommandException;
import hust.soict.bkstorage.exceptions.StartupExeption;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author toant_000
 */
public class Wizard {

    public static void main(String[] args) {
        try {
            Options.load(Options.CONFIG_FILE);
            CommandLineBll commandLineBll = new CommandLineBll();
            commandLineBll.connect2DB(Options.MYSQL_HOST_VALUE, Options.MYSQL_PORT_VALUE,
                    Options.MYSQL_USER_NAME_VALUE, Options.MYSQL_PASSWORD_VALUE,
                    Options.MYSQL_DB_NAME_VALUE);
            commandLineBll.startup(Options.BIND_PORT_VALUE);
        } catch (IOException | NumberFormatException | ClassNotFoundException |
                SQLException | ErrorCommandException ex) {
            System.out.println("Không thể kết nối đến CSDL!");
            Logger.getLogger(Wizard.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        } catch (StartupExeption ex) {
            System.out.println("Không thể khởi động dịch vụ!");
            Logger.getLogger(Wizard.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
        CommandLine commandLine = new CommandLine(new CommandParser());
        commandLine.start();
    }
}
