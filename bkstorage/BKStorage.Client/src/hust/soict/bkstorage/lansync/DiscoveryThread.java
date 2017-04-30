/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hust.soict.bkstorage.lansync;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author thinhnt
 */
public class DiscoveryThread implements Runnable {

    public static final String DISCOVERY_REQUEST = "INSTACE_DICOVERY_REQUEST";
    public static final String DISCOVERY_RESPONSE = "INSTACE_DICOVERY_RESPONSE";
    public static final String ELECTION_COMPLETED = "ELECTION_COMPLETED";
    private List<ElectionCompleteListener> electionFinishListeners;
    private DatagramSocket socket;

    public DiscoveryThread() {
        electionFinishListeners = new ArrayList<>();
    }

    @Override
    public void run() {
        try {
            socket = new DatagramSocket(8889, InetAddress.getByName("0.0.0.0"));
            socket.setBroadcast(true);
            while (!Thread.interrupted()) {
                byte[] recvBuf = new byte[15000];
                DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                socket.receive(packet);
                String message = new String(packet.getData()).trim();

                System.out.println("[LanServer]Receive " + message + " packet received from: "
                        + packet.getAddress().getHostAddress());

                if (message.equals(DISCOVERY_REQUEST)) {
                    byte[] sendData = DISCOVERY_RESPONSE.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData,
                            sendData.length, packet.getAddress(), packet.getPort());
                    socket.send(sendPacket);
                    System.out.println("[LanServer]Sent "
                            + DISCOVERY_RESPONSE + " response packet to: "
                            + sendPacket.getAddress().getHostAddress());
                } else if (message.startsWith(ELECTION_COMPLETED)) {
                    System.out.println("[LanClient]Receive "
                            + ELECTION_COMPLETED + " packet from: "
                            + packet.getAddress().getHostAddress());
                    String broadcastAddress = message.substring(message.indexOf(":") + 1);
                    fireElectionCompletedEvent(broadcastAddress);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(DiscoveryThread.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (socket != null) {
                socket.close();
                socket = null;
            }
        }
    }

    public void closeSocket() {
        if (socket != null) {
            socket.close();
            socket = null;
        }
    }

    public void addElectionListener(ElectionCompleteListener listener) {
        electionFinishListeners.add(listener);
    }

    public void removeElectionListener(ElectionCompleteListener listener) {
        electionFinishListeners.remove(listener);
    }

    private void fireElectionCompletedEvent(String broadcastAddress) {
        electionFinishListeners.stream().forEach((electionFinishListener) -> {
            electionFinishListener.onElectionCompleted(broadcastAddress);
        });
    }

    public static DiscoveryThread getInstance() {
        return DiscoveryThreadHolder.INSTANCE;
    }

    private static class DiscoveryThreadHolder {

        private static final DiscoveryThread INSTANCE = new DiscoveryThread();
    }

    public static void send() {
        // Find the server using UDP broadcast
        try {
            //Open a random port to send the package
            DatagramSocket c = new DatagramSocket();
            c.setBroadcast(true);

            byte[] sendData = "INSTACE_DICOVERY_REQUEST".getBytes();

            //Try the 255.255.255.255 first
//            try {
//                DatagramPacket sendPacket = new DatagramPacket(sendData,
//                        sendData.length, InetAddress.getByName("255.255.255.255"), 8889);
//                c.send(sendPacket);
//                System.out.println(">>> Request packet sent to: 255.255.255.255 (DEFAULT)");
//            } catch (Exception e) {
//            }
            // Broadcast the message over all the network interfaces
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();

                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue; // Don't want to broadcast to the loopback interface
                }

                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast == null) {
                        continue;
                    }
                    System.out.println("Broadcast: " + broadcast.getHostAddress());

                    // Send the broadcast package!
                    try {
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 8889);
                        c.send(sendPacket);
                    } catch (Exception e) {
                    }

                    System.out.println(">>> Request packet sent from: " + interfaceAddress.getAddress().getHostAddress());
                    System.out.println(">>> Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
                }
            }

            System.out.println(">>> Done looping over all network interfaces. Now waiting for a reply!");
            while (true) {
                //Wait for a response
                byte[] recvBuf = new byte[15000];
                DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
                c.receive(receivePacket);

                //We have a response
                System.out.println(">>> Broadcast response from server: " + receivePacket.getAddress().getHostAddress());

                //Check if the message is correct
                String message = new String(receivePacket.getData()).trim();
                if (message.equals("INSTACE_DICOVERY_RESPONSE")) {
                    //DO SOMETHING WITH THE SERVER'S IP (for example, store it in your controller)
                    System.out.println("Instance IP: " + receivePacket.getAddress());
                }
            }
            //Close the port!
//            c.close();
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
//        try {
//            DiscoveryThread r = DiscoveryThread.getInstance();
//            Thread t = new Thread(r);
//            t.start();
//            t.interrupt();
//            r.closeSocket();
//            t = new Thread(r);
//            t.start();
//            Thread.sleep(10000);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(InstanceDiscovery.class.getName()).log(Level.SEVERE, null, ex);
//        }
        send();
    }

}
