package pt.iscte.poo.game;

import java.io.*;
import java.util.*;
import pt.iscte.poo.gui.ImageGUI;

public class ScoreBoard {

    private static final String FILE_NAME = "tabelas.txt";

    // Método serve para guardar num ficheiro os resultados de cada jogo terminado
    public static void saveScore(List<Stats> yStatsList) {
        // O True no FileWriter permite adicionar ao fim do ficheiro (append) sem apagar o que lá estava
        try (PrintWriter xWriter = new PrintWriter(new FileWriter(FILE_NAME, true))) {
            for (Stats xS : yStatsList) {
                // Criamos uma string formatada especificamente para guardar no ficheiro
                // Ordem: Nome ; Moves ; Segundos ; Tentativas (de acordo com o construtor do Stats)
                String line = String.format("%s;%d;%d;%d", 
                    xS.mGetName(), 
                    xS.mGetTotalMoves(), 
                    xS.mGetTotalSeconds(), 
                    xS.mGetTentativas()
                );
                xWriter.println(line);
            }
        } catch (IOException e) {
            System.err.println("Erro ao guardar o score: " + e.getMessage());
        }
    }

    // Função lê do ficheiro e apresenta a ScoreBoard
    public static void showScoreBoard() {

        List<Stats> xAllScores = new ArrayList<>();
        File xFile = new File(FILE_NAME);

        // Ler do ficheiro se ele existir
        if (xFile.exists()) {
            try{
                Scanner xScanner = new Scanner(xFile);
                while (xScanner.hasNextLine()) {

                    String xLine = xScanner.nextLine().trim();

                    if (xLine.isEmpty()){
                        continue;
                    }

                    String[] xTokens = xLine.split(";");

                    String xName = xTokens[0];
                    int xMoves = Integer.parseInt(xTokens[1]);
                    int xTime = Integer.parseInt(xTokens[2]);
                    int xTentativas = Integer.parseInt(xTokens[3]);

                    // Adicionar à lista
                    xAllScores.add(new Stats(xName, xMoves, xTime, xTentativas));
                }
                xScanner.close();
            }catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        //Ordenar a lista (Usa o compareTo da classe Stats -> Ordem Crescente)
        Collections.sort(xAllScores);

        // Construir a mensagem para o utilizador
        StringBuilder xScoreBoard = new StringBuilder();
        xScoreBoard.append("=== TOP 10 MELHORES JOGADORES ===\n\n");
        xScoreBoard.append(String.format("%-15s | %-5s | %-5s | %-5s\n", "Nome", "Tent.", "Moves", "Tempo"));
        xScoreBoard.append("----------------------------------------------------\n");

        // Mostra apenas os Top 10
        int xCount = 0;
        for (Stats xStats : xAllScores) {

            if (xCount >= 10){
                break; 
            }

            int xMinutos = xStats.mGetTotalSeconds() / 60;
            int xSegundos = xStats.mGetTotalSeconds() % 60;
            String xTempo = String.format("%02d:%02d", xMinutos, xSegundos);

            xScoreBoard.append(String.format("%-15s | %-5d | %-5d | %s\n", 
                xStats.mGetName(), 
                xStats.mGetTentativas(), 
                xStats.mGetTotalMoves(), 
                xTempo
            ));
            xCount++;
        }

        if (xAllScores.isEmpty()) {
            xScoreBoard.append("\nAinda não há registos.");
        }

        // Apresentar a janela
        ImageGUI.getInstance().showMessage("Melhores Pontuações", xScoreBoard.toString());
    }
}