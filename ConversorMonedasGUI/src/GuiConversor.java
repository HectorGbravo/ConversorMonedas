import javax.swing.JFrame;
import org.json.JSONObject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class GuiConversor extends JFrame {
	
		private JLabel deMonedaLabel, aMonedaLabel, cantidadLabel;
		private JComboBox<Object> deMonedaCombo, aMonedaCombo;
		private JTextField cantidadTextField;
		private JButton convertirButton;

		String apiKey = "gCqzW1xD9sVXIO7URxjSPvh8v6YPW21Y";
		String deMoneda;
		String aMoneda;
		double cantidad;

		public GuiConversor() {
			
			// Se establece el titulo de la ventana
			setTitle("Convertidor de Monedas");
			
			// Se crea el objeto JPanel
			JPanel panelPrincipal = new JPanel();
			
			// Se asigna el Layout
			panelPrincipal.setLayout(new GridLayout(4,2));
			
			// Se crea y se añaden los elementos de la interfaz
			deMonedaLabel = new JLabel("  Moneda Origen:");
			deMonedaLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
			panelPrincipal.add(deMonedaLabel);

			aMonedaLabel = new JLabel("  Moneda destino:");
			aMonedaLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
			panelPrincipal.add(aMonedaLabel);

			// Se añaden los valores a la lista desplegable
			CargaComboBox();
			panelPrincipal.add(deMonedaCombo);
			panelPrincipal.add(aMonedaCombo);
			
			// Campo que solicita la cantidad para realizar la conversión
			cantidadLabel = new JLabel("  Cantidad a convertir:");
			cantidadLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
			panelPrincipal.add(cantidadLabel);
			
			cantidadTextField = new JTextField(10);
			panelPrincipal.add(cantidadTextField);
			
			// Boton para iniciar el proceso de convertir monedas
			convertirButton = new JButton("Convertir monedas");
			convertirButton.setBackground(new Color(0, 128, 255));
			convertirButton.setFont(new Font("Tahoma", Font.BOLD, 14));
			panelPrincipal.add(convertirButton);
			
			// Se añade el panelPrincipal a la ventana
			getContentPane().add(panelPrincipal);
			
			// Se agrega el ActionListener al boton
			convertirButton.addActionListener(new ConvertirListener());
			
			// Se establece el tamaño de la ventana y se h continua aquíace visible
			setSize(718,150);
			setVisible(true);
			
			// Se indica que al cerrar la ventana, se cierre el programa
			setDefaultCloseOperation(EXIT_ON_CLOSE);
		}
		
		private void CargaComboBox() {
			
			Path ruta = Paths.get("src/monedas.csv");
			File archivoCSV = ruta.toFile();
	        BufferedReader br = null;
	        String linea = "";
	        String[] datosMonedas;
	        deMonedaCombo = new JComboBox<>();
	        aMonedaCombo = new JComboBox<>();
	
	        try {
	            br = new BufferedReader(new FileReader(archivoCSV));
	            while ((linea = br.readLine()) != null) {
	                datosMonedas = linea.split(System.lineSeparator());
 	                String[] arrayMonedas = new String[datosMonedas.length];
 	                
	                // Se añaden las monedas a los ComboBox 
	                for (int i = 0; i < datosMonedas.length; i++) {
	                    arrayMonedas[i] = datosMonedas[i];
	                    deMonedaCombo.addItem(arrayMonedas[i]);
	                    aMonedaCombo.addItem(arrayMonedas[i]);
	                }
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            if (br != null) {
	                try {
	                    br.close();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
		}
	
		// Clase interna para manejar eventos
		class ConvertirListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				// Se obtiene la cantidad a convertir
				double cantidad = Double.parseDouble(cantidadTextField.getText());
				
				// Se obtiene la moneda origen
				String deMoneda = (String) deMonedaCombo.getSelectedItem();
				String siglaDeMoneda = deMoneda.substring(0,3);
						
				
				// Se obtiene la moneda destino
				String aMoneda = (String) aMonedaCombo.getSelectedItem();
				String siglaAMoneda = aMoneda.substring(0,3);
				
		        try {
		        	// Se llama a la API Apilayer a través del método GET
		        	URL url = new URL("https://api.apilayer.com/currency_data/convert?to=" + siglaAMoneda
		                    + "&from=" + siglaDeMoneda + "&amount=" + cantidad+"&apikey="+apiKey);
		            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		            connection.setRequestMethod("GET");
		            
		            // Si GET exitoso (200) se lee la respuesta de la API en formato JSON
		            
		            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		            String entradaDatos; 
		            StringBuffer respuesta = new StringBuffer();
		            while ((entradaDatos = in.readLine()) != null) {
		            	respuesta.append(entradaDatos);
		            }
		            in.close();
		            
		            // Se convierte el objeto JSON a string para obtener la informacion
		            JSONObject objetoJson = new JSONObject(respuesta.toString());
		            
		            // se obtienen los campos del array consulta
		            JSONObject consulta = objetoJson.getJSONObject("query");
		            consulta.getString("from");
		            consulta.getString("to");
		            consulta.getDouble("amount");
		
		            // Se obtienen los campos del array info
		            
		            JSONObject info = objetoJson.getJSONObject("info");
		            long timestamp = info.getLong("timestamp");
		            double tipoCambio = info.getDouble("quote");
		           
		            // Conversion de la fecha timestamp en formato UNIX A ZonedDateTime y se formatea acorde
		            ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());
		            DateTimeFormatter formateadorFecha = DateTimeFormatter.ofPattern("HH:mm:ss, yyyy/MM/dd");
		            String fechaFormateada = formateadorFecha.format(zdt);
		
		            // Se imprime el resultado de la conversión
		            double result = objetoJson.getDouble("result");
		
					// Se muestra el resultado
					JOptionPane.showMessageDialog(null, cantidad + " " + deMoneda + " = " + result + " " 
					+ aMoneda + "\n"+ "Hora/Fecha de Consulta:" + fechaFormateada + " Tipo de cambio:" + tipoCambio);
		
		        } catch (Exception a) {
		            System.out.println("Error: " + a.getMessage());
	        }
			
				
		}
	}
}
