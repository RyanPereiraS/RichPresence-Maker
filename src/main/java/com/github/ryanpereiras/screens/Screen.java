package com.github.ryanpereiras.screens;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Screen extends JFrame implements ActionListener {
    private boolean isRunning = false;
    private final DiscordRPC client;
    private final DiscordRichPresence richPresence;
    private final JButton conectar;
    private final JButton desconectar;
    private final JButton atualizar;
    private final JTextField idField;
    private final JTextField detailsField;
    private final JTextField largeImageKeyField;
    private final JTextField largeImageTextField;
    private final JTextField smallImageKeyField;
    private final JTextField smallImageTextField;
    private final JTextField statusField;

    public Screen(){
        // Configura a janela
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("RichPresence Makers");
        setSize(800, 500);
        setResizable(false);
        setLocationRelativeTo(null);

        // Define o layout da janela
        setLayout(new BorderLayout());

        // Cria o painel de configuração
        JPanel configPanel = new JPanel();
        configPanel.setLayout(new GridLayout(7, 2));
        configPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY),
                "Configurações",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Arial", Font.PLAIN, 14),
                Color.BLACK));

        // Cria as caixas de texto
        idField = new JTextField();
        detailsField = new JTextField();
        largeImageKeyField = new JTextField();
        largeImageTextField = new JTextField();
        smallImageKeyField = new JTextField();
        smallImageTextField = new JTextField();
        statusField = new JTextField();

        // Cria os rótulos para as caixas de texto
        JLabel idLabel = new JLabel("ID:");
        JLabel detailsLabel = new JLabel("Detalhes:");
        JLabel largeImageKeyLabel = new JLabel("LargeImageKey:");
        JLabel largeImageTextLabel = new JLabel("LargeImageText:");
        JLabel smallImageKeyLabel = new JLabel("SmallImageKey:");
        JLabel smallImageTextLabel = new JLabel("SmallImageText:");
        JLabel statusLabel = new JLabel("Status:");



        // Define a margem entre o rótulo e a caixa de texto
        int margin = 5;
        idLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, margin));
        detailsLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, margin));
        largeImageKeyLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, margin));
        largeImageTextLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, margin));
        smallImageKeyLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, margin));
        smallImageTextLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, margin));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, margin));

        // Adiciona as caixas de texto e rótulos ao painel de configuração
        configPanel.add(idLabel);
        configPanel.add(idField);
        configPanel.add(detailsLabel);
        configPanel.add(detailsField);
        configPanel.add(largeImageKeyLabel);
        configPanel.add(largeImageKeyField);
        configPanel.add(largeImageTextLabel);
        configPanel.add(largeImageTextField);
        configPanel.add(smallImageKeyLabel);
        configPanel.add(smallImageKeyField);
        configPanel.add(smallImageTextLabel);
        configPanel.add(smallImageTextField);
        configPanel.add(statusLabel);
        configPanel.add(statusField);

        // Adiciona o painel de configuração à janela
        add(configPanel, BorderLayout.CENTER);

        // Cria os botões
        conectar = new JButton("Conectar");
        conectar.setFont(new Font("Arial", Font.PLAIN, 12));
        conectar.addActionListener(this);

        atualizar = new JButton("Atualizar");
        atualizar.setFont(new Font("Arial", Font.PLAIN, 12));
        atualizar.setEnabled(false);
        atualizar.addActionListener(this);

        desconectar = new JButton("Desconectar");
        desconectar.setFont(new Font("Arial", Font.PLAIN, 12));
        desconectar.setEnabled(false);
        desconectar.addActionListener(this);

        // Cria o painel de botões
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 3));
        buttonPanel.add(conectar);
        buttonPanel.add(atualizar);
        buttonPanel.add(desconectar);

        // Adiciona o painel de botões à janela
        add(buttonPanel, BorderLayout.SOUTH);

        client = DiscordRPC.INSTANCE;
        richPresence = new DiscordRichPresence();
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == conectar){
            start();
            conectar.setEnabled(false);
            atualizar.setEnabled(true);
            desconectar.setEnabled(true);
            System.out.println(idField.getText());
        } else if (e.getSource() == desconectar){
            stop();
            conectar.setEnabled(true);
            atualizar.setEnabled(false);
            desconectar.setEnabled(false);
        } else if(e.getSource() == atualizar){
            stop();
            start();
        }

    }
    private void start(){
        isRunning = true;
        DiscordEventHandlers event = new DiscordEventHandlers();
        event.ready = (user) -> System.out.println("Discord Carregado");
        client.Discord_Initialize(idField.getText(), event, true, "0");
        richPresence.startTimestamp = System.currentTimeMillis()/1000;
        richPresence.details = detailsField.getText();
        richPresence.state = statusField.getText();
        richPresence.largeImageKey = largeImageKeyField.getText();
        richPresence.largeImageText = largeImageTextField.getText();
        richPresence.smallImageKey = smallImageKeyField.getText();
        richPresence.partySize = 0;
        richPresence.partyMax = 0;
        richPresence.smallImageText = smallImageTextField.getText();

        client.Discord_UpdatePresence(richPresence);
        new Thread("RPC-Callback-Handler") {
            @Override
            public void run() {
                while (isRunning) {
                    client.Discord_UpdatePresence(richPresence);
                    client.Discord_RunCallbacks();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ignored) {
                        isRunning = false;
                        client.Discord_Shutdown();
                    }
                }
            }
        }.start();
    }
    private void stop() {
        isRunning = false;
        client.Discord_Shutdown();
        System.out.println("Discord desconectado!");
    }
}
