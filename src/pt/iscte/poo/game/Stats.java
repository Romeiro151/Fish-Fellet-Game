package pt.iscte.poo.game;

public class Stats implements Comparable<Stats>{
    
    private String xPlayer;
    private int xTotalFishMoves;
    private int xTotalSeconds;
    private int xTentativas;

    //Construtor
    public Stats(String yPlayer, int yTotalFishMoves, int yTotalSeconds, int yTentativas){
        this.xPlayer =yPlayer;
        this.xTotalFishMoves = yTotalFishMoves;
        this.xTotalSeconds = yTotalSeconds;
        this.xTentativas = yTentativas;
    }

    //Getter dos movimentos totais
    public int mGetTotalMoves(){
        return xTotalFishMoves;
    }

    //Getter do nome dos Player
    public String mGetName(){
        return xPlayer;
    }

    //Getter dos seguntos totais
    public int mGetTotalSeconds(){
        return xTotalSeconds;
    }

    //Getter das tentativas
    public int mGetTentativas(){
        return xTentativas;
    }

    //To String
    @Override
    public String toString(){

        int xMinutos = mGetTotalSeconds() / 60;
        int xSegundos = mGetTotalSeconds() % 60;

        return String.format("%-15s | Moves: %-3d | Time: %02d:%02d | Tentativas: %-2d", 
                     xPlayer, xTotalFishMoves, xMinutos, xSegundos, xTentativas);

    }

    // Método necessário para ordenar para fazer o ranking
    @Override
    public int compareTo(Stats yOther){
        if(this.xTentativas != yOther.xTentativas){
            return Integer.compare(this.xTentativas, yOther.xTentativas);
        }
        if(this.xTotalSeconds != yOther.xTotalSeconds){
            return Integer.compare(this.xTotalSeconds, yOther.xTotalSeconds);
        }
        return Integer.compare(this.mGetTotalMoves(), yOther.mGetTotalMoves());
    }
}
