/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.gui;

import hust.soict.bkstorage.cli.CommandLine;
import hust.soict.bkstorage.cli.CommandParser;
import hust.soict.bkstorage.constants.Options;
import java.io.IOException;
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
        } catch (IOException ex) {
            Logger.getLogger(Wizard.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
        CommandLine commandLine = new CommandLine(new CommandParser());
        commandLine.start();
    }
}
