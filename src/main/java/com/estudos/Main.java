package com.estudos;

import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Consulta Completa de Ações (Brapi API) ===");
        System.out.print("Digite o código do ativo (ex: PETR4, VALE3, ITUB4): ");
        String symbol = scanner.nextLine().toUpperCase();

        // 🔐 Token fixo aqui — substitua pelo seu token da Brapi
        String token = "qHjBEXfLYupg3ewdvDk5rQ";

        try {
            BrapiClient client = new BrapiClient(token);
            client.getQuoteFull(symbol);
        } catch (Exception e) {
            System.err.println("Erro ao consultar a API: " + e.getMessage());
        } finally {
            scanner.close();
        }

    }
}