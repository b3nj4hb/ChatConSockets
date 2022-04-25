package chat;

import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class Servidor extends Thread {

	private static ArrayList<BufferedWriter> clientes;
	private static ServerSocket servidor;
	private String nombre;
	private Socket puerto;
	private InputStream entrada;
	private InputStreamReader lectorEntrada;
	private BufferedReader buferLectura;

	public Servidor(Socket puerto) {
		this.puerto = puerto;
		try {
			entrada = puerto.getInputStream();
			lectorEntrada = new InputStreamReader(entrada);
			buferLectura = new BufferedReader(lectorEntrada);
		} catch (IOException e) {
		}
	}

	@Override
	public void run() {

		try {
			String mensaje;
			OutputStream salida = this.puerto.getOutputStream();
			Writer escritura = new OutputStreamWriter(salida);
			BufferedWriter buferEscritura = new BufferedWriter(escritura);
			clientes.add(buferEscritura);
			nombre = mensaje = buferLectura.readLine();
			while (!"Salir".equalsIgnoreCase(mensaje) && mensaje != null) {
				mensaje = buferLectura.readLine();

				enviarMensajes(buferEscritura, mensaje);
				System.out.println(mensaje);

			}
		} catch (IOException e) {
		}
	}

	public void enviarMensajes(BufferedWriter escribir, String mensajeEscrito) throws IOException {
		BufferedWriter escrito;
		for (BufferedWriter buferEscrito : clientes) {
			escrito = (BufferedWriter) buferEscrito;
			if (!(escribir == escrito)) {
				buferEscrito.write(nombre + " -> " + mensajeEscrito + "\r\n");
				buferEscrito.flush();
			}
		}
	}

	public static void main(String[] args) {
		try {
			JLabel lblTitulo = new JLabel("Puerto en el servidor:");
			JTextField txtPuerto = new JTextField("1201");
			Object[] texts = { lblTitulo, txtPuerto };
			JOptionPane.showMessageDialog(null, texts);
			servidor = new ServerSocket(Integer.parseInt(txtPuerto.getText()));
			clientes = new ArrayList<>();
			JOptionPane.showMessageDialog(null, "Servidor activo en el puerto: " + txtPuerto.getText());
			while (true) {
				System.out.println("Esperando conexion ...");
				Socket puertoDeConexion = servidor.accept();

				System.out.println("Cliente conectado ...");
				Thread t = new Servidor(puertoDeConexion);
				t.start();
			}
		} catch (HeadlessException | IOException | NumberFormatException e) {
		}
	}

}