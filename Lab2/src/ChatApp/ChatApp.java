package ChatApp;

import javax.swing.*;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

public class ChatApp {
    public static void main(String args[]) throws IOException {
        JFrame frame = new JFrame("Chat App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(640,640);

        JTextArea heading = new JTextArea();
        heading.setBounds(10,10,600,50);
        heading.setText("Chat App");
        heading.setEditable(false);
        frame.add(heading);

        JTextField inputField = new JTextField(" <Your message> ");
        inputField.setBounds(10, 410, 500, 30);
        frame.add(inputField);

        JTextArea outputArea = new JTextArea();
        outputArea.setBounds(10,100,600,300);
        outputArea.setEditable(false);
        frame.add(outputArea);

        JButton button = new JButton("Send");
        button.setBounds(530, 410, 80, 30);
        button.setBackground(Color.BLUE);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Sender(outputArea).start();
            }
        });
        frame.add(button);

        frame.setLayout(null);
        frame.setVisible(true);

        new Receiver("Server", inputField).start();
    }
}

class Sender extends Thread{

    JTextArea outputArea;
    public Sender(JTextArea outputArea) {
        this.outputArea=outputArea;
    }

    public void run() {
        try {
            DatagramSocket socket = new DatagramSocket();
            byte[] buf = new byte[256];
            InetAddress adress = InetAddress.getByName("localhost");
            DatagramPacket packet = new DatagramPacket(buf, buf.length, adress, 4445);
            socket.send(packet);

            packet= new DatagramPacket(buf, buf.length);
            socket.receive(packet);

            String received = new String(packet.getData());
            outputArea.append("\n (Me)>"+received);

            socket.close();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}

class Receiver extends Thread{

    private DatagramSocket socket = null;
    private JTextField inputField;

    public Receiver(String name, JTextField inputField) throws IOException {
        super(name);
        socket = new DatagramSocket(4445);
        this.inputField = inputField;
    }

    public void run() {
        while(true) {
            try {
                byte[] buf = new byte[256];

                DatagramPacket packet = new DatagramPacket(buf, buf.length);

                socket.receive(packet);
                String dString = null;
                dString = inputField.getText();
                buf = dString.getBytes();

                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                packet = new DatagramPacket(buf, buf.length, address, port);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}