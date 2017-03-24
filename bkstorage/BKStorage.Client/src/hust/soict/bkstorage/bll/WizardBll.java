/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.bll;

import hust.soict.bkstorage.dal.Dal;
import hust.soict.bkstorage.dal.WizardDal;
import hust.soict.bkstorage.exception.FileEmptyException;
import hust.soict.bkstorage.exception.OptionsMappingException;
import hust.soict.bkstorage.exception.SnapshotMappingException;
import java.io.IOException;
import java.sql.SQLException;

/**
 *
 *
 * @author toant_000
 */
public class WizardBll {

    public WizardBll() {
    }

    public void init() throws ClassNotFoundException, SQLException,
            OptionsMappingException, IOException, SnapshotMappingException {
        new Dal().init();
    }

    public String readServerName() throws FileEmptyException, IOException {
        WizardDal wizardDal = new WizardDal();
        String serverName = wizardDal.readServerName();
        if (serverName == null) {
            throw new FileEmptyException("Tập tin rỗng");
        }
        return serverName;
    }

    public String readPort() throws IOException, FileEmptyException {
        WizardDal wizardDal = new WizardDal();
        String port = wizardDal.readPort();
        if (port == null) {
            throw new FileEmptyException("Tập tin rỗng");
        }
        return port;
    }

    public String readUserName() throws FileEmptyException, IOException {
        WizardDal wizardDal = new WizardDal();
        String userName = wizardDal.readUserName();
        if (userName == null) {
            throw new FileEmptyException("Tập tin rỗng");
        }
        return userName;
    }

    public String readPassword() throws FileEmptyException, IOException {
        WizardDal wizardDal = new WizardDal();
        String password = wizardDal.readPassword();
        if (password == null) {
            throw new FileEmptyException("Tập tin rỗng");
        }
        return password;
    }

}
