package meAjudaPFVR;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.*;
import java.net.Socket;

public class Client {
    private static String token = null;

    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Digite o endereço IP do servidor:");
            String serverIP = reader.readLine();

            Socket socket = new Socket(serverIP, 21234);
            System.out.println("Conectado ao servidor: " + serverIP);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Loop principal do cliente
            while (true) {
                System.out.println("Escolha uma opção:");
                System.out.println("1. Login");
                System.out.println("2. Cadastro");
                System.out.println("3. Atualização de dados");
                System.out.println("4. Exclusão de conta");
                System.out.println("5. Sair");
                System.out.println("6. Logout");
                System.out.println("7. Consultar informações da conta");
                int option;
                try {
                    option = Integer.parseInt(reader.readLine());
                } catch (NumberFormatException e) {
                    System.out.println("Opção inválida. Por favor, digite novamente.");
                    continue;
                }

                switch (option) {
                    case 1:
                        login(reader, out, in);
                        break;
                    case 2:
                        signup(reader, out, in);
                        break;
                    case 3:
                        updateAccount(reader, out, in);
                        break;
                    case 4:
                        deleteAccount(reader, out, in);
                        break;
                    case 5:
                        System.out.println("Encerrando o cliente...");
                        socket.close();
                        return;
                    case 6:
                        logout(reader, out, in);
                        break;
                    case 7:
                        lookupAccount(reader, out, in);
                        break;
                    default:
                        System.out.println("Opção inválida. Por favor, digite novamente.");
                }
            }

        } catch (IOException e) {
            System.err.println("Erro ao conectar ao servidor: " + e.getMessage());
        }
    }

    private static void login(BufferedReader reader, PrintWriter out, BufferedReader in) throws IOException {
        System.out.println("Digite o endereço de email:");
        String email = reader.readLine();

        System.out.println("Digite a senha:");
        String password = reader.readLine();

        JsonObject requestJson = Utils.createRequest("LOGIN_CANDIDATE");
        JsonObject data = new JsonObject();
        data.addProperty("email", email);
        data.addProperty("password", password);
        requestJson.add("data", data);

        String jsonResponse = Utils.sendRequest(requestJson, out, in);
        JsonObject responseJson = Utils.parseJson(jsonResponse);
        if (responseJson.get("status").getAsString().equals("SUCCESS")) {
            JsonObject dataObject = responseJson.getAsJsonObject("data");
            if (dataObject != null) {
                JsonElement tokenElement = dataObject.get("token");
                if (tokenElement != null) {
                    token = tokenElement.getAsString();
                } else {
                    System.out.println("Token não encontrado na resposta");
                }
            }
        }



        System.out.println(jsonResponse);
    }

    private static void signup(BufferedReader reader, PrintWriter out, BufferedReader in) throws IOException {
        System.out.println("Digite o endereço de email:");
        String email = reader.readLine();

        System.out.println("Digite a senha:");
        String password = reader.readLine();

        System.out.println("Digite o nome:");
        String name = reader.readLine();

        JsonObject requestJson = Utils.createRequest("SIGNUP_CANDIDATE");
        JsonObject data = new JsonObject();
        data.addProperty("email", email);
        data.addProperty("password", password);
        data.addProperty("name", name);
        requestJson.add("data", data);

        String jsonResponse = Utils.sendRequest(requestJson, out, in);
        System.out.println(jsonResponse);
    }

    private static void updateAccount(BufferedReader reader, PrintWriter out, BufferedReader in) throws IOException {
        if (token == null) {
            System.out.println("Por favor, faça login antes de atualizar a conta.");
            return;
        }

        System.out.println("Digite o novo endereço de email:");
        String email = reader.readLine();

        System.out.println("Digite a nova senha:");
        String password = reader.readLine();

        System.out.println("Digite o novo nome:");
        String name = reader.readLine();

        JsonObject requestJson = new JsonObject();
        requestJson.addProperty("operation", "UPDATE_ACCOUNT_CANDIDATE");
        requestJson.addProperty("token", token);
        JsonObject data = new JsonObject();
        data.addProperty("email", email);
        data.addProperty("password", password);
        data.addProperty("name", name);
        requestJson.add("data", data);

        out.println(requestJson.toString());
        String jsonResponse = in.readLine();
        System.out.println(jsonResponse);
    }
    
    
    private static void deleteAccount(BufferedReader reader, PrintWriter out, BufferedReader in) throws IOException {
        if (token == null) {
            System.out.println("Por favor, faça login antes de excluir a conta.");
            return;
        }

        System.out.println("Digite o endereço de email para excluir a conta:");
        String email = reader.readLine();

        JsonObject requestJson = Utils.createRequest("DELETE_ACCOUNT_CANDIDATE");

        JsonObject data = new JsonObject();
        data.addProperty("email", email);
        data.addProperty("token", token);
        requestJson.add("data", data);

        String jsonResponse = Utils.sendRequest(requestJson, out, in);
        System.out.println(jsonResponse);
    }
    private static void logout(BufferedReader reader, PrintWriter out, BufferedReader in) throws IOException {
        if (token == null) {
            System.out.println("Por favor, faça login antes de sair.");
            return;
        }

        JsonObject requestJson = Utils.createRequest("LOGOUT_CANDIDATE");
        JsonObject data = new JsonObject();
        data.addProperty("token", token);
        requestJson.add("data", data);

        String jsonResponse = Utils.sendRequest(requestJson, out, in);
        System.out.println(jsonResponse);

        // Limpa o token no lado do cliente
        token = null;
    }
    private static void lookupAccount(BufferedReader reader, PrintWriter out, BufferedReader in) throws IOException {
        if (token == null) {
            System.out.println("Por favor, faça login antes de consultar as informações da conta.");
            return;
        }

        JsonObject requestJson = Utils.createRequest("LOOKUP_ACCOUNT_CANDIDATE");
        JsonObject data = new JsonObject();
        data.addProperty("token", token);
        requestJson.add("data", data);

        String jsonResponse = Utils.sendRequest(requestJson, out, in);
        JsonObject responseJson = Utils.parseJson(jsonResponse);
        if (responseJson.get("status").getAsString().equals("SUCCESS")) {
            JsonObject dataObject = responseJson.getAsJsonObject("data");
            if (dataObject != null) {
            	JsonElement passwordElement = dataObject.get("password");
                JsonElement emailElement = dataObject.get("email");
                JsonElement nameElement = dataObject.get("name");
                if (emailElement != null) {
                    System.out.println("Email: " + emailElement.getAsString());
                }
                if (nameElement != null) {
                    System.out.println("Nome: " + nameElement.getAsString());
                }
                if (passwordElement != null) {
                	System.out.println("Senha: " + passwordElement.getAsString());
                }
            }
        } else {
            System.out.println("Erro ao consultar as informações da conta: " + responseJson.get("status").getAsString());
        }
    }


}
