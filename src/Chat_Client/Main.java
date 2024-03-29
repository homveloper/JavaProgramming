package Chat_Client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class Main extends Application {

    Socket socket;
    TextArea textArea;

    public void startClient(String IP, int PORT) {
        Thread thread = new Thread() {
            public void run() {
                try {
                    socket = new Socket(IP, PORT);
                    receive();
                } catch (Exception e) {
                    if (!socket.isClosed()) {
                        stopClient();
                        System.out.println("[서버 접속 실패]");
                        Platform.exit();
                    }
                }
            }
        };
        thread.start();
    }

    public void stopClient() {
        try {
            if(socket != null && socket.isClosed()){
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void receive() {
        while (true) {
            try {
                InputStream reader = socket.getInputStream();
                byte[] buffer = new byte[512];
                int length = reader.read(buffer);
                if(length == -1) throw new IOException();
                String message = new String(buffer,"UTF-8");
                Platform.runLater(()->{
                    textArea.appendText(message);
                });
            } catch (Exception e) {
                stopClient();
                break;
            }
        }
    }

    public void send(String message) {
        Thread thread = new Thread(){
            public void run(){
                try {
                    OutputStream writer = socket.getOutputStream();
                    byte[] buffer = message.getBytes("UTF-8");
                    writer.write(buffer);
                    writer.flush();
                } catch (Exception e) {
                    stopClient();
                }
            }
        };
        thread.start();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(5));

        HBox hbox = new HBox();
        hbox.setSpacing(5);

        TextField userName = new TextField();
        userName.setPrefWidth(150);
        userName.setPromptText("닉네임을 입력하세요");
        HBox.setHgrow(userName, Priority.ALWAYS);

        TextField IPText = new TextField("localhost");
        TextField PORTText = new TextField("4674");
        PORTText.setPrefWidth(80);

        hbox.getChildren().addAll(userName,IPText,PORTText);
        root.setTop(hbox);

        textArea = new TextArea();
        textArea.setEditable(false);
        root.setCenter(textArea);

        TextField input = new TextField();
        input.setPrefWidth(Double.MAX_VALUE);
        input.setDisable(true);

        input.setOnAction(event -> {
            send(userName.getText() + ": " + input.getText() +"\n");
            input.setText("");
            input.requestFocus();
        });

        Button sendButton = new Button("보내기");
        sendButton.setDisable(false);

        sendButton.setOnAction(event->{
            send(userName.getText() + ": " + input.getText() + "\n");
            input.setText("");
            input.requestFocus();
        });

        Button connectionButton = new Button("접속하기");
        connectionButton.setOnAction(event->{
            if(connectionButton.getText().equals("접속하기")){
                int PORT = 4674;
                try {
                    PORT = Integer.parseInt(PORTText.getText());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                startClient(IPText.getText(), PORT);
                Platform.runLater(()->{
                    textArea.appendText("[채팅방 접속]\n");
                });
                connectionButton.setText("종료하기");
                input.setDisable(false);
                sendButton.setDisable(false);
                input.requestFocus();
            }else{
                stopClient();
                Platform.runLater(()->{
                    textArea.appendText("[채팅방 퇴장]\n");
                });
                connectionButton.setText("접속하기");
                input.setDisable(true);
                sendButton.setDisable(true);
            }
        });

        BorderPane pane = new BorderPane();
        pane.setLeft(connectionButton);
        pane.setCenter(input);
        pane.setRight(sendButton);

        root.setBottom(pane);

        Scene scene = new Scene(root,400,400);
        primaryStage.setTitle("[채팅 클라이언트]");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(event->stopClient());
        primaryStage.show();

        connectionButton.requestFocus();
    }

    public static void main(String[] args) {
        launch(args);
    }

}