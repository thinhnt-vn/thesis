/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.cli;

import hust.soict.bkstorage.exceptions.CreateUserException;
import hust.soict.bkstorage.exceptions.EmptyCommandException;
import hust.soict.bkstorage.exceptions.ErrorCommandException;
import hust.soict.bkstorage.exceptions.StartupExeption;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Hiển thị giao diện dòng lệnh cho người dùng.
 *
 * @author toant_000
 */
public class CommandLine extends Thread {

    private boolean finished;
    private CommandParser commandParser;

    public CommandLine(CommandParser commandParser) {
        this.commandParser = commandParser;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);

        while (!finished) {
            newCommandLine();
            String command = scanner.nextLine();
            commandParser.setCommand(command);
            try {
                commandParser.parse();
            } catch (EmptyCommandException ex) {
            } catch (ErrorCommandException | StartupExeption | NumberFormatException |
                    CreateUserException | IOException ex) {
                newLine(ex.getMessage());
            } catch (ClassNotFoundException ex) {
                newLine("Lỗi! Không nạp được Driver.");
            } catch (SQLException ex) {
                newLine("Lỗi! Không kết nối được tới SQL Server.");
            }
        }

    }

    private void newCommandLine() {
        System.out.print("\n\t->MyStorage$");
    }

    private void newLine(String meassage) {
        System.out.print("\t->" + meassage);
    }

    public void kill() {
        finished = true;
    }

    public void execute(String command) {

    }
}
