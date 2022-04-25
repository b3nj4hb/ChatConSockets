package chat;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import javax.swing.*;

public class Cliente extends JFrame implements ActionListener, KeyListener {

	private static final long serialVersionUID = 5685553375855922686L;
	private final JTextField txtDireccionIP;
	private final JTextField txtPuerto;
	private final JTextField txtNombreCliente;
	private final JTextArea text;
	private final JTextField txtMensaje;
	private final JButton btnEnviar;
	private final JButton btnSalir;
	private final JLabel lblMensajesEnChat;
	private final JLabel lblMensaje;
	private final JPanel jpnlContenidoVentana;
	private Socket socket;
	private OutputStream streamEscritura;
	private Writer escribir;
	private BufferedWriter buferEscritura;

	public Cliente() throws IOException {

		JLabel lblMessage = new JLabel("Validar datos");
		txtDireccionIP = new JTextField("127.0.0.1");
		txtPuerto = new JTextField("1201");
		txtNombreCliente = new JTextField("Cliente");
		Object[] texts = { lblMessage, txtDireccionIP, txtPuerto, txtNombreCliente };
		JOptionPane.showMessageDialog(null, texts);
		jpnlContenidoVentana = new JPanel();
		text = new JTextArea(10, 20);
		text.setEditable(false);
		text.setBackground(new Color(255, 255, 255));
		txtMensaje = new JTextField(20);
		lblMensajesEnChat = new JLabel("Mensajes enviados");
		lblMensaje = new JLabel("Mensaje");
		btnEnviar = new JButton("Enviar");
		btnEnviar.setToolTipText("Enviar mensaje");
		btnSalir = new JButton("Salir");
		btnSalir.setToolTipText("Salir del chat");
		btnEnviar.addActionListener(this);
		btnSalir.addActionListener(this);
		btnEnviar.addKeyListener(this);
		txtMensaje.addKeyListener(this);
		JScrollPane scroll = new JScrollPane(text);
		text.setLineWrap(true);
		jpnlContenidoVentana.add(lblMensajesEnChat);
		jpnlContenidoVentana.add(scroll);
		jpnlContenidoVentana.add(lblMensaje);
		jpnlContenidoVentana.add(txtMensaje);
		jpnlContenidoVentana.add(btnSalir);
		jpnlContenidoVentana.add(btnEnviar);
		setTitle(txtNombreCliente.getText());
		setContentPane(jpnlContenidoVentana);
		setLocationRelativeTo(null);
		setResizable(false);
		setSize(250, 300);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public void conectarse() throws IOException {
		socket = new Socket(txtDireccionIP.getText(), Integer.parseInt(txtPuerto.getText()));
		streamEscritura = socket.getOutputStream();
		escribir = new OutputStreamWriter(streamEscritura);
		buferEscritura = new BufferedWriter(escribir);
		buferEscritura.write(txtNombreCliente.getText() + "\r\n");
		buferEscritura.flush();
	}

	public void enviarMensaje(String mensaje) throws IOException {
		if (mensaje.equals("Salir")) {
			buferEscritura.write("Desconectado \r\n");
			text.append("Disconnected \r\n");
		} else {
			buferEscritura.write(mensaje + "\r\n");
			text.append(txtNombreCliente.getText() + " : " + txtMensaje.getText() + "\r\n");
		}
		buferEscritura.flush();
		txtMensaje.setText("");
	}

	public void leer() throws IOException {

		InputStream lectura = socket.getInputStream();
		InputStreamReader streamDeLectura = new InputStreamReader(lectura);
		BufferedReader buferDeLectura = new BufferedReader(streamDeLectura);
		String mensaje = "";
		while (!"Salir".equalsIgnoreCase(mensaje)) {
			if (buferDeLectura.ready()) {
				mensaje = buferDeLectura.readLine();
				if (mensaje.equals("Salir")) {
					text.append("Servidor fuera de linea \r\n");
				} else {
					text.append(mensaje + "\r\n");
				}
			}
		}
	}

	public void sair() throws IOException {
		enviarMensaje("Salir");
		buferEscritura.close();
		escribir.close();
		streamEscritura.close();
		socket.close();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getActionCommand().equals(btnEnviar.getActionCommand())) {
				enviarMensaje(txtMensaje.getText());
			} else if (e.getActionCommand().equals(btnSalir.getActionCommand())) {
				sair();
			}
		} catch (IOException e1) {
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			try {
				enviarMensaje(txtMensaje.getText());
			} catch (IOException e1) {
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}

	public static void main(String[] args) throws IOException {
		Cliente app = new Cliente();
		app.conectarse();
		app.leer();
	}
}