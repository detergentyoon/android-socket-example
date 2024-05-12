package com.example.socket;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private String serverAddr = "Change to your server's IPv4 address";
    private int serverPort = 9998;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private TextView tvReceivedData;
    private EditText etInput;

    private String serverMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvReceivedData = findViewById(R.id.tvReceive);
        etInput = findViewById(R.id.etInput);
        Button btnSend = findViewById(R.id.btnSend);

        // Socket Connection
        new Thread(() -> {
            try {
                socket = new Socket(serverAddr, serverPort);

                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Receive messages from the server and output them to the screen
                while ((serverMessage = in.readLine()) != null) {
                    runOnUiThread(() -> {
                        tvReceivedData.append(serverMessage + "\n");
                    });
                }
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).start();

        btnSend.setOnClickListener(view -> {
            String message = etInput.getText().toString();
            sendMessage(message);
        });
    }

    private void sendMessage(String message) {
        new Thread(() -> {
            try {
                out.println(message); // Send message to server socket
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            // Close the socket when the app shuts down.
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            // TODO: Error Handling
        }
    }
}
