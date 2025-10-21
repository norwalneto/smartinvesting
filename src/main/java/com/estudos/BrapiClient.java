package com.estudos;

import com.google.gson.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BrapiClient {

    private final String token;
    private final Gson gson = new Gson();

    public BrapiClient(String token) {
        this.token = token;
    }

    public void getQuoteFull(String symbol) throws Exception {
        String urlString = String.format("https://brapi.dev/api/quote/%s?token=%s", symbol, token);
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder json = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            json.append(line);
        }
        reader.close();

        JsonObject response = gson.fromJson(json.toString(), JsonObject.class);

        if (response.has("results")) {
            JsonArray results = response.getAsJsonArray("results");
            if (results.size() > 0) {
                JsonObject quote = results.get(0).getAsJsonObject();
                printQuoteTable(quote);
            } else {
                System.out.println(ConsoleColors.YELLOW + "⚠ Nenhum resultado encontrado para o ativo informado." + ConsoleColors.RESET);
            }
        } else {
            System.out.println(ConsoleColors.RED + "❌ Resposta inválida da API." + ConsoleColors.RESET);
        }
    }

    // ✅ Exibe os dados em uma tabela formatada com cor na variação
    private void printQuoteTable(JsonObject q) {
        // Pega os valores numéricos para decidir a cor
        double variacao = parseDoubleSafe(get(q, "regularMarketChange"));
        double variacaoPercent = parseDoubleSafe(get(q, "regularMarketChangePercent"));

        // Define cor e símbolo
        String variacaoTexto;
        if (variacao > 0) {
            variacaoTexto = ConsoleColors.GREEN_BOLD + "↑ " + variacao + " (" + variacaoPercent + "%)" + ConsoleColors.RESET;
        } else if (variacao < 0) {
            variacaoTexto = ConsoleColors.RED + "↓ " + variacao + " (" + variacaoPercent + "%)" + ConsoleColors.RESET;
        } else {
            variacaoTexto = variacao + " (" + variacaoPercent + "%)";
        }

        System.out.println(ConsoleColors.BLUE_BOLD + "\n╔════════════════════════════════════════════════════════╗" + ConsoleColors.RESET);
        System.out.printf("║ %-22s : %-35s ║%n", "Nome", get(q, "longName"));
        System.out.printf("║ %-22s : %-35s ║%n", "Símbolo", get(q, "symbol"));
        System.out.printf("║ %-22s : R$ %-33s ║%n", "Preço atual", get(q, "regularMarketPrice"));
        System.out.printf("║ %-22s : %-35s ║%n", "Variação", variacaoTexto);
        System.out.printf("║ %-22s : %-35s ║%n", "Abertura", get(q, "regularMarketOpen"));
        System.out.printf("║ %-22s : %-35s ║%n", "Fechamento Anterior", get(q, "regularMarketPreviousClose"));
        System.out.printf("║ %-22s : %-35s ║%n", "Faixa 52 Semanas", get(q, "fiftyTwoWeekLow") + " - " + get(q, "fiftyTwoWeekHigh"));
        System.out.printf("║ %-22s : %-35s ║%n", "P/L (Preço/Lucro)", get(q, "priceEarnings"));
        System.out.printf("║ %-22s : %-35s ║%n", "Lucro por Ação", get(q, "earningsPerShare"));
        System.out.printf("║ %-22s : %-35s ║%n", "Volume", get(q, "regularMarketVolume"));
        System.out.printf("║ %-22s : %-35s ║%n", "Moeda", get(q, "currency"));
        System.out.printf("║ %-22s : %-35s ║%n", "Logo", get(q, "logourl"));
        System.out.println("╚════════════════════════════════════════════════════════╝");
        System.out.println(ConsoleColors.GREEN_BOLD + "✅ Consulta concluída com sucesso!" + ConsoleColors.RESET);
    }

    private String get(JsonObject obj, String key) {
        return obj.has(key) && !obj.get(key).isJsonNull() ? obj.get(key).getAsString() : "—";
    }

    private double parseDoubleSafe(String value) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return 0.0;
        }
    }
}
